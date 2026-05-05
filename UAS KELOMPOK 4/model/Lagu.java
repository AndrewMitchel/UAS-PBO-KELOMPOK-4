package model;

public class Lagu extends MediaAudio implements DataTemplate {
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