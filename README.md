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
   luas petak (m²) dan jumlah tanaman → dosis nyata tampil dalam kg/gram
   per petak dan gram per tanaman, termasuk aplikasi berikutnya & hitung mundur harinya.
4. **Pantau Pertumbuhan** — tambah tanaman/petak, progres ke panen, pengingat
   pupuk berikutnya, dan log pertumbuhan (tinggi cm, jumlah daun, kondisi, catatan).

Semua data tersimpan lokal di HP. Tanpa internet, tanpa akun.

> Dosis adalah acuan umum praktik ATPH. Kebutuhan nyata menyesuaikan uji
> tanah, varietas & kondisi lahan — konsultasikan dengan guru/penyuluh setempat.

## Teknis

- Package: `id.autodosingagro` • Kotlin + Jetpack Compose (Material 3)
- Build hanya via GitHub Actions; keystore tetap (JKS) di-pin sejak rilis
  pertama lewat secret `AUTODOSING_*`, fingerprint diverifikasi `apksigner`
  di CI sebelum APK diterbitkan.
