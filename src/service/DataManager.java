package service;

import model.PesananWarmindo;
import model.Topping;
import model.VarianMie;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataManager {
    private static final String CSV_FILE = "log_warmindo.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        inisialisasiFileJikaBelumAda();
    }

    private static void inisialisasiFileJikaBelumAda() {
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                writer.println("ID_Pesanan,Waktu_Pesanan,Nama_Mie,Jenis_Mie,Level_Pedas,Toppings,Total_Biaya");
            } catch (IOException e) {
                System.err.println("Gagal menginisialisasi file log: " + e.getMessage());
            }
        }
    }

    /**
     * Menyimpan satu pesanan ke log_warmindo.csv
     */
    public static synchronized boolean simpanPesanan(PesananWarmindo pesanan) {
        File file = new File(CSV_FILE);
        boolean tulisHeader = !file.exists() || file.length() == 0;

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            if (tulisHeader) {
                writer.println("ID_Pesanan,Waktu_Pesanan,Nama_Mie,Jenis_Mie,Level_Pedas,Toppings,Total_Biaya");
            }

            // Gabungkan nama topping dengan pemisah ';' agar aman dalam format CSV
            StringBuilder sbTopping = new StringBuilder();
            if (pesanan.getListTopping() != null) {
                for (int i = 0; i < pesanan.getListTopping().size(); i++) {
                    sbTopping.append(pesanan.getListTopping().get(i).getNamaTopping());
                    if (i < pesanan.getListTopping().size() - 1) {
                        sbTopping.append(";");
                    }
                }
            }
            String toppingStr = sbTopping.toString().isEmpty() ? "Tanpa Topping" : sbTopping.toString();

            String baris = String.format("%s,%s,%s,%s,%d,\"%s\",%.0f",
                    pesanan.getIdPesanan(),
                    pesanan.getFormattedWaktu(),
                    pesanan.getMieUtama().getNamaMie(),
                    pesanan.getMieUtama().getJenis(),
                    pesanan.getLevelPedas(),
                    toppingStr,
                    pesanan.hitungBiayaTotal()
            );

            writer.println(baris);
            return true;
        } catch (IOException e) {
            System.err.println("Error saat menulis ke log_warmindo.csv: " + e.getMessage());
            return false;
        }
    }

    /**
     * Membaca seluruh data pesanan dari log_warmindo.csv
     */
    public static synchronized List<PesananWarmindo> muatSemuaPesanan() {
        List<PesananWarmindo> list = new ArrayList<>();
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String header = reader.readLine(); // Lewati header
            if (header == null) return list;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                PesananWarmindo pesanan = parseBarisCsv(line);
                if (pesanan != null) {
                    list.add(pesanan);
                }
            }
        } catch (IOException e) {
            System.err.println("Error saat membaca log_warmindo.csv: " + e.getMessage());
        }

        return list;
    }

    private static PesananWarmindo parseBarisCsv(String line) {
        try {
            // Regex untuk split CSV dengan memperhitungkan tanda kutip ganda
            List<String> tokens = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            boolean dalamKutip = false;

            for (char c : line.toCharArray()) {
                if (c == '\"') {
                    dalamKutip = !dalamKutip;
                } else if (c == ',' && !dalamKutip) {
                    tokens.add(sb.toString().trim());
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
            tokens.add(sb.toString().trim());

            if (tokens.size() >= 7) {
                String id = tokens.get(0);
                LocalDateTime waktu;
                try {
                    waktu = LocalDateTime.parse(tokens.get(1), FORMATTER);
                } catch (Exception e) {
                    waktu = LocalDateTime.now();
                }
                String namaMie = tokens.get(2);
                String jenisMie = tokens.get(3);
                int levelPedas = Integer.parseInt(tokens.get(4));
                String rawToppings = tokens.get(5);
                double totalBiaya = Double.parseDouble(tokens.get(6));

                List<Topping> toppings = new ArrayList<>();
                if (!rawToppings.equalsIgnoreCase("Tanpa Topping") && !rawToppings.isEmpty()) {
                    String[] namaTops = rawToppings.split(";");
                    for (String t : namaTops) {
                        if (!t.trim().isEmpty()) {
                            toppings.add(new Topping(t.trim(), 0)); // Harga bisa diisi default
                        }
                    }
                }

                VarianMie mie = new VarianMie(namaMie, jenisMie, totalBiaya);
                return new PesananWarmindo(id, waktu, mie, levelPedas, toppings);
            }
        } catch (Exception e) {
            System.err.println("Gagal parsing baris: " + line + " (" + e.getMessage() + ")");
        }
        return null;
    }

    /**
     * Menghitung statistik frekuensi tiap topping dari log CSV untuk menentukan Best Seller
     */
    public static synchronized Map<String, Integer> hitungStatistikTopping() {
        Map<String, Integer> frekuensi = new LinkedHashMap<>();
        List<PesananWarmindo> semuaPesanan = muatSemuaPesanan();

        for (PesananWarmindo p : semuaPesanan) {
            for (Topping t : p.getListTopping()) {
                String nama = t.getNamaTopping().trim();
                if (!nama.equalsIgnoreCase("Tanpa Topping") && !nama.isEmpty()) {
                    frekuensi.put(nama, frekuensi.getOrDefault(nama, 0) + 1);
                }
            }
        }

        // Urutkan berdasarkan jumlah pesanan terbanyak (descending)
        List<Map.Entry<String, Integer>> list = new ArrayList<>(frekuensi.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    /**
     * Mendapatkan nama topping paling favorit (Best Seller)
     */
    public static synchronized String getToppingBestSeller() {
        Map<String, Integer> stats = hitungStatistikTopping();
        if (stats.isEmpty()) {
            return "Belum ada data topping";
        }
        Map.Entry<String, Integer> topEntry = stats.entrySet().iterator().next();
        return topEntry.getKey() + " (" + topEntry.getValue() + " kali dipesan)";
    }
}
