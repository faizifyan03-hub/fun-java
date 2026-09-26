package model;

public class VarianMie {
    private String namaMie;
    private String jenis; // "Goreng" atau "Kuah"
    private double hargaDasar;

    public VarianMie(String namaMie, String jenis, double hargaDasar) {
        this.namaMie = namaMie;
        this.jenis = jenis;
        this.hargaDasar = hargaDasar;
    }

    public String getNamaMie() {
        return namaMie;
    }

    public void setNamaMie(String namaMie) {
        this.namaMie = namaMie;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public double getHargaDasar() {
        return hargaDasar;
    }

    public void setHargaDasar(double hargaDasar) {
        this.hargaDasar = hargaDasar;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Rp %,.0f", namaMie, jenis, hargaDasar);
    }
}
