package id.autodosingagro

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
            val cocok = if (info == null) true else info.second.contains(bulan)
            Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp)) {
                Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    TanamanIcon(t, size = 28.dp, color = if (cocok) Biru else TeksAbu)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(t.nama, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                            color = if (cocok) TeksGelap else TeksAbu)
                        Text(info?.first ?: "Tanaman kustom — dianggap cocok sepanjang tahun", fontSize = 12.sp, color = TeksAbu)
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

fun simpanFotoDariUri(context: Context, uri: Uri): String? {
    return try {
        val input = context.contentResolver.openInputStream(uri) ?: return null
        val bmp = BitmapFactory.decodeStream(input); input.close()
        if (bmp == null) return null
        val s = minOf(bmp.width, bmp.height)
        val crop = Bitmap.createBitmap(bmp, (bmp.width - s) / 2, (bmp.height - s) / 2, s, s)
        val scaled = Bitmap.createScaledBitmap(crop, 512, 512, true)
        val dir = File(context.filesDir, "foto_kustom").apply { mkdirs() }
        val out = File(dir, "foto_" + System.currentTimeMillis() + ".jpg")
        FileOutputStream(out).use { scaled.compress(Bitmap.CompressFormat.JPEG, 82, it) }
        out.absolutePath
    } catch (e: Exception) { null }
}

/* ---------------- TAMBAH TANAMAN KUSTOM ---------------- */

@Composable
fun TambahTanamanScreen(onSimpan: (Tanaman) -> Unit, onBack: () -> Unit) {
    var nama by remember { mutableStateOf("") }
    var latin by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("Sayuran") }
    var panenMin by remember { mutableStateOf("") }
    var panenMax by remember { mutableStateOf("") }
    var jarak by remember { mutableStateOf("") }
    var populasi by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var ikon by remember { mutableStateOf(R.drawable.ic_kedelai) }
    var fotoRes by remember { mutableStateOf(0) }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var pesan by remember { mutableStateOf("") }
    val context = LocalContext.current
    val pemilihFoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) fotoUri = uri
    }

    val pilihanIkon = listOf(
        R.drawable.ic_padi, R.drawable.ic_jagung, R.drawable.ic_kedelai,
        R.drawable.ic_cabai_merah, R.drawable.ic_cabai_rawit, R.drawable.ic_tomat,
        R.drawable.ic_bawang_merah, R.drawable.ic_mentimun
    )
    val pilihanKategori = listOf("Tanaman Pangan", "Sayuran", "Buah", "Umbi", "Rempah", "Lainnya")

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            KepalaLayar("Tambah Tanaman Baru", "Tanaman buatanmu sendiri — muncul di Jenis Tanaman, Pantau, Kalkulator & Kalender", onBack)
            Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Field("Nama tanaman *", nama, { nama = it }, hint = "contoh: Bayam")
                Field("Nama latin (opsional)", latin, { latin = it }, hint = "contoh: Amaranthus spp.")
                LabelInput("Kategori")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(pilihanKategori) { k -> ChipPilih(k, kategori == k) { kategori = k } }
                }
                LabelInput("Foto / Ikon tanaman")
                Text("Pakai foto bawaan, foto tanamanmu sendiri dari galeri, atau ikon garis.",
                    fontSize = 12.sp, color = TeksAbu)
                val pilihanFoto = listOf(
                    R.drawable.foto_padi, R.drawable.foto_jagung, R.drawable.foto_kedelai,
                    R.drawable.foto_cabai_merah, R.drawable.foto_cabai_rawit, R.drawable.foto_tomat,
                    R.drawable.foto_bawang_merah, R.drawable.foto_mentimun
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(pilihanFoto) { res ->
                        Box(
                            Modifier.size(52.dp).clip(RoundedCornerShape(12.dp))
                                .background(if (fotoRes == res && fotoUri == null) Biru else Color.White)
                                .clickable { fotoRes = res; fotoUri = null },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(painter = androidx.compose.ui.res.painterResource(res),
                                contentDescription = "foto bawaan",
                                modifier = Modifier.size(46.dp).clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop)
                        }
                    }
                }
                OutlinedButton(onClick = { pemilihFoto.launch("image/*") },
                    shape = RoundedCornerShape(10.dp)) {
                    FaIcon(Fa.IMAGE, size = 14.sp, color = Biru)
                    Spacer(Modifier.width(8.dp))
                    Text("Pilih Foto dari Galeri", fontSize = 13.sp)
                }
                val uriTerpilih = fotoUri
                if (uriTerpilih != null) {
                    val bmpPreview = remember(uriTerpilih) {
                        try {
                            context.contentResolver.openInputStream(uriTerpilih)?.use { BitmapFactory.decodeStream(it) }
                        } catch (e: Exception) { null }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (bmpPreview != null) {
                            Image(bitmap = bmpPreview.asImageBitmap(), contentDescription = "foto pilihan",
                                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop)
                            Spacer(Modifier.width(10.dp))
                        }
                        Text("Foto dari galerimu akan dipakai untuk tanaman ini.",
                            fontSize = 12.sp, color = TeksGelap, modifier = Modifier.weight(1f))
                        FaIcon(Fa.TRASH, size = 15.sp, color = TeksAbu,
                            modifier = Modifier.clickable { fotoUri = null })
                    }
                }
                LabelInput("atau ikon garis")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(pilihanIkon) { res ->
                        Box(
                            Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                                .background(if (ikon == res && fotoRes == 0 && fotoUri == null) BiruSangatMuda else Color.White)
                                .clickable { ikon = res; fotoRes = 0; fotoUri = null },
                            contentAlignment = Alignment.Center
                        ) { IkonTanamanRes(res, "ikon", size = 28.dp, color = Biru) }
                    }
                }
                Field("Panen mulai (HST) *", panenMin, { panenMin = it }, angka = true, hint = "contoh: 25")
                Field("Panen selesai (HST) *", panenMax, { panenMax = it }, angka = true, hint = "contoh: 35")
                Field("Jarak tanam", jarak, { jarak = it }, hint = "contoh: 20 × 20 cm")
                Field("Populasi per hektar (tanaman)", populasi, { populasi = it }, angka = true, hint = "kosongkan bila tidak tahu")
                Field("Deskripsi singkat", deskripsi, { deskripsi = it }, hint = "catatan tentang tanaman ini")
                Text("Fase pertumbuhan & jadwal pupuknya dibuat otomatis memakai pola umum (4 fase + pupuk dasar & 2 susulan) mengikuti umur panen yang kamu isi — dosisnya bisa kamu sesuaikan lagi lewat Kalkulator Dosis.",
                    fontSize = 12.sp, color = TeksAbu)
                if (pesan.isNotBlank()) Text(pesan, color = Color(0xFFB3261E), fontSize = 13.sp)
                Button(onClick = {
                    val pMin = panenMin.toIntOrNull(); val pMax = panenMax.toIntOrNull()
                    when {
                        nama.isBlank() -> pesan = "Nama tanaman wajib diisi."
                        pMin == null || pMax == null || pMin <= 0 || pMax <= pMin ->
                            pesan = "Umur panen tidak valid — panen selesai harus lebih besar dari panen mulai (HST)."
                        else -> {
                            val m = pMax
                            val tanaman = Tanaman(
                                id = "kustom-" + System.currentTimeMillis(),
                                nama = nama.trim(),
                                latin = latin.trim().ifBlank { "—" },
                                kategori = kategori,
                                ikon = ikon,
                                foto = if (fotoUri == null) fotoRes else 0,
                                fotoPath = fotoUri?.let { simpanFotoDariUri(context, it) } ?: "", 
                                panenMin = pMin, panenMax = pMax,
                                jarakTanam = jarak.trim().ifBlank { "-" },
                                populasiPerHa = populasi.toIntOrNull() ?: 20000,
                                deskripsi = deskripsi.trim().ifBlank { "Tanaman kustom buatan pengguna." },
                                fase = listOf(
                                    Fase("Persemaian & Awal Tanam", 0, (m * 0.15).toInt().coerceAtLeast(1),
                                        "Benih berkecambah / bibit baru pindah tanam, daun pertama muncul.",
                                        "Jaga kelembapan media, lindungi dari hujan deras & hama awal."),
                                    Fase("Pertumbuhan Vegetatif", (m * 0.15).toInt().coerceAtLeast(1), (m * 0.45).toInt(),
                                        "Daun & batang tumbuh cepat, tanaman meninggi dan merimbun.",
                                        "Pupuk susulan sumber N, penyiangan, penyiraman teratur."),
                                    Fase("Generatif", (m * 0.45).toInt(), (m * 0.8).toInt(),
                                        "Muncul bunga / bakal buah / umbi mulai terbentuk & membesar.",
                                        "Tekankan pupuk K, jaga air stabil, pantau hama buah & penyakit."),
                                    Fase("Pematangan & Panen", (m * 0.8).toInt(), m,
                                        "Hasil mulai matang & siap dipanen bertahap atau serentak.",
                                        "Kurangi pupuk N, panen tepat waktu agar kualitas terbaik.")
                                ),
                                jadwal = listOf(
                                    AplikasiPupuk("Dasar", -3, listOf(Pupuk("Pupuk Kandang", 5000.0), Pupuk("NPK 15-15-15", 150.0)),
                                        "Jadwal umum tanaman kustom — sesuaikan dengan rekomendasi setempat."),
                                    AplikasiPupuk("Susulan 1", (m * 0.3).toInt().coerceAtLeast(1), listOf(Pupuk("NPK 15-15-15", 100.0)),
                                        "Berikan di fase vegetatif."),
                                    AplikasiPupuk("Susulan 2", (m * 0.55).toInt(), listOf(Pupuk("NPK 15-15-15", 75.0), Pupuk("KCl", 50.0)),
                                        "Berikan saat masuk fase generatif.")
                                )
                            )
                            onSimpan(tanaman)
                        }
                    }
                }, modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Biru),
                    shape = RoundedCornerShape(12.dp)) { Text("Simpan Tanaman Baru", fontWeight = FontWeight.Bold) }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

/* ---------------- JADWAL PUPUK ---------------- */

@Composable
fun JadwalScreen(tanamanSaya: List<TanamanSaya>, pengingatAktif: Boolean, onBack: () -> Unit) {
    val hariIni = LocalDate.now()
    val ctxJadwal = LocalContext.current
    val storeJadwal = remember { Store(ctxJadwal) }
    val jamTeks = "%02d.%02d".format(storeJadwal.pengingatJam() / 60, storeJadwal.pengingatJam() % 60)
    val h1Teks = if (storeJadwal.pengingatH1()) " + pengingat H-1" else "" 
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            KepalaLayar("Jadwal Pupuk", "Semua jadwal pemupukan tanamanmu lengkap dengan tanggalnya", onBack)
            Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    FaIcon(Fa.BELL, size = 18.sp, color = if (pengingatAktif) Hijau else TeksAbu)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            if (pengingatAktif) "Notifikasi pengingat AKTIF" else "Notifikasi pengingat MATI",
                            fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TeksGelap
                        )
                        Text(
                            if (pengingatAktif) "Kamu akan diingatkan jam $jamTeks di hari pemupukan$h1Teks. Atur di menu Pengaturan."
                            else "Aktifkan saklar Pengingat Pupuk di menu Pengaturan agar diingatkan di hari pemupukan.",
                            fontSize = 12.sp, color = TeksAbu
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        if (tanamanSaya.isEmpty()) {
            item {
                Text("Belum ada tanaman dipantau. Tambahkan tanamanmu dulu di menu Pantau — jadwal pupuknya otomatis tampil di sini.",
                    color = TeksAbu, modifier = Modifier.padding(horizontal = 18.dp))
            }
        }
        items(tanamanSaya) { ts ->
            val t = TanamanDB.byId(ts.tanamanId) ?: TanamanDB.semua.first()
            Kartu(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 5.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TanamanIcon(t, size = 30.dp, color = Biru)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(ts.label, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TeksGelap)
                            Text("${t.nama} • Tanam ${ts.tanggalTanam.format(fmtTanggal)} • Umur ${ts.hst()} HST",
                                fontSize = 12.sp, color = TeksAbu)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    t.jadwal.sortedBy { it.hst }.forEach { app ->
                        val tanggal = ts.tanggalTanam.plusDays(app.hst.toLong())
                        val selisih = tanggal.toEpochDay() - hariIni.toEpochDay()
                        Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.Top) {
                            Column(Modifier.weight(1f)) {
                                Text("${app.label} — umur ${app.hst} HST", fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold, color = BiruKartu)
                                Text(app.pupuk.joinToString { p -> "${p.nama} ${"%.0f".format(p.kgPerHa)} kg/ha" },
                                    fontSize = 12.sp, color = TeksGelap)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(tanggal.format(fmtTanggal), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    when {
                                        selisih < 0 -> "sudah lewat"
                                        selisih == 0L -> "HARI INI"
                                        else -> "$selisih hari lagi"
                                    },
                                    fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                    color = when {
                                        selisih < 0 -> TeksAbu
                                        selisih == 0L -> Hijau
                                        selisih <= 3 -> Oranye
                                        else -> TeksAbu
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

/* ---------------- KARTU CUACA (BERANDA) ---------------- */

private fun cuacaDariCache(json: String?): CuacaInfo? {
    if (json == null) return null
    return try {
        val j = org.json.JSONObject(json)
        CuacaInfo(j.getDouble("suhu"), j.getInt("kode"), j.getInt("lembap"), j.getDouble("angin"),
            j.getInt("hujan"), j.getDouble("max"), j.getDouble("min"))
    } catch (e: Exception) { null }
}

private fun cuacaKeCache(info: CuacaInfo): String =
    org.json.JSONObject().apply {
        put("suhu", info.suhu); put("kode", info.kode); put("lembap", info.kelembapan)
        put("angin", info.angin); put("hujan", info.peluangHujan); put("max", info.suhuMax); put("min", info.suhuMin)
    }.toString()

@Composable
fun KartuCuaca() {
    val context = LocalContext.current
    val store = remember { Store(context) }
    var kota by remember { mutableStateOf(store.cuacaLokasi()) }
    var info by remember { mutableStateOf(cuacaDariCache(store.cuacaCache())) }
    var memuat by remember { mutableStateOf(false) }
    var dialogKota by remember { mutableStateOf(false) }

    fun segarkan() {
        memuat = true
        CuacaApi.ambil(kota.lat, kota.lon) { hasil ->
            memuat = false
            if (hasil != null) { info = hasil; store.simpanCuacaCache(cuacaKeCache(hasil)) }
        }
    }
    LaunchedEffect(kota) { segarkan() }

    val c = info
    Kartu(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FaIcon(Fa.PIN, size = 13.sp, color = Biru)
                Spacer(Modifier.width(6.dp))
                Text(kota.nama, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TeksGelap,
                    modifier = Modifier.weight(1f))
                Text("Ganti", fontSize = 12.sp, color = Biru, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { dialogKota = true }.padding(4.dp))
                FaIcon(Fa.UPDATE, size = 13.sp, color = Biru,
                    modifier = Modifier.clickable { segarkan() }.padding(4.dp))
            }
            if (c == null) {
                Spacer(Modifier.height(8.dp))
                Text(if (memuat) "Memuat cuaca…" else "Cuaca belum bisa dimuat — periksa internet lalu ketuk ikon segarkan.",
                    fontSize = 12.sp, color = TeksAbu)
            } else {
                val (label, kategori) = labelCuaca(c.kode)
                val glyph = when (kategori) {
                    "cerah" -> Fa.SUN
                    "cerahberawan" -> Fa.CLOUD_SUN
                    "hujan" -> Fa.RAIN
                    "petir" -> Fa.BOLT
                    "salju" -> Fa.SNOW
                    "kabut" -> Fa.SMOG
                    else -> Fa.CLOUD
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(58.dp).clip(CircleShape).background(HijauMuda),
                        contentAlignment = Alignment.Center) {
                        FaIcon(glyph, size = 26.sp, color = Hijau)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("${"%.0f".format(c.suhu)}°C", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = TeksGelap)
                        Text("$label • ${"%.0f".format(c.suhuMin)}°–${"%.0f".format(c.suhuMax)}°C hari ini",
                            fontSize = 12.sp, color = TeksAbu)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FaIcon(Fa.DROPLET, size = 12.sp, color = Biru); Spacer(Modifier.width(5.dp))
                        Text("Lembap ${c.kelembapan}%", fontSize = 12.sp, color = TeksGelap)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FaIcon(Fa.WIND, size = 12.sp, color = Biru); Spacer(Modifier.width(5.dp))
                        Text("Angin ${"%.0f".format(c.angin)} km/j", fontSize = 12.sp, color = TeksGelap)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FaIcon(Fa.RAIN, size = 12.sp, color = Biru); Spacer(Modifier.width(5.dp))
                        Text("Hujan ${c.peluangHujan}%", fontSize = 12.sp, color = TeksGelap)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(HijauMuda).padding(10.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        FaIcon(Fa.SEEDLING, size = 13.sp, color = HijauTua)
                        Spacer(Modifier.width(8.dp))
                        Text(saranCuaca(c), fontSize = 12.sp, color = HijauTua)
                    }
                }
                Text("Cuaca: Open-Meteo • diperbarui tiap aplikasi dibuka", fontSize = 10.sp, color = TeksAbu,
                    modifier = Modifier.padding(top = 8.dp))
            }
        }
    }

    if (dialogKota) {
        var kata by remember { mutableStateOf("") }
        var hasil by remember { mutableStateOf<List<KotaCuaca>>(emptyList()) }
        var dicari by remember { mutableStateOf(false) }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { dialogKota = false },
            title = { Text("Pilih Kota / Daerah", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(value = kata, onValueChange = { kata = it },
                        label = { Text("Nama kota / kabupaten") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        if (kata.isNotBlank()) { dicari = true; CuacaApi.cariKota(kata.trim()) { r -> hasil = r; dicari = false } }
                    }, shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Biru)) {
                        Text("Cari", fontSize = 13.sp)
                    }
                    if (dicari) Text("Mencari…", fontSize = 12.sp, color = TeksAbu, modifier = Modifier.padding(top = 8.dp))
                    hasil.forEach { k ->
                        Text(k.nama, fontSize = 13.sp, color = TeksGelap,
                            modifier = Modifier.fillMaxWidth().clickable {
                                kota = k; store.simpanCuacaLokasi(k); dialogKota = false
                            }.padding(vertical = 10.dp))
                    }
                }
            },
            confirmButton = {
                OutlinedButton(onClick = { dialogKota = false }, shape = RoundedCornerShape(10.dp)) { Text("Tutup") }
            }
        )
    }
}
