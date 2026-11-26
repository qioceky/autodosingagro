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

## v1.12.0

Menu Utama dipertegas sesuai dokumen sistem (SISTEM_APLIKASI_PERTANIAN):

- **Menu Utama sekarang persis 6 menu dokumen**: Jenis Tanaman, Organisme
  Perusak Tanaman, Alur Budidaya, Kalkulator Dosis, Diagnosis Tanaman,
  Pengaturan. (Jadwal Pupuk tetap bisa dibuka dari kartu Kegiatan Hari Ini
  di Beranda; Dosis Pupuk menyatu ke Kalkulator Dosis.)
- **Menu baru Organisme Perusak Tanaman**: 10 OPT utama (wereng coklat, ulat
  grayak, kutu daun, penggerek batang, lalat buah, blast, hawar daun bakteri,
  layu fusarium, antraknosa, virus kuning) — gejala, pencegahan &
  pengendalian, 7 di antaranya bergambar (Wikimedia Commons CC).
- **Menu baru Alur Budidaya**: 8 tahapan dari persiapan benih sampai
  pascapanen, 4 tahap bergambar.
- **5 tanaman baru sesuai dokumen**: Paria/Pare, Labu Madu, Semangka, Melon,
  Sawi Hijau — lengkap dengan fase pertumbuhan, jadwal pupuk, foto asli &
  kalender tanam (total 13 tanaman bawaan).
- **Diagnosis Tanaman**: tombol **Scan / Foto Tanaman Rusak** (kamera) —
  foto ditampilkan untuk dicocokkan dengan daftar gejala.
- **Beranda**: strip **Tanaman yang Sedang Diamati** (progres fase tanaman
  Pantau), dan deretan Kategori Tanaman kini **bergerak otomatis** ke kanan.
- **Notifikasi H-1 kini default menyala** (sesuai dokumen: notifikasi muncul
  1 hari sebelum waktu pemupukan).

## v1.11.0

- **Kotak Menu Utama seragam + tema tanaman**: semua kotak sekarang tinggi
  sama (108dp), latar hijau muda, garis tepi hijau, ikon di lingkaran putih
  hijau, label hijau tua tebal.
- **Kartu Progres Pertumbuhan di Beranda diganti Informasi Cuaca**
  (Open-Meteo, gratis tanpa API key): suhu & kondisi sekarang, rentang suhu
  hari ini, kelembapan, angin, peluang hujan — plus **saran kegiatan tani**
  sesuai cuaca (mis. peluang hujan besar → tunda pemupukan). Kota bisa diganti
  (pencarian kota/kabupaten), ada tombol segarkan, dan data terakhir
  tersimpan sebagai cache saat offline. Default: Jakarta.
- **Kartu Perhatian diganti Kegiatan Hari Ini** (kotak notifikasi di Beranda):
  daftar kegiatan yang harus dilakukan — pupuk HARI INI (dengan dosisnya),
  pupuk besok (siapkan), panen hari ini / ≤3 hari, lewat masa panen, dan
  catatan pertumbuhan yang tertunda ≥7 hari, masing-masing dengan ikonnya.

## v1.10.0

- **Tombol Tes Notifikasi** di Pengaturan → Pengingat Pupuk: mengirim
  notifikasi contoh saat itu juga ("Waktunya pupuk: Contoh — Petak 1…") supaya
  bisa memastikan izin & tampilannya beres tanpa menunggu jadwal beneran.
  Bila izin belum diberikan, tombol ini meminta izin dulu.

## v1.9.0

- **Jam pengingat sekarang bisa custom bebas** — selain 8 chip preset, ada
  tombol **Atur Jam Sendiri** yang membuka pemilih jam (time picker 24 jam):
  jam & menit berapa pun bisa dipilih (mis. 06.45 atau 19.30). Berlaku untuk
  pengingat hari-H dan H-1.

## v1.8.0

- **Pengingat pupuk sekarang bisa diatur lengkap** (Pengaturan → Pengingat
  Pupuk): **jam pengingat bisa dipilih** (05.00 / 05.30 / 06.00 / 06.30 /
  07.00 / 08.00 / 16.00 / 17.00, default 07.00) dan ada saklar **Pengingat
  H-1** — notifikasi tambahan sehari sebelumnya di jam yang sama ("Besok
  waktunya pupuk… siapkan dari sekarang"). Alarm hari-H & H-1 dijadwalkan
  ulang otomatis setiap setelan berubah. Kartu status di menu Jadwal Pupuk
  menampilkan setelan yang sedang aktif.

## v1.7.0

- **Menu Utama dipangkas jadi 6 fokus**: Jenis Tanaman, Dosis Pupuk, Jadwal
  Pupuk, Kalkulator Dosis, Diagnosa Gejala, Pengaturan. (Fitur lain tetap ada
  lewat tab bawah: Pantau untuk umur/catat tumbuh, dsb.)
- **Menu Jadwal Pupuk sekarang layar khusus**: timeline semua aplikasi pupuk
  per petak lengkap dengan tanggal kalender & status (sudah lewat / HARI INI /
  n hari lagi).
- **Notifikasi pengingat pupuk**: alarm jam 07.00 pagi di hari pemupukan
  (judul petak + pupuk & dosisnya), dijadwalkan ulang otomatis setiap aplikasi
  dibuka, setiap data tanaman berubah, dan setelah HP menyala ulang (boot
  receiver). Saklar on/off + permintaan izin notifikasi ada di Pengaturan.

## v1.6.0

- **Foto tanaman kustom bisa diatur sendiri** — di form Tambah Tanaman Baru,
  bagian Foto/Ikon sekarang punya 3 pilihan: (1) foto bawaan (8 foto tanaman),
  (2) **foto dari galeri HP sendiri** (dipangkas persegi 512px & disimpan di
  penyimpanan internal aplikasi), (3) ikon garis seperti sebelumnya.
  Foto galeri ikut terhapus otomatis bila tanaman kustomnya dihapus.

## v1.5.0

- **Ikon tanaman diganti FOTO tanaman asli** — foto jauh lebih gampang dikenali
  (dan lebih edukatif) daripada ikon garis. 8 foto tanaman bawaan dari
  Wikimedia Commons / Flickr berlisensi bebas (CC BY / CC BY-SA), dipotong
  persegi 512px (total ±517 KB, tetap ringan & offline). Ikon vektor IconOven
  tetap dipakai untuk tanaman kustom & cadangan.
- Kredit foto: Padi — kontributor Wikimedia Commons (CC BY-SA 4.0);
  Jagung — theilr (CC BY-SA); Kedelai — United Soybean Board (CC BY);
  Cabai Merah — Johnnybam (CC BY-SA 4.0); Cabai Rawit — Prathvi Acharya
  (CC BY-SA 4.0); Tomat — Steffen Bauer / saatpilot.de (CC BY 4.0);
  Bawang Merah — Evan-Amos (CC BY-SA 3.0); Mentimun — OakleyOriginals (CC BY).

## v1.4.0

- **Tambah Tanaman Baru** — di menu Jenis Tanaman sekarang ada tombol tambah:
  pengguna bisa membuat tanaman sendiri (nama, nama latin opsional, kategori,
  pilih ikon dari 8 ikon tanaman, umur panen HST, jarak tanam, populasi/ha,
  deskripsi). Fase pertumbuhan (4 fase) & jadwal pupuk umum (dasar + 2 susulan)
  dibuat otomatis mengikuti umur panennya. Tanaman kustom tersimpan lokal dan
  langsung bisa dipakai di Pantau, Kalkulator Dosis & Kalender Tanam; bisa
  dihapus lagi dari halaman detailnya.

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
