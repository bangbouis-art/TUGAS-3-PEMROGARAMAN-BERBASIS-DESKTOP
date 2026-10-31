import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

// =========================================================================
// 1. CUSTOM EXCEPTION
// =========================================================================
class ItemTidakDitemukanException extends Exception {
    private static final long serialVersionUID = 1L;

    public ItemTidakDitemukanException(String message) {
        super(message);
    }
}

// =========================================================================
// 2. KELAS ABSTRAK MENU ITEM (Inheritance & Encapsulation)
// =========================================================================
abstract class MenuItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nama;
    private double harga;
    private String kategori;

    public MenuItem(String nama, double harga, String kategori) {
        this.nama = nama;
        this.harga = harga;
        this.kategori = kategori;
    }

    // Getter dan Setter
    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    // Metode abstrak
    public abstract void tampilMenu();
}

// =========================================================================
// 3. KELAS TURUNAN: MAKANAN
// =========================================================================
class Makanan extends MenuItem {
    private static final long serialVersionUID = 1L;
    private String jenisMakanan;

    public Makanan(String nama, double harga, String kategori, String jenisMakanan) {
        super(nama, harga, kategori);
        this.jenisMakanan = jenisMakanan;
    }

    public String getJenisMakanan() {
        return jenisMakanan;
    }

    public void setJenisMakanan(String jenisMakanan) {
        this.jenisMakanan = jenisMakanan;
    }

    @Override
    public void tampilMenu() {
        System.out.printf("[Makanan] %s | Jenis: %s | Kategori: %s | Harga: Rp%.2f\n", 
                getNama(), jenisMakanan, getKategori(), getHarga());
    }
}

// =========================================================================
// 4. KELAS TURUNAN: MINUMAN
// =========================================================================
class Minuman extends MenuItem {
    private static final long serialVersionUID = 1L;
    private String jenisMinuman;

    public Minuman(String nama, double harga, String kategori, String jenisMinuman) {
        super(nama, harga, kategori);
        this.jenisMinuman = jenisMinuman;
    }

    public String getJenisMinuman() {
        return jenisMinuman;
    }

    public void setJenisMinuman(String jenisMinuman) {
        this.jenisMinuman = jenisMinuman;
    }

    @Override
    public void tampilMenu() {
        System.out.printf("[Minuman] %s | Jenis: %s | Kategori: %s | Harga: Rp%.2f\n", 
                getNama(), jenisMinuman, getKategori(), getHarga());
    }
}

// =========================================================================
// 5. KELAS TURUNAN: DISKON
// =========================================================================
class Diskon extends MenuItem {
    private static final long serialVersionUID = 1L;
    private double persentaseDiskon;

    public Diskon(String nama, double hargaDasar, String kategori, double persentaseDiskon) {
        super(nama, hargaDasar, kategori);
        this.persentaseDiskon = persentaseDiskon;
    }

    public double getPersentaseDiskon() {
        return persentaseDiskon;
    }

    public void setPersentaseDiskon(double persentaseDiskon) {
        this.persentaseDiskon = persentaseDiskon;
    }

    @Override
    public void tampilMenu() {
        System.out.printf("[Diskon] %s | Potongan: %.1f%% | Kategori: %s | Nilai Acuan: Rp%.2f\n", 
                getNama(), persentaseDiskon, getKategori(), getHarga());
    }
}

// =========================================================================
// 6. KELAS MENU (Pengelola Item & File I/O)
// =========================================================================
class Menu {
    private ArrayList<MenuItem> daftarItem;

    public Menu() {
        this.daftarItem = new ArrayList<>();
    }

    public void tambahItem(MenuItem item) {
        daftarItem.add(item);
    }

    public ArrayList<MenuItem> getDaftarItem() {
        return daftarItem;
    }

    public void tampilkanSemuaMenu() {
        if (daftarItem.isEmpty()) {
            System.out.println("Menu restoran masih kosong.");
            return;
        }
        System.out.println("================ DAFTAR MENU RESTORAN ================");
        for (int i = 0; i < daftarItem.size(); i++) {
            System.out.print((i + 1) + ". ");
            daftarItem.get(i).tampilMenu();
        }
        System.out.println("======================================================");
    }

    public MenuItem cariItem(String nama) throws ItemTidakDitemukanException {
        for (MenuItem item : daftarItem) {
            if (item.getNama().equalsIgnoreCase(nama)) {
                return item;
            }
        }
        throw new ItemTidakDitemukanException("Item menu dengan nama '" + nama + "' tidak ditemukan!");
    }

    public void simpanMenuKeFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(daftarItem);
            System.out.println("Daftar menu berhasil disimpan ke file " + filename);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan menu: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void muatMenuDariFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            daftarItem = (ArrayList<MenuItem>) ois.readObject();
            System.out.println("Daftar menu berhasil dimuat dari file " + filename);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Gagal memuat menu: " + e.getMessage());
        }
    }
}

// =========================================================================
// 7. KELAS PESANAN (Pencatatan Pesanan & Struk)
// =========================================================================
class Pesanan {
    private ArrayList<MenuItem> itemDipesan;
    private Diskon diskonTerapan;

    public Pesanan() {
        this.itemDipesan = new ArrayList<>();
        this.diskonTerapan = null;
    }

    public void tambahItemPesanan(MenuItem item) {
        if (item instanceof Diskon) {
            this.diskonTerapan = (Diskon) item;
            System.out.println("Diskon '" + item.getNama() + "' berhasil diterapkan pada pesanan.");
        } else {
            itemDipesan.add(item);
            System.out.println("'" + item.getNama() + "' ditambahkan ke pesanan.");
        }
    }

    public double hitungSubtotal() {
        double subtotal = 0;
        for (MenuItem item : itemDipesan) {
            subtotal += item.getHarga();
        }
        return subtotal;
    }

    public double hitungTotalAkhir() {
        double subtotal = hitungSubtotal();
        if (diskonTerapan != null) {
            double potongan = subtotal * (diskonTerapan.getPersentaseDiskon() / 100.0);
            return subtotal - potongan;
        }
        return subtotal;
    }

    public void cetakStruk() {
        if (itemDipesan.isEmpty()) {
            System.out.println("Belum ada item makanan/minuman dalam pesanan.");
            return;
        }
        
        StringBuilder struk = new StringBuilder();
        struk.append("\n================== STRUK PESANAN PELANGGAN ==================\n");
        for (MenuItem item : itemDipesan) {
            struk.append(String.format("- %-20s : Rp%.2f\n", item.getNama(), item.getHarga()));
        }
        struk.append("-------------------------------------------------------------\n");
        double subtotal = hitungSubtotal();
        struk.append(String.format("Subtotal\t\t\t: Rp%.2f\n", subtotal));
        
        if (diskonTerapan != null) {
            double nilaiDiskon = subtotal * (diskonTerapan.getPersentaseDiskon() / 100.0);
            struk.append(String.format("Diskon (%s - %.1f%%)\t: -Rp%.2f\n", 
                    diskonTerapan.getNama(), diskonTerapan.getPersentaseDiskon(), nilaiDiskon));
        } else {
            struk.append("Diskon\t\t\t: Tidak ada\n");
        }
        
        struk.append(String.format("Total Biaya Akhir\t\t: Rp%.2f\n", hitungTotalAkhir()));
        struk.append("============================================================-\n");

        System.out.println(struk.toString());
        simpanStrukKeFile(struk.toString());
    }

    private void simpanStrukKeFile(String isiStruk) {
        String filename = "struk_pesanan.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(isiStruk);
            writer.newLine();
            System.out.println("Struk berhasil dicatat dan disimpan ke file " + filename);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan file struk: " + e.getMessage());
        }
    }
}

// =========================================================================
// 8. KELAS UTAMA (Main Application)
// =========================================================================
public class RestoranApp {
    private static final String MENU_FILE = "menu_restoran.dat";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Menu restoranMenu = new Menu();
        restoranMenu.muatMenuDariFile(MENU_FILE);

        Pesanan pesananAktif = null;
        int pilihan = 0;

        do {
            System.out.println("\n========== APLIKASI MANAJEMEN RESTORAN ==========");
            System.out.println("1. Tambah Item Baru ke Menu");
            System.out.println("2. Tampilkan Menu Restoran");
            System.out.println("3. Buat Pesanan Baru");
            System.out.println("4. Tambah Item ke Pesanan Aktif");
            System.out.println("5. Tampilkan Struk & Hitung Total Pesanan");
            System.out.println("6. Simpan Menu ke File");
            System.out.println("7. Keluar");
            System.out.print("Pilih menu (1-7): ");

            try {
                pilihan = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Masukan tidak valid! Harap masukkan angka.");
                continue;
            }

            switch (pilihan) {
                case 1:
                    System.out.println("\n--- Pilih Jenis Item ---");
                    System.out.println("1. Makanan");
                    System.out.println("2. Minuman");
                    System.out.println("3. Diskon");
                    System.out.print("Pilihan: ");
                    int jenis = 0;
                    try {
                        jenis = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Pilihan tidak valid.");
                        break;
                    }

                    System.out.print("Masukkan Nama Item: ");
                    String nama = scanner.nextLine();
                    System.out.print("Masukkan Harga (atau nilai acuan diskon): ");
                    double harga = Double.parseDouble(scanner.nextLine());
                    System.out.print("Masukkan Kategori: ");
                    String kategori = scanner.nextLine();

                    if (jenis == 1) {
                        System.out.print("Masukkan Jenis Makanan (cth: Berat/Ringan): ");
                        String jenisMakanan = scanner.nextLine();
                        restoranMenu.tambahItem(new Makanan(nama, harga, kategori, jenisMakanan));
                        System.out.println("Item Makanan berhasil ditambahkan!");
                    } else if (jenis == 2) {
                        System.out.print("Masukkan Jenis Minuman (cth: Dingin/Hangat): ");
                        String jenisMinuman = scanner.nextLine();
                        restoranMenu.tambahItem(new Minuman(nama, harga, kategori, jenisMinuman));
                        System.out.println("Item Minuman berhasil ditambahkan!");
                    } else if (jenis == 3) {
                        System.out.print("Masukkan Persentase Diskon (cth: 15 untuk 15%): ");
                        double diskonVal = Double.parseDouble(scanner.nextLine());
                        restoranMenu.tambahItem(new Diskon(nama, harga, kategori, diskonVal));
                        System.out.println("Item Diskon berhasil ditambahkan!");
                    } else {
                        System.out.println("Jenis tidak dikenal.");
                    }
                    break;

                case 2:
                    restoranMenu.tampilkanSemuaMenu();
                    break;

                case 3:
                    pesananAktif = new Pesanan();
                    System.out.println("Pesanan baru berhasil dibuat.");
                    break;

                case 4:
                    if (pesananAktif == null) {
                        System.out.println("Belum ada pesanan aktif. Silakan buat pesanan baru terlebih dahulu (Menu 3).");
                        break;
                    }
                    restoranMenu.tampilkanSemuaMenu();
                    System.out.print("Masukkan nama item yang ingin dipesan: ");
                    String namaPesanan = scanner.nextLine();

                    try {
                        MenuItem itemDitemukan = restoranMenu.cariItem(namaPesanan);
                        pesananAktif.tambahItemPesanan(itemDitemukan);
                    } catch (ItemTidakDitemukanException e) {
                        System.out.println("Pengecualian: " + e.getMessage());
                    }
                    break;

                case 5:
                    if (pesananAktif == null) {
                        System.out.println("Belum ada pesanan aktif untuk dihitung.");
                        break;
                    }
                    pesananAktif.cetakStruk();
                    break;

                case 6:
                    restoranMenu.simpanMenuKeFile(MENU_FILE);
                    break;

                case 7:
                    restoranMenu.simpanMenuKeFile(MENU_FILE);
                    System.out.println("Terima kasih telah menggunakan aplikasi restoran!");
                    break;

                default:
                    System.out.println("Pilihan tidak valid, silakan coba lagi.");
            }
        } while (pilihan != 7);

        scanner.close();
    }
}