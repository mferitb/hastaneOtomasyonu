import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HospitalGUI {
    private JFrame frame;
    private JLabel saatLabel;
    private JLabel hastaBilgiLabel;
    private JButton baslatButton;
    private JButton durdurButton;
    private JPanel hastaPanel;
    private Timer muayeneTimer;
    private boolean muayeneDevamEdiyor = false;
    private double currentSaat = 9.00;
    private DefaultListModel<String> hastaListModel = new DefaultListModel<>();
    private DefaultTableModel tableModel;
    private JTextArea logArea;
    private JPanel heapPanel;
    private JTable table;
    private JList<String> hastaList;
    private boolean isFirstRun = true;
    private int[] saatDakika = {9, 0};
    private Hasta[] hastalar;
    private int hastaSayisi;
    private Heap oncelikSirasi;

    public HospitalGUI(Hasta[] hastalar, int hastaSayisi, Heap oncelikSirasi) {
        this.hastalar = hastalar;
        this.hastaSayisi = hastaSayisi;
        this.oncelikSirasi = oncelikSirasi;
        initializeGUI();
    }

    private void initializeGUI() {
        // Ana pencere oluşturma
        frame = new JFrame("Hasta Muayene Sistemi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 800);
        frame.setLayout(new BorderLayout());

        // Üstte saat başlığı
        JPanel saatPanel = new JPanel();
        saatLabel = new JLabel("09:00");
        saatLabel.setFont(new Font("Arial", Font.BOLD, 36));
        saatPanel.add(saatLabel);
        frame.add(saatPanel, BorderLayout.NORTH);

        // Sol Panel: Muayene Sırası ve Butonlar
        JPanel solPanel = createSolPanel();
        
        // Orta Panel: Tablo ve Log
        JPanel ortaPanel = createOrtaPanel();

        // SplitPane ile böl
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, solPanel, ortaPanel);
        splitPane.setDividerLocation(300);
        frame.add(splitPane, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createSolPanel() {
        JPanel solPanel = new JPanel();
        solPanel.setLayout(new BorderLayout());
        hastaListModel.clear();
        for (int i = 0; i < hastaSayisi; i++) {
            Hasta h = hastalar[i];
            hastaListModel.addElement(h.hastaAdi + " - Oncelik Puanı: " + h.oncelikPuani);
        }
        hastaList = new JList<>(hastaListModel);
        JScrollPane listScroll = new JScrollPane(hastaList);
        solPanel.add(listScroll, BorderLayout.CENTER);

        JPanel solButonPanel = new JPanel();
        solButonPanel.setLayout(new GridLayout(2, 1, 5, 5));
        JButton siraGosterBtn = new JButton("Muayene Sırasını Göster");
        JButton muayeneGonderBtn = new JButton("Muayeneye Başla");
        solButonPanel.add(siraGosterBtn);
        solButonPanel.add(muayeneGonderBtn);
        solPanel.add(solButonPanel, BorderLayout.NORTH);
        solPanel.setPreferredSize(new Dimension(300, 800));

        // Buton aksiyonları
        muayeneGonderBtn.addActionListener(e -> muayeneBaslat());
        siraGosterBtn.addActionListener(e -> muayeneSirasiniGoster());

        return solPanel;
    }

    private JPanel createOrtaPanel() {
        // Tablo oluşturma
        String[] kolonlar = {"Hasta No", "Hasta Adı", "Hasta Yaş", "Cinsiyet", "Mahkumluk Durumu", 
            "Engellilik Oranı", "Kanama Durumu", "Kayıt Saati", "Muayene Saati", "Muayene Süresi", "Öncelik Puanı"};
        tableModel = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                int oncelik = 0;
                try {
                    oncelik = Integer.parseInt(table.getValueAt(row, 10).toString());
                } catch (Exception ex) {}
                if (!isSelected) {
                    if (oncelik >= 0 && oncelik < 50) c.setBackground(new Color(200, 255, 200));
                    else if (oncelik >= 50 && oncelik < 75) c.setBackground(new Color(255, 255, 180));
                    else if (oncelik >= 75 && oncelik <= 125) c.setBackground(new Color(255, 180, 180));
                    else c.setBackground(table.getBackground());
                } else {
                    c.setBackground(table.getSelectionBackground());
                }
                return c;
            }
        });
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(900, 350));

        // Log alanı
        logArea = new JTextArea(5, 80);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(900, 70));

        JPanel ortaPanel = new JPanel();
        ortaPanel.setLayout(new BorderLayout());
        ortaPanel.add(tableScroll, BorderLayout.NORTH);
        ortaPanel.add(logScroll, BorderLayout.CENTER);

        return ortaPanel;
    }

    private void muayeneBaslat() {
        if (isFirstRun) {
            saatDakika[0] = 9;
            saatDakika[1] = 0;
            saatLabel.setText("09:00");
            isFirstRun = false;
        }

        // Hastaları öncelik puanına göre sırala
        Hasta[] siraliHastalar = new Hasta[hastaSayisi];
        for (int i = 0; i < hastaSayisi; i++) {
            siraliHastalar[i] = hastalar[i];
        }

        // Sıralama işlemi (bubble sort)
        for (int i = 0; i < hastaSayisi - 1; i++) {
            for (int j = 0; j < hastaSayisi - i - 1; j++) {
                Hasta h1 = siraliHastalar[j];
                Hasta h2 = siraliHastalar[j + 1];
                boolean swap = false;

                // Önce öncelik puanına göre karşılaştır
                if (h2.oncelikPuani != h1.oncelikPuani) {
                    swap = h2.oncelikPuani > h1.oncelikPuani;
                } else {
                    // Öncelik puanları eşitse kayıt saatine göre karşılaştır
                    try {
                        String[] s1 = h1.kayitSaati.split(":");
                        String[] s2 = h2.kayitSaati.split(":");
                        int saat1 = Integer.parseInt(s1[0]);
                        int dakika1 = Integer.parseInt(s1[1]);
                        int saat2 = Integer.parseInt(s2[0]);
                        int dakika2 = Integer.parseInt(s2[1]);

                        if (saat1 != saat2) {
                            swap = saat1 > saat2;
                        } else {
                            swap = dakika1 > dakika2;
                        }
                    } catch (Exception ex) {
                        try {
                            String[] s1 = h1.kayitSaati.split("\\.");
                            String[] s2 = h2.kayitSaati.split("\\.");
                            int saat1 = Integer.parseInt(s1[0]);
                            int dakika1 = Integer.parseInt(s1[1]);
                            int saat2 = Integer.parseInt(s2[0]);
                            int dakika2 = Integer.parseInt(s2[1]);

                            if (saat1 != saat2) {
                                swap = saat1 > saat2;
                            } else {
                                swap = dakika1 > dakika2;
                            }
                        } catch (Exception ex2) {
                            swap = false;
                        }
                    }
                }

                if (swap) {
                    siraliHastalar[j] = h2;
                    siraliHastalar[j + 1] = h1;
                }
            }
        }

        // Kalan hastaları takip etmek için boolean dizi kullan
        boolean[] muayeneEdildi = new boolean[hastaSayisi];

        tableModel.setRowCount(0);
        logArea.setText("");
        final int[] kalanSure = {0};
        final Hasta[] aktifHasta = {null};

        if (muayeneTimer != null) {
            muayeneTimer.stop();
        }

        muayeneTimer = new Timer(250, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (aktifHasta[0] == null || kalanSure[0] <= 0) {
                    // Tüm hastalar muayene edildi mi kontrol et
                    boolean tumHastalarMuayeneEdildi = true;
                    for (int i = 0; i < hastaSayisi; i++) {
                        if (!muayeneEdildi[i]) {
                            tumHastalarMuayeneEdildi = false;
                            break;
                        }
                    }
                    
                    if (tumHastalarMuayeneEdildi) {
                        muayeneTimer.stop();
                        logArea.append("Tüm hastalar muayene edildi.\n");
                        return;
                    }

                    // Şu anki saat için uygun hastaları bul
                    Hasta[] uygunlar = new Hasta[hastaSayisi];
                    int uygunSayisi = 0;
                    
                    for (int i = 0; i < hastaSayisi; i++) {
                        if (muayeneEdildi[i]) continue;
                        
                        Hasta h = siraliHastalar[i];
                        try {
                            String[] ks = h.kayitSaati.split(":");
                            int kayitSaat = Integer.parseInt(ks[0]);
                            int kayitDakika = Integer.parseInt(ks[1]);
                            
                            if (kayitSaat < saatDakika[0] || 
                                (kayitSaat == saatDakika[0] && kayitDakika <= saatDakika[1])) {
                                uygunlar[uygunSayisi++] = h;
                            }
                        } catch (Exception ex) {
                            try {
                                String[] ks = h.kayitSaati.split("\\.");
                                int kayitSaat = Integer.parseInt(ks[0]);
                                int kayitDakika = Integer.parseInt(ks[1]);
                                
                                if (kayitSaat < saatDakika[0] || 
                                    (kayitSaat == saatDakika[0] && kayitDakika <= saatDakika[1])) {
                                    uygunlar[uygunSayisi++] = h;
                                }
                            } catch (Exception ex2) {
                                // Hatalı kayıt saati formatı, bu hastayı atla
                            }
                        }
                    }

                    // Uygun hastaları öncelik puanına göre sırala
                    for (int i = 0; i < uygunSayisi - 1; i++) {
                        for (int j = 0; j < uygunSayisi - i - 1; j++) {
                            Hasta h1 = uygunlar[j];
                            Hasta h2 = uygunlar[j + 1];
                            
                            if (h2.oncelikPuani > h1.oncelikPuani) {
                                uygunlar[j] = h2;
                                uygunlar[j + 1] = h1;
                            }
                        }
                    }

                    if (uygunSayisi == 0) {
                        // Saati ilerlet
                        saatDakika[1] += 1;
                        if (saatDakika[1] >= 60) {
                            saatDakika[0] += 1;
                            saatDakika[1] = 0;
                        }
                        if (saatDakika[0] >= 24) {
                            saatDakika[0] = 9;
                            saatDakika[1] = 0;
                            logArea.append("Yeni gün başladı. Saat 09:00'a ayarlandı.\n");
                        }
                        saatLabel.setText(String.format("%02d:%02d", saatDakika[0], saatDakika[1]));
                        return;
                    }

                    // Uygun hastalar arasından en yüksek öncelikli olanı seç
                    Hasta h = uygunlar[0];
                    aktifHasta[0] = h;
                    kalanSure[0] = (int) h.muayeneSuresi;
                    String muayeneSaatiStr = String.format("%02d:%02d", saatDakika[0], saatDakika[1]);
                    h.muayeneSaati = muayeneSaatiStr;

                    // Tabloya hasta bilgilerini ekle
                    Object[] row = {h.hastaNo, h.hastaAdi, h.hastaYasi, h.cinsiyet, h.mahkumlukDurumu, 
                        h.engellilikOrani, h.kanamaDurumu, h.kayitSaati, h.muayeneSaati, h.muayeneSuresi, h.oncelikPuani};
                    tableModel.addRow(row);

                    // Log mesajını güncelle
                    logArea.append(String.format("%02d:%02d - %d. sırada %d numaralı %s adlı hasta muayeneye gönderildi. (Öncelik: %d)\n",
                        saatDakika[0], saatDakika[1], tableModel.getRowCount(), h.hastaNo, h.hastaAdi, h.oncelikPuani));

                    // Hastayı muayene edildi olarak işaretle
                    for (int i = 0; i < hastaSayisi; i++) {
                        if (siraliHastalar[i] == h) {
                            muayeneEdildi[i] = true;
                            break;
                        }
                    }
                }

                // Her tick'te süreyi azalt
                kalanSure[0] -= 1;

                // Her 4 tick'te (1 saniyede) saati 1 dakika ilerlet
                if (muayeneTimer.getDelay() * 4 <= 1000) {
                    saatDakika[1] += 1;
                    if (saatDakika[1] >= 60) {
                        saatDakika[0] += 1;
                        saatDakika[1] = 0;
                    }
                    if (saatDakika[0] >= 24) {
                        saatDakika[0] = 9;
                        saatDakika[1] = 0;
                        logArea.append("Yeni gün başladı. Saat 09:00'a ayarlandı.\n");
                    }
                    saatLabel.setText(String.format("%02d:%02d", saatDakika[0], saatDakika[1]));
                }
            }
        });
        muayeneTimer.start();
    }

    private void muayeneSirasiniGoster() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hastaSayisi; i++) {
            Hasta h = hastalar[i];
            sb.append((i+1) + ". " + h.hastaAdi + " - Öncelik: " + h.oncelikPuani + "\n");
        }
        JOptionPane.showMessageDialog(frame, sb.toString(), "Muayene Sırası", JOptionPane.INFORMATION_MESSAGE);
    }
} 