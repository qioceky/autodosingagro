package id.autodosingagro.data

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class CuacaInfo(
    val suhu: Double,
    val kode: Int,
    val kelembapan: Int,
    val angin: Double,
    val peluangHujan: Int,
    val suhuMax: Double,
    val suhuMin: Double
)

data class KotaCuaca(val nama: String, val lat: Double, val lon: Double)

/** Cuaca dari Open-Meteo (gratis, tanpa API key). Callback selalu di main thread. */
object CuacaApi {
    private fun getJson(url: String): JSONObject? {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        conn.setRequestProperty("User-Agent", "AutoDosingAgro")
        return try {
            if (conn.responseCode !in 200..299) null
            else JSONObject(conn.inputStream.bufferedReader().readText())
        } catch (e: Exception) { null } finally { conn.disconnect() }
    }

    fun ambil(lat: Double, lon: Double, cb: (CuacaInfo?) -> Unit) {
        val main = Handler(Looper.getMainLooper())
        Thread {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon" +
                "&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m" +
                "&daily=precipitation_probability_max,temperature_2m_max,temperature_2m_min" +
                "&forecast_days=1&timezone=auto"
            val hasil = try {
                val j = getJson(url)
                if (j == null) null else {
                    val c = j.getJSONObject("current")
                    val d = j.getJSONObject("daily")
                    CuacaInfo(
                        suhu = c.getDouble("temperature_2m"),
                        kode = c.getInt("weather_code"),
                        kelembapan = c.getInt("relative_humidity_2m"),
                        angin = c.getDouble("wind_speed_10m"),
                        peluangHujan = d.getJSONArray("precipitation_probability_max").optInt(0, 0),
                        suhuMax = d.getJSONArray("temperature_2m_max").getDouble(0),
                        suhuMin = d.getJSONArray("temperature_2m_min").getDouble(0)
                    )
                }
            } catch (e: Exception) { null }
            main.post { cb(hasil) }
        }.start()
    }

    fun cariKota(nama: String, cb: (List<KotaCuaca>) -> Unit) {
        val main = Handler(Looper.getMainLooper())
        Thread {
            val q = URLEncoder.encode(nama, "UTF-8")
            val url = "https://geocoding-api.open-meteo.com/v1/search?name=$q&count=5&language=id&format=json"
            val hasil = try {
                val j = getJson(url)
                val arr = j?.optJSONArray("results")
                if (arr == null) emptyList() else (0 until arr.length()).map { i ->
                    val o = arr.getJSONObject(i)
                    val label = o.getString("name") +
                        (o.optString("admin1").takeIf { it.isNotBlank() }?.let { ", $it" } ?: "") +
                        (o.optString("country").takeIf { it.isNotBlank() }?.let { " — $it" } ?: "")
                    KotaCuaca(label, o.getDouble("latitude"), o.getDouble("longitude"))
                }
            } catch (e: Exception) { emptyList() }
            main.post { cb(hasil) }
        }.start()
    }
}

/** Kode cuaca WMO -> (label Indonesia, kategori ikon). */
fun labelCuaca(kode: Int): Pair<String, String> = when (kode) {
    0 -> "Cerah" to "cerah"
    1 -> "Cerah Berawan" to "cerahberawan"
    2 -> "Berawan Sebagian" to "cerahberawan"
    3 -> "Mendung" to "mendung"
    45, 48 -> "Berkabut" to "kabut"
    51, 53, 55 -> "Gerimis" to "hujan"
    56, 57, 66, 67 -> "Hujan Beku" to "hujan"
    61 -> "Hujan Ringan" to "hujan"
    63 -> "Hujan Sedang" to "hujan"
    65 -> "Hujan Lebat" to "hujan"
    71, 73, 75, 77 -> "Salju" to "salju"
    80 -> "Hujan Lokal Ringan" to "hujan"
    81 -> "Hujan Lokal" to "hujan"
    82 -> "Hujan Lokal Lebat" to "hujan"
    85, 86 -> "Salju" to "salju"
    95 -> "Badai Petir" to "petir"
    96, 99 -> "Badai Petir & Hujan Es" to "petir"
    else -> "Berawan" to "mendung"
}

/** Saran kegiatan tani berdasarkan cuaca hari ini. */
fun saranCuaca(info: CuacaInfo): String = when {
    info.peluangHujan >= 70 ->
        "Peluang hujan besar hari ini — tunda pemupukan dulu, pupuk gampang tercuci hujan. Sekalian cek drainase petakmu."
    info.kode in listOf(61, 63, 65, 80, 81, 82, 95, 96, 99) ->
        "Hari ini hujan — hindari pemupukan kocor & penyemprotan, tunggu cuaca reda supaya tidak terbuang."
    info.suhu >= 34.0 ->
        "Panas terik hari ini — siram tanaman pagi atau sore hari, dan jangan memupuk di siang bolong."
    info.kode == 0 && info.peluangHujan < 30 ->
        "Cuaca cerah — waktu yang baik untuk pemupukan, penyemprotan, dan kegiatan kebun lainnya."
    else -> "Cuaca relatif aman — kegiatan kebun bisa berjalan seperti biasa."
}
