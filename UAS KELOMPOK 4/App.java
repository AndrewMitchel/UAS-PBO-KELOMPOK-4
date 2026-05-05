import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// --- Model Data ---
class MediaAudio {
    protected int idLagu;
    protected String judulLagu;

    public MediaAudio(int idLagu, String judulLagu) {
        this.idLagu = idLagu;
        this.judulLagu = judulLagu;
    }

    public int getIdLagu() { return idLagu; }
    public String getJudulLagu() { return judulLagu; }
}

interface DataTemplate {
    int getId();
    String getNama();
    String getDetailTambahan();
    void setNama(String namaBaru);
    void setDetailTambahan(String detailBaru);
}

class Lagu extends MediaAudio implements DataTemplate {
    private String namaArtis;

    public Lagu(int idLagu, String judulLagu, String namaArtis) {
        super(idLagu, judulLagu);
        this.namaArtis = namaArtis;
    }

    public int getId() { return idLagu; }
    public String getNama() { return judulLagu; }
    public String getDetailTambahan() { return namaArtis; }
    public void setNama(String namaBaru) { this.judulLagu = namaBaru; }
    public void setDetailTambahan(String detailBaru) { this.namaArtis = detailBaru; }
}

// --- Mesin Logika ---
class LogicEngine<T extends DataTemplate> {
    public void sortById(List<T> list) {
        list.sort((a, b) -> a.getId() - b.getId());
    }

    public void sortByNama(List<T> list) {
        list.sort((a, b) -> a.getNama().compareToIgnoreCase(b.getNama()));
    }

    public T smartSearch(List<T> list, int id) {
        for (T item : list) {
            if (item.getId() == id) return item;
        }
        return null;
    }

    public T smartSearch(List<T> list, String keyword) {
        boolean isAngka = keyword.matches("\\d+");
        for (T item : list) {
            if (isAngka) {
                if (item.getId() == Integer.parseInt(keyword)) return item;
            } else {
                if (item.getNama().equalsIgnoreCase(keyword)) return item;
            }
        }
        return null;
    }

    public void printTable(List<T> list) {
        System.out.println("\n+============================================================+");
        System.out.println("|                 PROGRAM PLAYLIST LAGU                        |");
        System.out.println("+------+----------------------+------------------------------+");
        System.out.printf("| %-4s | %-20s | %-28s |\n", "ID", "JUDUL LAGU", "ARTIS / PENYANYI");
        System.out.println("+------+----------------------+------------------------------+");
        if (list.isEmpty()) {
            System.out.printf("| %-58s |\n", "       --- Belum ada lagu dalam antrian ---");
        } else {
            for (T item : list) {
                System.out.printf("| %-4d | %-20s | %-28s |\n", item.getId(), item.getNama(), item.getDetailTambahan());
            }
        }
        System.out.println("+------+----------------------+------------------------------+");
    }
}

// --- Program Utama ---
public class App {
    public static void ketik(String teks) {
        for (int i = 0; i < teks.length(); i++) {
            System.out.print(teks.charAt(i));
            try { Thread.sleep(5); } catch (Exception e) {}
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        LogicEngine<Lagu> engine = new LogicEngine<>();
        List<Lagu> playlist = new ArrayList<>();

        ketik("=========================================");
        ketik("   PROGRAM PLAYLIST LAGU                 ");
        ketik("=========================================");
        System.out.println("Nama : Yesica Candra Carlota");
        System.out.println("NIM  : 25051204423");

        int pilihan = -1;
        do {
            System.out.println("\n>>> MENU UTAMA <<<");
            System.out.println("1. Tambah Lagu Baru");
            System.out.println("2. Tampilkan Playlist");
            System.out.println("3. Sorting by ID");
            System.out.println("4. Sorting by Judul");
            System.out.println("5. Cari & Update Data");
            System.out.println("6. Hapus Lagu");
            System.out.println("0. Keluar");
            System.out.print("Pilih Opsi: ");

            try {
                pilihan = Integer.parseInt(input.nextLine());
            } catch (Exception e) {
                ketik(">> Salah Input! Masukkan angka menu (0-6).");
                pilihan = -1;
                continue;
            }

            switch (pilihan) {
                case 1:
                    try {
                        System.out.println("\n--- Input Data Lagu ---");
                        System.out.print("ID Lagu      : ");
                        int id = Integer.parseInt(input.nextLine());
                        System.out.print("Judul Lagu   : ");
                        String judul = input.nextLine();
                        System.out.print("Nama Artis   : ");
                        String artis = input.nextLine();
                        playlist.add(new Lagu(id, judul, artis));
                        ketik(">> Berhasil disimpan.");
                    } catch (Exception e) {
                        ketik(">> Salah Input! ID harus berupa angka.");
                    }
                    break;

                case 2:
                    engine.printTable(playlist);
                    break;

                case 3:
                    engine.sortById(playlist);
                    ketik(">> Diurutkan berdasarkan ID.");
                    engine.printTable(playlist);
                    break;

                case 4:
                    engine.sortByNama(playlist);
                    ketik(">> Diurutkan berdasarkan Judul.");
                    engine.printTable(playlist);
                    break;

                case 5:
                    System.out.print("Cari ID/Judul: ");
                    String cari = input.nextLine();
                    Lagu hasil = (cari.matches("\\d+")) ? 
                                 engine.smartSearch(playlist, Integer.parseInt(cari)) : 
                                 engine.smartSearch(playlist, cari);

                    if (hasil != null) {
                        System.out.print("Ubah Judul (- skip): ");
                        String j = input.nextLine();
                        if (!j.equals("-")) hasil.setNama(j);
                        System.out.print("Ubah Artis (- skip): ");
                        String a = input.nextLine();
                        if (!a.equals("-")) hasil.setDetailTambahan(a);
                        ketik(">> Update sukses.");
                    } else {
                        ketik(">> Data tidak ditemukan.");
                    }
                    break;

                case 6:
                    try {
                        System.out.print("Masukkan ID lagu yang dihapus: ");
                        int idHapus = Integer.parseInt(input.nextLine());
                        boolean ok = playlist.removeIf(l -> l.getId() == idHapus);
                        if (ok) ketik(">> Lagu berhasil dihapus.");
                        else ketik(">> ID tidak ketemu.");
                    } catch (Exception e) {
                        ketik(">> Salah Input! Masukkan angka ID yang bener.");
                    }
                    break;

                case 0:
                    ketik("Terima Kasih Dan Sampai jumpa!");
                    break;
            }
        } while (pilihan != 0);
        input.close();
    }
}