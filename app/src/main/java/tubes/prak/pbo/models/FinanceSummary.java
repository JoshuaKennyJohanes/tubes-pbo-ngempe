package tubes.prak.pbo.models;

public class FinanceSummary {
    private final double totalCash;
    private final double nilaiRawMaterial;
    private final double nilaiFinishedGoods;
    private final double totalPembelianBulanan;
    private final double totalOpnameBulanan;
    private final double totalRevenueBulanan;
    private final double totalCogsBulanan;

    public FinanceSummary(double totalCash, double nilaiRawMaterial, double nilaiFinishedGoods,
                          double totalPembelianBulanan, double totalOpnameBulanan,
                          double totalRevenueBulanan, double totalCogsBulanan) {
        this.totalCash = totalCash;
        this.nilaiRawMaterial = nilaiRawMaterial;
        this.nilaiFinishedGoods = nilaiFinishedGoods;
        this.totalPembelianBulanan = totalPembelianBulanan;
        this.totalOpnameBulanan = totalOpnameBulanan;
        this.totalRevenueBulanan = totalRevenueBulanan;
        this.totalCogsBulanan = totalCogsBulanan;
    }

    public double getTotalCash() {
        return totalCash;
    }

    public double getNilaiRawMaterial() {
        return nilaiRawMaterial;
    }

    public double getNilaiFinishedGoods() {
        return nilaiFinishedGoods;
    }

    public double getTotalPembelianBulanan() {
        return totalPembelianBulanan;
    }

    public double getTotalOpnameBulanan() {
        return totalOpnameBulanan;
    }

    public double getTotalExpensesBulanan() {
        return totalPembelianBulanan + totalOpnameBulanan;
    }

    public double getTotalRevenueBulanan() {
        return totalRevenueBulanan;
    }

    public double getTotalCogsBulanan() {
        return totalCogsBulanan;
    }

    public double getNetProfitBulanan() {
        return totalRevenueBulanan - totalCogsBulanan - totalOpnameBulanan;
    }

    public double getTotalAssets() {
        return totalCash + nilaiRawMaterial + nilaiFinishedGoods + getNetProfitBulanan();
    }
}