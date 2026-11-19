package id.autodosingagro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.autodosingagro.data.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val BiruGelap = Color(0xFF0E2A47)
val BiruKartu = Color(0xFF123A63)
val Biru = Color(0xFF1D6FB8)
val BiruTerang = Color(0xFF3E97DD)
val BiruMuda = Color(0xFFD9EAFB)
val BiruSangatMuda = Color(0xFFEFF6FE)
val Hijau = Color(0xFF22B14C)
val Latar = Color(0xFFFAFBFC)
val TeksGelap = Color(0xFF12263A)
val TeksAbu = Color(0xFF5B7186)
val Oranye = Color(0xFFF5A623)
val Garis = Color(0xFFE3EAF1)

val fmtTanggal: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")

enum class Tab { BERANDA, TANAMAN, DOSIS, PANTAU, INFO }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Biru, background = Latar, surface = Color.White,
                    onPrimary = Color.White, onBackground = TeksGelap, onSurface = TeksGelap
                )
            ) { App() }
        }
    }
}

/** Kartu minimalis: putih, garis tepi tipis, sudut 14dp, tanpa bayangan. */
@Composable
fun Kartu(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
    ) { Column(content = content) }
}

@Composable
fun App() {
    val context = LocalContext.current
    val store = remember { Store(context) }
    var tab by remember { mutableStateOf(Tab.BERANDA) }
    var detailId by remember { mutableStateOf<String?>(null) }
    var tanamanSaya by remember { mutableStateOf(store.loadTanamanSaya()) }
    var logs by remember { mutableStateOf(store.loadLog()) }
    var nama by remember { mutableStateOf(store.namaPengguna()) }

    fun simpanTanaman(list: List<TanamanSaya>) { tanamanSaya = list; store.saveTanamanSaya(list) }
    fun simpanLog(list: List<LogTumbuh>) { logs = list; store.saveLog(list) }

    Box(Modifier.fillMaxSize().background(Latar)) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                when {
                    detailId != null -> DetailTanamanScreen(
                        tanaman = TanamanDB.byId(detailId!!)!!,
                        onBack = { detailId = null },
                        onHitung = { detailId = null; tab = Tab.DOSIS }
                    )
                    tab == Tab.BERANDA -> BerandaScreen(
                        nama = nama, tanamanSaya = tanamanSaya,
                        onMenu = { t -> tab = t },
                        onDetail = { detailId = it },
                        onPantau = { tab = Tab.PANTAU }
                    )
                    tab == Tab.TANAMAN -> TanamanScreen(onDetail = { detailId = it })
                    tab == Tab.DOSIS -> DosisScreen(tanamanSaya = tanamanSaya)
                    tab == Tab.PANTAU -> PantauScreen(
                        tanamanSaya = tanamanSaya, logs = logs,
                        onTambah = { simpanTanaman(tanamanSaya + it) },
                        onHapus = { id -> simpanTanaman(tanamanSaya.filterNot { t -> t.id == id }) },
                        onTambahLog = { simpanLog(logs + it) },
                        onHapusLog = { id -> simpanLog(logs.filterNot { l -> l.id == id }) }
                    )
                    tab == Tab.INFO -> InfoScreen(nama = nama, onSimpanNama = { nama = it; store.simpanNama(it) })
                }
            }
            BottomNav(tab = tab, onTab = { tab = it; detailId = null })
        }
    }
}

@Composable
fun BottomNav(tab: Tab, onTab: (Tab) -> Unit) {
    Box {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = Color.White, shadowElevation = 4.dp
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(Fa.HOUSE, "Beranda", tab == Tab.BERANDA) { onTab(Tab.BERANDA) }
                NavItem(Fa.LEAF, "Tanaman", tab == Tab.TANAMAN) { onTab(Tab.TANAMAN) }
                Spacer(Modifier.width(52.dp))
                NavItem(Fa.CHART, "Pantau", tab == Tab.PANTAU) { onTab(Tab.PANTAU) }
                NavItem(Fa.GEAR, "Pengaturan", tab == Tab.INFO) { onTab(Tab.INFO) }
            }
        }
        Box(
            Modifier.align(Alignment.TopCenter).size(56.dp).clip(CircleShape)
                .background(Biru)
                .clickable { onTab(Tab.DOSIS) },
            contentAlignment = Alignment.Center
        ) {
            FaIcon(Fa.SCALE, size = 24.sp, color = Color.White)
        }
    }
}

@Composable
fun NavItem(ikon: String, label: String, aktif: Boolean, onClick: () -> Unit) {
    Column(
        Modifier.clip(RoundedCornerShape(10.dp)).clickable { onClick() }.padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FaIcon(ikon, size = 20.sp, color = if (aktif) Biru else TeksAbu)
        Spacer(Modifier.height(2.dp))
        Text(label, fontSize = 11.sp, fontWeight = if (aktif) FontWeight.Bold else FontWeight.Normal,
            color = if (aktif) Biru else TeksAbu)
    }
}

@Composable
fun LingkaranProgres(persen: Float, ukuran: Int, tebal: Float, warna: Color = Hijau) {
    Box(Modifier.size(ukuran.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(ukuran.dp)) {
            drawArc(color = Garis, startAngle = -90f, sweepAngle = 360f,
                useCenter = false, style = Stroke(width = tebal, cap = StrokeCap.Round))
            drawArc(color = warna, startAngle = -90f, sweepAngle = 360f * persen.coerceIn(0f, 1f),
                useCenter = false, style = Stroke(width = tebal, cap = StrokeCap.Round))
        }
        Text("${(persen * 100).toInt()}%", color = TeksGelap, fontWeight = FontWeight.ExtraBold,
            fontSize = (ukuran / 4.2).sp)
    }
}

/* ---------------- BERANDA (minimalis, ikon Font Awesome) ---------------- */

@Composable
fun BerandaScreen(
    nama: String, tanamanSaya: List<TanamanSaya>,
    onMenu: (Tab) -> Unit, onDetail: (String) -> Unit, onPantau: () -> Unit
) {
    var monitoringOn by remember { mutableStateOf(true) }
    val utama = tanamanSaya.firstOrNull()
    val tanamanUtama = utama?.let { TanamanDB.byId(it.tanamanId) }
    val hstUtama = utama?.hst() ?: 25
    val progres = tanamanUtama?.progres(hstUtama) ?: 0.75f
    val faseSelesai = tanamanUtama?.fase?.count { hstUtama > it.hstSelesai } ?: 3
    val faseTotal = tanamanUtama?.fase?.size ?: 5

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            // Header biru rata (flat) — tanpa gradasi
            Box(
                Modifier.fillMaxWidth().background(Biru)
                    .padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 58.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(48.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                        FaIcon(Fa.SEEDLING, size = 22.sp, color = Biru)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Halo ${if (nama.isBlank()) "Siswa ATPH" else nama},", color = Color.White,
                            fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Siswa SMK • Jurusan ATPH", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                    }
                    Row(
                        Modifier.clip(RoundedCornerShape(50)).background(Color.White)
                            .clickable { monitoringOn = !monitoringOn }.padding(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.clip(RoundedCornerShape(50))
                                .background(if (monitoringOn) Biru else Color.Transparent)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) { Text("ON", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = if (monitoringOn) Color.White else TeksAbu) }
                        Box(
                            Modifier.clip(RoundedCornerShape(50))
                                .background(if (!monitoringOn) Biru else Color.Transparent)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) { Text("OFF", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = if (!monitoringOn) Color.White else TeksAbu) }
                    }
                    Spacer(Modifier.width(10.dp))
                    FaIcon(Fa.BELL, size = 19.sp, color = Color.White)
                }
            }
        }
        item {
            Box(Modifier.padding(horizontal = 18.dp).offset(y = (-42).dp)) {
                Kartu(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LingkaranProgres(progres, 84, 11f)
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Progres Pertumbuhan Kamu", color = TeksGelap, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(3.dp))
                                Text("$faseSelesai dari $faseTotal Fase ${tanamanUtama?.nama ?: ""} Selesai",
                                    color = TeksGelap, fontSize = 14.sp)
                                Spacer(Modifier.height(9.dp))
                                LinearProgressIndicator(
                                    progress = { progres }, color = Hijau, trackColor = Garis,
                                    modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(50))
                                )
                                Spacer(Modifier.height(7.dp))
                                Text(
                                    if (utama != null && tanamanUtama != null)
                                        "${utama.label} • Umur $hstUtama HST • ${tanamanUtama.fasePada(hstUtama)?.nama ?: "Menjelang Panen"}"
                                    else "Contoh: Padi • Umur 25 HST • Fase Vegetatif / Anakan — tambah tanamanmu di menu Pantau",
                                    color = TeksAbu, fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(
                                onClick = onPantau,
                                colors = ButtonDefaults.buttonColors(containerColor = Biru),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Lanjut Pantau", fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.width(8.dp))
                                FaIcon(Fa.ARROW_R, size = 14.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 18.dp).offset(y = (-26).dp)) {
                Text("Menu Utama", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TeksGelap)
                Spacer(Modifier.height(12.dp))
                val menu = listOf(
                    Triple(Fa.BOOK_OPEN, "Jenis\nTanaman", Tab.TANAMAN),
                    Triple(Fa.CALENDAR, "Umur\nTanaman", Tab.PANTAU),
                    Triple(Fa.SCALE, "Dosis\nPupuk", Tab.DOSIS),
                    Triple(Fa.CLIPBOARD, "Jadwal\nPupuk", Tab.TANAMAN),
                    Triple(Fa.BULLHORN, "Tanam\nBaru", Tab.PANTAU),
                    Triple(Fa.NOTE, "Catat\nTumbuh", Tab.PANTAU),
                    Triple(Fa.FOLDER, "Rekapan", Tab.PANTAU),
                    Triple(Fa.CALC, "Kalkulator\nDosis", Tab.DOSIS)
                )
                menu.chunked(4).forEach { rowItems ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        rowItems.forEach { (ikon, label, target) ->
                            Kartu(Modifier.weight(1f).clickable { onMenu(target) }) {
                                Column(
                                    Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    FaIcon(ikon, size = 22.sp, color = Biru)
                                    Spacer(Modifier.height(7.dp))
                                    Text(label, fontSize = 11.sp, textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Medium, color = TeksGelap)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
        item {
            Column(Modifier.padding(start = 18.dp).offset(y = (-12).dp)) {
                Row(Modifier.fillMaxWidth().padding(end = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Kategori Tanaman", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TeksGelap,
                        modifier = Modifier.weight(1f))
                    Text("Lihat Semua", color = Biru, fontSize = 13.sp,
                        modifier = Modifier.clickable { onMenu(Tab.TANAMAN) })
                }
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(end = 18.dp)) {
                    items(TanamanDB.semua) { t ->
                        Kartu(Modifier.width(180.dp).clickable { onDetail(t.id) }) {
                            Column(Modifier.padding(14.dp)) {
                                FaIcon(t.ikon, size = 24.sp, color = Biru)
                                Spacer(Modifier.height(8.dp))
                                Text(t.nama, color = TeksGelap, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                Text("${t.jadwal.size} Jadwal Pupuk • Panen ${t.panenMin}–${t.panenMax} HST",
                                    color = TeksAbu, fontSize = 12.sp)
                                Spacer(Modifier.height(9.dp))
                                LinearProgressIndicator(
                                    progress = { 0.5f }, color = Hijau, trackColor = Garis,
                                    modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(50))
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 18.dp)) {
                Text("Aktifitas Harian", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TeksGelap)
                Spacer(Modifier.height(10.dp))
                val faseAktif = tanamanUtama?.fasePada(hstUtama)
                val tugas = listOf(
                    "Cek kelembapan tanah & kondisi daun pagi ini",
                    faseAktif?.perawatan ?: "Catat tinggi tanaman & jumlah daun di menu Pantau",
                    tanamanUtama?.let {
                        val next = it.aplikasiBerikutnya(hstUtama)
                        if (next != null) "Siapkan pupuk: ${next.label} pada ${next.hst} HST (${next.pupuk.joinToString { p -> p.nama }})"
                        else "Masa pemupukan selesai — fokus ke perawatan & panen"
                    } ?: "Buka Kalkulator Dosis untuk menghitung kebutuhan pupuk petakmu"
                )
                var centang by remember { mutableStateOf(setOf<Int>()) }
                tugas.forEachIndexed { i, t ->
                    Kartu(Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        .clickable { centang = if (i in centang) centang - i else centang + i }) {
                        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                            FaIcon(if (i in centang) Fa.SQ_CHECK else Fa.SQUARE, size = 19.sp,
                                color = if (i in centang) Hijau else TeksAbu)
                            Spacer(Modifier.width(10.dp))
                            Text(t, fontSize = 13.sp, color = if (i in centang) TeksAbu else TeksGelap)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
