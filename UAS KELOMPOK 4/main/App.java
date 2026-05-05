package main;

import java.util.*;
import model.Lagu;
import logic.LogicEngine;

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

        int pilihan;
        do {
            System.out.println("\n1. Tambah Lagu");
            System.out.println("2. Tampilkan");
            System.out.println("3. Sort ID");
            System.out.println("4. Sort Judul");
            System.out.println("5. Cari & Update");
            System.out.println("6. Hapus");
            System.out.println("0. Keluar");
            System.out.print("Pilih: ");

            pilihan = Integer.parseInt(input.nextLine());

            switch (pilihan) {
                case 1:
                    System.out.print("ID: ");
                    int id = Integer.parseInt(input.nextLine());
                    System.out.print("Judul: ");
                    String judul = input.nextLine();
                    System.out.print("Artis: ");
                    String artis = input.nextLine();
                    playlist.add(new Lagu(id, judul, artis));
                    break;

                case 2:
                    for (Lagu l : playlist) {
                        System.out.println(l.getId() + " - " + l.getNama() + " - " + l.getDetailTambahan());
                    }
                    break;

                case 3:
                    engine.sortById(playlist);
                    break;

                case 4:
                    engine.sortByNama(playlist);
                    break;

                case 5:
                    System.out.print("Cari: ");
                    String cari = input.nextLine();
                    Lagu hasil = engine.smartSearch(playlist, cari);
                    if (hasil != null) {
                        System.out.print("Judul baru: ");
                        hasil.setNama(input.nextLine());
                        System.out.print("Artis baru: ");
                        hasil.setDetailTambahan(input.nextLine());
                    }
                    break;

                case 6:
                    System.out.print("ID hapus: ");
                    int hapus = Integer.parseInt(input.nextLine());
                    playlist.removeIf(l -> l.getId() == hapus);
                    break;
            }

        } while (pilihan != 0);
        input.close();
    }
}