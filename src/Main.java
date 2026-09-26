import gui.WarmindoFrame;
import model.PesananWarmindo;
import model.Topping;
import model.VarianMie;
import service.DataManager;

import javax.swing.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Optimasi font rendering dan antialiasing di Java Swing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            // Gunakan System Look and Feel agar tampilan native & modern
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback ke default jika tidak tersedia
        }

        // Siapkan data awal contoh ke log_warmindo.csv jika file belum pernah ada
        siapkanDataAwalJikaPerlu();

        // Luncurkan antarmuka GUI di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            WarmindoFrame app = new WarmindoFrame();
            app.setVisible(true);
        });
    }

    private static void siapkanDataAwalJikaPerlu() {
        File file = new File("log_warmindo.csv");
        if (!file.exists() || file.length() <= 80) { // Belum ada transaksi selain header
            Topping tTelur = new Topping("Telur Ceplok / Dadar", 4000);
            Topping tKornet = new Topping("Kornet Sapi Gurih", 5000);
            Topping tKeju = new Topping("Keju Cheddar Parut", 4000);
            Topping tSosis = new Topping("Sosis Panggang", 4000);
            Topping tSawi = new Topping("Sayur Sawi Hijau", 2000);

            VarianMie mieGorengOri = new VarianMie("Indomie Goreng Original", "Goreng", 8000);
            VarianMie mieAyamBawang = new VarianMie("Indomie Ayam Bawang", "Kuah", 8000);
            VarianMie mieAceh = new VarianMie("Indomie Goreng Aceh", "Goreng", 9000);

            // Simpan beberapa sampel pesanan awal untuk menunjukkan fitur Best Seller
            DataManager.simpanPesanan(new PesananWarmindo("WMD-1001", LocalDateTime.now().minusHours(3), mieGorengOri, 3, Arrays.asList(tTelur, tKornet, tKeju)));
            DataManager.simpanPesanan(new PesananWarmindo("WMD-1002", LocalDateTime.now().minusHours(2), mieAyamBawang, 1, Arrays.asList(tTelur, tSawi)));
            DataManager.simpanPesanan(new PesananWarmindo("WMD-1003", LocalDateTime.now().minusHours(1), mieAceh, 4, Arrays.asList(tTelur, tSosis, tKornet)));
            DataManager.simpanPesanan(new PesananWarmindo("WMD-1004", LocalDateTime.now().minusMinutes(30), mieGorengOri, 2, Arrays.asList(tTelur, tKeju)));
        }
    }
}
