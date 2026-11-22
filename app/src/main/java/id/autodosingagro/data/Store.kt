package id.autodosingagro.data

import android.content.Context
import id.autodosingagro.R
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

    fun loadKustom(): List<Tanaman> {
        val raw = prefs.getString("tanaman_kustom", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                val faseArr = o.getJSONArray("fase")
                val fase = (0 until faseArr.length()).map { j ->
                    val f = faseArr.getJSONObject(j)
                    Fase(f.getString("nama"), f.getInt("mulai"), f.getInt("selesai"),
                        f.optString("ciri", ""), f.optString("perawatan", ""))
                }
                val jadArr = o.getJSONArray("jadwal")
                val jadwal = (0 until jadArr.length()).map { j ->
                    val a = jadArr.getJSONObject(j)
                    val pArr = a.getJSONArray("pupuk")
                    val pupuk = (0 until pArr.length()).map { k ->
                        val pp = pArr.getJSONObject(k)
                        Pupuk(pp.getString("nama"), pp.getDouble("kg"))
                    }
                    AplikasiPupuk(a.getString("label"), a.getInt("hst"), pupuk, a.optString("catatan", ""))
                }
                Tanaman(
                    id = o.getString("id"), nama = o.getString("nama"),
                    latin = o.optString("latin", ""), kategori = o.optString("kategori", "Tanaman Kustom"),
                    ikon = o.optInt("ikon", R.drawable.ic_kedelai), foto = o.optInt("foto", 0),
                    fotoPath = o.optString("fotoPath", ""),
                    panenMin = o.optInt("panenMin", 60), panenMax = o.optInt("panenMax", 90),
                    jarakTanam = o.optString("jarak", "-"), populasiPerHa = o.optInt("populasi", 20000),
                    deskripsi = o.optString("deskripsi", ""), fase = fase, jadwal = jadwal
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    fun saveKustom(list: List<Tanaman>) {
        val arr = JSONArray()
        list.forEach { t ->
            arr.put(JSONObject().apply {
                put("id", t.id); put("nama", t.nama); put("latin", t.latin); put("kategori", t.kategori)
                put("ikon", t.ikon); put("foto", t.foto); put("fotoPath", t.fotoPath); put("panenMin", t.panenMin); put("panenMax", t.panenMax)
                put("jarak", t.jarakTanam); put("populasi", t.populasiPerHa); put("deskripsi", t.deskripsi)
                val fa = JSONArray(); t.fase.forEach { f -> fa.put(JSONObject().apply {
                    put("nama", f.nama); put("mulai", f.hstMulai); put("selesai", f.hstSelesai)
                    put("ciri", f.ciri); put("perawatan", f.perawatan) }) }
                put("fase", fa)
                val ja = JSONArray(); t.jadwal.forEach { a -> ja.put(JSONObject().apply {
                    put("label", a.label); put("hst", a.hst); put("catatan", a.catatan)
                    val pa = JSONArray(); a.pupuk.forEach { pp -> pa.put(JSONObject().apply {
                        put("nama", pp.nama); put("kg", pp.kgPerHa) }) }
                    put("pupuk", pa) }) }
                put("jadwal", ja)
            })
        }
        prefs.edit().putString("tanaman_kustom", arr.toString()).apply()
    }

    fun pengingatAktif(): Boolean = prefs.getBoolean("pengingat_aktif", true)
    fun pengingatJam(): Int = prefs.getInt("pengingat_jam", 420) // menit sejak 00.00, default 07.00
    fun setPengingatJam(menit: Int) = prefs.edit().putInt("pengingat_jam", menit).apply()
    fun pengingatH1(): Boolean = prefs.getBoolean("pengingat_h1", false)
    fun setPengingatH1(aktif: Boolean) = prefs.edit().putBoolean("pengingat_h1", aktif).apply()
    fun setPengingatAktif(aktif: Boolean) = prefs.edit().putBoolean("pengingat_aktif", aktif).apply()
    fun prefsCodes(): List<Int> =
        prefs.getString("pengingat_codes", "")?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
    fun simpanCodes(codes: List<Int>) =
        prefs.edit().putString("pengingat_codes", codes.joinToString(",")).apply()

    fun namaPengguna(): String = prefs.getString("nama", "") ?: ""
    fun simpanNama(nama: String) { prefs.edit().putString("nama", nama).apply() }
}
