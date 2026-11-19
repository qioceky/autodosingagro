package id.autodosingagro

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.autodosingagro.data.*
import java.time.LocalDate

@Composable
fun JudulHalaman(judul: String, sub: String) {
    Column(Modifier.fillMaxWidth().padding(18.dp)) {
        Text(judul, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TeksGelap)
        Spacer(Modifier.height(2.dp))
        Text(sub, fontSize = 13.sp, color = TeksAbu)
    }
}

@Composable
fun PilihanTanaman(terpilih: String, onPilih: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(horizontal = 18.dp)) {
        items(TanamanDB.semua) { t ->
            val aktif = t.id == terpilih
            Row(
                Modifier.clip(RoundedCornerShape(50))
                    .background(if (aktif) Biru else Color.White)
                    .border(1.dp, if (aktif) Biru else Garis, RoundedCornerShape(50))
                    .clickable { onPilih(t.id) }
                    .padding(horizontal = 13.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TanamanIcon(t, size = 14.dp, color = if (aktif) Color.White else Biru)
                Spacer(Modifier.width(7.dp))
                Text(t.nama, color = if (aktif) Color.White else TeksGelap,
                    fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun Field(label: String, value: String, onChange: (String) -> Unit, angka: Boolean = false, hint: String = "") {
    OutlinedTextField(
        value = value, onValueChange = onChange, label = { Text(label) },
        placeholder = if (hint.isNotBlank()) ({ Text(hint) }) else null,
        keyboardOptions = KeyboardOptions(keyboardType = if (angka) KeyboardType.Decimal else KeyboardType.Text),
        singleLine = true, modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

/* ---------------- DAFTAR & DETAIL JENIS TANAMAN ---------------- */

@Composable
fun TanamanScreen(onDetail: (String) -> Unit) {
    LazyColumn(Modifier.fillMaxSize()) {
        item { JudulHalaman("Jenis Tanaman", "Tanaman pangan & hortikultura untuk praktik ATPH — ketuk untuk fase & jadwal pupuknya") }
        items(TanamanDB.semua) { t ->
            Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 5.dp)
                .clickable { onDetail(t.id) }) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(BiruSangatMuda),
                        contentAlignment = Alignment.Center) { TanamanIcon(t, size = 22.dp, color = Biru) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(t.nama, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TeksGelap)
                        Text(t.latin, fontSize = 12.sp, fontStyle = FontStyle.Italic, color = TeksAbu)
                        Text("${t.kategori} • Panen ${t.panenMin}–${t.panenMax} HST • ${t.jadwal.size}× pemupukan",
                            fontSize = 12.sp, color = Biru)
                    }
                    FaIcon(Fa.CHEV_R, size = 14.sp, color = TeksAbu)
                }
            }
        }
        item { Spacer(Modifier.height(18.dp)) }
    }
}

@Composable
fun DetailTanamanScreen(tanaman: Tanaman, onBack: () -> Unit, onHitung: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                FaIcon(Fa.CHEV_L, size = 20.sp, color = Biru,
                    modifier = Modifier.clickable { onBack() }.padding(end = 14.dp))
                Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(BiruSangatMuda),
                    contentAlignment = Alignment.Center) { TanamanIcon(tanaman, size = 20.dp, color = Biru) }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(tanaman.nama, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TeksGelap)
                    Text("${tanaman.latin} • ${tanaman.kategori}", fontSize = 12.sp, fontStyle = FontStyle.Italic, color = TeksAbu)
                }
            }
        }
        item {
            Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text(tanaman.deskripsi, color = TeksGelap, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Jarak tanam: ${tanaman.jarakTanam}  •  Populasi ±${"%,d".format(tanaman.populasiPerHa).replace(',', '.')} tanaman/ha",
                        color = TeksAbu, fontSize = 12.sp)
                    Text("Umur panen: ${tanaman.panenMin}–${tanaman.panenMax} HST", color = TeksAbu, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Fase Pertumbuhan (Umur Tanaman)", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                color = TeksGelap, modifier = Modifier.padding(horizontal = 18.dp))
            Spacer(Modifier.height(8.dp))
        }
        items(tanaman.fase) { f ->
            Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp)) {
                Column(Modifier.padding(13.dp)) {
                    Text("${f.nama}  •  ${f.hstMulai}–${f.hstSelesai} HST", fontWeight = FontWeight.Bold, color = BiruKartu, fontSize = 14.sp)
                    Text(f.ciri, fontSize = 13.sp, color = TeksGelap)
                    Text("Perawatan: ${f.perawatan}", fontSize = 13.sp, color = TeksAbu)
                }
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
            Text("Jadwal & Dosis Pemupukan", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                color = TeksGelap, modifier = Modifier.padding(horizontal = 18.dp))
            Spacer(Modifier.height(8.dp))
        }
        items(tanaman.jadwal) { app ->
            Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp)) {
                Column(Modifier.padding(13.dp)) {
                    Text("${app.label} — umur ${app.hst} HST", fontWeight = FontWeight.Bold, color = BiruKartu, fontSize = 14.sp)
                    app.pupuk.forEach { p -> Text("• ${p.nama}: ${"%.0f".format(p.kgPerHa)} kg/ha", fontSize = 13.sp, color = TeksGelap) }
                    if (app.catatan.isNotBlank()) Text(app.catatan, fontSize = 12.sp, color = TeksAbu)
                }
            }
        }
        item {
            Kartu(Modifier.fillMaxWidth().padding(18.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text("Total Satu Musim Tanam", fontWeight = FontWeight.Bold, color = TeksGelap)
                    tanaman.totalPupuk().forEach { p -> Text("• ${p.nama}: ${"%.0f".format(p.kgPerHa)} kg/ha", fontSize = 13.sp) }
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = onHitung, modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Biru),
                        shape = RoundedCornerShape(10.dp)) {
                        FaIcon(Fa.SCALE, size = 15.sp, color = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Hitung Dosis untuk Petak Saya")
                    }
                }
            }
        }
    }
}

/* ---------------- KALKULATOR DOSIS ---------------- */

@Composable
fun LabelInput(teks: String) {
    Text(teks, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BiruKartu,
        modifier = Modifier.padding(top = 6.dp))
}

@Composable
fun ChipPilih(teks: String, aktif: Boolean, ikon: String? = null, onClick: () -> Unit) {
    Row(
        Modifier.clip(RoundedCornerShape(50))
            .background(if (aktif) Biru else Color.White)
            .border(1.dp, if (aktif) Biru else Garis, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (ikon != null) {
            FaIcon(ikon, size = 13.sp, color = if (aktif) Color.White else Biru)
            Spacer(Modifier.width(7.dp))
        }
        Text(teks, color = if (aktif) Color.White else TeksGelap, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
fun DosisScreen(tanamanSaya: List<TanamanSaya>) {
    var tanamanId by remember { mutableStateOf(TanamanDB.semua.first().id) }
    var jumlahText by remember { mutableStateOf("") }
    var hstText by remember { mutableStateOf("") }
    var tanggalText by remember { mutableStateOf(LocalDate.now().minusDays(25).toString()) }
    var luasText by remember { mutableStateOf("100") }
    var cara by remember { mutableStateOf("Tabur") }
    var pupukPilih by remember { mutableStateOf(setOf("NPK", "KCl")) }
    var musim by remember { mutableStateOf("Kemarau") }
    var hasil by remember { mutableStateOf<Pair<Tanaman, Int>?>(null) }
    var pesan by remember { mutableStateOf("") }
    val tanaman = TanamanDB.byId(tanamanId)!!

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            JudulHalaman("Kalkulator Dosis", "Pilih jenis tanaman, lalu isi 5 input di bawah — dosis dihitung sesuai umur, cara pemupukan, jenis pupuk & musim")
            PilihanTanaman(tanamanId) { tanamanId = it; hasil = null }
            Spacer(Modifier.height(10.dp))
            Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                LabelInput("1. Jumlah Tanaman")
                Field("Jumlah tanaman yang dipupuk", jumlahText, { jumlahText = it }, angka = true, hint = "contoh: 320")

                LabelInput("2. Usia Tanaman")
                Field("Umur tanaman (HST) — isi ini ATAU tanggal tanam di bawah", hstText, { hstText = it }, angka = true, hint = "contoh: 25")
                Field("Tanggal tanam (TTTT-BB-HH)", tanggalText, { tanggalText = it }, hint = "2026-11-01")

                LabelInput("3. Cara Pemupukan")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipPilih("a. Tabur", cara == "Tabur", Fa.HAND) { cara = "Tabur" }
                    ChipPilih("b. Kocor", cara == "Kocor", Fa.DROPLET) { cara = "Kocor" }
                }
                Text(
                    if (cara == "Tabur") "Pupuk ditaburkan / ditugal di sekeliling tanaman lalu ditutup tanah tipis."
                    else "Pupuk dilarutkan dalam air lalu disiramkan ke pangkal tanaman.",
                    fontSize = 12.sp, color = TeksAbu
                )

                LabelInput("4. Jenis Pupuk (bisa pilih banyak)")
                PupukDB.semua.chunked(2).forEach { baris ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        baris.forEach { p ->
                            ChipPilih(p.nama, p.nama in pupukPilih, Fa.FLASK) {
                                pupukPilih = if (p.nama in pupukPilih) pupukPilih - p.nama else pupukPilih + p.nama
                            }
                        }
                    }
                }
                Text("Sudah dipilih: ${if (pupukPilih.isEmpty()) "belum ada" else pupukPilih.joinToString(", ")}",
                    fontSize = 12.sp, color = TeksAbu)

                LabelInput("5. Musim")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipPilih("a. Kemarau", musim == "Kemarau", Fa.SUN) { musim = "Kemarau" }
                    ChipPilih("b. Hujan", musim == "Hujan", Fa.RAIN) { musim = "Hujan" }
                }

                LabelInput("Tambahan: Luas Petak")
                Field("Luas petak (m²) — untuk total kebutuhan satu petak", luasText, { luasText = it }, angka = true, hint = "contoh: 100")

                if (pesan.isNotBlank()) Text(pesan, color = Color(0xFFB3261E), fontSize = 13.sp)
                Button(
                    onClick = {
                        val hst = hstText.toIntOrNull() ?: try {
                            (LocalDate.now().toEpochDay() - LocalDate.parse(tanggalText.trim()).toEpochDay()).toInt()
                        } catch (e: Exception) { -999 }
                        when {
                            pupukPilih.isEmpty() -> pesan = "Pilih minimal satu jenis pupuk dulu (nomor 4)."
                            jumlahText.toIntOrNull() == null && luasText.toDoubleOrNull() == null ->
                                pesan = "Isi jumlah tanaman (nomor 1) atau luas petak dulu."
                            hst == -999 -> pesan = "Tanggal tanam tidak valid — pakai format 2026-11-01 atau isi Umur (HST)."
                            hst < -10 -> pesan = "Usia tanaman tidak masuk akal, cek lagi tanggalnya."
                            else -> { pesan = ""; hasil = tanaman to hst }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Biru),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Hitung Dosis Pupuk", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.height(8.dp))
        }
        val h = hasil
        if (h != null) {
            val (t, hst) = h
            val luas = luasText.toDoubleOrNull()
            val jumlah = jumlahText.toIntOrNull()
            val fase = t.fasePada(hst)
            val berikut = t.aplikasiBerikutnya(hst)
            val volumeKocorMl = if (musim == "Kemarau") 250 else 150
            item {
                Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 5.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TanamanIcon(t, size = 18.dp, color = Biru)
                            Spacer(Modifier.width(8.dp))
                            Text("${t.nama} • Usia $hst HST", color = TeksGelap, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Fase: ${fase?.nama ?: if (hst > t.panenMax) "Sudah lewat masa tanam" else "Persiapan / sebelum tanam"}",
                            color = TeksGelap, fontSize = 13.sp)
                        if (fase != null) Text(fase.perawatan, color = TeksAbu, fontSize = 12.sp)
                        Text("Cara: $cara  •  Musim: $musim  •  Progres ke panen ${(t.progres(hst) * 100).toInt()}%",
                            color = TeksAbu, fontSize = 12.sp)
                    }
                }
            }
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9F1)),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 5.dp)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FaIcon(Fa.FLASK, size = 15.sp, color = Color(0xFF14532D))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (berikut != null) "Dosis Aplikasi: ${berikut.label} (usia ${berikut.hst} HST" +
                                    (if (berikut.hst > hst) ", ${berikut.hst - hst} hari lagi" else ", SEKARANG") + ")"
                                else "Dosis Aplikasi (jadwal tanaman ini sudah selesai — pakai dosis umum)",
                                fontWeight = FontWeight.Bold, color = Color(0xFF14532D), fontSize = 15.sp
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        pupukPilih.forEach { namaPupuk ->
                            val dariJadwal = berikut?.pupuk?.firstOrNull { it.nama.equals(namaPupuk, true) || (namaPupuk == "NPK" && it.nama.startsWith("NPK")) }
                            val pilihan = PupukDB.byNama(namaPupuk)
                            val kgHaDasar = dariJadwal?.kgPerHa ?: pilihan?.dosisUmumKgPerHa ?: 0.0
                            val faktor = if (cara == "Kocor") (pilihan?.faktorKocor ?: 1.0) else 1.0
                            val kgHa = kgHaDasar * faktor
                            val sumber = (if (dariJadwal != null) "dari jadwal ${t.nama}" else "dosis umum") +
                                (if (cara == "Kocor" && faktor != 1.0) " • disesuaikan utk kocor (×${faktor} dari dosis tabur, diberikan lebih sering ±tiap 7 hari)" else "")
                            val tidakLarut = cara == "Kocor" && pilihan?.bisaKocor == false
                            Text("• $namaPupuk ($sumber): ${formatDosis(kgHa, luas, jumlah, t.populasiPerHa)}",
                                fontSize = 14.sp, color = TeksGelap, fontWeight = FontWeight.SemiBold)
                            if (cara == "Kocor" && !tidakLarut && t.populasiPerHa > 0) {
                                val gramPerTanaman = kgHa * 1000.0 / t.populasiPerHa
                                val gramPerLiter = gramPerTanaman / (volumeKocorMl / 1000.0)
                                val totalLiter = if (jumlah != null) " • total larutan ±${"%.1f".format(volumeKocorMl * jumlah / 1000.0)} liter untuk $jumlah tanaman" else ""
                                Text("   Larutkan ${"%.1f".format(gramPerTanaman)} gram/tanaman dalam $volumeKocorMl ml air (±${"%.0f".format(gramPerLiter)} gram/liter)$totalLiter",
                                    fontSize = 12.sp, color = TeksGelap)
                            }
                            if (tidakLarut) {
                                Text("   Perhatian: $namaPupuk tidak larut air — jangan dikocor, berikan dengan cara TABUR/tugal.",
                                    fontSize = 12.sp, color = Color(0xFFB3261E))
                            }
                            if (pilihan != null && pilihan.catatan.isNotBlank()) {
                                Text("   ${pilihan.catatan}", fontSize = 12.sp, color = TeksAbu)
                            }
                        }
                    }
                }
            }
            item {
                Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 5.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FaIcon(Fa.INFO, size = 15.sp, color = Biru)
                            Spacer(Modifier.width(8.dp))
                            Text("Cara Aplikasi — $cara, Musim $musim", fontWeight = FontWeight.Bold, color = BiruKartu, fontSize = 14.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        val panduan = when {
                            cara == "Tabur" && musim == "Kemarau" -> listOf(
                                "Taburkan merata / tugal 5–10 cm di samping tanaman, jangan kena batang & daun.",
                                "Tutup pupuk dengan tanah tipis, lalu SIRAM — musim kemarau pupuk mudah menguap (terutama Urea) bila dibiarkan kering.",
                                "Waktu terbaik: pagi sebelum jam 9 atau sore setelah jam 15."
                            )
                            cara == "Tabur" && musim == "Hujan" -> listOf(
                                "JANGAN memupuk saat hujan deras atau lahan tergenang — pupuk mudah tercuci (leaching).",
                                "Taburkan saat tanah lembap tapi tidak becek, tutup tanah tipis agar tidak hanyut.",
                                "Untuk pupuk mudah larut (NPK, KNO3, MKP, KCl): bagi dosis jadi 2 tahap selang ±7 hari agar tidak banyak yang hilang tercuci."
                            )
                            cara == "Kocor" && musim == "Kemarau" -> listOf(
                                "Larutkan dosis per tanaman dalam $volumeKocorMl ml air (volume lebih banyak karena kemarau), aduk sampai larut.",
                                "Siramkan ke pangkal tanaman / daerah akar, jangan ke daun. Lakukan pagi atau sore hari.",
                                "Setelah kocor, tanah dijaga tetap lembap — jangan biarkan kekeringan."
                            )
                            else -> listOf(
                                "Larutkan dosis per tanaman dalam $volumeKocorMl ml air saja (musim hujan — jangan menambah genangan).",
                                "Kocor ke pangkal tanaman saat TIDAK hujan dan tanah tidak tergenang.",
                                "Tunda kocor bila hujan deras turun — larutan akan langsung tercuci."
                            )
                        }
                        panduan.forEach { Text("• $it", fontSize = 12.sp, color = TeksGelap) }
                    }
                }
            }
            item {
                Text("Semua Jadwal ${t.nama} (sudah diskalakan ke petakmu)", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    color = TeksGelap, modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp))
            }
            items(t.jadwal) { app ->
                Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${app.label} — ${app.hst} HST ${if (app.hst <= hst) "(sudah waktunya / lewat)" else ""}",
                            fontWeight = FontWeight.SemiBold, color = BiruKartu, fontSize = 13.sp)
                        app.pupuk.forEach { p ->
                            Text("• ${p.nama}: ${formatDosis(p.kgPerHa, luas, jumlah, t.populasiPerHa)}", fontSize = 12.sp)
                        }
                    }
                }
            }
            item {
                Text("Catatan: dosis jadwal mengikuti rekomendasi tanaman; pupuk di luar jadwal memakai dosis umum per aplikasi. Hasil terbaik tetap menyesuaikan uji tanah, varietas & kondisi lahan setempat.",
                    fontSize = 12.sp, color = TeksAbu, modifier = Modifier.padding(18.dp))
            }
        }
    }
}

/* ---------------- PANTAU PERTUMBUHAN ---------------- */

@Composable
fun PantauScreen(
    tanamanSaya: List<TanamanSaya>, logs: List<LogTumbuh>,
    onTambah: (TanamanSaya) -> Unit, onHapus: (Long) -> Unit,
    onTambahLog: (LogTumbuh) -> Unit, onHapusLog: (Long) -> Unit
) {
    var label by remember { mutableStateOf("") }
    var tanamanId by remember { mutableStateOf(TanamanDB.semua.first().id) }
    var tanggal by remember { mutableStateOf(LocalDate.now().toString()) }
    var luas by remember { mutableStateOf("") }
    var jumlah by remember { mutableStateOf("") }
    var pesan by remember { mutableStateOf("") }
    var logUntuk by remember { mutableStateOf<Long?>(null) }
    var tinggi by remember { mutableStateOf("") }
    var daun by remember { mutableStateOf("") }
    var kondisi by remember { mutableStateOf("Sehat") }
    var catatan by remember { mutableStateOf("") }

    LazyColumn(Modifier.fillMaxSize()) {
        item { JudulHalaman("Pantau Pertumbuhan", "Tanaman yang kamu tanam, umurnya (HST), fasenya & jadwal pupuk berikutnya") }
        if (tanamanSaya.isEmpty()) {
            item { Text("Belum ada tanaman. Tambahkan lewat formulir di bawah.", color = TeksAbu,
                modifier = Modifier.padding(horizontal = 18.dp)) }
        }
        items(tanamanSaya) { ts ->
            val t = TanamanDB.byId(ts.tanamanId) ?: TanamanDB.semua.first()
            val hst = ts.hst()
            val fase = t.fasePada(hst)
            val berikut = t.aplikasiBerikutnya(hst)
            val logSaya = logs.filter { it.tanamanSayaId == ts.id }.sortedByDescending { it.tanggal }
            Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 5.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TanamanIcon(t, size = 17.dp, color = Biru)
                        Spacer(Modifier.width(8.dp))
                        Text(ts.label, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TeksGelap,
                            modifier = Modifier.weight(1f))
                        FaIcon(Fa.TRASH, size = 15.sp, color = TeksAbu,
                            modifier = Modifier.clickable { onHapus(ts.id) })
                    }
                    Text("${t.nama} • Tanam ${ts.tanggalTanam.format(fmtTanggal)} • Umur $hst HST",
                        fontSize = 12.sp, color = TeksAbu)
                    Text("Fase: ${fase?.nama ?: "Menjelang / lewat panen"} • Progres ${(t.progres(hst) * 100).toInt()}%",
                        fontSize = 12.sp, color = Biru, fontWeight = FontWeight.SemiBold)
                    LinearProgressIndicator(progress = { t.progres(hst) }, color = Hijau,
                        trackColor = Garis, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            .height(7.dp).clip(RoundedCornerShape(50)))
                    if (berikut != null) {
                        Text("Pupuk berikutnya: ${berikut.label} di ${berikut.hst} HST" +
                            (if (berikut.hst > hst) " (${berikut.hst - hst} hari lagi)" else " — SEKARANG") +
                            ": ${berikut.pupuk.joinToString { p -> "${p.nama} ${formatDosis(p.kgPerHa, ts.luasM2.takeIf { it > 0 }, ts.jumlahTanaman.takeIf { it > 0 }, t.populasiPerHa)}" }}",
                            fontSize = 12.sp, color = Color(0xFF14532D))
                    } else {
                        Text("Semua jadwal pupuk sudah lewat — fokus perawatan & panen.", fontSize = 12.sp, color = Color(0xFF14532D))
                    }
                    if (logSaya.isNotEmpty()) {
                        val terakhir = logSaya.first()
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FaIcon(Fa.RULER, size = 13.sp, color = TeksAbu)
                            Spacer(Modifier.width(6.dp))
                            Text("Terakhir dicatat ${terakhir.tanggal.format(fmtTanggal)}: tinggi ${terakhir.tinggiCm} cm, ${terakhir.jumlahDaun} daun, kondisi ${terakhir.kondisi}",
                                fontSize = 12.sp, color = TeksGelap)
                        }
                    }
                    Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { logUntuk = if (logUntuk == ts.id) null else ts.id },
                            colors = ButtonDefaults.buttonColors(containerColor = Biru),
                            shape = RoundedCornerShape(10.dp)) { Text("Catat Tumbuh", fontSize = 13.sp) }
                        OutlinedButton(onClick = { logUntuk = if (logUntuk == ts.id) null else ts.id },
                            shape = RoundedCornerShape(10.dp)) { Text("Lihat Log (${logSaya.size})", fontSize = 13.sp) }
                    }
                    if (logUntuk == ts.id) {
                        Spacer(Modifier.height(10.dp))
                        Field("Tinggi tanaman (cm)", tinggi, { tinggi = it }, angka = true)
                        Spacer(Modifier.height(8.dp))
                        Field("Jumlah daun", daun, { daun = it }, angka = true)
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listOf("Sehat", "Daun Menguning", "Layu", "Terserang Hama", "Lainnya")) { k ->
                                ChipPilih(k, kondisi == k) { kondisi = k }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Field("Catatan (opsional)", catatan, { catatan = it })
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            val tg = tinggi.toDoubleOrNull()
                            if (tg == null) { pesan = "Isi tinggi tanaman dulu (angka cm)." }
                            else {
                                onTambahLog(LogTumbuh(System.currentTimeMillis(), ts.id, LocalDate.now(),
                                    tg, daun.toIntOrNull() ?: 0, kondisi, catatan))
                                tinggi = ""; daun = ""; catatan = ""; pesan = ""
                            }
                        }, modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Hijau),
                            shape = RoundedCornerShape(10.dp)) { Text("Simpan Catatan Pertumbuhan", fontSize = 13.sp) }
                        if (logSaya.size >= 2) {
                            Spacer(Modifier.height(12.dp))
                            GrafikTinggi(logSaya)
                        }
                        logSaya.forEach { l ->
                            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("${l.tanggal.format(fmtTanggal)} • ${l.tinggiCm} cm • ${l.jumlahDaun} daun • ${l.kondisi}",
                                        fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    if (l.catatan.isNotBlank()) Text(l.catatan, fontSize = 12.sp, color = TeksAbu)
                                }
                                FaIcon(Fa.TRASH, size = 13.sp, color = TeksAbu,
                                    modifier = Modifier.clickable { onHapusLog(l.id) })
                            }
                        }
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(14.dp))
            Row(Modifier.padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                FaIcon(Fa.PLUS, size = 15.sp, color = Biru)
                Spacer(Modifier.width(8.dp))
                Text("Tanam Baru", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TeksGelap)
            }
            Spacer(Modifier.height(8.dp))
            PilihanTanaman(tanamanId) { tanamanId = it }
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Field("Nama petak / label", label, { label = it }, hint = "contoh: Cabai Petak 1")
                Field("Tanggal tanam (TTTT-BB-HH)", tanggal, { tanggal = it }, hint = "2026-11-10")
                Field("Luas petak (m²)", luas, { luas = it }, angka = true)
                Field("Jumlah tanaman", jumlah, { jumlah = it }, angka = true)
                if (pesan.isNotBlank()) Text(pesan, color = Color(0xFFB3261E), fontSize = 13.sp)
                Button(onClick = {
                    val tgl = try { LocalDate.parse(tanggal.trim()) } catch (e: Exception) { null }
                    if (label.isBlank()) pesan = "Isi nama petak dulu."
                    else if (tgl == null) pesan = "Tanggal tidak valid — format 2026-11-10."
                    else {
                        onTambah(TanamanSaya(System.currentTimeMillis(), label.trim(), tanamanId, tgl,
                            luas.toDoubleOrNull() ?: 0.0, jumlah.toIntOrNull() ?: 0))
                        label = ""; luas = ""; jumlah = ""; pesan = ""
                    }
                }, modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Biru),
                    shape = RoundedCornerShape(12.dp)) { Text("Simpan Tanaman Saya", fontWeight = FontWeight.Bold) }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

/* ---------------- PENGATURAN (profil, pembaruan, tentang) ---------------- */

@Composable
fun InfoScreen(nama: String, onSimpanNama: (String) -> Unit) {
    val context = LocalContext.current
    var namaEdit by remember(nama) { mutableStateOf(nama) }
    var statusUpdate by remember { mutableStateOf("belum") } // belum | memeriksa | terbaru | ada | gagal
    var infoUpdate by remember { mutableStateOf<InfoUpdate?>(null) }
    var pesanUpdate by remember { mutableStateOf("") }

    fun bukaUrl(url: String) {
        try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } catch (e: Exception) { }
    }

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            JudulHalaman("Pengaturan", "Profil, pembaruan aplikasi & tentang Auto Dosing Agro")
            Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                Kartu(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FaIcon(Fa.USER, size = 15.sp, color = Biru)
                            Spacer(Modifier.width(8.dp))
                            Text("Profil", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                        Field("Nama kamu (tampil di Beranda)", namaEdit, { namaEdit = it }, hint = "contoh: Rizki")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { onSimpanNama(namaEdit.trim()) },
                            colors = ButtonDefaults.buttonColors(containerColor = Biru),
                            shape = RoundedCornerShape(10.dp)) { Text("Simpan Nama", fontSize = 13.sp) }
                    }
                }

                Kartu(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FaIcon(Fa.UPDATE, size = 15.sp, color = Biru)
                            Spacer(Modifier.width(8.dp))
                            Text("Pembaruan Aplikasi", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("Versi terpasang: v${BuildConfig.VERSION_NAME}", fontSize = 13.sp, color = TeksAbu)
                        Spacer(Modifier.height(10.dp))
                        when (statusUpdate) {
                            "memeriksa" -> Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(Modifier.size(18.dp), color = Biru, strokeWidth = 2.dp)
                                Spacer(Modifier.width(10.dp))
                                Text("Memeriksa pembaruan…", fontSize = 13.sp)
                            }
                            "terbaru" -> Row(verticalAlignment = Alignment.CenterVertically) {
                                FaIcon(Fa.CHECK, size = 14.sp, color = Hijau)
                                Spacer(Modifier.width(8.dp))
                                Text("Sudah versi terbaru.", fontSize = 13.sp, color = Color(0xFF14532D))
                            }
                            "gagal" -> Row(verticalAlignment = Alignment.Top) {
                                FaIcon(Fa.WARN, size = 14.sp, color = Color(0xFFB3261E))
                                Spacer(Modifier.width(8.dp))
                                Text(pesanUpdate, fontSize = 13.sp, color = Color(0xFFB3261E))
                            }
                            "ada" -> {
                                val u = infoUpdate
                                if (u != null) {
                                    Text("Ada versi baru: v${u.versiBaru}", fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold, color = Color(0xFF14532D))
                                    if (u.catatan.isNotBlank()) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(u.catatan, fontSize = 12.sp, color = TeksGelap, maxLines = 8)
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (u.urlApk != null) {
                                            Button(onClick = { bukaUrl(u.urlApk) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Hijau),
                                                shape = RoundedCornerShape(10.dp)) {
                                                FaIcon(Fa.DOWNLOAD, size = 13.sp, color = Color.White)
                                                Spacer(Modifier.width(7.dp))
                                                Text("Unduh APK", fontSize = 13.sp)
                                            }
                                        }
                                        OutlinedButton(onClick = { bukaUrl(u.urlRilis) },
                                            shape = RoundedCornerShape(10.dp)) { Text("Halaman Rilis", fontSize = 13.sp) }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = {
                                statusUpdate = "memeriksa"
                                UpdateChecker.cek(BuildConfig.VERSION_NAME) { update, error ->
                                    when {
                                        error != null -> { statusUpdate = "gagal"; pesanUpdate = error }
                                        update != null -> { statusUpdate = "ada"; infoUpdate = update }
                                        else -> statusUpdate = "terbaru"
                                    }
                                }
                            },
                            enabled = statusUpdate != "memeriksa",
                            colors = ButtonDefaults.buttonColors(containerColor = Biru),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            FaIcon(Fa.UPDATE, size = 13.sp, color = Color.White)
                            Spacer(Modifier.width(7.dp))
                            Text("Periksa Pembaruan", fontSize = 13.sp)
                        }
                        Text("Pemeriksaan mengambil rilis terbaru dari GitHub (butuh internet). Unduhan APK tetap dari halaman rilis resmi repo aplikasi ini.",
                            fontSize = 11.sp, color = TeksAbu)
                    }
                }

                Kartu(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FaIcon(Fa.BOOK_OPEN, size = 15.sp, color = Biru)
                            Spacer(Modifier.width(8.dp))
                            Text("Cara Pakai", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("1. Buka menu Jenis Tanaman untuk mempelajari fase & jadwal pupuk tiap tanaman.\n" +
                            "2. Tambahkan tanamanmu di menu Pantau (nama petak + tanggal tanam) — umur (HST), fase & progresnya dihitung otomatis.\n" +
                            "3. Ketuk tombol timbangan di tengah untuk Kalkulator Dosis: isi jumlah tanaman, usia, cara pemupukan, jenis pupuk & musim.\n" +
                            "4. Catat tinggi & jumlah daun secara berkala lewat Catat Tumbuh untuk memantau pertumbuhan.", fontSize = 13.sp)
                    }
                }

                Kartu(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FaIcon(Fa.INFO, size = 15.sp, color = Biru)
                            Spacer(Modifier.width(8.dp))
                            Text("Tentang Dosis", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("Dosis di aplikasi ini adalah acuan umum praktik ATPH per hektar (kg/ha) dari rekomendasi budidaya yang lazim dipakai. " +
                            "Kebutuhan nyata bisa berbeda menurut kesuburan tanah, varietas, musim & hasil uji tanah — konsultasikan dengan guru / penyuluh setempat. " +
                            "HST = Hari Setelah Tanam. Pupuk dasar hortikultura sebagian diberikan sebelum tanam (HST negatif = hari sebelum tanam).", fontSize = 13.sp)
                    }
                }

                Text("Auto Dosing Agro Technology v${BuildConfig.VERSION_NAME} • Ikon UI: Font Awesome Free 6.5.2 (CC BY 4.0); ikon tanaman: set IconOven (MIT) — keduanya dibundel lokal. Data tanaman tersimpan lokal di HP.",
                    fontSize = 11.sp, color = TeksAbu)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
