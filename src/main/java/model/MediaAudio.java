package model;

public class MediaAudio {
    protected int idLagu;
    protected String judulLagu;

    public MediaAudio(int idLagu, String judulLagu) {
        this.idLagu = idLagu;
        this.judulLagu = judulLagu;
    }

    public int getIdLagu() { return idLagu; }
    public String getJudulLagu() { return judulLagu; }
}