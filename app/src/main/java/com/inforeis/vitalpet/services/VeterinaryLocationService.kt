package com.inforeis.vitalpet.services

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class VeterinaryFacility(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String? = null,
    val is24Hours: Boolean = false,
    val distanceKm: Double? = null
)

private data class NominatimResult(
    @SerializedName("place_id") val placeId: Long,
    val lat: String,
    val lon: String,
    val name: String?,
    @SerializedName("display_name") val displayName: String?,
    val address: Map<String, String>?,
    val extratags: Map<String, String>?
)

object VeterinaryLocationService {
    private val gson = Gson()

    // Default reference 24h emergency hospitals
    private val fallbackHospitals = listOf(
        VeterinaryFacility(
            name = "Hospital Escolar Veterinário (FMV-ULisboa)",
            address = "Avenida da Universidade Técnica, Alto da Ajuda, Lisboa",
            latitude = 38.7135,
            longitude = -9.1963,
            phone = "213 652 893",
            is24Hours = true
        ),
        VeterinaryFacility(
            name = "Hospital Veterinário do Restelo (24h)",
            address = "Rua Gonçalves Zarco 2B, 1400-191 Lisboa",
            latitude = 38.7018,
            longitude = -9.2144,
            phone = "213 031 600",
            is24Hours = true
        ),
        VeterinaryFacility(
            name = "Hospital Veterinário Central de Lisboa (24h)",
            address = "Rua Ilha do Pico 4, 1000-169 Lisboa",
            latitude = 38.7369,
            longitude = -9.1412,
            phone = "217 995 240",
            is24Hours = true
        ),
        VeterinaryFacility(
            name = "Hospital Veterinário do Porto (HVP 24h)",
            address = "Travessa Silva Porto 174, 4250-475 Porto",
            latitude = 41.1712,
            longitude = -8.6189,
            phone = "228 348 170",
            is24Hours = true
        ),
        VeterinaryFacility(
            name = "Hospital Veterinário de Santa Marinha (V.N. Gaia 24h)",
            address = "Rua Cândido dos Reis 576, 4400-058 Vila Nova de Gaia",
            latitude = 41.1309,
            longitude = -8.6251,
            phone = "223 700 800",
            is24Hours = true
        ),
        VeterinaryFacility(
            name = "Hospital Veterinário de Coimbra (24h)",
            address = "Quinta da Maia, Lote 1, 3030-043 Coimbra",
            latitude = 40.2056,
            longitude = -8.4115,
            phone = "239 701 400",
            is24Hours = true
        ),
        VeterinaryFacility(
            name = "Hospital Veterinário do Algarve (Faro 24h)",
            address = "Estrada da Penha, 8005-139 Faro",
            latitude = 37.0264,
            longitude = -7.9254,
            phone = "289 824 040",
            is24Hours = true
        )
    )

    suspend fun searchVeterinaries(
        userLat: Double?,
        userLon: Double?,
        cityQuery: String? = null
    ): List<VeterinaryFacility> = withContext(Dispatchers.IO) {
        try {
            val queryText = if (!cityQuery.isNullOrBlank()) {
                "veterinario $cityQuery"
            } else if (userLat != null && userLon != null) {
                "hospital veterinario"
            } else {
                "hospital veterinario 24h portugal"
            }

            val encodedQuery = URLEncoder.encode(queryText, "UTF-8")
            val urlString = StringBuilder("https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&addressdetails=1&extratags=1&limit=25")

            if (userLat != null && userLon != null && cityQuery.isNullOrBlank()) {
                // Bias search near user location (delta ~0.4 deg ~ 45km)
                val delta = 0.45
                val minLon = userLon - delta
                val maxLon = userLon + delta
                val minLat = userLat - delta
                val maxLat = userLat + delta
                urlString.append("&viewbox=$minLon,$maxLat,$maxLon,$minLat&bounded=0")
            }

            val url = URL(urlString.toString())
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "VitalPetAndroid/1.0 (contact@vitalpet.app)")
                connectTimeout = 8000
                readTimeout = 8000
            }

            if (conn.responseCode == 200) {
                val reader = InputStreamReader(conn.inputStream, "UTF-8")
                val results: Array<NominatimResult> = gson.fromJson(reader, Array<NominatimResult>::class.java)
                reader.close()
                conn.disconnect()

                if (results.isNotEmpty()) {
                    val facilities = results.mapNotNull { item ->
                        val lat = item.lat.toDoubleOrNull() ?: return@mapNotNull null
                        val lon = item.lon.toDoubleOrNull() ?: return@mapNotNull null

                        val facilityName = item.name?.takeIf { it.isNotBlank() }
                            ?: item.address?.get("amenity")
                            ?: item.address?.get("building")
                            ?: item.displayName?.split(",")?.firstOrNull()
                            ?: "Clínica Veterinária"

                        val road = item.address?.get("road") ?: ""
                        val city = item.address?.get("city") ?: item.address?.get("town") ?: item.address?.get("village") ?: ""
                        val fullAddress = listOf(road, city).filter { it.isNotBlank() }.joinToString(", ").ifBlank {
                            item.displayName ?: "Localização indicada no mapa"
                        }

                        val phone = item.extratags?.get("phone")
                            ?: item.extratags?.get("contact:phone")

                        val openingHours = item.extratags?.get("opening_hours") ?: ""
                        val is24h = openingHours.contains("24/7") ||
                                openingHours.contains("00:00-24:00") ||
                                facilityName.contains("24h", ignoreCase = true) ||
                                facilityName.contains("24 horas", ignoreCase = true) ||
                                facilityName.contains("urgência", ignoreCase = true) ||
                                facilityName.contains("urgencia", ignoreCase = true) ||
                                facilityName.contains("emergency", ignoreCase = true)

                        val dist = if (userLat != null && userLon != null) {
                            calculateDistanceKm(userLat, userLon, lat, lon)
                        } else null

                        VeterinaryFacility(
                            id = item.placeId.toString(),
                            name = facilityName,
                            address = fullAddress,
                            latitude = lat,
                            longitude = lon,
                            phone = phone,
                            is24Hours = is24h,
                            distanceKm = dist
                        )
                    }

                    return@withContext if (userLat != null && userLon != null) {
                        facilities.sortedBy { it.distanceKm ?: Double.MAX_VALUE }
                    } else {
                        facilities
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Return fallback list with calculated distances if user location is available
        fallbackHospitals.map { h ->
            val dist = if (userLat != null && userLon != null) {
                calculateDistanceKm(userLat, userLon, h.latitude, h.longitude)
            } else null
            h.copy(distanceKm = dist)
        }.sortedBy { it.distanceKm ?: Double.MAX_VALUE }
    }

    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}
