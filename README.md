# Auto Dosing Agro Technology

Aplikasi Android untuk **SMK jurusan ATPH** (Agribisnis Tanaman Pangan dan Hortikultura):
memantau pertumbuhan tanaman serta dosis pemupukan sesuai umur tanaman.

Tampilan Beranda mengikuti referensi yang diberikan: header biru gradasi,
kartu progres pertumbuhan gelap dengan lingkaran progres, grid **Menu Utama**,
kartu kategori tanaman geser, **Aktifitas Harian**, dan navigasi bawah dengan
tombol **D** (Dosis) besar di tengah.

## Fitur

1. **Jenis Tanaman** — 8 tanaman dengan nama latin, kategori, jarak tanam &
   populasi per hektar:
   Padi, Jagung, Kedelai (tanaman pangan) dan Cabai Merah, Cabai Rawit,
   Tomat, Bawang Merah, Mentimun (hortikultura).
2. **Umur Tanaman** — fase pertumbuhan per rentang HST (Hari Setelah Tanam)
   lengkap dengan ciri & perawatannya; umur tanaman saya dihitung otomatis
   dari tanggal tanam.
3. **Dosis Pemupukan** — jadwal pupuk dasar & susulan per tanaman (kg/ha)
   + **Kalkulator Dosis**: isi jenis tanaman, umur (HST) atau tanggal tanam,
   Input kalkulator (v1.1): 1) jumlah tanaman, 2) usia tanaman (HST/tanggal tanam),
   3) cara pemupukan — Tabur / Kocor, 4) jenis pupuk (sesuai ketetapan user:
   NPK, KNO3, MKP, KCl — bisa pilih banyak), 5) musim — Kemarau / Hujan,
   plus luas petak. Hasil: dosis
   per pupuk terpilih (dari jadwal tanaman atau dosis umum), resep larutan kocor
   (gram/tanaman, ml air, total liter) & panduan cara aplikasi sesuai musim.
4. **Pantau Pertumbuhan** — tambah tanaman/petak, progres ke panen, pengingat
   pupuk berikutnya, dan log pertumbuhan (tinggi cm, jumlah daun, kondisi, catatan).

Semua data tersimpan lokal di HP. Tanpa internet, tanpa akun.

> Dosis adalah acuan umum praktik ATPH. Kebutuhan nyata menyesuaikan uji
> tanah, varietas & kondisi lahan — konsultasikan dengan guru/penyuluh setempat.

## v1.3.1

- **Ikon tanaman diganti total** — ikon Font Awesome untuk tanaman memang tidak
  ada yang persis (mentimun tadinya wortel, tomat apel, bawang merah layer).
  Sekarang memakai set ikon **IconOven (lisensi MIT)** yang digambar sesuai
  tanamannya: padi = malai gandum, jagung = tongkol berkulit, kedelai = polong
  berbiji, cabai merah = cabai, cabai rawit = cabai + api (lebih pedas),
  tomat = tomat berkelopak, bawang merah = umbi berakar, mentimun = mentimun
  + irisannya. Dibundel sebagai vector drawable, tetap offline & mengikuti warna tema.
  Ikon UI lain tetap Font Awesome.

## v1.3.0 — monitoring makin lengkap, multifungsi

Pilihan fitur dari sisi pengembang, sesuai tujuan aplikasi (monitoring + multifungsi):

- **Perhatian Hari Ini** (Beranda) — peringatan otomatis dari data monitoring:
  pupuk jatuh tempo ≤3 hari, panen ≤7 hari, lewat masa panen, dan tanaman yang
  ≥7 hari belum dicatat pertumbuhannya.
- **Grafik Pertumbuhan** (Pantau) — grafik garis tinggi tanaman dari log +
  statistik kenaikan cm & rata-rata cm/hari.
- **Diagnosa Gejala** — 9 gejala umum (daun kuning, bercak, layu, keriting,
  bunga rontok, busuk ujung buah, dll) dengan kemungkinan penyebab & tindakan.
- **Kalender Tanam** — tanaman yang cocok ditanam per bulan + musimnya.
- **Usaha Tani** — catat biaya & hasil panen per petak, laba/rugi otomatis,
  dan **Bagikan Rekap** ke aplikasi lain (mis. WhatsApp).

## v1.2.0

- Semua emoji diganti ikon **Font Awesome Free Solid 6.5.2** (font dibundel
  lokal di `assets/fonts/`, offline) — komponen `FaIcon`, kode glif diverifikasi dari cmap font.
- Tampilan **minimalis**: header biru rata tanpa gradasi, kartu putih bergaris
  tipis tanpa bayangan, sudut 14dp, chip putih bergaris (biru saat aktif).
- Tab Info menjadi **Pengaturan**: profil nama, **Periksa Pembaruan** (cek rilis
  terbaru dari GitHub Releases repo ini; bila ada versi baru tampil catatan
  rilis + tombol Unduh APK / Halaman Rilis), Cara Pakai & Tentang Dosis.

## Teknis

- Package: `id.autodosingagro` • Kotlin + Jetpack Compose (Material 3)
- Build hanya via GitHub Actions; keystore tetap (JKS) di-pin sejak rilis
  pertama lewat secret `AUTODOSING_*`, fingerprint diverifikasi `apksigner`
  di CI sebelum APK diterbitkan.
