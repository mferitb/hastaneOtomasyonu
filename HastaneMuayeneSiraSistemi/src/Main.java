import java.io.*;
import javax.swing.JOptionPane;

public class Main {
    private static final int MAX_HASTA = 1000; // Maksimum hasta sayısı
    private static Hasta[] hastalar = new Hasta[MAX_HASTA];
    private static int hastaSayisi = 0;
    static Heap oncelikSirasi = new Heap(100);

    public static void main(String[] args) {
        hastaKayitEkle("Hasta.txt");
        for (int i = 0; i < hastaSayisi; i++) {
            Hasta h = hastalar[i];
            oncelikPuaniHesapla(h);
            muayeneSuresiHesapla(h);
            oncelikSirasi.insert(h);
        }

        // GUI'yi başlat
        new HospitalGUI(hastalar, hastaSayisi, oncelikSirasi);
    }

    static void hastaKayitEkle(String dosyaAdi) {
        File dosya = new File("src/" + dosyaAdi);
        if (!dosya.exists()) {
            dosya = new File(dosyaAdi);
        }
        System.out.println("Hasta dosyası okunuyor: " + dosya.getAbsolutePath());
        if (!dosya.exists()) {
            JOptionPane.showMessageDialog(null, 
                "Hasta.txt dosyası bulunamadı! Lütfen dosyanın src klasöründe olduğundan emin olun.",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String line;
            int hastaNo = 1;
            int okunanSatir = 0;
            while ((line = br.readLine()) != null && hastaSayisi < MAX_HASTA) {
                okunanSatir++;
                if (okunanSatir == 1) continue; // Sadece ilk satırı atla (başlık)
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(",");
                if (tokens.length < 8) {
                    System.out.println("Geçersiz satır atlandı (satır " + okunanSatir + "): " + line);
                    continue;
                }
                try {
                    String hastaAdi = tokens[1].trim();
                    int hastaYasi = Integer.parseInt(tokens[2].trim());
                    String cinsiyet = tokens[3].trim();
                    boolean mahkumlukDurumu = tokens[4].trim().equalsIgnoreCase("true");
                    int engellilikOrani = Integer.parseInt(tokens[5].trim());
                    String kanamaDurumu = tokens[6].trim().toLowerCase();
                    String kayitSaati = tokens[7].trim();
                    String aciliyetDurumu = "normal";
                    double muayeneSuresi = 10.0;
                    if (tokens.length > 8 && !tokens[8].trim().equals("*")) {
                        try {
                            muayeneSuresi = Double.parseDouble(tokens[8].trim());
                        } catch (Exception ex) {
                            muayeneSuresi = 10.0;
                        }
                    }
                    if (kanamaDurumu.equals("kanamayok")) kanamaDurumu = "yok";
                    else if (kanamaDurumu.equals("agirkanama")) kanamaDurumu = "ağır kanama";
                    else if (kanamaDurumu.equals("kanama")) kanamaDurumu = "kanama";
                    Hasta h = new Hasta(hastaNo++, hastaAdi, hastaYasi, kanamaDurumu,
                            mahkumlukDurumu, engellilikOrani, aciliyetDurumu, muayeneSuresi, cinsiyet, kayitSaati);
                    hastalar[hastaSayisi++] = h;
                    System.out.println("Hasta eklendi (satır " + okunanSatir + "): " + h.hastaAdi +
                            " (No: " + h.hastaNo + ", Yaş: " + h.hastaYasi + ", Kanama: " + h.kanamaDurumu + ", Cinsiyet: " + h.cinsiyet + ", Kayıt: " + h.kayitSaati + ")");
                } catch (NumberFormatException e) {
                    System.out.println("Sayısal değer dönüştürme hatası (satır " + okunanSatir + "): " + line);
                    continue;
                } catch (Exception e) {
                    System.out.println("Hasta eklenirken hata (satır " + okunanSatir + "): " + e.getMessage());
                    continue;
                }
            }
            System.out.println("Dosya okuma tamamlandı:");
            System.out.println("- Toplam okunan satır: " + okunanSatir);
            System.out.println("- Eklenen hasta sayısı: " + hastaSayisi);
            if (hastaSayisi == 0) {
                JOptionPane.showMessageDialog(null,
                        "Hiç hasta kaydı bulunamadı! Lütfen Hasta.txt dosyasını kontrol edin.",
                        "Uyarı",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException e) {
            System.out.println("Dosya okuma hatası: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Hasta kayıtları okunurken hata oluştu: " + e.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    static void oncelikPuaniHesapla(Hasta h) {
        int yasPuani = 0;
        if (h.hastaYasi >= 0 && h.hastaYasi < 5) yasPuani = 20;
        else if (h.hastaYasi >= 5 && h.hastaYasi < 45) yasPuani = 0;
        else if (h.hastaYasi >= 45 && h.hastaYasi < 65) yasPuani = 15;
        else if (h.hastaYasi >= 65) yasPuani = 25;

        int mahkumlukDurumBilgisi = h.mahkumlukDurumu ? 50 : 0;
        int engellilikPuani = h.engellilikOrani / 4;

        int kanamaliHastaDurumBilgisi = 0;
        switch (h.kanamaDurumu.toLowerCase()) {
            case "kanamayok":
            case "yok":
                kanamaliHastaDurumBilgisi = 0; break;
            case "kanama":
                kanamaliHastaDurumBilgisi = 20; break;
            case "ağır kanama":
            case "agirkanama":
                kanamaliHastaDurumBilgisi = 50; break;
        }

        h.oncelikPuani = yasPuani + engellilikPuani + mahkumlukDurumBilgisi + kanamaliHastaDurumBilgisi;
    }

    static void muayeneSuresiHesapla(Hasta h) {
        int yasPuani = (h.hastaYasi > 65) ? 15 : 0;
        int engellilikPuani = h.engellilikOrani / 5;

        int kanamaliHastaDurumBilgisi = 0;
        switch (h.kanamaDurumu.toLowerCase()) {
            case "kanamayok":
            case "yok":
                kanamaliHastaDurumBilgisi = 0; break;
            case "kanama":
                kanamaliHastaDurumBilgisi = 10; break;
            case "ağır kanama":
            case "agirkanama":
                kanamaliHastaDurumBilgisi = 20; break;
        }

        h.muayeneSuresi = yasPuani + engellilikPuani + kanamaliHastaDurumBilgisi + 10;
    }
}
