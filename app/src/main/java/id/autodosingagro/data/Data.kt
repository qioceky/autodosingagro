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
    val ikon: Int,
    val foto: Int = 0,
    val fotoPath: String = "",           // emoji sbg ikon visual kartu
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
            ikon = R.drawable.ic_padi, foto = R.drawable.foto_padi, panenMin = 105, panenMax = 120, jarakTanam = "25 × 25 cm",
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
            ikon = R.drawable.ic_jagung, foto = R.drawable.foto_jagung, panenMin = 95, panenMax = 110, jarakTanam = "75 × 20 cm",
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
            ikon = R.drawable.ic_kedelai, foto = R.drawable.foto_kedelai, panenMin = 75, panenMax = 90, jarakTanam = "40 × 15 cm",
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
            ikon = R.drawable.ic_cabai_merah, foto = R.drawable.foto_cabai_merah, panenMin = 75, panenMax = 120, jarakTanam = "60 × 50 cm",
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
            ikon = R.drawable.ic_cabai_rawit, foto = R.drawable.foto_cabai_rawit, panenMin = 70, panenMax = 150, jarakTanam = "60 × 50 cm",
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
            ikon = R.drawable.ic_tomat, foto = R.drawable.foto_tomat, panenMin = 60, panenMax = 100, jarakTanam = "60 × 50 cm",
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
            ikon = R.drawable.ic_bawang_merah, foto = R.drawable.foto_bawang_merah, panenMin = 55, panenMax = 70, jarakTanam = "15 × 15 cm",
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
            ikon = R.drawable.ic_mentimun, foto = R.drawable.foto_mentimun, panenMin = 35, panenMax = 60, jarakTanam = "60 × 40 cm",
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
        ),
        Tanaman(
            id = "paria", nama = "Paria / Pare", latin = "Momordica charantia", kategori = "Hortikultura",
            ikon = R.drawable.ic_paria, foto = R.drawable.foto_paria, panenMin = 55, panenMax = 90, jarakTanam = "100 × 60 cm (lanjaran)",
            populasiPerHa = 16000,
            deskripsi = "Sayuran buah merambat, panen bertahap mulai ±55 HST. Dosis acuan: kandang 10 ton/ha + NPK bertahap, KCl saat berbuah.",
            fase = listOf(
                Fase("Perkecambahan", 0, 7, "Benih berkecambah, daun lembaga membuka.", "Tanah lembap terus, jangan tergenang."),
                Fase("Vegetatif / Merambat", 8, 25, "Sulur memanjang, daun melebar.", "Pasang lanjaran/paranet, arahkan sulur, pangkas tunas air berlebih."),
                Fase("Berbunga", 26, 40, "Bunga jantan & betina muncul bergantian.", "Air cukup, waspada lalat buah mulai fase ini."),
                Fase("Berbuah", 41, 54, "Buah muda membesar & bergerigi khas pare.", "Bungkus buah muda untuk cegah lalat buah."),
                Fase("Panen Bertahap", 55, 90, "Buah dipetik saat gerigi masih rapat, belum menguning.", "Petik tiap 2–3 hari agar tanaman terus berbuah.")
            ),
            jadwal = listOf(
                AplikasiPupuk("Dasar", -3, listOf(Pupuk("Pupuk Kandang", 10000.0), Pupuk("SP-36", 150.0)), "Campur ke lubang tanam sebelum tanam."),
                AplikasiPupuk("Susulan 1", 14, listOf(Pupuk("NPK 16-16-16", 100.0)), "Kocor / tugal saat sulur aktif."),
                AplikasiPupuk("Susulan 2", 28, listOf(Pupuk("NPK 16-16-16", 100.0), Pupuk("KCl", 50.0)), "Awal berbunga."),
                AplikasiPupuk("Susulan 3", 42, listOf(Pupuk("NPK 16-16-16", 75.0), Pupuk("KCl", 50.0)), "Pengisian buah pertama.")
            )
        ),
        Tanaman(
            id = "labu-madu", nama = "Labu Madu", latin = "Cucurbita moschata", kategori = "Hortikultura",
            ikon = R.drawable.ic_labu_madu, foto = R.drawable.foto_labu_madu, panenMin = 85, panenMax = 110, jarakTanam = "200 × 100 cm",
            populasiPerHa = 5000,
            deskripsi = "Labu manis berbentuk botol, dipanen tua umur ±85–110 HST. Dosis acuan: kandang 10 ton/ha + NPK & KCl bertahap.",
            fase = listOf(
                Fase("Perkecambahan", 0, 7, "Benih berkecambah, daun lembaga besar membuka.", "Tanah lembap, jauhkan dari genangan."),
                Fase("Vegetatif", 8, 30, "Sulur menjalar cepat, daun lebar.", "Atur arah sulur, beri alas jerami agar buah tidak kontak tanah basah."),
                Fase("Berbunga", 31, 50, "Bunga besar kuning, bunga betina punya bakal buah di pangkalnya.", "Bantu penyerbukan pagi hari bila lebah sedikit."),
                Fase("Pembesaran Buah", 51, 84, "Buah membesar, kulit mengeras & berubah krem.", "Batasi 2–3 buah per tanaman agar besar maksimal."),
                Fase("Pematangan & Panen", 85, 110, "Tangkai mengering, kulit keras & warna krem merata.", "Panen dengan tangkai, simpan di tempat kering — makin lama disimpan makin manis.")
            ),
            jadwal = listOf(
                AplikasiPupuk("Dasar", -3, listOf(Pupuk("Pupuk Kandang", 10000.0), Pupuk("SP-36", 150.0)), "Campur ke lubang tanam sebelum tanam."),
                AplikasiPupuk("Susulan 1", 21, listOf(Pupuk("NPK 16-16-16", 100.0)), "Saat sulur aktif menjalar."),
                AplikasiPupuk("Susulan 2", 40, listOf(Pupuk("NPK 16-16-16", 100.0), Pupuk("KCl", 75.0)), "Awal berbuah."),
                AplikasiPupuk("Susulan 3", 60, listOf(Pupuk("KCl", 50.0)), "Pembesaran buah — K untuk kemanisan.")
            )
        ),
        Tanaman(
            id = "semangka", nama = "Semangka", latin = "Citrullus lanatus", kategori = "Hortikultura",
            ikon = R.drawable.ic_semangka, foto = R.drawable.foto_semangka, panenMin = 70, panenMax = 90, jarakTanam = "200 × 60 cm",
            populasiPerHa = 8000,
            deskripsi = "Buah besar berair, panen umur ±70–90 HST. Dosis acuan: kandang 15 ton/ha + NPK & KCl bertahap, K tinggi saat pembesaran buah.",
            fase = listOf(
                Fase("Perkecambahan", 0, 7, "Benih berkecambah, daun lembaga membuka.", "Media lembap, semai di polibag lebih aman."),
                Fase("Vegetatif", 8, 25, "Sulur menjalar, daun menjari.", "Atur sulur searah bedengan, mulsa membantu menekan gulma."),
                Fase("Berbunga", 26, 40, "Bunga betina muncul di ketiak daun.", "Penyerbukan pagi hari; batasi buah 2–3 per tanaman."),
                Fase("Pembesaran Buah", 41, 69, "Buah membesar cepat, butuh air & K banyak.", "Air jangan putus; beri alas agar buah tidak busuk."),
                Fase("Pematangan & Panen", 70, 90, "Sulur di pangkal buah mengering, bunyi buah nyaring saat diketuk.", "Kurangi air 5–7 hari sebelum panen agar manis.")
            ),
            jadwal = listOf(
                AplikasiPupuk("Dasar", -3, listOf(Pupuk("Pupuk Kandang", 15000.0), Pupuk("SP-36", 200.0)), "Campur ke lubang/bedengan sebelum tanam."),
                AplikasiPupuk("Susulan 1", 15, listOf(Pupuk("NPK 16-16-16", 100.0)), "Kocor saat sulur aktif."),
                AplikasiPupuk("Susulan 2", 30, listOf(Pupuk("NPK 16-16-16", 100.0), Pupuk("KCl", 50.0)), "Awal berbuah."),
                AplikasiPupuk("Susulan 3", 45, listOf(Pupuk("KCl", 75.0), Pupuk("NPK 16-16-16", 50.0)), "Pembesaran buah — K untuk ukuran & kemanisan.")
            )
        ),
        Tanaman(
            id = "melon", nama = "Melon", latin = "Cucumis melo", kategori = "Hortikultura",
            ikon = R.drawable.ic_melon, foto = R.drawable.foto_melon, panenMin = 60, panenMax = 80, jarakTanam = "120 × 60 cm (lanjaran)",
            populasiPerHa = 13000,
            deskripsi = "Buah manis berjaring (net), panen umur ±60–80 HST. Dosis acuan: kandang 10 ton/ha + NPK & KCl bertahap.",
            fase = listOf(
                Fase("Perkecambahan", 0, 7, "Benih berkecambah cepat, daun lembaga membuka.", "Semai di polibag, pindah tanam umur 10–14 hari."),
                Fase("Vegetatif", 8, 22, "Batang memanjang, sulur keluar.", "Ikat ke lanjaran, pangkas cabang bawah."),
                Fase("Berbunga", 23, 35, "Bunga betina di cabang lateral.", "Bantu penyerbukan; pilih 1–2 buah terbaik per tanaman."),
                Fase("Pembesaran Buah", 36, 59, "Buah membesar, jaring (net) mulai terbentuk.", "Gantung buah dengan jaring/tali; air stabil agar tidak pecah."),
                Fase("Pematangan & Panen", 60, 80, "Aroma harum, tangkai mulai retak (slip).", "Kurangi air menjelang panen agar manis maksimal.")
            ),
            jadwal = listOf(
                AplikasiPupuk("Dasar", -3, listOf(Pupuk("Pupuk Kandang", 10000.0), Pupuk("SP-36", 200.0)), "Campur ke bedengan sebelum tanam."),
                AplikasiPupuk("Susulan 1", 14, listOf(Pupuk("NPK 16-16-16", 100.0)), "Kocor saat vegetatif aktif."),
                AplikasiPupuk("Susulan 2", 28, listOf(Pupuk("NPK 16-16-16", 100.0), Pupuk("KCl", 50.0)), "Awal berbuah."),
                AplikasiPupuk("Susulan 3", 42, listOf(Pupuk("KCl", 75.0)), "Pembesaran buah — K untuk kemanisan & net.")
            )
        ),
        Tanaman(
            id = "sawi", nama = "Sawi Hijau", latin = "Brassica juncea", kategori = "Hortikultura",
            ikon = R.drawable.ic_sawi, foto = R.drawable.foto_sawi, panenMin = 30, panenMax = 45, jarakTanam = "25 × 25 cm",
            populasiPerHa = 160000,
            deskripsi = "Sayuran daun umur sangat pendek, panen ±30–45 HST. Dosis acuan: kandang 10 ton/ha + NPK dua kali susulan.",
            fase = listOf(
                Fase("Perkecambahan", 0, 5, "Benih kecil berkecambah serempak.", "Bedengan lembap, tutup tipis dengan jerami/karung basah."),
                Fase("Vegetatif Awal", 6, 15, "Daun sejati 3–5 helai, tanaman mulai rimbun.", "Penjarangan bila terlalu rapat; siram pagi & sore."),
                Fase("Pembentukan Daun", 16, 29, "Daun melebar & menebal, warna hijau segar.", "Jaga kelembapan tanah; waspada ulat & kutu daun."),
                Fase("Panen", 30, 45, "Daun besar & segar, sebelum berbunga.", "Panen cabut atau potong pangkal; jangan tunggu berbunga (daun pahit & keras).")
            ),
            jadwal = listOf(
                AplikasiPupuk("Dasar", -3, listOf(Pupuk("Pupuk Kandang", 10000.0), Pupuk("SP-36", 100.0)), "Campur merata ke bedengan sebelum tanam."),
                AplikasiPupuk("Susulan 1", 10, listOf(Pupuk("NPK 16-16-16", 75.0)), "Kocor / tabur antar baris."),
                AplikasiPupuk("Susulan 2", 20, listOf(Pupuk("NPK 16-16-16", 75.0)), "Pembentukan daun — berhenti pupuk ±7 hari sebelum panen.")
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
    // Jenis pupuk di Kalkulator Dosis — 6 jenis sesuai dokumen sistem (keputusan user 2026-11-26):
    // NPK, KCl, UREA, SP36, KNO3, MKP.
    val semua: List<PupukPilihan> = listOf(
        PupukPilihan("NPK", 150.0, true, "Pupuk majemuk N-P-K seimbang, praktis untuk susulan — di jadwal tertulis sbg NPK 16-16-16.", faktorKocor = 0.67),
        PupukPilihan("KCl", 50.0, true, "Sumber K — untuk bunga, buah, umbi & ketahanan tanaman.", faktorKocor = 0.7),
        PupukPilihan("Urea", 100.0, true, "Sumber N utama untuk daun & pertumbuhan vegetatif. Kocor jangan sampai kena daun — bisa gosong.", faktorKocor = 0.8),
        PupukPilihan("SP-36", 100.0, false, "Sumber P untuk akar & awal pertumbuhan — sukar larut, berikan TABUR sebagai pupuk dasar, bukan kocor.", faktorKocor = 1.0),
        PupukPilihan("KNO3", 50.0, true, "Sumber K + N nitrat, cocok fase generatif (bunga & buah) & dikocor.", faktorKocor = 0.8),
        PupukPilihan("MKP", 25.0, true, "Sumber P + K, merangsang bunga — dosis kecil, biasanya dikocor.", faktorKocor = 1.0)
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
        "mentimun" to ("Sepanjang tahun, terbaik awal hujan & awal kemarau" to listOf(2, 3, 4, 5, 8, 9, 10, 11)),
        "paria" to ("Awal kemarau & awal hujan — butuh sinar penuh & lanjaran" to listOf(3, 4, 5, 9, 10, 11)),
        "labu-madu" to ("Musim kemarau — buah tidak mudah busuk" to listOf(4, 5, 6, 7, 8)),
        "semangka" to ("Musim kemarau — manis maksimal, penyakit lebih ringan" to listOf(4, 5, 6, 7, 8)),
        "melon" to ("Musim kemarau — hindari hujan saat pembesaran buah" to listOf(4, 5, 6, 7, 8)),
        "sawi" to ("Sepanjang tahun, terbaik musim kemarau dengan penyiraman cukup" to listOf(3, 4, 5, 6, 7, 8, 9))
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

/** Organisme Pengganggu Tanaman (OPT): hama & penyakit utama + pencegahan/pengendalian. */
data class Opt(
    val nama: String,
    val jenis: String,          // "Hama" / "Penyakit"
    val foto: Int = 0,
    val sasaran: String,
    val gejala: String,
    val pencegahan: String,
    val pengendalian: String,
    val ilmiah: String = "",    // nama ilmiah (Latin)
    val catatan: String = ""    // catatan lapangan
)

object OptDB {
    val semua: List<Opt> = listOf(
        Opt("Wereng Batang Cokelat", "Hama", R.drawable.opt_wereng, "Padi",
            "Rumpun menguning lalu mengering seperti terbakar (hopperburn), sering mulai dari petak atau bagian rumpun tertentu; embun madu dapat memicu jelaga. Nimfa & dewasa mengisap cairan di pangkal rumpun — dewasa berwarna cokelat (ada bentuk bersayap panjang & pendek), nimfa lebih kecil belum bersayap sempurna. Juga dapat menularkan virus kerdil hampa/kerdil rumput.",
            "Varietas yang sesuai anjuran wilayah setempat, tanam serempak, hindari pemupukan nitrogen berlebihan.",
            "Pantau pangkal rumpun berkala (ketuk rumpun di atas air/alas untuk melihat nimfa & dewasa). Jaga musuh alami (laba-laba, kumbang & kepik predator), hindari penyemprotan rutin. Bila populasi melewati ambang kendali lokal, gunakan insektisida terdaftar yang selektif sesuai rekomendasi POPT & label; rotasikan kelompok cara kerja.",
            ilmiah = "Nilaparvata lugens",
            catatan = "Jangan menyemprot hanya karena melihat satu-dua individu. Pertimbangkan kepadatan, umur tanaman, varietas, musuh alami & ambang kendali lokal."),
        Opt("Penggerek Batang Padi", "Hama", R.drawable.opt_penggerek, "Padi",
            "Fase vegetatif muncul sundep: daun tengah/pucuk mengering & mudah dicabut. Fase generatif muncul beluk: malai putih/hampa, gabah tidak terisi; batang berlubang & ada kotoran gerekan. Ngengat dewasa kuning pucat; larva krem-kekuningan hidup di dalam batang; telur berkelompok pada daun ditutupi sisik/bulu.",
            "Tanam serempak, sanitasi sisa tanaman & ratun sesuai rekomendasi setempat, amati kelompok telur sejak awal.",
            "Kumpulkan & musnahkan kelompok telur/bagian terserang bila masih memungkinkan. Pelihara parasitoid telur & musuh alami; hindari insektisida spektrum luas tanpa kebutuhan. Bila perlu, tindakan terdaftar & tepat waktu berdasarkan stadia hama serta rekomendasi POPT.",
            ilmiah = "Scirpophaga incertulas (penggerek batang kuning)",
            catatan = "Larva di dalam batang — penyemprotan setelah larva masuk sering kurang efektif. Pencegahan & deteksi dini sangat penting."),
        Opt("Walang Sangit", "Hama", R.drawable.opt_walang_sangit, "Padi",
            "Mengisap cairan bulir terutama fase berbunga hingga pengisian gabah: gabah hampa/keriput, mutu beras turun, titik tusukan bercak kehitaman. Dewasa bertubuh ramping hijau kecokelatan, kaki & antena panjang; nimfa mirip dewasa tapi lebih kecil & belum bersayap sempurna. Mengeluarkan bau khas saat terganggu.",
            "Tanam serempak & panen tepat waktu; kendalikan gulma rumput di pematang/sekitar lahan (tempat singgah) tanpa merusak habitat musuh alami.",
            "Pantau sejak malai keluar sampai pengisian gabah. Tangkap mekanis pagi/sore saat kurang aktif (jaring membantu di petak kecil). Pestisida hanya bila populasi/kerusakan mencapai ambang kendali setempat & sesuai label.",
            ilmiah = "Leptocorisa oratorius",
            catatan = "Kerusakan terpenting saat bulir terbentuk & terisi — pengamatan diarahkan ke malai, bukan hanya daun."),
        Opt("Ulat Grayak Jagung (Fall Armyworm)", "Hama", R.drawable.opt_ulat_grayak, "Jagung",
            "Daun muda berlubang tidak beraturan, tepi terkoyak, ada kotoran larva seperti serbuk kasar di pucuk/whorl; serangan berat merusak titik tumbuh & tongkol. Larva hijau-cokelat gelap bergaris memanjang; kepala berpola huruf Y terbalik & ruas abdomen ke-8 punya 4 titik gelap tersusun persegi (perlu kaca pembesar). Ngengat aktif malam hari.",
            "Rotasi tanaman, sanitasi sisa tanaman, pengelolaan gulma inang sesuai rekomendasi lokal.",
            "Periksa pucuk/whorl rutin sejak muda; ambil & musnahkan kelompok telur & larva pada serangan awal skala kecil. Manfaatkan musuh alami & agens hayati terdaftar (parasitoid, Bacillus thuringiensis). Bila ambang tercapai: insektisida terdaftar efektif pada larva muda, arahkan ke pucuk, patuhi label & rotasi cara kerja.",
            ilmiah = "Spodoptera frugiperda",
            catatan = "Larva instar awal jauh lebih mudah dikendalikan daripada larva besar yang bersembunyi di pucuk. Jangan mengandalkan kalender penyemprotan."),
        Opt("Thrips pada Cabai", "Hama", R.drawable.opt_thrips, "Cabai & sayuran lain",
            "Daun muda mengeriting, menebal, berubah bentuk atau berwarna perunggu/perak; pucuk & bunga rusak, buah muda berjaringan parut/bercak (dapat mirip tungau/gangguan fisiologis). Serangga sangat kecil & ramping (±1 mm), betina cokelat tua-kehitaman bersayap sempit berumbai; nimfa pucat kekuningan tak bersayap; bersembunyi di bunga, pucuk & bawah daun.",
            "Bibit sehat, sanitasi gulma inang, singkirkan bagian terserang berat; perangkap perekat membantu pemantauan.",
            "Periksa pucuk, bunga & bawah daun — ketuk di atas kertas putih untuk melihat yang jatuh. Konservasi predator & agens hayati legal. Bila perlu: insektisida terdaftar sesuai label, rotasikan cara kerja, arahkan ke tempat hama bersembunyi.",
            ilmiah = "Thrips parvispinus (salah satu thrips penting pada cabai)",
            catatan = "Thrips sangat kecil — gejala saja tidak cukup memastikan spesies. Periksa dengan lup & minta konfirmasi petugas bila ragu."),
        Opt("Kutu Daun", "Hama", R.drawable.opt_kutu_daun, "Cabai, mentimun & sayuran",
            "Daun muda menggulung & keriting, tanaman kerdil, pucuk melemah; embun madu membuat daun lengket & ditumbuhi cendawan jelaga; beberapa spesies menularkan virus. Serangga kecil lunak berbentuk buah pir (hijau, kuning, atau kehitaman), berkoloni di pucuk & bawah daun, punya sepasang sifunkuli (tabung kecil) di belakang abdomen.",
            "Kendalikan gulma inang, hindari nitrogen berlebihan (pucuk terlalu lunak), lestarikan kumbang koksi, larva lalat bunga, lacewing & parasitoid.",
            "Pantau pucuk & bawah daun (catat koloni, semut & musuh alami). Serangan ringan: pangkas bagian padat koloni atau semprot air terarah. Serangan berat: produk terdaftar sesuai komoditas & target, ikuti label serta masa pra-panen.",
            ilmiah = "Aphis gossypii (kutu daun kapas/melon)",
            catatan = "Semut sering melindungi kutu daun karena embun madunya — keberadaan semut petunjuk untuk memeriksa koloni."),
        Opt("Kutu Kebul", "Hama", R.drawable.opt_kutu_kebul, "Cabai, tomat, kedelai & hortikultura",
            "Daun menguning, tanaman melemah, embun madu menimbulkan jelaga; populasi tinggi menurunkan vigor. Vektor penting berbagai virus tanaman (termasuk kelompok virus keriting kuning). Dewasa sangat kecil putih seperti ngengat mini, sayap berserbuk lilin; nimfa pipih oval menempel di bawah daun seperti sisik kecil.",
            "Bibit sehat, cabut tanaman sumber virus sesuai arahan setempat, kendalikan gulma inang; mulsa reflektif/penghalang fisik bila sesuai.",
            "Periksa bawah daun (daun muda-tengah); perangkap kuning untuk pemantauan, bukan satu-satunya dasar keputusan. Konservasi parasitoid nimfa (Encarsia/Eretmocerus) & predator. Bila perlu: insektisida terdaftar efektif terhadap stadia sasaran, rotasikan cara kerja, patuhi label.",
            ilmiah = "Bemisia tabaci",
            catatan = "Penularan virus dapat terjadi sebelum gejala terlihat — pencegahan masuknya vektor & bibit sehat sangat penting."),
        Opt("Lalat Buah", "Hama", R.drawable.opt_lalat_buah, "Jambu, mangga, cabai & buah-buahan",
            "Titik tusukan/bercak pada kulit buah, jaringan sekitar tusukan melunak, buah membusuk & gugur; dibelah: daging buah berisi larva & lorong makan. Dewasa kekuningan-cokelat berpola gelap pada toraks & abdomen, sayap bening berpola; larva belatung putih-krem tanpa kaki di dalam buah.",
            "Bungkus buah pada fase tepat, panen tepat waktu, pangkas tajuk agar sirkulasi baik, koordinasikan pengendalian satu hamparan.",
            "Kumpulkan buah terserang & gugur setiap hari, musnahkan sesuai anjuran petugas (penguburan cukup dalam/pemusnahan tertutup) — jangan dibiarkan di kebun. Perangkap atraktan sesuai spesies (mis. metil eugenol untuk beberapa Bactrocera jantan) untuk pemantauan/penekanan populasi — bukan pengganti sanitasi. Umpan beracun/insektisida hanya bila terdaftar & sesuai petunjuk teknis.",
            ilmiah = "Bactrocera dorsalis (kompleks lalat buah)",
            catatan = "Atraktan bersifat khusus spesies & jenis kelamin — satu jenis perangkap belum tentu efektif untuk semua lalat buah."),
        Opt("Blast Padi", "Penyakit", R.drawable.opt_blast, "Padi",
            "Bercak belah ketupat abu-abu di daun; blast leher: pangkal malai busuk hitam sehingga malai patah & gabah hampa.",
            "Varietas tahan, hindari urea berlebih, jarak tanam agak lebar, benih sehat & perlakuan benih.",
            "Fungisida trisiklazol saat gejala awal muncul, terutama menjelang berbunga di daerah endemik."),
        Opt("Hawar Daun Bakteri (Kresek)", "Penyakit", R.drawable.opt_hawar, "Padi",
            "Tepi daun mengering bergelombang kuning keabu-abuan dari ujung, daun seperti terbakar; tanaman muda bisa mati (kresek).",
            "Benih sehat, sanitasi jerami sakit, hindari luka saat pindah tanam, varietas tahan.",
            "Kurangi genangan & pupuk N, semprot bakterisida berbahan tembaga; tanaman parah dicabut."),
        Opt("Layu Fusarium", "Penyakit", R.drawable.opt_layu_fusarium, "Tomat, cabai, melon, semangka",
            "Layu sepihak mulai daun bawah, menguning, pangkal batang coklat bila dibelah; tanaman mati perlahan meski tanah basah.",
            "Rotasi tanaman, drainase baik, bibit sehat, jangan pindah tanah dari lahan sakit.",
            "Cabut & bakar tanaman sakit beserta tanah di sekitarnya; aplikasikan Trichoderma harzianum di lubang tanam."),
        Opt("Antraknosa (Patek)", "Penyakit", R.drawable.opt_antraknosa, "Cabai, tomat, melon",
            "Bercak hitam cekung melingkar di buah, buah membusuk & mengering seperti mumi; menyerang saat buah mulai masak.",
            "Benih sehat, jarak tanam cukup, mulsa, hindari penyiraman dari atas saat berbuah.",
            "Petik & musnahkan buah sakit, fungisida mankozeb/propineb bergilir sesuai label."),
        Opt("Virus Kuning (Gemini)", "Penyakit", R.drawable.opt_virus_kuning, "Cabai, tomat",
            "Daun menguning terang, keriting & mengecil, tanaman kerdil; ditularkan kutu kebul (whitefly).",
            "Bibit sehat & bebas virus, perangkap kuning lengket, tanaman penghalang (jagung) di tepi lahan, mulsa perak.",
            "Cabut & bakar tanaman bergejala sejak dini; kendalikan kutu kebul dengan insektisida kontak/nimba.")
    )
}

/** Alur budidaya: tahapan umum dari benih sampai pascapanen. */
data class TahapBudidaya(
    val nama: String,
    val foto: Int = 0,
    val ringkasan: String,
    val langkah: List<String>
)

object AlurDB {
    val semua: List<TahapBudidaya> = listOf(
        TahapBudidaya("1. Persiapan Benih & Persemaian", R.drawable.alur_persemaian,
            "Benih bermutu adalah separuh keberhasilan. Untuk padi & sayuran buah, benih disemai dulu; sawi & semangka bisa tanam langsung/semaian singkat.",
            listOf(
                "Pilih benih berlabel/bersertifikat, daya tumbuh >85%; rendam & tiriskan untuk benih besar (paria, labu, semangka).",
                "Semai di bedengan/polybag media tanah + kompos (1:1), jaga lembap, beri naungan 50%.",
                "Pindah tanam saat bibit berdaun sejati 3–5 helai (padi 15–21 hari, sayuran buah 10–14 hari)."
            )),
        TahapBudidaya("2. Pengolahan Lahan & Bedengan", R.drawable.alur_olah_lahan,
            "Lahan diolah agar gembur, drainase baik, dan gulma terkendali sebelum tanam.",
            listOf(
                "Bajak/cangkul sedalam 20–30 cm, buat bedengan lebar 80–100 cm untuk sayuran, parit drainase antar bedengan.",
                "Campurkan pupuk kandang matang 10–20 ton/ha + kapur dolomit bila tanah masam (pH <5,5), diamkan 5–7 hari.",
                "Pasang mulsa plastik hitam-perak untuk cabai, tomat, melon & semangka — menekan gulma & mengurangi percikan tanah pembawa penyakit."
            )),
        TahapBudidaya("3. Penanaman", R.drawable.alur_tanam,
            "Tanam pada sore hari agar bibit tidak stres, sesuai jarak tanam tiap komoditas.",
            listOf(
                "Buat lubang tanam, beri pupuk dasar (SP-36/NPK) tercampur tanah — jangan kontak langsung dengan akar.",
                "Tanam bibit tegak, padatkan tanah di pangkal, siram secukupnya (kocor awal).",
                "Sulam (ganti) bibit mati dalam 3–5 hari pertama agar populasi tetap penuh."
            )),
        TahapBudidaya("4. Pemeliharaan: Air & Penyiangan", 0,
            "Air yang stabil dan lahan bebas gulma membuat tanaman tumbuh seragam.",
            listOf(
                "Siram pagi/sore; sayuran buah butuh air stabil — kekeringan saat berbunga membuat bunga rontok.",
                "Siangi gulma 2–3 kali (umur 15, 30, 45 HST) sebelum gulma berbunga.",
                "Pasang ajir/lanjaran untuk tomat, paria, melon & mentimun sejak umur ±15 HST."
            )),
        TahapBudidaya("5. Pemupukan", 0,
            "Pupuk diberikan bertahap mengikuti fase: dasar sebelum tanam, susulan saat vegetatif & generatif. Hitung dosisnya di menu Kalkulator Dosis.",
            listOf(
                "Pupuk dasar: kandang + SP-36 sebelum/saat tanam untuk akar & awal tumbuh.",
                "Susulan vegetatif: NPK/Urea untuk daun & batang; susulan generatif: tambah KCl/KNO3 untuk bunga, buah & kemanisan.",
                "Cara tabur ditugal lalu ditutup tanah; cara kocor dilarutkan & disiram ke pangkal — dosis kocor lebih hemat (lihat faktor di kalkulator)."
            )),
        TahapBudidaya("6. Pengendalian OPT", 0,
            "Amati tanaman minimal seminggu sekali; kendalikan hama & penyakit sejak gejala awal (lihat menu Organisme Perusak Tanaman).",
            listOf(
                "Pantau pucuk, bawah daun & buah — catat gejala di menu Diagnosis Tanaman.",
                "Utamakan cara mekanis & hayati: pungut ulat, perangkap, bungkus buah, musuh alami.",
                "Pestisida kimia pilihan terakhir: tepat sasaran, tepat dosis, perhatikan waktu panen (masa tunggu)."
            )),
        TahapBudidaya("7. Panen", R.drawable.alur_panen,
            "Panen pada umur & tingkat kematangan yang tepat menentukan mutu & harga.",
            listOf(
                "Padi: 85–90% gabah menguning. Sayuran buah: petik bertahap tiap 2–3 hari (cabai, paria, mentimun).",
                "Semangka & labu madu: panen tua sekali petik — tandai dengan sulur pangkal buah kering & bunyi nyaring.",
                "Sawi: panen 30–45 HST sebelum berbunga agar daun tidak pahit & keras."
            )),
        TahapBudidaya("8. Pascapanen", 0,
            "Penanganan setelah panen menjaga mutu sampai ke pembeli/konsumen.",
            listOf(
                "Sortasi: pisahkan yang rusak/terinfeksi OPT; bersihkan tanah & kotoran.",
                "Padi dijemur/dikeringkan ke kadar air ±14% sebelum disimpan/digiling.",
                "Sayuran daun & buah segar: simpan di tempat teduh & sejuk, jual secepatnya; labu madu justru makin manis setelah disimpan 1–2 minggu."
            ))
    )
}
