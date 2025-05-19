# hastaneOtomasyonu

# Hastane Muayene Sıra Sistemi

Bu proje, hastanelerde hasta muayene sırası yönetimini sağlayan bir masaüstü uygulamasıdır. Sistem, hastaların önceliklerine göre sıralanmasını ve randevu yönetimini kolaylaştırmayı amaçlamaktadır.

## Özellikler

- Hasta kayıt ve bilgi yönetimi
- Öncelikli sıra sistemi (Heap veri yapısı kullanılarak)
- Kullanıcı dostu grafiksel arayüz
- Hasta bilgilerinin dosyada saklanması
- Randevu takibi ve yönetimi

## Teknik Detaylar

### Kullanılan Teknolojiler

- Java Programming Language
- Swing GUI Framework
- Heap Veri Yapısı
- Dosya İşlemleri

### Proje Yapısı

```
src/
├── Main.java              # Ana uygulama başlangıç noktası
├── HospitalGUI.java       # Grafiksel kullanıcı arayüzü
├── Hasta.java            # Hasta sınıfı ve ilgili işlemler
├── Heap.java             # Öncelikli sıra için heap implementasyonu
└── Hasta.txt             # Hasta verilerinin saklandığı dosya
```

## Kurulum

1. Projeyi bilgisayarınıza klonlayın:
```bash
git clone [repository-url]
```

2. Projeyi bir Java IDE'sinde açın (IntelliJ IDEA, Eclipse vb.)

3. Projeyi derleyin ve çalıştırın:
   - IDE üzerinden `Main.java` dosyasını çalıştırın
   - veya terminal üzerinden:
   ```bash
   javac src/*.java
   java -cp src Main
   ```

## Kullanım

1. Uygulama başlatıldığında ana pencere açılacaktır
2. Yeni hasta eklemek için ilgili form alanlarını doldurun
3. Hastaları önceliklerine göre sıralamak için sistem otomatik olarak heap veri yapısını kullanır
4. Hasta bilgileri otomatik olarak `Hasta.txt` dosyasında saklanır

## Geliştirici

- Mehmet Ferit Bilen

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için `LICENSE` dosyasına bakınız. 


![image](https://github.com/user-attachments/assets/302fcf2c-9b39-4fea-98cd-c59d52fa1558)
