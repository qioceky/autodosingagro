package id.autodosingagro.data

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class InfoUpdate(
    val versiBaru: String,
    val urlRilis: String,
    val urlApk: String?,
    val catatan: String
)

/** Bandingkan versi "1.2.0" vs "1.10.0" secara numerik per segmen. */
fun versiLebihBaru(baru: String, sekarang: String): Boolean {
    val a = baru.trim().removePrefix("v").split(".").map { it.toIntOrNull() ?: 0 }
    val b = sekarang.trim().removePrefix("v").split(".").map { it.toIntOrNull() ?: 0 }
    for (i in 0 until maxOf(a.size, b.size)) {
        val x = a.getOrElse(i) { 0 }; val y = b.getOrElse(i) { 0 }
        if (x != y) return x > y
    }
    return false
}

/**
 * Periksa pembaruan dari GitHub Releases repo aplikasi ini.
 * Callback selalu dipanggil di main thread: (update tersedia?, pesan error?)
 * - update != null  -> ada versi lebih baru
 * - update == null && error == null -> sudah versi terbaru
 * - error != null -> pemeriksaan gagal (mis. tidak ada internet)
 */
object UpdateChecker {
    private const val API =
        "https://api.github.com/repos/qioceky/autodosingagro/releases/latest"

    fun cek(versiSekarang: String, cb: (InfoUpdate?, String?) -> Unit) {
        val main = Handler(Looper.getMainLooper())
        Thread {
            try {
                val conn = URL(API).openConnection() as HttpURLConnection
                conn.connectTimeout = 12000
                conn.readTimeout = 12000
                conn.setRequestProperty("User-Agent", "AutoDosingAgro-Android")
                conn.setRequestProperty("Accept", "application/vnd.github+json")
                val code = conn.responseCode
                if (code != 200) throw Exception("HTTP $code")
                val body = conn.inputStream.bufferedReader().readText()
                val o = JSONObject(body)
                val tag = o.optString("tag_name", "")
                val versi = tag.removePrefix("v")
                var urlApk: String? = null
                val assets = o.optJSONArray("assets")
                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val a = assets.getJSONObject(i)
                        if (a.optString("name").endsWith(".apk")) {
                            urlApk = a.optString("browser_download_url"); break
                        }
                    }
                }
                val hasil = if (versiLebihBaru(versi, versiSekarang)) {
                    InfoUpdate(
                        versiBaru = versi,
                        urlRilis = o.optString("html_url", "https://github.com/qioceky/autodosingagro/releases"),
                        urlApk = urlApk,
                        catatan = o.optString("body", "").take(600)
                    )
                } else null
                main.post { cb(hasil, null) }
            } catch (e: Exception) {
                main.post { cb(null, "Gagal memeriksa pembaruan — cek koneksi internet lalu coba lagi. (${e.message ?: "jaringan"})") }
            }
        }.start()
    }
}
