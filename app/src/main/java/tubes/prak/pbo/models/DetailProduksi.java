package tubes.prak.pbo.models;

public class DetailProduksi {
    private int idDetailProduksi;
    private int idProduksi;
    private int idRawMaterial;
    private String namaMaterial;
    private double qtyPerUnit;
    private double totalKebutuhan;
    private double hargaRata2SaatItu;
    private double subtotalHpp;

    public DetailProduksi(int idDetailProduksi, int idProduksi, int idRawMaterial,
                          String namaMaterial, double qtyPerUnit, double totalKebutuhan,
                          double hargaRata2SaatItu, double subtotalHpp) {
        this.idDetailProduksi = idDetailProduksi;
        this.idProduksi = idProduksi;
        this.idRawMaterial = idRawMaterial;
        this.namaMaterial = namaMaterial;
        this.qtyPerUnit = qtyPerUnit;
        this.totalKebutuhan = totalKebutuhan;
        this.hargaRata2SaatItu = hargaRata2SaatItu;
        this.subtotalHpp = subtotalHpp;
    }

    public int getIdDetailProduksi() { return idDetailProduksi; }
    public int getIdProduksi() { return idProduksi; }
    public int getIdRawMaterial() { return idRawMaterial; }
    public String getNamaMaterial() { return namaMaterial; }
    public double getQtyPerUnit() { return qtyPerUnit; }
    public double getTotalKebutuhan() { return totalKebutuhan; }
    public double getHargaRata2SaatItu() { return hargaRata2SaatItu; }
    public double getSubtotalHpp() { return subtotalHpp; }
}