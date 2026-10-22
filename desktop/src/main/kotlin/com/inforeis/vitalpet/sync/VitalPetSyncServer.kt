package com.inforeis.vitalpet.sync

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.inforeis.vitalpet.services.DesktopPetRepository
import com.inforeis.vitalpet.services.VitalPetJson
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.util.UUID
import java.util.concurrent.Executors

data class SyncServerState(
    val isRunning: Boolean = false,
    val localIp: String = "127.0.0.1",
    val port: Int = 8920,
    val authToken: String = "",
    val qrCodePayload: String = "",
    val qrBitmap: ImageBitmap? = null,
    val lastSyncMessage: String = "",
    val lastSyncTimestamp: Long = 0L,
    val connectedClientsCount: Int = 0
)

object VitalPetSyncServer {
    private const val DEFAULT_PORT = 8920
    private var server: HttpServer? = null
    private val gson = VitalPetJson.gson

    private val _state = MutableStateFlow(SyncServerState())
    val state: StateFlow<SyncServerState> = _state.asStateFlow()

    @Synchronized
    fun start(port: Int = DEFAULT_PORT) {
        if (server != null) return

        try {
            val localIp = findLocalLanAddress()
            val token = UUID.randomUUID().toString().take(8).uppercase()
            val qrContent = "vitalpet://sync?host=$localIp&port=$port&token=$token"
            val qrBitmap = generateQrCodeBitmap(qrContent, 300)

            val http = HttpServer.create(InetSocketAddress(port), 0)
            http.executor = Executors.newCachedThreadPool()

            http.createContext("/api/sync/info", InfoHandler(localIp, port, token))
            http.createContext("/api/sync/exchange", ExchangeHandler(token))

            http.start()
            server = http

            startUdpBeacon(localIp, port, token)

            _state.value = SyncServerState(
                isRunning = true,
                localIp = localIp,
                port = port,
                authToken = token,
                qrCodePayload = qrContent,
                qrBitmap = qrBitmap,
                lastSyncMessage = "Servidor ativo e pronto para emparelhamento",
                lastSyncTimestamp = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = _state.value.copy(
                isRunning = false,
                lastSyncMessage = "Erro ao iniciar servidor: ${e.localizedMessage}"
            )
        }
    }

    private var udpSocket: java.net.DatagramSocket? = null
    private var udpThread: Thread? = null

    private fun startUdpBeacon(localIp: String, port: Int, token: String) {
        try {
            val socket = java.net.DatagramSocket(8921, InetAddress.getByName("0.0.0.0"))
            socket.broadcast = true
            udpSocket = socket

            val thread = Thread {
                val buf = ByteArray(1024)
                while (!socket.isClosed) {
                    try {
                        val packet = java.net.DatagramPacket(buf, buf.size)
                        socket.receive(packet)
                        val msg = String(packet.data, 0, packet.length, Charsets.UTF_8).trim()
                        if (msg == "vitalpet_discover") {
                            val responseJson = """{"host":"$localIp","port":$port,"token":"$token","name":"VitalPet Windows"}"""
                            val resBytes = responseJson.toByteArray(Charsets.UTF_8)
                            val reply = java.net.DatagramPacket(resBytes, resBytes.size, packet.address, packet.port)
                            socket.send(reply)
                        }
                    } catch (e: Exception) {
                        if (socket.isClosed) break
                    }
                }
            }
            thread.isDaemon = true
            thread.start()
            udpThread = thread
        } catch (e: Exception) {
            // Porta UDP pode estar em uso ou bloqueada
        }
    }

    @Synchronized
    fun stop() {
        server?.stop(0)
        server = null
        try {
            udpSocket?.close()
        } catch (e: Exception) {
            // Ignorar
        }
        udpSocket = null
        udpThread = null
        _state.value = _state.value.copy(
            isRunning = false,
            lastSyncMessage = "Servidor de sincronização parado"
        )
    }

    fun restart() {
        stop()
        start()
    }

    private class InfoHandler(
        private val ip: String,
        private val port: Int,
        private val token: String
    ) : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            exchange.responseHeaders.add("Content-Type", "application/json; charset=UTF-8")
            exchange.responseHeaders.add("Access-Control-Allow-Origin", "*")

            val info = mapOf(
                "appName" to "VitalPet Desktop",
                "version" to "1.0",
                "ip" to ip,
                "port" to port,
                "token" to token,
                "totalPets" to DesktopPetRepository.pets.value.size
            )
            val json = VitalPetJson.gson.toJson(info)
            val bytes = json.toByteArray(Charsets.UTF_8)
            exchange.sendResponseHeaders(200, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
    }

    private class ExchangeHandler(private val expectedToken: String) : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            exchange.responseHeaders.add("Content-Type", "application/json; charset=UTF-8")
            exchange.responseHeaders.add("Access-Control-Allow-Origin", "*")
            exchange.responseHeaders.add("Access-Control-Allow-Headers", "*")
            exchange.responseHeaders.add("Access-Control-Allow-Methods", "POST, OPTIONS")

            if (exchange.requestMethod.equals("OPTIONS", ignoreCase = true)) {
                exchange.sendResponseHeaders(204, -1)
                return
            }

            if (!exchange.requestMethod.equals("POST", ignoreCase = true)) {
                sendError(exchange, 405, "Método não permitido")
                return
            }

            try {
                val reader = InputStreamReader(exchange.requestBody, Charsets.UTF_8)
                val type = object : TypeToken<SyncPayload>() {}.type
                val payload: SyncPayload = VitalPetJson.gson.fromJson(reader, type)

                if (payload.authToken.isNotBlank() && payload.authToken != expectedToken) {
                    sendError(exchange, 401, "Token de autorização inválido")
                    return
                }

                val localPets = DesktopPetRepository.pets.value
                val mergeResult = PetSyncEngine.merge(localPets, payload.pets)

                // Atualizar o repositório desktop
                DesktopPetRepository.replaceAllPets(mergeResult.mergedPets)

                val response = SyncResponse(
                    success = true,
                    message = "Sincronização bem sucedida. +${mergeResult.addedCount} novos, ${mergeResult.updatedCount} atualizados.",
                    addedPets = mergeResult.addedCount,
                    updatedPets = mergeResult.updatedCount,
                    totalPets = mergeResult.mergedPets.size,
                    pets = mergeResult.mergedPets
                )

                _state.value = _state.value.copy(
                    lastSyncMessage = "Sincronizado com ${payload.deviceName} (${payload.platform.uppercase()}) às ${java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date())}",
                    lastSyncTimestamp = System.currentTimeMillis(),
                    connectedClientsCount = _state.value.connectedClientsCount + 1
                )

                val json = VitalPetJson.gson.toJson(response)
                val bytes = json.toByteArray(Charsets.UTF_8)
                exchange.sendResponseHeaders(200, bytes.size.toLong())
                exchange.responseBody.use { it.write(bytes) }
            } catch (e: Exception) {
                e.printStackTrace()
                sendError(exchange, 500, "Erro interno: ${e.message}")
            }
        }

        private fun sendError(exchange: HttpExchange, code: Int, msg: String) {
            val err = mapOf("success" to false, "message" to msg)
            val bytes = VitalPetJson.gson.toJson(err).toByteArray(Charsets.UTF_8)
            exchange.sendResponseHeaders(code, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
    }

    private fun findLocalLanAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                if (iface.isLoopback || !iface.isUp) continue

                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (addr is Inet4Address && !addr.isLoopbackAddress) {
                        val host = addr.hostAddress
                        if (host.startsWith("192.168.") || host.startsWith("10.") || host.startsWith("172.")) {
                            return host
                        }
                    }
                }
            }
            return InetAddress.getLocalHost().hostAddress
        } catch (e: Exception) {
            return "127.0.0.1"
        }
    }

    fun generateQrCodeBitmap(text: String, size: Int = 300): ImageBitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height

            val pixels = ByteArray(width * height * 4)
            var idx = 0
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val isBlack = bitMatrix.get(x, y)
                    val color: Byte = if (isBlack) 0 else 255.toByte()
                    // BGRA ou RGBA
                    pixels[idx++] = color // R
                    pixels[idx++] = color // G
                    pixels[idx++] = color // B
                    pixels[idx++] = 255.toByte() // A
                }
            }

            val imageInfo = ImageInfo.makeN32Premul(width, height)
            val bitmap = Bitmap()
            bitmap.allocPixels(imageInfo)
            bitmap.installPixels(imageInfo, pixels, width * 4)

            bitmap.asComposeImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
