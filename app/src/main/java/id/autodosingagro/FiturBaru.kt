package id.autodosingagro

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.autodosingagro.data.*
import java.time.LocalDate

@Composable
fun KepalaLayar(judul: String, sub: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
        FaIcon(Fa.CHEV_L, size = 20.sp, color = Biru,
            modifier = Modifier.clickable { onBack() }.padding(end = 14.dp))
        Column {
            Text(judul, fontSize = 21.sp, fontWeight = FontWeight.Bold, color = TeksGelap)
            Text(sub, fontSize = 12.sp, color = TeksAbu)
        }
    }
}

fun formatRp(n: Long): String = "Rp " + "%,d".format(n).replace(',', '.')

/* ---------------- DIAGNOSA GEJALA ---------------- */

@Composable
fun DiagnosaScreen(onBack: () -> Unit) {
    var pilih by remember { mutableStateOf<Gejala?>(null) }
    LazyColumn(Modifier.fillMaxSize()) {
        item { KepalaLayar("Diagnosa Gejala", "Pilih gejala yang terlihat — tampil kemungkinan penyebab & tindakannya", onBack) }
        items(GejalaDB.semua) { g ->
            val aktif = pilih?.nama == g.nama
            Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp)
                .clickable { pilih = if (aktif) null else g }) {
                Column(Modifier.padding(13.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FaIcon(g.ikon, size = 17.sp, color = Biru)
                        Spacer(Modifier.width(10.dp))
                        Text(g.nama, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TeksGelap,
                            modifier = Modifier.weight(1f))
                        FaIcon(if (aktif) Fa.CHEV_L else Fa.CHEV_R, size = 12.sp, color = TeksAbu)
                    }
                    if (aktif) {
                        Spacer(Modifier.height(8.dp))
                        Text("Kemungkinan penyebab", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BiruKartu)
                        Text(g.kemungkinan, fontSize = 13.sp, color = TeksGelap)
                        Spacer(Modifier.height(6.dp))
                        Text("Tindakan", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BiruKartu)
                        Text(g.tindakan, fontSize = 13.sp, color = TeksGelap)
                    }
                }
            }
        }
        item {
            Text("Diagnosa ini berbasis gejala umum untuk bahan belajar ATPH — untuk kepastian di lapangan, cocokkan dengan pengamatan langsung & konsultasikan ke guru/penyuluh.",
                fontSize = 11.sp, color = TeksAbu, modifier = Modifier.padding(18.dp))
        }
    }
}

/* ---------------- KALENDER TANAM ---------------- */

@Composable
fun KalenderScreen(onBack: () -> Unit) {
    val namaBulan = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
    var bulan by remember { mutableStateOf(LocalDate.now().monthValue) }
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            KepalaLayar("Kalender Tanam", "Tanaman yang cocok mulai ditanam per bulan & musimnya", onBack)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 18.dp)) {
                items((1..12).toList()) { b ->
                    ChipPilih(namaBulan[b - 1], bulan == b, if (KalenderDB.musimBulan(b) == "Musim Hujan") Fa.RAIN else Fa.SUN) { bulan = b }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text("${namaBulan[bulan - 1]} — ${KalenderDB.musimBulan(bulan)}",
                fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BiruKartu,
                modifier = Modifier.padding(horizontal = 18.dp))
            Spacer(Modifier.height(8.dp))
        }
        items(TanamanDB.semua) { t ->
            val info = KalenderDB.data[t.id]
            val cocok = info?.second?.contains(bulan) == true
            Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp)) {
                Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    FaIcon(t.ikon, size = 19.sp, color = if (cocok) Biru else TeksAbu)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(t.nama, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                            color = if (cocok) TeksGelap else TeksAbu)
                        Text(info?.first ?: "", fontSize = 12.sp, color = TeksAbu)
                    }
                    Text(if (cocok) "Cocok tanam" else "Kurang cocok",
                        fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        color = if (cocok) Hijau else TeksAbu)
                }
            }
        }
        item {
            Text("Acuan umum iklim Indonesia (musim hujan ±Okt–Apr, kemarau ±Mei–Sep) — tiap daerah bisa bergeser, sesuaikan dengan pola hujan setempat & ketersediaan air.",
                fontSize = 11.sp, color = TeksAbu, modifier = Modifier.padding(18.dp))
        }
    }
}

/* ---------------- USAHA TANI (BIAYA & PANEN) ---------------- */

@Composable
fun UsahaScreen(
    tanamanSaya: List<TanamanSaya>, logs: List<LogTumbuh>, transaksi: List<Transaksi>,
    onTambah: (Transaksi) -> Unit, onHapus: (Long) -> Unit, onBack: () -> Unit
) {
    val context = LocalContext.current
    var petakId by remember { mutableStateOf(tanamanSaya.firstOrNull()?.id) }
    var jenis by remember { mutableStateOf("Biaya") }
    var keterangan by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var hasilKg by remember { mutableStateOf("") }
    var pesan by remember { mutableStateOf("") }

    val petak = tanamanSaya.firstOrNull { it.id == petakId } ?: tanamanSaya.firstOrNull()
    val trxPetak = transaksi.filter { it.tanamanSayaId == petak?.id }.sortedByDescending { it.tanggal }
    val totalBiaya = trxPetak.filter { it.jenis == "Biaya" }.sumOf { it.nominal }
    val totalPanenRp = trxPetak.filter { it.jenis == "Panen" }.sumOf { it.nominal }
    val totalKg = trxPetak.filter { it.jenis == "Panen" }.sumOf { it.hasilKg }
    val laba = totalPanenRp - totalBiaya

    LazyColumn(Modifier.fillMaxSize()) {
        item { KepalaLayar("Usaha Tani", "Catat biaya & hasil panen per petak — laba/rugi dihitung otomatis", onBack) }
        if (tanamanSaya.isEmpty()) {
            item { Text("Belum ada tanaman. Tambahkan dulu tanamanmu di menu Pantau, baru catat biaya & panennya di sini.",
                color = TeksAbu, modifier = Modifier.padding(horizontal = 18.dp)) }
        } else {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp)) {
                    items(tanamanSaya) { ts ->
                        ChipPilih(ts.label, petak?.id == ts.id) { petakId = ts.id }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FaIcon(Fa.WALLET, size = 15.sp, color = Biru)
                            Spacer(Modifier.width(8.dp))
                            Text("Ringkasan ${petak?.label ?: ""}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Total biaya: ${formatRp(totalBiaya)}", fontSize = 13.sp)
                        Text("Total hasil panen: ${formatRp(totalPanenRp)} (${"%.1f".format(totalKg)} kg)", fontSize = 13.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FaIcon(Fa.TREND, size = 13.sp, color = if (laba >= 0) Hijau else Color(0xFFB3261E))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                (if (laba >= 0) "Laba: " else "Rugi: ") + formatRp(kotlin.math.abs(laba)),
                                fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                color = if (laba >= 0) Color(0xFF14532D) else Color(0xFFB3261E)
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        OutlinedButton(onClick = {
                            val t = petak?.let { TanamanDB.byId(it.tanamanId) }
                            val teks = buildString {
                                append("REKAP — Auto Dosing Agro\n")
                                append("Petak: ${petak?.label} (${t?.nama})\n")
                                append("Umur: ${petak?.hst()} HST • Fase: ${t?.fasePada(petak?.hst() ?: 0)?.nama ?: "-"}\n")
                                append("Total biaya: ${formatRp(totalBiaya)}\n")
                                append("Hasil panen: ${"%.1f".format(totalKg)} kg = ${formatRp(totalPanenRp)}\n")
                                append((if (laba >= 0) "Laba: " else "Rugi: ") + formatRp(kotlin.math.abs(laba)))
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"; putExtra(Intent.EXTRA_TEXT, teks)
                            }
                            try { context.startActivity(Intent.createChooser(intent, "Bagikan rekap")) } catch (e: Exception) { }
                        }, shape = RoundedCornerShape(10.dp)) {
                            FaIcon(Fa.SHARE, size = 13.sp, color = Biru)
                            Spacer(Modifier.width(7.dp))
                            Text("Bagikan Rekap", fontSize = 13.sp)
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text("Tambah Catatan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TeksGelap,
                    modifier = Modifier.padding(horizontal = 18.dp))
                Spacer(Modifier.height(8.dp))
                Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ChipPilih("Biaya", jenis == "Biaya", Fa.WALLET) { jenis = "Biaya" }
                        ChipPilih("Panen", jenis == "Panen", Fa.COINS) { jenis = "Panen" }
                    }
                    Field(if (jenis == "Biaya") "Keterangan biaya (mis. Beli NPK 10 kg)" else "Keterangan panen (mis. Panen ke-3 cabai)",
                        keterangan, { keterangan = it })
                    Field("Nominal (Rp)", nominal, { nominal = it }, angka = true, hint = "contoh: 150000")
                    if (jenis == "Panen") Field("Hasil panen (kg)", hasilKg, { hasilKg = it }, angka = true, hint = "contoh: 25")
                    if (pesan.isNotBlank()) Text(pesan, color = Color(0xFFB3261E), fontSize = 13.sp)
                    Button(onClick = {
                        val nom = nominal.replace(".", "").replace(",", "").toLongOrNull()
                        when {
                            petak == null -> pesan = "Pilih petak dulu."
                            keterangan.isBlank() -> pesan = "Isi keterangan dulu."
                            nom == null || nom <= 0 -> pesan = "Nominal tidak valid — isi angka rupiah, mis. 150000."
                            else -> {
                                onTambah(Transaksi(System.currentTimeMillis(), petak.id, LocalDate.now(), jenis,
                                    keterangan.trim(), nom, if (jenis == "Panen") hasilKg.toDoubleOrNull() ?: 0.0 else 0.0))
                                keterangan = ""; nominal = ""; hasilKg = ""; pesan = ""
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Biru),
                        shape = RoundedCornerShape(12.dp)) { Text("Simpan Catatan", fontWeight = FontWeight.Bold) }
                }
                Spacer(Modifier.height(12.dp))
                Text("Riwayat ${petak?.label ?: ""}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TeksGelap,
                    modifier = Modifier.padding(horizontal = 18.dp))
                Spacer(Modifier.height(6.dp))
            }
            items(trxPetak) { tr ->
                Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        FaIcon(if (tr.jenis == "Panen") Fa.COINS else Fa.WALLET, size = 15.sp,
                            color = if (tr.jenis == "Panen") Hijau else Biru)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(tr.keterangan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("${tr.tanggal.format(fmtTanggal)} • ${tr.jenis}" +
                                (if (tr.jenis == "Panen" && tr.hasilKg > 0) " • ${tr.hasilKg} kg" else ""),
                                fontSize = 11.sp, color = TeksAbu)
                        }
                        Text((if (tr.jenis == "Panen") "+ " else "− ") + formatRp(tr.nominal),
                            fontSize = 13.sp, fontWeight = FontWeight.Bold,
                            color = if (tr.jenis == "Panen") Color(0xFF14532D) else TeksGelap)
                        Spacer(Modifier.width(8.dp))
                        FaIcon(Fa.TRASH, size = 13.sp, color = TeksAbu,
                            modifier = Modifier.clickable { onHapus(tr.id) })
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

/* ---------------- GRAFIK PERTUMBUHAN ---------------- */

@Composable
fun GrafikTinggi(logs: List<LogTumbuh>) {
    val urut = logs.sortedBy { it.tanggal }
    if (urut.size < 2) return
    val minT = urut.minOf { it.tinggiCm }
    val maxT = urut.maxOf { it.tinggiCm }
    val rentang = (maxT - minT).takeIf { it > 0 } ?: 1.0
    val naik = urut.last().tinggiCm - urut.first().tinggiCm
    val hari = (urut.last().tanggal.toEpochDay() - urut.first().tanggal.toEpochDay()).coerceAtLeast(1)
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FaIcon(Fa.TREND, size = 14.sp, color = Biru)
            Spacer(Modifier.width(7.dp))
            Text("Grafik Tinggi Tanaman", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BiruKartu)
        }
        Spacer(Modifier.height(6.dp))
        androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(110.dp)) {
            val padKiri = 8f; val padAtas = 10f; val padBawah = 10f
            val w = size.width - padKiri * 2
            val h = size.height - padAtas - padBawah
            val titik = urut.mapIndexed { i, l ->
                val x = padKiri + if (urut.size == 1) w / 2 else w * i / (urut.size - 1)
                val y = padAtas + h * (1 - (l.tinggiCm - minT) / rentang).toFloat()
                androidx.compose.ui.geometry.Offset(x, y)
            }
            for (i in 0 until titik.size - 1) {
                drawLine(color = Biru, start = titik[i], end = titik[i + 1], strokeWidth = 4f,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round)
            }
            titik.forEach { drawCircle(color = Hijau, radius = 6f, center = it) }
        }
        Text("Tertinggi ${maxT} cm • terendah ${minT} cm • naik ${"%.1f".format(naik)} cm dalam $hari hari (±${"%.2f".format(naik / hari)} cm/hari)",
            fontSize = 11.sp, color = TeksAbu)
    }
}
