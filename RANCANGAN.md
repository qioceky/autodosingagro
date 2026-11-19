# Rancangan — Auto Dosing Agro Technology v1.0.0

- **Untuk:** SMK jurusan ATPH (Agribisnis Tanaman Pangan dan Hortikultura)
- **Deskripsi:** memantau pertumbuhan tanaman serta dosis pemupukan sesuai umur tanaman
- **Referensi tampilan:** screenshot aplikasi belajar biru (header "Halo …",
  kartu Progres Belajar gelap + lingkaran %, grid Menu Utama 4 kolom,
  kartu kategori geser, Aktifitas Harian, bottom nav + tombol tengah besar)
  → diterjemahkan: Progres Belajar → Progres Pertumbuhan, Kategori Kelas →
  Kategori Tanaman, tombol tengah → Kalkulator Dosis.

## Layar

| Tab | Isi |
|---|---|
| Beranda | Header, kartu progres tanaman utama, Menu Utama (8), Kategori Tanaman, Aktifitas Harian |
| Tanaman | Daftar 8 jenis tanaman → detail fase (umur) + jadwal & dosis pupuk + total semusim |
| Dosis (tombol tengah) | Kalkulator: jenis + umur HST/tanggal tanam + luas + jumlah tanaman → dosis per aplikasi |
| Pantau | Tanaman Saya (HST & fase otomatis, pupuk berikutnya) + log tinggi/daun/kondisi |
| Info | Profil nama, cara pakai, penjelasan dosis & HST |

## Data dosis (kg/ha, acuan umum)

Padi Urea 225/SP-36 100/KCl 100 (3×) • Jagung Urea 350/SP-36 150/KCl 100 +
kandang 2.000 • Kedelai Urea 50/SP-36 100/KCl 100 • Cabai Merah kandang
10.000 + NPK bertahap + KCl • Cabai Rawit kandang 8.000 + NPK bertahap •
Tomat kandang 10.000 + NPK/KCl bertahap • Bawang Merah kandang 5.000 +
SP-36 200/KCl 200/ZA 300 • Mentimun kandang 5.000 + NPK bertahap.

Konversi kalkulator: kg untuk petak = kg/ha × luas(m²)/10.000;
gram/tanaman = kg/ha × 1.000 / populasi per ha.
