package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PesananWarmindo {
    private String idPesanan;
    private LocalDateTime waktuPesanan;
    private VarianMie mieUtama;
    private int levelPedas; // 0 - 5
    private List<Topping> listTopping;

    public PesananWarmindo(String idPesanan, VarianMie mieUtama, int levelPedas, List<Topping> listTopping) {
        this.idPesanan = idPesanan;
        this.waktuPesanan = LocalDateTime.now();
        this.mieUtama = mieUtama;
        this.levelPedas = levelPedas;
        this.listTopping = (listTopping != null) ? new ArrayList<>(listTopping) : new ArrayList<>();
    }

    public PesananWarmindo(String idPesanan, LocalDateTime waktuPesanan, VarianMie mieUtama, int levelPedas, List<Topping> listTopping) {
        this.idPesanan = idPesanan;
        this.waktuPesanan = waktuPesanan;
        this.mieUtama = mieUtama;
        this.levelPedas = levelPedas;
        this.listTopping = (listTopping != null) ? new ArrayList<>(listTopping) : new ArrayList<>();
    }

    /**
     * Menghitung total biaya: Harga Dasar Mie + Total Harga Semua Topping
     */
    public double hitungBiayaTotal() {
        double total = 0.0;
        if (mieUtama != null) {
            total += mieUtama.getHargaDasar();
        }
        if (listTopping != null) {
            for (Topping topping : listTopping) {
                total += topping.getHargaTopping();
            }
        }
        return total;
    }

    public double hitungTotalBiayaTopping() {
        double total = 0.0;
        if (listTopping != null) {
            for (Topping topping : listTopping) {
                total += topping.getHargaTopping();
            }
        }
        return total;
    }

    public String getIdPesanan() {
        return idPesanan;
    }

    public void setIdPesanan(String idPesanan) {
        this.idPesanan = idPesanan;
    }

    public LocalDateTime getWaktuPesanan() {
        return waktuPesanan;
    }

    public void setWaktuPesanan(LocalDateTime waktuPesanan) {
        this.waktuPesanan = waktuPesanan;
    }

    public VarianMie getMieUtama() {
        return mieUtama;
    }

    public void setMieUtama(VarianMie mieUtama) {
        this.mieUtama = mieUtama;
    }

    public int getLevelPedas() {
        return levelPedas;
    }

    public void setLevelPedas(int levelPedas) {
        this.levelPedas = levelPedas;
    }

    public List<Topping> getListTopping() {
        return listTopping;
    }

    public void setListTopping(List<Topping> listTopping) {
        this.listTopping = listTopping;
    }

    public String getFormattedWaktu() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return (waktuPesanan != null) ? waktuPesanan.format(formatter) : "";
    }

    public String getToppingSummary() {
        if (listTopping == null || listTopping.isEmpty()) {
            return "Tanpa Topping";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listTopping.size(); i++) {
            sb.append(listTopping.get(i).getNamaTopping());
            if (i < listTopping.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public String getDeskripsiLevelPedas() {
        switch (levelPedas) {
            case 0: return "Level 0 (Original - Tidak Pedas)";
            case 1: return "Level 1 (Sedang - 1 Cabai)";
            case 2: return "Level 2 (Pedas - 3 Cabai)";
            case 3: return "Level 3 (Ekstra Pedas - 6 Cabai)";
            case 4: return "Level 4 (Super Pedas - 10 Cabai)";
            case 5: return "Level 5 (Level Mampus - 15 Cabai)";
            default: return "Level " + levelPedas;
        }
    }
}
