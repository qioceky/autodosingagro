package id.autodosingagro.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import id.autodosingagro.R

/** Satu jenis pupuk dalam satu kali aplikasi. Dosis dalam kg per hektar. */
data class Pupuk(val nama: String, val kgPerHa: Double)

/** Satu jadwal aplikasi pupuk pada umur tertentu (HST = Hari Setelah Tanam). */
data class AplikasiPupuk(
    val label: String,          // "Dasar", "Susulan 1", dst
    val hst: Int,               // umur tanaman saat aplikasi (hari)
    val pupuk: List<Pupuk>,
    val catatan: String = ""
)

/** Fase pertumbuhan tanaman. */
data class Fase(
    val nama: String,
    val hstMulai: Int,
    val hstSelesai: Int,
    val ciri: String,
    val perawatan: String
)

data class Tanaman(
    val id: String,
    val nama: String,
    val latin: String,
    val kategori: String,       // "Tanaman Pangan" / "Hortikultura"
    val ikon: Int,           // emoji sbg ikon visual kartu
    val panenMin: Int,          // HST awal panen
    val panenMax: Int,          // HST akhir masa tanam
    val jarakTanam: String,
    val populasiPerHa: Int,     // perkiraan jumlah tanaman per hektar
    val deskripsi: String,
    val fase: List<Fase>,
    val jadwal: List<AplikasiPupuk>
) {
    fun fasePada(hst: Int): Fase? = fase.firstOrNull { hst in it.hstMulai..it.hstSelesai }
        ?: if (hst > panenMax) null else fase.lastOrNull()

    fun aplikasiBerikutnya(hst: Int): AplikasiPupuk? = jadwal.firstOrNull { it.hst >= hst }

    fun aplikasiSaatIni(hst: Int): AplikasiPupuk? =
        jadwal.filter { it.hst <= hst }.maxByOrNull { it.hst } ?: jadwal.firstOrNull()

    fun totalPupuk(): List<Pupuk> {
        val map = linkedMapOf<String, Double>()
        jadwal.forEach { app -> app.pupuk.forEach { p -> map[p.nama] = (map[p.nama] ?: 0.0) + p.kgPerHa } }
        return map.map { Pupuk(it.key, it.value) }
    }

    fun progres(hst: Int): Float {
        if (panenMax <= 0) return 0f
        return (hst.toFloat() / panenMax.toFloat()).coerceIn(0f, 1f)
    }
}

object TanamanDB {
    /** Tanaman kustom buatan pengguna (diisi dari penyimpanan lokal saat aplikasi mulai). */
    var kustom: List<Tanaman> by mutableStateOf(emptyList())

    /** Semua tanaman: bawaan + kustom. */
    val semua: List<Tanaman> get() = bawaan + kustom

    private val bawaan: List<Tanaman> = listOf(
        Tanaman(
            id = "padi", nama = "Padi", latin = "Oryza sativa", kategori = "Tanaman Pangan",
            ikon = R.drawable.ic_padi, panenMin = 105, panenMax = 120, jarakTanam = "25 × 25 cm",
            populasiPerHa = 160000,
            deskripsi = "Tanaman pangan utama. Dosis acuan: Urea 225 kg/ha, SP-36 100 kg/ha, KCl 100 kg/ha, dibagi 3 kali aplikasi.",
            fase = listOf(
                Fase("Persemaian & Tanam", 0, 7, "Bibit pindah tanam, akar mulai pulih.", "Jaga air macak-macak, sulam bibit yang mati."),
                Fase("Vegetatif / Anakan", 8, 45, "Jumlah anakan bertambah cepat, daun hijau memanjang.", "Penyiangan 1–2 kali, jaga air 2–5 cm, waspada keong & wereng."),
                Fase("Generatif / Bunting", 46, 65, "Malai mulai terbentuk di dalam batang.", "Jangan sampai kekeringan, waspada penggerek batang."),
                Fase("Berbunga & Pengisian Gabah", 66, 95, "Malai keluar, gabah terisi susu sampai menguning.", "Air tetap cukup, waspada walang sangit & burung."),
                Fase("Pematangan & Panen", 96, 120, "Gabah menguning 85–90%.", "Keringkan lahan 7–10 hari sebelum panen.")
            ),
            jadwal = listOf(
                AplikasiPupuk("Dasar", 7, listOf(Pupuk("Urea", 75.0), Pupuk("SP-36", 100.0), Pupuk("KCl", 50.0)), "Berikan 7–10 HST, tabur merata saat air macak-macak."),
                AplikasiPupuk("Susulan 1", 25, listOf(Pupuk("Urea", 75.0), Pupuk("KCl", 50.0)), "Saat anakan aktif (21–25 HST)."),
                AplikasiPupuk("Susulan 2", 40, listOf(Pupuk("Urea", 75.0)), "35–42 HST, sebelum fase bunting. Daun kuning = dosis bisa ditambah 25 kg/ha.")
            )
        ),
        Tanaman(
            id = "jagung", nama = "Jagung", latin = "Zea mays", kategori = "Tanaman Pangan",
            ikon = R.drawable.ic_jagung, panenMin = 95, panenMax = 110, jarakTanam = "75 × 20 cm",
            populasiPerHa = 66000,
            deskripsi = "Dosis acuan: Urea 350 kg/ha, SP-36 150 kg/ha, KCl 100 kg/ha + pupuk kandang 2.000 kg/ha sebagai dasar.",
            fase = listOf(
                Fase("Perkecambahan", 0, 10, "Kecambah muncul, daun pertama membuka.", "Jaga kelembapan, sulam tanaman yang tidak tumbuh."),
                Fase("Vegetatif", 11, 45, "Batang & daun tumbuh cepat, kebutuhan N tinggi.", "Penyiangan + pembumbunan, waspada ulat grayak."),
                Fase("Berbunga", 46, 60, "Bunga jantan (tassel) dan tongkol muncul.", "Air jangan kurang, kekurangan air saat berbunga menurunkan hasil."),
                Fase("Pengisian Biji", 61, 85, "Biji terisi dari susu menjadi keras.", "Jaga daun tetap hijau, waspada tikus."),
                Fase("Panen", 86, 110, "Kelobot kering, biji keras & mengkilap.", "Panen saat kadar air biji ±30%, lalu jemur.")
            ),
            jadwal = listOf(
                AplikasiPupuk("Dasar", 0, listOf(Pupuk("Pupuk Kandang", 2000.0), Pupuk("Urea", 100.0), Pupuk("SP-36", 150.0), Pupuk("KCl", 100.0)), "Kandang saat olah tanah; Urea/SP-36/KCl ditugal di samping lubang tanam."),
                AplikasiPupuk("Susulan 1", 25, listOf(Pupuk("Urea", 125.0)), "21–28 HST, tugal 5 cm dari batang lalu tutup tanah."),
                AplikasiPupuk("Susulan 2", 40, listOf(Pupuk("Urea", 125.0)), "35–45 HST, sebelum berbunga, sekalian pembumbunan.")
            )
        ),
        Tanaman(
            id = "kedelai", nama = "Kedelai", latin = "Glycine max", kategori = "Tanaman Pangan",
            ikon = R.drawable.ic_kedelai, panenMin = 75, panenMax = 90, jarakTanam = "40 × 15 cm",
            populasiPerHa = 166000,
            deskripsi = "Kedelai mengikat N dari udara (bintil akar), jadi Urea rendah. Dosis acuan: Urea 50, SP-36 100, KCl 100 kg/ha, semua sebagai pupuk dasar.",
            fase = listOf(
                Fase("Perkecambahan", 0, 7, "Kotiledon muncul ke permukaan.", "Tanah lembap, jangan tergenang."),
                Fase("Vegetatif", 8, 35, "Daun trifoliat bertambah, bintil akar aktif.", "Penyiangan hati-hati, drainase lancar."),
                Fase("Berbunga & Polong", 36, 60, "Bunga ungu/putih, polong mulai terisi.", "Air cukup saat pengisian polong."),
                Fase("Panen", 61, 90, "Daun menguning & rontok, polong cokelat.", "Panen pagi hari agar polong tidak pecah.")
            ),
            jadwal = listOf(
                AplikasiPupuk("Dasar", 0, listOf(Pupuk("Pupuk Kandang", 1000.0), Pupuk("Urea", 50.0), Pupuk("SP-36", 100.0), Pupuk("KCl", 100.0)), "Semua diberikan saat tanam, larik di samping barisan benih.")
            )
        ),
        Tanaman(
            id = "cabai-merah", nama = "Cabai Merah", latin = "Capsicum annuum", kategori = "Hortikultura",
            ikon = R.drawable.ic_cabai_merah, panenMin = 75, panenMax = 120, jarakTanam = "60 × 50 cm",
            populasiPerHa = 33000,
            deskripsi = "Dosis acuan: pupuk kandang 10 ton/ha sebagai dasar, lalu NPK susulan bertahap. Panen bertahap berkali-kali mulai ±75 HST.",
            fase = listOf(
                Fase("Vegetatif Awal", 0, 20, "Tanaman beradaptasi, tunas & daun baru tumbuh.", "Siram teratur, pasang ajir sejak dini."),
                Fase("Vegetatif / Cabang", 21, 40, "Percabangan Y terbentuk, kuncup bunga muncul.", "Buang tunas air di bawah cabang Y (perempelan)."),
                Fase("Berbunga & Berbuah", 41, 70, "Bunga mekar, buah hijau membesar.", "Kebutuhan K naik untuk kualitas buah, waspada trips & antraknosa."),
                Fase("Panen Bertahap", 71, 120, "Buah memerah, dipetik berkali-kali.", "Petik buah merah tiap 3–5 hari, lanjutkan NPK ringan tiap 2 minggu.")
            ),
            jadwal = listOf(
                AplikasiPupuk("Dasar", -7, listOf(Pupuk("Pupuk Kandang", 10000.0), Pupuk("SP-36", 300.0), Pupuk("ZA", 100.0)), "7 hari sebelum tanam, campur ke bedengan lalu tutup mulsa."),
                AplikasiPupuk("Susulan 1", 15, listOf(Pupuk("NPK 16-16-16", 150.0)), "Kocor atau tugal di sekeliling tanaman."),
                AplikasiPupuk("Susulan 2", 30, listOf(Pupuk("NPK 16-16-16", 150.0), Pupuk("KCl", 100.0)), "Mendukung bunga & buah pertama."),
                AplikasiPupuk("Susulan 3", 45, listOf(Pupuk("NPK 16-16-16", 150.0)), "Saat buah mulai membesar."),
                AplikasiPupuk("Susulan 4", 60, listOf(Pupuk("NPK 16-16-16", 100.0), Pupuk("KCl", 100.0)), "Menjelang panen pertama, K untuk warna & ketahanan buah.")
            )
        ),
        Tanaman(
            id = "cabai-rawit", nama = "Cabai Rawit", latin = "Capsicum frutescens", kategori = "Hortikultura",
            ikon = R.drawable.ic_cabai_rawit, panenMin = 70, panenMax = 150, jarakTanam = "60 × 50 cm",
            populasiPerHa = 33000,
            deskripsi = "Lebih bandel & berumur panjang dari cabai merah. Dosis mirip, sedikit lebih ringan di awal.",
            fase = listOf(
                Fase("Vegetatif Awal", 0, 20, "Pertumbuhan daun & akar.", "Siram teratur, ajir sejak dini."),
                Fase("Percabangan", 21, 45, "Cabang banyak, bunga mulai muncul.", "Perempelan tunas air seperlunya."),
                Fase("Berbuah", 46, 69, "Buah hijau bertambah banyak.", "K cukup, waspada lalat buah."),
                Fase("Panen Bertahap", 70, 150, "Panen berkali-kali selama tanaman sehat.", "NPK ringan tiap 2–3 minggu selama masa panen.")
            ),
            jadwal = listOf(
                AplikasiPupuk("Dasar", -7, listOf(Pupuk("Pupuk Kandang", 8000.0), Pupuk("SP-36", 250.0)), "Campur ke bedengan 7 hari sebelum tanam."),
                AplikasiPupuk("Susulan 1", 15, listOf(Pupuk("NPK 16-16-16", 125.0)), "Kocor / tugal."),
                AplikasiPupuk("Susulan 2", 30, listOf(Pupuk("NPK 16-16-16", 125.0), Pupuk("KCl", 75.0)), ""),
                AplikasiPupuk("Susulan 3", 50, listOf(Pupuk("NPK 16-16-16", 125.0)), "Saat buah lebat."),
                AplikasiPupuk("Susulan 4", 70, listOf(Pupuk("NPK 16-16-16", 100.0), Pupuk("KCl", 75.0)), "Awal masa panen.")
            )
        ),
        Tanaman(
            id = "tomat", nama = "Tomat", latin = "Solanum lycopersicum", kategori = "Hortikultura",
            ikon = R.drawable.ic_tomat, panenMin = 60, panenMax = 100, jarakTanam = "60 × 50 cm",
            populasiPerHa = 33000,
            deskripsi = "Dosis acuan: kandang 10 ton/ha + SP-36 dasar, NPK susulan bertahap. Tomat peka genangan & kekurangan kalsium (busuk ujung buah).",
            fase = listOf(
                Fase("Vegetatif Awal", 0, 15, "Tanaman tegak, daun bertambah.", "Pasang ajir & ikat batang secara berkala."),
                Fase("Berbunga", 16, 35, "Tandan bunga pertama mekar.", "Buang tunas air, jaga air stabil agar bunga tidak rontok."),
                Fase("Berbuah", 36, 59, "Buah hijau membesar per tandan.", "K & Ca penting, hindari siram membasahi daun."),
                Fase("Panen Bertahap", 60, 100, "Buah semburat merah dipetik bertahap.", "Petik saat breaker (semburat) untuk jual jauh, merah penuh untuk dekat.")
            ),
            jadwal = listOf(
                AplikasiPupuk("Dasar", -7, listOf(Pupuk("Pupuk Kandang", 10000.0), Pupuk("SP-36", 250.0), Pupuk("Kapur Dolomit", 500.0)), "Dolomit bila tanah masam (pH < 6), campur ke bedengan."),
                AplikasiPupuk("Susulan 1", 15, listOf(Pupuk("NPK 16-16-16", 150.0)), "Kocor 200 ml/tanaman atau tugal."),
                AplikasiPupuk("Susulan 2", 30, listOf(Pupuk("NPK 16-16-16", 150.0), Pupuk("KCl", 75.0)), "Saat bunga & buah pertama."),
                AplikasiPupuk("Susulan 3", 45, listOf(Pupuk("NPK 16-16-16", 100.0), Pupuk("KCl", 75.0)), "Pengisian buah.")
            )
        ),
        Tanaman(
            id = "bawang-merah", nama = "Bawang Merah", latin = "Allium ascalonicum", kategori = "Hortikultura",
            ikon = R.drawable.ic_bawang_merah, panenMin = 55, panenMax = 70, jarakTanam = "15 × 15 cm",
            populasiPerHa = 400000,
            deskripsi = "Dosis acuan per ha: kandang 5 ton, SP-36 200, KCl 200 (dibagi 2), ZA/Urea bertahap. Umbi peka genangan.",
            fase = listOf(
                Fase("Tumbuh Awal", 0, 14, "Tunas daun keluar dari umbi bibit.", "Siram pagi-sore, tanah selalu lembap tidak becek."),
                Fase("Vegetatif / Anakan", 15, 35, "Jumlah daun & anakan umbi bertambah.", "Penyiangan hati-hati, waspada ulat & bercak ungu."),
                Fase("Pembentukan Umbi", 36, 54, "Umbi mulai membesar, daun mulai rebah sebagian.", "Kurangi air bertahap, K untuk pembesaran umbi."),
                Fase("Panen", 55, 70, "60–70% daun rebah & menguning.", "Cabut, jemur umbi + daunnya 5–7 hari.")
            ),
            jadwal = listOf(
                AplikasiPupuk("Dasar", -3, listOf(Pupuk("Pupuk Kandang", 5000.0), Pupuk("SP-36", 200.0), Pupuk("KCl", 100.0)), "3 hari sebelum tanam, tebar & campur ke bedengan."),
                AplikasiPupuk("Susulan 1", 15, listOf(Pupuk("ZA", 150.0), Pupuk("Urea", 50.0)), "Tabur di larikan antar baris, lalu siram."),
                AplikasiPupuk("Susulan 2", 30, listOf(Pupuk("ZA", 150.0), Pupuk("KCl", 100.0)), "Mendukung pembentukan umbi.")
            )
        ),
        Tanaman(
            id = "mentimun", nama = "Mentimun", latin = "Cucumis sativus", kategori = "Hortikultura",
            ikon = R.drawable.ic_mentimun, panenMin = 35, panenMax = 60, jarakTanam = "60 × 40 cm",
            populasiPerHa = 41000,
            deskripsi = "Umur pendek, panen cepat mulai ±35 HST. Dosis acuan: kandang 5 ton/ha + NPK bertahap tiap 10 hari.",
            fase = listOf(
                Fase("Perkecambahan", 0, 7, "Benih berkecambah, daun lembaga membuka.", "Tanah lembap terus."),
                Fase("Vegetatif / Merambat", 8, 20, "Sulur mulai merambat ke ajir/lanjaran.", "Arahkan sulur ke lanjaran, siram cukup."),
                Fase("Berbunga & Berbuah", 21, 34, "Bunga betina jadi buah muda.", "Air jangan kurang, kekurangan air = buah pahit & bengkok."),
                Fase("Panen Bertahap", 35, 60, "Buah dipetik muda tiap 2 hari.", "Sering petik merangsang buah baru.")
            ),
            jadwal = listOf(
                AplikasiPupuk("Dasar", -3, listOf(Pupuk("Pupuk Kandang", 5000.0), Pupuk("SP-36", 150.0)), "Campur ke lubang/bedengan sebelum tanam."),
                AplikasiPupuk("Susulan 1", 10, listOf(Pupuk("NPK 16-16-16", 100.0)), "Kocor / tugal."),
                AplikasiPupuk("Susulan 2", 20, listOf(Pupuk("NPK 16-16-16", 100.0)), "Saat mulai berbunga."),
                AplikasiPupuk("Susulan 3", 30, listOf(Pupuk("NPK 16-16-16", 75.0), Pupuk("KCl", 50.0)), "Pengisian buah pertama.")
            )
        )
    )

    fun byId(id: String): Tanaman? = semua.firstOrNull { it.id == id }
}

/** Pilihan jenis pupuk untuk Kalkulator Dosis. dosisUmum = acuan umum per aplikasi (kg/ha)
 *  bila pupuk tidak ada di jadwal tanaman yang dipilih. */
data class PupukPilihan(
    val nama: String, val dosisUmumKgPerHa: Double, val bisaKocor: Boolean, val catatan: String = "",
    /** Faktor dosis bila diberikan dgn cara KOCOR dibanding TABUR (tiap pupuk beda, sesuai kolomnya). */
    val faktorKocor: Double = 1.0
)

object PupukDB {
    // Jenis pupuk di Kalkulator Dosis — sesuai ketetapan user (2026-11-19): hanya 4 ini.
    val semua: List<PupukPilihan> = listOf(
        PupukPilihan("NPK", 150.0, true, "Pupuk majemuk N-P-K seimbang, praktis untuk susulan — di jadwal tertulis sbg NPK 16-16-16.", faktorKocor = 0.67),
        PupukPilihan("KNO3", 50.0, true, "Sumber K + N nitrat, cocok fase generatif (bunga & buah) & dikocor.", faktorKocor = 0.8),
        PupukPilihan("MKP", 25.0, true, "Sumber P + K, merangsang bunga — dosis kecil, biasanya dikocor.", faktorKocor = 1.0),
        PupukPilihan("KCl", 50.0, true, "Sumber K — untuk bunga, buah, umbi & ketahanan tanaman.", faktorKocor = 0.7)
    )
    fun byNama(nama: String): PupukPilihan? = semua.firstOrNull { it.nama.equals(nama, ignoreCase = true) }
}

/** Format dosis: dari kg/ha menjadi kebutuhan nyata berdasarkan luas atau jumlah tanaman. */
fun formatDosis(kgPerHa: Double, luasM2: Double?, jumlahTanaman: Int?, populasiPerHa: Int): String {
    val hasil = mutableListOf<String>()
    if (luasM2 != null && luasM2 > 0) {
        val kg = kgPerHa * luasM2 / 10000.0
        hasil.add(if (kg >= 1.0) "%.2f kg".format(kg) else "%.0f gram".format(kg * 1000))
    }
    if (jumlahTanaman != null && jumlahTanaman > 0 && populasiPerHa > 0) {
        val gramPerTanaman = kgPerHa * 1000.0 / populasiPerHa
        hasil.add("%.1f gram/tanaman".format(gramPerTanaman))
    }
    if (hasil.isEmpty()) return "%.0f kg/ha".format(kgPerHa)
    return hasil.joinToString("  •  ")
}

/** Kalender tanam: bulan-bulan (1-12) yang umumnya cocok untuk mulai tanam + catatan musim. */
object KalenderDB {
    val data: Map<String, Pair<String, List<Int>>> = mapOf(
        "padi" to ("Musim hujan & awal kemarau — butuh air banyak" to listOf(10, 11, 12, 1, 3, 4, 5)),
        "jagung" to ("Awal musim hujan atau akhir kemarau" to listOf(2, 3, 4, 8, 9, 10)),
        "kedelai" to ("Kemarau awal, tanah lembap tidak tergenang (sering setelah panen padi)" to listOf(3, 4, 5, 6)),
        "cabai-merah" to ("Akhir musim hujan / awal kemarau — hindari puncak hujan (rawan penyakit)" to listOf(3, 4, 5, 8, 9)),
        "cabai-rawit" to ("Akhir musim hujan / awal kemarau, lebih bandel dari cabai merah" to listOf(3, 4, 5, 8, 9, 10)),
        "tomat" to ("Musim kemarau — buah tidak mudah busuk & penyakit daun lebih ringan" to listOf(4, 5, 6, 7, 8)),
        "bawang-merah" to ("Musim kemarau — umbi peka genangan & hujan" to listOf(4, 5, 6, 7, 8, 9)),
        "mentimun" to ("Sepanjang tahun, terbaik awal hujan & awal kemarau" to listOf(2, 3, 4, 5, 8, 9, 10, 11))
    )
    fun musimBulan(bulan: Int): String = if (bulan in listOf(10, 11, 12, 1, 2, 3, 4)) "Musim Hujan" else "Musim Kemarau"
}

/** Diagnosa gejala sederhana berbasis aturan — bahan belajar, bukan pengganti penyuluh. */
data class Gejala(
    val nama: String, val ikon: String,
    val kemungkinan: String, val tindakan: String
)

object GejalaDB {
    val semua: List<Gejala> = listOf(
        Gejala("Daun menguning merata", "\uF06C",
            "Kekurangan nitrogen (N), atau akar tergenang sehingga penyerapan hara terganggu.",
            "Beri pupuk sumber N (sesuai jadwal: NPK/KNO3) sebagai susulan; perbaiki drainase, jangan biarkan lahan becek."),
        Gejala("Daun kuning di antara tulang daun", "\uF06C",
            "Kekurangan magnesium (Mg) atau besi (Fe) — sering di tanah masam / terlalu sering dipupuk K tinggi.",
            "Tabur kapur dolomit bila tanah masam; beri NPK seimbang, hindari KCl berlebihan sementara."),
        Gejala("Bercak cokelat / hitam pada daun", "\uF06E",
            "Penyakit bercak daun / antraknosa (jamur) — menyebar cepat di musim hujan & daun basah lama.",
            "Buang & musnahkan daun/buah sakit (jangan dikompos di lahan), atur jarak & sirkulasi, fungisida sesuai label bila serangan meluas."),
        Gejala("Daun berlubang / bekas gigitan", "\uF188",
            "Hama pengunyah: ulat grayak, ulat buah, atau belalang.",
            "Cek bagian bawah daun pagi/sore, ambil manual bila sedikit; bila ambang terlampaui pakai pestisida selektif sesuai label & bergilir bahan aktif."),
        Gejala("Tanaman layu", "\uF2C9",
            "Bila hanya siang & pulih sore: kekurangan air. Bila layu terus & pangkal batang berlendir/berbau: layu bakteri; bila batang cokelat kering: layu fusarium (jamur tanah).",
            "Siram cukup & cek kelembapan. Tanaman layu bakteri/fusarium: cabut + musnahkan jauh dari lahan, jangan tanam sefamili di petak itu dulu, perbaiki drainase."),
        Gejala("Daun keriting & mengecil", "\uF188",
            "Sering virus yang dibawa kutu daun / thrips / kutu kebul, atau kelebihan herbisida.",
            "Tanaman terinfeksi parah dicabut; kendalikan serangga vektor (perangkap kuning, pestisida selektif), cabut gulma inang di sekitar lahan."),
        Gejala("Bunga banyak rontok", "\uF043",
            "Air tidak stabil (kering lalu basah berlebihan), kekurangan K / boron, atau suhu ekstrem.",
            "Jaga penyiraman teratur, beri KNO3 / MKP lewat kocor sesuai kalkulator, pasang mulsa agar kelembapan stabil."),
        Gejala("Ujung buah busuk (tomat/cabai)", "\uF5D1",
            "Busuk ujung buah (blossom end rot) — kekurangan kalsium di buah, biasanya karena air tidak stabil, bukan selalu karena tanah miskin Ca.",
            "Stabilkan penyiraman, beri dolomit/sumber kalsium saat olah tanah, hindari pupuk N berlebihan yang memacu daun."),
        Gejala("Pertumbuhan kerdil", "\uF4D8",
            "Tanah padat / masam, pupuk dasar kurang, atau akar terganggu (genangan, nematoda).",
            "Gemburkan tanah, cek pH (target 5,5–7), tambah pupuk kandang/kompos matang, lalu susulan NPK sesuai jadwal umur tanaman.")
    )
}
