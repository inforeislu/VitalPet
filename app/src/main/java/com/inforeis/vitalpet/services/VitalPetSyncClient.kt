package com.inforeis.vitalpet.services

import android.os.Build
import com.inforeis.vitalpet.models.DewormingLog
import com.inforeis.vitalpet.models.DietPlan
import com.inforeis.vitalpet.models.EvolutionPhoto
import com.inforeis.vitalpet.models.FastingLog
import com.inforeis.vitalpet.models.MedicalVisit
import com.inforeis.vitalpet.models.Medication
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.models.SymptomLog
import com.inforeis.vitalpet.models.VaccineLog
import com.inforeis.vitalpet.models.WalkSession
import com.inforeis.vitalpet.models.WeightLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

data class MobileSyncPayload(
    val deviceId: String = UUID.randomUUID().toString(),
    val deviceName: String = "${Build.MANUFACTURER} ${Build.MODEL}",
    val platform: String = "android",
    val timestamp: Long = System.currentTimeMillis(),
    val appVersion: String = "1.0.2",
    val authToken: String = "",
    val pets: List<Pet>
)

data class MobileSyncResponse(
    val success: Boolean,
    val message: String,
    val addedPets: Int,
    val updatedPets: Int,
    val totalPets: Int,
    val serverTimestamp: Long = System.currentTimeMillis(),
    val pets: List<Pet> = emptyList()
)

data class SyncResult(
    val success: Boolean,
    val message: String,
    val totalPetsSynced: Int = 0
)

data class DiscoveredServer(
    val host: String = "",
    val port: Int = 8920,
    val token: String = "",
    val name: String = "VitalPet Windows"
)

object VitalPetSyncClient {
    private val gson = VitalPetJson.gson

    /**
     * Analisa o payload do QR Code do Windows (vitalpet://sync?host=192.168.1.15&port=8920&token=XYZ)
     */
    fun parseQrCode(qrText: String): Triple<String, Int, String>? {
        return try {
            val clean = qrText.trim()
            if (clean.startsWith("vitalpet://sync")) {
                val uri = android.net.Uri.parse(clean)
                val host = uri.getQueryParameter("host") ?: return null
                val port = uri.getQueryParameter("port")?.toIntOrNull() ?: 8920
                val token = uri.getQueryParameter("token") ?: ""
                Triple(host, port, token)
            } else if (clean.contains(":") || clean.contains(".")) {
                // Formato manual simples IP:Porta
                val parts = clean.split(":")
                val host = parts[0]
                val port = parts.getOrNull(1)?.toIntOrNull() ?: 8920
                Triple(host, port, "")
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Executa a sincronização P2P por Wi-Fi com o VitalPet Windows.
     */
    suspend fun syncWithDesktop(
        host: String,
        port: Int = 8920,
        token: String = ""
    ): SyncResult = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://$host:$port/api/sync/exchange")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = 8000
            conn.readTimeout = 10000
            conn.doOutput = true
            conn.doInput = true
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.setRequestProperty("Accept", "application/json")

            val currentPets = PetRepository.pets.value
            val payload = MobileSyncPayload(
                authToken = token,
                pets = currentPets
            )

            val jsonOutput = gson.toJson(payload)
            OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(jsonOutput)
                writer.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode == 200) {
                val reader = InputStreamReader(conn.inputStream, Charsets.UTF_8)
                val response = gson.fromJson(reader, MobileSyncResponse::class.java)

                if (response.success && response.pets.isNotEmpty()) {
                    // Atualizar repositório local com os dados mesclados pelo computador
                    val jsonMerged = gson.toJson(response.pets)
                    PetRepository.importJson(jsonMerged)

                    SyncResult(
                        success = true,
                        message = response.message,
                        totalPetsSynced = response.totalPets
                    )
                } else {
                    SyncResult(
                        success = response.success,
                        message = response.message,
                        totalPetsSynced = response.totalPets
                    )
                }
            } else {
                val errText = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                SyncResult(
                    success = false,
                    message = "Erro de conexão ($responseCode): $errText"
                )
            }
        } catch (e: Exception) {
            SyncResult(
                success = false,
                message = "Não foi possível conectar ao computador: ${e.localizedMessage}"
            )
        }
    }

    /**
     * Procura automaticamente servidores VitalPet Desktop anunciados via UDP broadcast na porta 8921.
     */
    suspend fun discoverLocalServers(timeoutMs: Int = 1800): List<DiscoveredServer> = withContext(Dispatchers.IO) {
        val found = mutableListOf<DiscoveredServer>()
        var socket: java.net.DatagramSocket? = null
        try {
            socket = java.net.DatagramSocket()
            socket.broadcast = true
            socket.soTimeout = 400

            val msg = "vitalpet_discover".toByteArray(Charsets.UTF_8)
            val packet = java.net.DatagramPacket(
                msg,
                msg.size,
                java.net.InetAddress.getByName("255.255.255.255"),
                8921
            )
            socket.send(packet)

            val buf = ByteArray(1024)
            val deadline = System.currentTimeMillis() + timeoutMs
            while (System.currentTimeMillis() < deadline) {
                try {
                    val recvPacket = java.net.DatagramPacket(buf, buf.size)
                    socket.receive(recvPacket)
                    val reply = String(recvPacket.data, 0, recvPacket.length, Charsets.UTF_8).trim()
                    if (reply.startsWith("{") && reply.contains("port")) {
                        val server = gson.fromJson(reply, DiscoveredServer::class.java)
                        val senderHost = recvPacket.address.hostAddress ?: server.host
                        val resolvedHost = if (server.host.isNotBlank() && !server.host.startsWith("127.")) server.host else senderHost
                        val resolved = server.copy(host = resolvedHost)
                        if (found.none { it.host == resolved.host && it.port == resolved.port }) {
                            found.add(resolved)
                        }
                    }
                } catch (e: java.net.SocketTimeoutException) {
                    if (System.currentTimeMillis() >= deadline) break
                } catch (e: Exception) {
                    // Ignora pacotes malformados
                }
            }
        } catch (e: Exception) {
            // Em redes onde broadcast é bloqueado
        } finally {
            socket?.close()
        }
        found
    }
}

