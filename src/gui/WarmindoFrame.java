package gui;

import model.PesananWarmindo;
import model.Topping;
import model.VarianMie;
import service.DataManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class WarmindoFrame extends JFrame {
    // Theme Colors
    private static final Color PRIMARY_COLOR = new Color(211, 47, 47);      // Warmindo Red
    private static final Color ACCENT_COLOR = new Color(245, 124, 0);       // Spicy Orange
    private static final Color BG_DARK = new Color(24, 26, 31);             // Dark Charcoal
    private static final Color CARD_BG = new Color(255, 255, 255);          // Card White
    private static final Color TEXT_DARK = new Color(33, 33, 33);
    private static final Color TEXT_MUTED = new Color(117, 117, 117);
    private static final Color SUCCESS_COLOR = new Color(46, 125, 50);

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    // Form Components
    private JComboBox<VarianMie> comboVarianMie;
    private JRadioButton radioSemua, radioGoreng, radioKuah;
    private JSlider sliderLevelPedas;
    private JLabel lblStatusPedas;
    private final List<JCheckBox> toppingCheckBoxes = new ArrayList<>();
    private final List<Topping> daftarToppingTersedia = new ArrayList<>();
    private final List<VarianMie> daftarSemuaMie = new ArrayList<>();

    // Dynamic Calculation Display Labels
    private JLabel lblHargaMie;
    private JLabel lblTotalTopping;
    private JLabel lblGrandTotal;
    private JTextArea txtPreviewStruk;

    // Table & Analytics
    private DefaultTableModel tableModel;
    private JTable tableLog;
    private JLabel lblBestSellerBanner;
    private JPanel panelBestSellerList;

    public WarmindoFrame() {
        setTitle("Warmindo Digital Express — Kasir Racikan Custom & Topping");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 780);
        setMinimumSize(new Dimension(1050, 700));
        setLocationRelativeTo(null);

        initDataMaster();
        initComponents();
        updateKalkulasiDinamis();
        refreshTabelDanStatistik();
    }

    private void initDataMaster() {
        // Master Varian Mie
        daftarSemuaMie.add(new VarianMie("Indomie Goreng Original", "Goreng", 8000));
        daftarSemuaMie.add(new VarianMie("Indomie Goreng Aceh", "Goreng", 9000));
        daftarSemuaMie.add(new VarianMie("Indomie Goreng Rendang", "Goreng", 9000));
        daftarSemuaMie.add(new VarianMie("Indomie Goreng Pedas", "Goreng", 8500));
        daftarSemuaMie.add(new VarianMie("Indomie Ayam Bawang", "Kuah", 8000));
        daftarSemuaMie.add(new VarianMie("Indomie Soto Mie Spesial", "Kuah", 8000));
        daftarSemuaMie.add(new VarianMie("Indomie Kari Ayam Kental", "Kuah", 8500));
        daftarSemuaMie.add(new VarianMie("Indomie Seblak Hot Jeletot", "Kuah", 9500));
        daftarSemuaMie.add(new VarianMie("Pop Mie Pedas Dower", "Kuah", 10000));

        // Master Topping
        daftarToppingTersedia.add(new Topping("Telur Ceplok / Dadar", 4000));
        daftarToppingTersedia.add(new Topping("Kornet Sapi Gurih", 5000));
        daftarToppingTersedia.add(new Topping("Sosis Panggang", 4000));
        daftarToppingTersedia.add(new Topping("Keju Cheddar Parut", 4000));
        daftarToppingTersedia.add(new Topping("Bakso Sapi Kenyal", 5000));
        daftarToppingTersedia.add(new Topping("Sayur Sawi Hijau", 2000));
        daftarToppingTersedia.add(new Topping("Kulit Ayam Crispy", 6000));
        daftarToppingTersedia.add(new Topping("Sambal Bawang Rawit", 2000));
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header Banner
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Split (Kiri: Form Input Racikan, Kanan: Dashboard & Data)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(520);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);

        JPanel leftPanel = createFormPanel();
        JPanel rightPanel = createDashboardPanel();

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel title = new JLabel("🍜 WARMINDO DIGITAL EXPRESS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(255, 193, 7)); // Yellow/Amber

        JLabel subtitle = new JLabel("Sistem Kasir Pintar Custom Racikan Mie, Level Kepedasan & Multi-Topping");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(220, 220, 220));

        JPanel textGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        textGroup.setOpaque(false);
        textGroup.add(title);
        textGroup.add(subtitle);

        // Tagline badge
        JLabel tag = new JLabel("Best Seller Engine Active ✓ ");
        tag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tag.setForeground(new Color(129, 199, 132));
        tag.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(textGroup, BorderLayout.WEST);
        panel.add(tag, BorderLayout.EAST);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(245, 247, 250));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 16, 16, 16));

        // 1. Panel Varian Mie
        form.add(createVarianMieSection());
        form.add(Box.createRigidArea(new Dimension(0, 12)));

        // 2. Panel Level Kepedasan
        form.add(createLevelPedasSection());
        form.add(Box.createRigidArea(new Dimension(0, 12)));

        // 3. Panel Multi-Topping (Checkbox)
        form.add(createToppingSection());
        form.add(Box.createRigidArea(new Dimension(0, 12)));

        // 4. Panel Kalkulasi Dinamis Total
        form.add(createDynamicCalculationSection());
        form.add(Box.createRigidArea(new Dimension(0, 12)));

        // 5. Action Buttons
        form.add(createActionButtonsSection());

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel createVarianMieSection() {
        JPanel card = createCardPanel("1. PILIH VARIAN INDOMIE UTAMA");

        // Filter Jenis Radio Buttons
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        JLabel lblFilter = new JLabel("Filter Jenis: ");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));

        radioSemua = new JRadioButton("Semua", true);
        radioGoreng = new JRadioButton("Mie Goreng");
        radioKuah = new JRadioButton("Mie Kuah");

        ButtonGroup bg = new ButtonGroup();
        bg.add(radioSemua);
        bg.add(radioGoreng);
        bg.add(radioKuah);

        radioSemua.addActionListener(e -> filterVarianMie("Semua"));
        radioGoreng.addActionListener(e -> filterVarianMie("Goreng"));
        radioKuah.addActionListener(e -> filterVarianMie("Kuah"));

        filterPanel.add(lblFilter);
        filterPanel.add(radioSemua);
        filterPanel.add(radioGoreng);
        filterPanel.add(radioKuah);

        // Combo Box Varian Mie
        comboVarianMie = new JComboBox<>();
        comboVarianMie.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboVarianMie.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        populateComboVarian("Semua");

        comboVarianMie.addActionListener(e -> updateKalkulasiDinamis());

        card.add(filterPanel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(comboVarianMie);

        return card;
    }

    private void populateComboVarian(String filter) {
        comboVarianMie.removeAllItems();
        for (VarianMie mie : daftarSemuaMie) {
            if (filter.equals("Semua") || mie.getJenis().equalsIgnoreCase(filter)) {
                comboVarianMie.addItem(mie);
            }
        }
    }

    private void filterVarianMie(String jenis) {
        populateComboVarian(jenis);
        updateKalkulasiDinamis();
    }

    private JPanel createLevelPedasSection() {
        JPanel card = createCardPanel("2. RACIK LEVEL KEPEDASAN CABAI RAWIT (0 - 5)");

        sliderLevelPedas = new JSlider(0, 5, 1);
        sliderLevelPedas.setMajorTickSpacing(1);
        sliderLevelPedas.setPaintTicks(true);
        sliderLevelPedas.setPaintLabels(true);
        sliderLevelPedas.setSnapToTicks(true);
        sliderLevelPedas.setOpaque(false);

        lblStatusPedas = new JLabel("Level 1: Sedang (1 Cabai Rawit) 🟡", SwingConstants.CENTER);
        lblStatusPedas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatusPedas.setForeground(ACCENT_COLOR);
        lblStatusPedas.setAlignmentX(Component.CENTER_ALIGNMENT);

        sliderLevelPedas.addChangeListener(e -> {
            int level = sliderLevelPedas.getValue();
            switch (level) {
                case 0:
                    lblStatusPedas.setText("Level 0: Original (Tanpa Cabai) 🟢");
                    lblStatusPedas.setForeground(new Color(46, 125, 50));
                    break;
                case 1:
                    lblStatusPedas.setText("Level 1: Sedang (1 Cabai Rawit) 🟡");
                    lblStatusPedas.setForeground(new Color(245, 124, 0));
                    break;
                case 2:
                    lblStatusPedas.setText("Level 2: Pedas (3 Cabai Rawit) 🟠");
                    lblStatusPedas.setForeground(new Color(230, 81, 0));
                    break;
                case 3:
                    lblStatusPedas.setText("Level 3: Ekstra Pedas (6 Cabai Rawit) 🌶️");
                    lblStatusPedas.setForeground(new Color(211, 47, 47));
                    break;
                case 4:
                    lblStatusPedas.setText("Level 4: Super Pedas (10 Cabai Rawit) 🌶️🌶️");
                    lblStatusPedas.setForeground(new Color(183, 28, 28));
                    break;
                case 5:
                    lblStatusPedas.setText("Level 5: Level Mampus (15 Cabai Rawit) 🔥🌶️🔥");
                    lblStatusPedas.setForeground(new Color(136, 14, 79));
                    break;
            }
            updateKalkulasiDinamis();
        });

        card.add(sliderLevelPedas);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(lblStatusPedas);

        return card;
    }

    private JPanel createToppingSection() {
        JPanel card = createCardPanel("3. PILIHAN MULTI-TOPPING (CUSTOM RACIKAN)");

        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        gridPanel.setOpaque(false);

        toppingCheckBoxes.clear();
        for (Topping topping : daftarToppingTersedia) {
            JCheckBox cb = new JCheckBox(String.format("<html><b>%s</b> <font color='#D32F2F'>+Rp %,.0f</font></html>",
                    topping.getNamaTopping(), topping.getHargaTopping()));
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            cb.setOpaque(false);
            cb.putClientProperty("toppingObj", topping);

            cb.addActionListener(e -> updateKalkulasiDinamis());

            toppingCheckBoxes.add(cb);
            gridPanel.add(cb);
        }

        // Quick button select all / clear all
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.setOpaque(false);
        JButton btnClearAll = new JButton("Batal Semua Topping");
        btnClearAll.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnClearAll.addActionListener(e -> {
            for (JCheckBox cb : toppingCheckBoxes) {
                cb.setSelected(false);
            }
            updateKalkulasiDinamis();
        });
        btnPanel.add(btnClearAll);

        card.add(gridPanel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(btnPanel);

        return card;
    }

    private JPanel createDynamicCalculationSection() {
        JPanel card = createCardPanel("4. KALKULASI DINAMIS BIAYA PESANAN");
        card.setBackground(new Color(255, 248, 225)); // Light warm amber

        JPanel grid = new JPanel(new GridLayout(3, 2, 6, 6));
        grid.setOpaque(false);

        JLabel t1 = new JLabel("Harga Mie Utama:");
        t1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblHargaMie = new JLabel("Rp 0", SwingConstants.RIGHT);
        lblHargaMie.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel t2 = new JLabel("Penambahan Biaya Topping:");
        t2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTotalTopping = new JLabel("Rp 0", SwingConstants.RIGHT);
        lblTotalTopping.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalTopping.setForeground(PRIMARY_COLOR);

        JLabel t3 = new JLabel("TOTAL BIAYA AKHIR:");
        t3.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblGrandTotal = new JLabel("Rp 0", SwingConstants.RIGHT);
        lblGrandTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblGrandTotal.setForeground(SUCCESS_COLOR);

        grid.add(t1); grid.add(lblHargaMie);
        grid.add(t2); grid.add(lblTotalTopping);
        grid.add(t3); grid.add(lblGrandTotal);

        card.add(grid);
        return card;
    }

    private JPanel createActionButtonsSection() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setOpaque(false);

        JButton btnReset = new JButton("🔄 Reset Racikan");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReset.setBackground(new Color(224, 224, 224));
        btnReset.setForeground(TEXT_DARK);
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(e -> resetForm());

        JButton btnPesan = new JButton("🔥 SIMPAN & PROSES PESANAN");
        btnPesan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPesan.setBackground(PRIMARY_COLOR);
        btnPesan.setForeground(Color.WHITE);
        btnPesan.setFocusPainted(false);
        btnPesan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPesan.addActionListener(e -> prosesSimpanPesanan());

        panel.add(btnReset);
        panel.add(btnPesan);
        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(238, 242, 246));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Best Seller Banner Card
        JPanel bannerCard = new JPanel(new BorderLayout());
        bannerCard.setBackground(CARD_BG);
        bannerCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 179, 0), 2, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel titleBadge = new JLabel("🏆 TOPPING PALING FAVORIT (BEST SELLER)");
        titleBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleBadge.setForeground(new Color(230, 81, 0));

        lblBestSellerBanner = new JLabel("Memuat data best seller...");
        lblBestSellerBanner.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBestSellerBanner.setForeground(TEXT_DARK);

        bannerCard.add(titleBadge, BorderLayout.NORTH);
        bannerCard.add(lblBestSellerBanner, BorderLayout.CENTER);

        // Tabbed Pane for Tables & Analytics
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Tab 1: Log Transaksi (CSV)
        JPanel tabLog = createLogTableTab();
        tabbedPane.addTab("📋 Log Pesanan (CSV)", tabLog);

        // Tab 2: Visual Ranking Topping
        JPanel tabRank = createRankingToppingTab();
        tabbedPane.addTab("📊 Statistik Ranking Topping", tabRank);

        panel.add(bannerCard, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLogTableTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));

        String[] columns = {"ID", "Waktu", "Varian Mie", "Jenis", "Pedas", "Topping", "Total Biaya"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableLog = new JTable(tableModel);
        tableLog.setRowHeight(26);
        tableLog.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableLog.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableLog.getTableHeader().setBackground(new Color(230, 235, 240));

        // Column widths
        tableLog.getColumnModel().getColumn(0).setPreferredWidth(70);
        tableLog.getColumnModel().getColumn(1).setPreferredWidth(125);
        tableLog.getColumnModel().getColumn(2).setPreferredWidth(150);
        tableLog.getColumnModel().getColumn(3).setPreferredWidth(65);
        tableLog.getColumnModel().getColumn(4).setPreferredWidth(50);
        tableLog.getColumnModel().getColumn(5).setPreferredWidth(180);
        tableLog.getColumnModel().getColumn(6).setPreferredWidth(95);

        // Align right for currency
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tableLog.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);

        JScrollPane scrollTable = new JScrollPane(tableLog);
        scrollTable.setBorder(new LineBorder(new Color(200, 205, 210), 1));

        // Bottom action bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bottomBar.setOpaque(false);

        JButton btnMuatUlang = new JButton("🔄 Segarkan Data");
        btnMuatUlang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnMuatUlang.addActionListener(e -> refreshTabelDanStatistik());

        JButton btnBukaCsv = new JButton("📂 Buka File CSV");
        btnBukaCsv.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBukaCsv.addActionListener(e -> {
            try {
                File file = new File("log_warmindo.csv");
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                } else {
                    JOptionPane.showMessageDialog(this, "File log_warmindo.csv belum dibuat.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Tidak dapat membuka file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bottomBar.add(btnBukaCsv);
        bottomBar.add(btnMuatUlang);

        panel.add(scrollTable, BorderLayout.CENTER);
        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRankingToppingTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        panelBestSellerList = new JPanel();
        panelBestSellerList.setLayout(new BoxLayout(panelBestSellerList, BoxLayout.Y_AXIS));
        panelBestSellerList.setOpaque(false);

        JScrollPane scroll = new JScrollPane(panelBestSellerList);
        scroll.setBorder(null);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCardPanel(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
                " " + title + " ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                PRIMARY_COLOR
        );
        card.setBorder(new CompoundBorder(border, new EmptyBorder(8, 12, 12, 12)));
        return card;
    }

    /**
     * Hitung total dinamis real-time saat ada perubahan pilihan mie, level pedas, atau topping
     */
    private void updateKalkulasiDinamis() {
        VarianMie mie = (VarianMie) comboVarianMie.getSelectedItem();
        double hargaMie = (mie != null) ? mie.getHargaDasar() : 0.0;

        double totalTopping = 0.0;
        for (JCheckBox cb : toppingCheckBoxes) {
            if (cb.isSelected()) {
                Topping t = (Topping) cb.getClientProperty("toppingObj");
                if (t != null) {
                    totalTopping += t.getHargaTopping();
                }
            }
        }

        double totalAkhir = hargaMie + totalTopping;

        lblHargaMie.setText(String.format("Rp %,.0f", hargaMie));
        lblTotalTopping.setText(String.format("+ Rp %,.0f", totalTopping));
        lblGrandTotal.setText(String.format("Rp %,.0f", totalAkhir));
    }

    /**
     * Mengambil daftar topping yang sedang dicentang oleh user
     */
    private List<Topping> getSelectedToppings() {
        List<Topping> list = new ArrayList<>();
        for (JCheckBox cb : toppingCheckBoxes) {
            if (cb.isSelected()) {
                Topping t = (Topping) cb.getClientProperty("toppingObj");
                if (t != null) {
                    list.add(t);
                }
            }
        }
        return list;
    }

    /**
     * Proses validasi dan simpan pesanan ke file CSV
     */
    private void prosesSimpanPesanan() {
        VarianMie mie = (VarianMie) comboVarianMie.getSelectedItem();
        if (mie == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih varian mie terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int levelPedas = sliderLevelPedas.getValue();
        List<Topping> toppings = getSelectedToppings();

        // Generate ID Pesanan: WMD-XXXX
        String idPesanan = "WMD-" + System.currentTimeMillis() % 10000;

        PesananWarmindo pesanan = new PesananWarmindo(idPesanan, mie, levelPedas, toppings);
        double totalBiaya = pesanan.hitungBiayaTotal();

        // Simpan ke log CSV (Data Persistence)
        boolean sukses = DataManager.simpanPesanan(pesanan);
        if (sukses) {
            tampilkanStrukDialog(pesanan);
            resetForm();
            refreshTabelDanStatistik();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan pesanan ke log CSV!", "Error File", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        if (comboVarianMie.getItemCount() > 0) {
            comboVarianMie.setSelectedIndex(0);
        }
        sliderLevelPedas.setValue(1);
        for (JCheckBox cb : toppingCheckBoxes) {
            cb.setSelected(false);
        }
        updateKalkulasiDinamis();
    }

    /**
     * Memperbarui data pada tabel log dan panel analitik Best Seller
     */
    private void refreshTabelDanStatistik() {
        // 1. Refresh Tabel
        tableModel.setRowCount(0);
        List<PesananWarmindo> listPesanan = DataManager.muatSemuaPesanan();
        for (PesananWarmindo p : listPesanan) {
            tableModel.addRow(new Object[]{
                    p.getIdPesanan(),
                    p.getFormattedWaktu(),
                    p.getMieUtama().getNamaMie(),
                    p.getMieUtama().getJenis(),
                    "Lvl " + p.getLevelPedas(),
                    p.getToppingSummary(),
                    String.format("Rp %,.0f", p.hitungBiayaTotal())
            });
        }

        // 2. Refresh Best Seller Banner & Chart
        Map<String, Integer> stats = DataManager.hitungStatistikTopping();
        if (stats.isEmpty()) {
            lblBestSellerBanner.setText("Belum ada data pesanan di log_warmindo.csv");
            panelBestSellerList.removeAll();
            JLabel emptyLabel = new JLabel("Belum ada data topping yang tercatat.", SwingConstants.CENTER);
            panelBestSellerList.add(emptyLabel);
        } else {
            // Ambil nomor 1
            Map.Entry<String, Integer> top1 = stats.entrySet().iterator().next();
            lblBestSellerBanner.setText(String.format("👑 %s (Terjual %d porsi)", top1.getKey(), top1.getValue()));

            // Gambar visual ranking list
            panelBestSellerList.removeAll();
            int totalPesananTopping = stats.values().stream().mapToInt(Integer::intValue).sum();
            int rank = 1;

            for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                JPanel itemPanel = new JPanel(new BorderLayout(8, 4));
                itemPanel.setOpaque(false);
                itemPanel.setBorder(new EmptyBorder(6, 4, 6, 4));

                String medal = (rank == 1) ? "🥇" : (rank == 2) ? "🥈" : (rank == 3) ? "🥉" : "#" + rank;
                JLabel lblNama = new JLabel(String.format("%s %s (%d pesanan)", medal, entry.getKey(), entry.getValue()));
                lblNama.setFont(new Font("Segoe UI", Font.BOLD, 12));

                int persen = (int) Math.round(((double) entry.getValue() / totalPesananTopping) * 100);
                JProgressBar bar = new JProgressBar(0, 100);
                bar.setValue(persen);
                bar.setStringPainted(true);
                bar.setString(persen + "%");
                bar.setForeground((rank == 1) ? PRIMARY_COLOR : (rank <= 3) ? ACCENT_COLOR : new Color(33, 150, 243));

                itemPanel.add(lblNama, BorderLayout.NORTH);
                itemPanel.add(bar, BorderLayout.CENTER);

                panelBestSellerList.add(itemPanel);
                rank++;
            }
        }

        panelBestSellerList.revalidate();
        panelBestSellerList.repaint();
    }

    /**
     * Menampilkan dialog struk thermal digital yang estetik
     */
    private void tampilkanStrukDialog(PesananWarmindo pesanan) {
        JDialog dialog = new JDialog(this, "Struk Kasir Digital Warmindo", true);
        dialog.setSize(380, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTextArea receiptText = new JTextArea();
        receiptText.setFont(new Font("Courier New", Font.PLAIN, 12));
        receiptText.setEditable(false);
        receiptText.setBackground(new Color(254, 254, 250));
        receiptText.setBorder(new EmptyBorder(16, 20, 16, 20));

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("       WARMINDO DIGITAL EXPRESS         \n");
        sb.append("     Spesialis Racikan Mie & Topping    \n");
        sb.append("========================================\n");
        sb.append("ID Pesanan : ").append(pesanan.getIdPesanan()).append("\n");
        sb.append("Waktu      : ").append(pesanan.getFormattedWaktu()).append("\n");
        sb.append("Kasir      : Kasir Digital #01\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-26s %12s\n", pesanan.getMieUtama().getNamaMie(),
                String.format("Rp %,.0f", pesanan.getMieUtama().getHargaDasar())));
        sb.append(" - Jenis    : ").append(pesanan.getMieUtama().getJenis()).append("\n");
        sb.append(" - Kepedasan: ").append(pesanan.getDeskripsiLevelPedas()).append("\n");
        sb.append("\nTopping Tambahan:\n");

        if (pesanan.getListTopping().isEmpty()) {
            sb.append(" (Tanpa Topping Tambahan)\n");
        } else {
            for (Topping t : pesanan.getListTopping()) {
                sb.append(String.format(" + %-23s %12s\n", t.getNamaTopping(),
                        String.format("Rp %,.0f", t.getHargaTopping())));
            }
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("%-26s %12s\n", "TOTAL PEMBAYARAN:",
                String.format("Rp %,.0f", pesanan.hitungBiayaTotal())));
        sb.append("========================================\n");
        sb.append("    Data tersimpan ke log_warmindo.csv  \n");
        sb.append("   Terima kasih telah berkunjung! ^_^   \n");
        sb.append("========================================\n");

        receiptText.setText(sb.toString());

        JButton btnTutup = new JButton("Tutup Struk");
        btnTutup.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTutup.addActionListener(e -> dialog.dispose());

        dialog.add(new JScrollPane(receiptText), BorderLayout.CENTER);
        dialog.add(btnTutup, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
