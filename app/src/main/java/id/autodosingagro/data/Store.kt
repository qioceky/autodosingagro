package id.autodosingagro.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

data class TanamanSaya(
    val id: Long,
    val label: String,          // nama petak, mis. "Cabai Petak 1"
    val tanamanId: String,
    val tanggalTanam: LocalDate,
    val luasM2: Double,
    val jumlahTanaman: Int
) {
    fun hst(pada: LocalDate = LocalDate.now()): Int =
        (pada.toEpochDay() - tanggalTanam.toEpochDay()).toInt()
}

data class LogTumbuh(
    val id: Long,
    val tanamanSayaId: Long,
    val tanggal: LocalDate,
    val tinggiCm: Double,
    val jumlahDaun: Int,
    val kondisi: String,        // Sehat / Daun Menguning / Layu / Terserang Hama / Lainnya
    val catatan: String
)

data class Transaksi(
    val id: Long,
    val tanamanSayaId: Long,
    val tanggal: LocalDate,
    val jenis: String,          // "Biaya" / "Panen"
    val keterangan: String,
    val nominal: Long,          // Rupiah
    val hasilKg: Double         // khusus Panen, 0 bila Biaya
)

class Store(context: Context) {
    private val prefs = context.getSharedPreferences("autodosing", Context.MODE_PRIVATE)

    fun loadTanamanSaya(): List<TanamanSaya> {
        val raw = prefs.getString("tanaman_saya", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                TanamanSaya(
                    id = o.getLong("id"),
                    label = o.getString("label"),
                    tanamanId = o.getString("tanamanId"),
                    tanggalTanam = LocalDate.parse(o.getString("tanggalTanam")),
                    luasM2 = o.optDouble("luasM2", 0.0),
                    jumlahTanaman = o.optInt("jumlahTanaman", 0)
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    fun saveTanamanSaya(list: List<TanamanSaya>) {
        val arr = JSONArray()
        list.forEach { t ->
            arr.put(JSONObject().apply {
                put("id", t.id); put("label", t.label); put("tanamanId", t.tanamanId)
                put("tanggalTanam", t.tanggalTanam.toString())
                put("luasM2", t.luasM2); put("jumlahTanaman", t.jumlahTanaman)
            })
        }
        prefs.edit().putString("tanaman_saya", arr.toString()).apply()
    }

    fun loadLog(): List<LogTumbuh> {
        val raw = prefs.getString("log_tumbuh", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                LogTumbuh(
                    id = o.getLong("id"),
                    tanamanSayaId = o.getLong("tanamanSayaId"),
                    tanggal = LocalDate.parse(o.getString("tanggal")),
                    tinggiCm = o.optDouble("tinggiCm", 0.0),
                    jumlahDaun = o.optInt("jumlahDaun", 0),
                    kondisi = o.optString("kondisi", "Sehat"),
                    catatan = o.optString("catatan", "")
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    fun saveLog(list: List<LogTumbuh>) {
        val arr = JSONArray()
        list.forEach { l ->
            arr.put(JSONObject().apply {
                put("id", l.id); put("tanamanSayaId", l.tanamanSayaId)
                put("tanggal", l.tanggal.toString()); put("tinggiCm", l.tinggiCm)
                put("jumlahDaun", l.jumlahDaun); put("kondisi", l.kondisi); put("catatan", l.catatan)
            })
        }
        prefs.edit().putString("log_tumbuh", arr.toString()).apply()
    }

    fun loadTransaksi(): List<Transaksi> {
        val raw = prefs.getString("transaksi", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Transaksi(
                    id = o.getLong("id"),
                    tanamanSayaId = o.getLong("tanamanSayaId"),
                    tanggal = LocalDate.parse(o.getString("tanggal")),
                    jenis = o.optString("jenis", "Biaya"),
                    keterangan = o.optString("keterangan", ""),
                    nominal = o.optLong("nominal", 0),
                    hasilKg = o.optDouble("hasilKg", 0.0)
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    fun saveTransaksi(list: List<Transaksi>) {
        val arr = JSONArray()
        list.forEach { t ->
            arr.put(JSONObject().apply {
                put("id", t.id); put("tanamanSayaId", t.tanamanSayaId)
                put("tanggal", t.tanggal.toString()); put("jenis", t.jenis)
                put("keterangan", t.keterangan); put("nominal", t.nominal); put("hasilKg", t.hasilKg)
            })
        }
        prefs.edit().putString("transaksi", arr.toString()).apply()
    }

    fun namaPengguna(): String = prefs.getString("nama", "") ?: ""
    fun simpanNama(nama: String) { prefs.edit().putString("nama", nama).apply() }
}
