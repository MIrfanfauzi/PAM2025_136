# 📱 RotiBox - User Manual

> **Panduan Pengguna Lengkap Aplikasi RotiBox**  
> Aplikasi pemesanan roti berbasis Android dengan fitur manajemen menu, keranjang belanja, dan laporan penjualan.

---
## Tampilan Aplikasi
<img width="800" height="350" alt="1" src="https://github.com/user-attachments/assets/40901881-17ee-4674-85cb-229e97b1906b" />
<img width="800" height="350" alt="2" src="https://github.com/user-attachments/assets/37907a15-e58d-4cf4-85db-8539de9c5fac" />
<img width="800" height="350" alt="3" src="https://github.com/user-attachments/assets/b8e4942c-2fb4-42a2-9c89-68fb96c08ae4" />
<img width="800" height="350" alt="4" src="https://github.com/user-attachments/assets/851767a0-b759-4d95-b099-b5ee616671c0" />

## � Daftar Isi

- [Tentang Aplikasi](#tentang-aplikasi)
- [Fitur Utama](#fitur-utama)
- [Panduan Pelanggan](#panduan-pelanggan)
- [Panduan Admin](#panduan-admin)
- [Keamanan](#keamanan)
- [FAQ](#faq)

---

## 🎯 Tentang Aplikasi

**RotiBox** adalah aplikasi mobile berbasis Android untuk pemesanan roti secara online. Aplikasi ini memudahkan pelanggan untuk memesan roti favorit mereka dan membantu admin mengelola menu, pesanan, serta melihat laporan penjualan.

### Teknologi
- **Platform**: Android (Kotlin)
- **Database**: Room (SQLite)
- **UI**: Jetpack Compose
- **Security**: BCrypt password hashing

---

## ✨ Fitur Utama

### Untuk Pelanggan
✅ Registrasi dan Login  
✅ Melihat katalog menu roti  
✅ Menambahkan produk ke keranjang  
✅ Melakukan pemesanan  
✅ Melihat riwayat pesanan  
✅ Mengelola profil  

### Untuk Admin
✅ Dashboard dengan statistik real-time  
✅ Kelola menu (Tambah, Edit, Hapus)  
✅ Kelola pesanan dengan filter status  
✅ Export struk PDF  
✅ Laporan penjualan dengan export CSV  
✅ Kelola informasi kontak toko  

---

## 👤 Panduan Pelanggan

### 1. Registrasi & Login

#### Registrasi Akun Baru
1. Buka aplikasi RotiBox
2. Klik tombol **"Daftar"**
3. Isi formulir: Nama, Email, Password, Konfirmasi Password
4. Klik **"Daftar"**
5. Otomatis login dan masuk ke beranda

#### Login
1. Masukkan Email dan Password
2. Klik **"Masuk"**
3. Berhasil masuk ke beranda

### 2. Melihat & Memilih Menu

1. Di halaman **Beranda**, lihat katalog menu roti
2. Klik card menu untuk melihat detail
3. Pilih jumlah dengan tombol **[-]** dan **[+]**
4. Klik **"Tambah ke Keranjang"**

### 3. Keranjang Belanja

1. Klik icon **Keranjang** di bottom navigation
2. Lihat semua item yang ditambahkan
3. Ubah jumlah atau hapus item (icon 🗑️)
4. Klik **"Checkout"** untuk lanjut

### 4. Melakukan Pemesanan

1. Isi informasi: Nama, Telepon, Alamat
2. Pilih **Metode Pengiriman** (Antar/Ambil di Toko)
3. Pilih **Tanggal Pengantaran**
4. Pilih **Metode Pembayaran**
5. Tambahkan catatan (opsional)
6. Klik **"Buat Pesanan"**

### 5. Melihat Pesanan Saya

1. Klik tab **"Pesanan"**
2. Lihat semua pesanan dengan status:
   - 🟠 Menunggu Konfirmasi
   - 🔵 Dikonfirmasi
   - 🟢 Selesai
   - 🔴 Dibatalkan
3. Klik card untuk lihat detail

### 6. Profil

1. Klik tab **"Profil"**
2. Lihat informasi akun
3. Klik **"Logout"** untuk keluar

---

## 👨‍💼 Panduan Admin

### 1. Login Admin

**Kredensial:**
```
Email: admin@rotibox.com
Password: admin123
```

### 2. Dashboard Admin

Dashboard menampilkan 3 statistik:
- **Menu Aktif**: Jumlah menu yang dijual
- **Perlu Konfirmasi**: Pesanan pending
- **Pesanan Hari Ini**: Pesanan dengan tanggal pengantaran hari ini

### 3. Kelola Menu

#### Menambah Menu
1. Klik tombol **"+"**
2. Isi: Nama, Deskripsi, Harga, URL Gambar, Stok
3. Klik **"Simpan"**

#### Mengedit Menu
1. Klik icon **✏️** pada card menu
2. Edit field yang diperlukan
3. Klik **"Simpan"**

#### Toggle Status
- Klik **switch** untuk aktifkan/nonaktifkan menu

#### Menghapus Menu
- Klik icon **🗑️** untuk hapus (permanen)

### 4. Kelola Pesanan

#### Filter Pesanan
- **Semua**: Semua pesanan
- **Menunggu** (Default): Perlu konfirmasi
- **Proses**: Sedang diproses
- **Selesai**: Sudah selesai
- **Dibatalkan**: Pesanan batal

#### Update Status
1. Buka detail pesanan
2. Klik tombol sesuai status:
   - **"Konfirmasi Pesanan"**: Menunggu → Dikonfirmasi
   - **"Tandai Selesai"**: Dikonfirmasi → Selesai
   - **"Batalkan Pesanan"**: Any → Dibatalkan

#### Export Struk PDF
1. Di detail pesanan
2. Klik **"Export PDF Struk"**
3. PDF tersimpan di Downloads

### 5. Laporan Penjualan

#### Filter Periode
- **Hari Ini**
- **Minggu Ini**
- **Bulan Ini**
- **Custom** (pilih tanggal sendiri)

#### Metrik
- Total Pendapatan
- Total Pesanan
- Rata-rata/Pesanan

#### Export CSV
1. Klik **"Export CSV"**
2. File tersimpan di Downloads

### 6. Informasi Kontak

1. Klik **"Informasi Kontak Toko"**
2. Klik **"Edit"**
3. Ubah: Telepon, Alamat, Rekening Bank
4. Klik **"Simpan"**

---

## 🔒 Keamanan

### Password Security
- Password di-hash dengan **BCrypt** (cost factor 12)
- Password tersembunyi (●●●●) saat input
- Tidak disimpan dalam plain text

### Data Privacy
- Data tersimpan lokal di device (Room Database)
- Session management dengan auto-logout
- Konfirmasi sebelum logout

---

## ❓ FAQ

### Pelanggan

**Q: Bagaimana cara reset password?**  
A: Hubungi admin untuk bantuan reset password.

**Q: Apakah bisa edit pesanan setelah checkout?**  
A: Tidak. Hubungi admin untuk pembatalan.

**Q: Kenapa menu tidak muncul?**  
A: Menu mungkin dinonaktifkan atau stok habis.

### Admin

**Q: Apakah stok berkurang otomatis?**  
A: Tidak. Stok adalah batas maksimal pemesanan.

**Q: Bagaimana cara backup data?**  
A: Export laporan CSV secara berkala.

**Q: Bagaimana cara melihat pesanan hari ini?**  
A: Lihat di Dashboard → "Pesanan Hari Ini"

---

## � Kontak Support

- **Email**: admin@rotibox.com
- **Telepon**: Lihat di menu "Informasi Kontak Toko"

---

## 📝 Catatan Penting

> **Untuk Instalasi Pertama:**  
> Uninstall aplikasi lama → Install baru → Database dibuat otomatis

> **Keamanan:**  
> Jangan share password admin → Logout setelah selesai → Ganti password berkala

> **Best Practices:**  
> Backup data berkala → Update stok menu → Konfirmasi pesanan cepat

---

## 🎉 Selamat Menggunakan RotiBox!

**Version**: 1.0.0  
**Last Updated**: Januari 2026  
**Platform**: Android 8.0+

---

*Dokumentasi ini akan terus diperbarui seiring dengan penambahan fitur baru.*
