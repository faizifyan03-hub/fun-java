package model;

import java.util.Objects;

public class Topping {
    private String namaTopping;
    private double hargaTopping;

    public Topping(String namaTopping, double hargaTopping) {
        this.namaTopping = namaTopping;
        this.hargaTopping = hargaTopping;
    }

    public String getNamaTopping() {
        return namaTopping;
    }

    public void setNamaTopping(String namaTopping) {
        this.namaTopping = namaTopping;
    }

    public double getHargaTopping() {
        return hargaTopping;
    }

    public void setHargaTopping(double hargaTopping) {
        this.hargaTopping = hargaTopping;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topping topping = (Topping) o;
        return Objects.equals(namaTopping, topping.namaTopping);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namaTopping);
    }

    @Override
    public String toString() {
        return String.format("%s (+Rp %,.0f)", namaTopping, hargaTopping);
    }
}
