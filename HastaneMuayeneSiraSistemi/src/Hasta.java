public class Hasta {
    public int hastaNo;
    public int siraNo;
    public String hastaAdi;
    public int hastaYasi;
    public String kanamaDurumu;
    public boolean mahkumlukDurumu;
    public int engellilikOrani;
    public String aciliyetDurumu;
    public double muayeneSuresi;
    public String muayeneSaatiNum;
    public int oncelikPuani;
    public String cinsiyet;
    public String kayitSaati;
    public String muayeneSaati;

    public Hasta(int hastaNo, String hastaAdi, int hastaYasi, String kanamaDurumu,
                boolean mahkumlukDurumu, int engellilikOrani, String aciliyetDurumu,
                double muayeneSuresi, String cinsiyet, String kayitSaati) {
        this.hastaNo = hastaNo;
        this.hastaAdi = hastaAdi;
        this.hastaYasi = hastaYasi;
        this.kanamaDurumu = kanamaDurumu;
        this.mahkumlukDurumu = mahkumlukDurumu;
        this.engellilikOrani = engellilikOrani;
        this.aciliyetDurumu = aciliyetDurumu;
        this.muayeneSuresi = muayeneSuresi;
        this.siraNo = 0;
        this.cinsiyet = cinsiyet;
        this.kayitSaati = kayitSaati;
        this.muayeneSaati = "-";
        this.muayeneSaatiNum = "-";
    }
}
