# Bahan Presentasi — Ngempe Commerce & MRP

Dokumen ini merangkum fungsi aplikasi, struktur kode, alasan pemakaian konsep PBO, dan status requirement berdasarkan implementasi saat ini. Status yang belum terpenuhi ditulis terbuka agar materi presentasi sesuai dengan kode.

## 1. Gambaran aplikasi

**Ngempe Commerce & MRP** adalah aplikasi desktop Java Swing untuk alur pembeli dan operasional usaha: melihat produk, mengelola keranjang dan pesanan, mengatur stok, membuat BOM dan jadwal produksi, melakukan pembelian bahan baku dari vendor, serta melihat data keuangan.

Topik tersebut terlihat dari nama aplikasi di [`App.java`](./app/src/main/java/tubes/prak/pbo/App.java) dan fitur-fitur pada folder [`view`](./app/src/main/java/tubes/prak/pbo/view). Apakah topik ini berbeda dari kelompok lain perlu dibandingkan dengan topik yang dipilih kelompok lain; hal itu tidak dapat dipastikan dari repository saja. Aplikasi ini bukan kiosk McDonald's.

### Alur demo yang disarankan

1. Tampilkan halaman login dan jelaskan autentikasi pengguna.
2. Tampilkan etalase produk untuk role pembeli, lalu buka detail atau keranjang.
3. Tampilkan halaman operasional staff, seperti Warehouse atau Produksi.
4. Jelaskan BOM, perubahan status produksi, dan pengaruh alur itu terhadap stok.
5. Tampilkan Purchase Order atau Finance sebagai contoh alur operasional lain.

## 2. Struktur program

| Bagian | Lokasi | Tanggung jawab |
|---|---|---|
| Entry point dan jendela utama | [`App.java`](./app/src/main/java/tubes/prak/pbo/App.java) | Menjalankan aplikasi Swing dan menampilkan halaman awal. |
| View / GUI | [`view`](./app/src/main/java/tubes/prak/pbo/view) dan [`components`](./app/src/main/java/tubes/prak/pbo/components) | Membuat form, halaman, daftar, tombol, dan kartu data. |
| Controller | [`controller`](./app/src/main/java/tubes/prak/pbo/controller) | Mengatur validasi serta alur antara GUI, model, dan repository. |
| Repository | [`repositories`](./app/src/main/java/tubes/prak/pbo/repositories) | Membaca dan menulis data melalui SQL/JDBC. |
| Model | [`models`](./app/src/main/java/tubes/prak/pbo/models) | Merepresentasikan pengguna, produk, pesanan, produksi, vendor, dan data bisnis lainnya. |
| Kontrak/interface | [`Interfaces`](./app/src/main/java/tubes/prak/pbo/Interfaces) | Mendefinisikan kemampuan yang dapat dipakai atau diimplementasikan kelas lain. |

Pemisahan ini memudahkan presenter menunjukkan bahwa tampilan, aturan alur aplikasi, dan akses database tidak semuanya ditumpuk dalam satu kelas.

## 3. Requirement dan lokasi implementasi

| Requirement | Status saat ini | Lokasi dan alasan |
|---|---|---|
| Memilih topik aplikasi | **Ada; keunikan antarkelompok perlu dikonfirmasi** | Aplikasi mencakup commerce dan MRP: etalase, pesanan, stok, BOM, produksi, purchasing, dan finance. Topik kelompok lain tidak tersedia di repository. |
| GUI | **Terpenuhi** | GUI memakai Swing. [`App.java`](./app/src/main/java/tubes/prak/pbo/App.java) adalah entry point; halaman utama dibangun dari `JPanel` dan navigasi `CardLayout`, misalnya di [`MainShell.java`](./app/src/main/java/tubes/prak/pbo/view/MainShell.java). Swing dipakai agar pengguna mengakses fungsi aplikasi melalui komponen visual. |
| Database | **Terpenuhi** | [`DatabaseConfig.java`](./app/src/main/java/tubes/prak/pbo/config/DatabaseConfig.java) menyediakan koneksi JDBC; repository seperti [`ProdukRepository.java`](./app/src/main/java/tubes/prak/pbo/repositories/ProdukRepository.java) menjalankan query. Repository memisahkan operasi data dari tampilan. |
| Minimal 2 interface | **Terpenuhi** | Ada beberapa interface: [`StoreAble.java`](./app/src/main/java/tubes/prak/pbo/Interfaces/StoreAble.java), [`SellAble.java`](./app/src/main/java/tubes/prak/pbo/Interfaces/SellAble.java), [`PurchaseAble.java`](./app/src/main/java/tubes/prak/pbo/Interfaces/PurchaseAble.java), [`ItemService.java`](./app/src/main/java/tubes/prak/pbo/Interfaces/ItemService.java), [`ProductionService.java`](./app/src/main/java/tubes/prak/pbo/Interfaces/ProductionService.java), [`ProductionDataSource.java`](./app/src/main/java/tubes/prak/pbo/Interfaces/ProductionDataSource.java), dan [`UserPanelVisitor.java`](./app/src/main/java/tubes/prak/pbo/Interfaces/UserPanelVisitor.java). Interface membatasi kontrak sesuai kebutuhan kelas pemakainya. |
| Minimal 2 abstract class | **Terpenuhi** | Contoh: [`Item.java`](./app/src/main/java/tubes/prak/pbo/models/productRelated/Item.java), [`User.java`](./app/src/main/java/tubes/prak/pbo/models/users/User.java), [`UserStaff.java`](./app/src/main/java/tubes/prak/pbo/models/users/UserStaff.java), dan [`BaseCard.java`](./app/src/main/java/tubes/prak/pbo/components/base/BaseCard.java). Abstract class berbagi data/perilaku umum tanpa harus menjadi objek konkret. |
| Minimal 2 generasi inheritance | **Terpenuhi** | [`User`](./app/src/main/java/tubes/prak/pbo/models/users/User.java) → [`UserStaff`](./app/src/main/java/tubes/prak/pbo/models/users/UserStaff.java) → [`UserManager`](./app/src/main/java/tubes/prak/pbo/models/users/UserManager.java) atau [`UserWorker`](./app/src/main/java/tubes/prak/pbo/models/users/UserWorker.java). Rantai tersebut memiliki lebih dari satu tingkat pewarisan dan merepresentasikan hak umum staff serta variasi role. `Item` juga menjadi induk `Produk` dan `RawMaterial`. |
| Minimal 1 generic | **Terpenuhi** | [`CardListPanel<T>.java`](./app/src/main/java/tubes/prak/pbo/components/base/CardListPanel.java) dapat menampilkan daftar berbagai jenis model dengan komponen kartu yang sesuai. Generic mengurangi duplikasi daftar khusus per tipe data. |
| Login atau minimal 3 role | **Terpenuhi** | Login ada di [`LoginView.java`](./app/src/main/java/tubes/prak/pbo/view/LoginView.java) dan [`LoginController.java`](./app/src/main/java/tubes/prak/pbo/controller/LoginController.java). Role `PEMBELI`, `MANAGER`, dan `WORKER` didefinisikan di [`Role.java`](./app/src/main/java/tubes/prak/pbo/ENUM/Role.java). [`UserRepository.java`](./app/src/main/java/tubes/prak/pbo/repositories/UserRepository.java) membentuk tipe pengguna berdasarkan role; [`MainShell.java`](./app/src/main/java/tubes/prak/pbo/view/MainShell.java) menyiapkan halaman staff berdasarkan hak aksesnya. |
| Exception handling dan custom exception | **Terpenuhi** | [`InvalidCredentialsException.java`](./app/src/main/java/tubes/prak/pbo/exception/InvalidCredentialsException.java) adalah custom exception untuk kredensial tidak valid. Login melempar error melalui controller dan menanganinya di view. Operasi database juga menggunakan `SQLException`. |
| SOLID | **Ada penerapan yang dapat ditunjukkan** | Contoh dijelaskan pada bagian SOLID di bawah. Ini menunjukkan pemisahan tanggung jawab dan penggunaan kontrak; bukan klaim bahwa setiap kelas sudah sempurna terhadap semua prinsip. |
| Minimal 3 design pattern | **Terpenuhi dengan contoh yang jelas** | MVC, Repository, dan Visitor. Lihat bagian design pattern di bawah. |
| Konfigurasi per pengguna yang di-gitignore | **Belum terpenuhi sepenuhnya** | [`DatabaseConfig.java`](./app/src/main/java/tubes/prak/pbo/config/DatabaseConfig.java) menyimpan pengaturan koneksi di source code. `.gitignore` berisi pola `config/DatabaseConfig.java`, tetapi file aktual berada di `app/src/main/java/tubes/prak/pbo/config/DatabaseConfig.java`; pola itu tidak mengabaikan file aktual. Jadi pengaturan belum benar-benar terpisah untuk tiap pengguna. |
| Tidak memakai `JOptionPane` atau dialog | **Belum terpenuhi** | Banyak view masih memanggil `JOptionPane` untuk notifikasi, validasi, dan konfirmasi. `JOptionPane` menampilkan dialog, sehingga tetap bertentangan dengan larangan tersebut meskipun form tambah Vendor sudah inline. |
| Tidak membuat jendela tambahan | **Perlu diperhatikan** | Jendela utama aplikasi adalah [`App.java`](./app/src/main/java/tubes/prak/pbo/App.java). Ada juga [`TestApp.java`](./app/src/main/java/tubes/prak/pbo/TestApp.java) yang membuat `JFrame` untuk preview kartu. Tidak ditemukan pemakaian langsung kelas `JDialog`, tetapi pemakaian `JOptionPane` tetap menampilkan dialog. |
| Tidak memakai tag HTML | **Terpenuhi berdasarkan pemeriksaan source Java** | Tidak ditemukan tag `<html>` pada source Java yang diperiksa. |

### Lokasi `JOptionPane` yang perlu disebut saat menjelaskan status

Pemanggilan ditemukan di [`LoginView.java`](./app/src/main/java/tubes/prak/pbo/view/LoginView.java), [`RegisterView.java`](./app/src/main/java/tubes/prak/pbo/view/RegisterView.java), [`FormProduksiPanel.java`](./app/src/main/java/tubes/prak/pbo/components/panels/FormProduksiPanel.java), [`DetailProduksiPanel.java`](./app/src/main/java/tubes/prak/pbo/components/panels/DetailProduksiPanel.java), [`FinanceView.java`](./app/src/main/java/tubes/prak/pbo/view/ViewManager/FinanceView.java), [`BomView.java`](./app/src/main/java/tubes/prak/pbo/view/ViewManager/BomView.java), [`PurchaseOrderView.java`](./app/src/main/java/tubes/prak/pbo/view/ViewManager/PurchaseOrderView.java), [`WarehouseView.java`](./app/src/main/java/tubes/prak/pbo/view/ViewManager/WarehouseView.java), [`ProduksiView.java`](./app/src/main/java/tubes/prak/pbo/view/ViewManager/ProduksiView.java), [`SalesOrderView.java`](./app/src/main/java/tubes/prak/pbo/view/ViewManager/SalesOrderView.java), [`RegisterItemView.java`](./app/src/main/java/tubes/prak/pbo/view/ViewManager/RegisterItemView.java), [`RiwayatTransaksiView.java`](./app/src/main/java/tubes/prak/pbo/view/ViewPembeli/RiwayatTransaksiView.java), [`Keranjang.java`](./app/src/main/java/tubes/prak/pbo/view/ViewPembeli/Keranjang.java), [`DetailProduk.java`](./app/src/main/java/tubes/prak/pbo/view/ViewPembeli/DetailProduk.java), dan [`EtalaseView.java`](./app/src/main/java/tubes/prak/pbo/view/ViewPembeli/EtalaseView.java).

## 4. Contoh SOLID yang bisa dijelaskan

- **Single Responsibility Principle (SRP):** view menangani tampilan dan input, controller menangani alur aplikasi, sedangkan repository menangani query database. Contohnya [`LoginView.java`](./app/src/main/java/tubes/prak/pbo/view/LoginView.java), [`LoginController.java`](./app/src/main/java/tubes/prak/pbo/controller/LoginController.java), dan [`UserRepository.java`](./app/src/main/java/tubes/prak/pbo/repositories/UserRepository.java).
- **Open/Closed Principle (OCP):** perilaku panel berdasarkan tipe pengguna dipisahkan melalui [`UserPanelVisitor`](./app/src/main/java/tubes/prak/pbo/Interfaces/UserPanelVisitor.java) dan implementasinya di [`MainShell.java`](./app/src/main/java/tubes/prak/pbo/view/MainShell.java). Penambahan variasi perilaku dapat diarahkan melalui kontrak tersebut.
- **Liskov Substitution Principle (LSP):** `UserManager` dan `UserWorker` adalah turunan `UserStaff`, lalu dapat diproses sebagai `User`/`UserStaff` dalam alur login dan pembangunan shell.
- **Interface Segregation Principle (ISP):** interface dibagi menurut kemampuan, misalnya `SellAble`, `PurchaseAble`, dan `StoreAble`, alih-alih mewajibkan semua item mengimplementasikan satu kontrak besar.
- **Dependency Inversion Principle (DIP):** alur produksi menggunakan interface layanan. [`ProduksiController.java`](./app/src/main/java/tubes/prak/pbo/controller/ProduksiController.java) menerima `ItemService` dan `ProductionDataSource`, sehingga logika controller tidak harus terikat pada satu implementasi konkret.

## 5. Design pattern yang digunakan

1. **MVC (Model–View–Controller)**  
   Model berada di [`models`](./app/src/main/java/tubes/prak/pbo/models), tampilan di [`view`](./app/src/main/java/tubes/prak/pbo/view), dan pengatur alur di [`controller`](./app/src/main/java/tubes/prak/pbo/controller). Pemisahan ini membuat perubahan tampilan tidak perlu sekaligus mengubah query SQL.
2. **Repository**  
   Kelas seperti [`ProdukRepository.java`](./app/src/main/java/tubes/prak/pbo/repositories/ProdukRepository.java) dan [`UserRepository.java`](./app/src/main/java/tubes/prak/pbo/repositories/UserRepository.java) mengisolasi akses database. Controller/view menggunakan operasi data, bukan menulis query di setiap komponen GUI.
3. **Visitor**  
   [`UserPanelVisitor.java`](./app/src/main/java/tubes/prak/pbo/Interfaces/UserPanelVisitor.java) mendefinisikan operasi untuk membangun panel pembeli dan staff. Subclass user memanggil `accept`, lalu [`MainShell.java`](./app/src/main/java/tubes/prak/pbo/view/MainShell.java) membangun halaman yang sesuai. Pola ini memisahkan variasi pembangunan UI dari kelas model user.

## 6. Catatan untuk file `ProductCard.java`

[`components/ProductCard.java`](./app/src/main/java/tubes/prak/pbo/components/ProductCard.java) adalah `JPanel` yang menerima satu model `Produk`. Kartu tersebut menampilkan gambar, nama, harga, stok, dan tombol detail. `loadScaledImage` mengecek path file dan ukuran gambar sebelum membuat gambar berskala; `getDetailButton()` memungkinkan pemanggil memasang aksi navigasi.

Ada dua kelas dengan nama sederhana `ProductCard`. Etalase saat ini mengimpor [`components/cards/ProductCard.java`](./app/src/main/java/tubes/prak/pbo/components/cards/ProductCard.java), bukan `components/ProductCard.java` yang ditandai. Karena itu, saat presentasi jangan menyebut file bertanda sebagai kartu yang sedang dipakai Etalase; jelaskan bahwa ia adalah implementasi komponen terpisah dan tunjukkan versi `components/cards` untuk alur etalase aktual.

## 7. Ringkasan narasi presentasi

> “Aplikasi Ngempe Commerce & MRP dibuat dengan Java Swing untuk menghubungkan proses pembelian, persediaan, produksi, dan penjualan. Kode dipisahkan menjadi view, controller, model, dan repository agar tanggung jawab tiap bagian lebih jelas. Pengguna masuk melalui login dan diarahkan ke fitur berdasarkan role. Dalam implementasinya kami menggunakan interface, abstract class, inheritance, generic, custom exception, serta pola MVC, Repository, dan Visitor. Ada dua hal yang masih perlu dibereskan agar sesuai seluruh spesifikasi: pemakaian `JOptionPane` di sejumlah halaman dan pemisahan konfigurasi database per pengguna yang benar-benar diabaikan Git.”

## 8. Pekerjaan lanjutan agar semua requirement benar-benar terpenuhi

1. Pindahkan konfigurasi koneksi database dari source code ke konfigurasi lokal atau environment variable; pastikan file/konfigurasi lokal tersebut benar-benar cocok dengan pola `.gitignore`.
2. Ganti seluruh notifikasi dan konfirmasi `JOptionPane` dengan komponen inline di halaman yang sudah ada, sesuai batasan tugas.
3. Pastikan apakah `TestApp.java` termasuk source yang dinilai. Jika larangan jendela juga berlaku pada kode demo/test, sesuaikan atau keluarkan file tersebut dari source yang dikumpulkan.
