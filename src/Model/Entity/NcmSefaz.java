package Model.Entity;

public class NcmSefaz {

    private final String ncm;
    private String cst = "";
    private String obs = "";
    private String aliquota = "";
    private String mvaOriginal = "";
    private String mva12 = "";
    private String mva4 = "";
    private String estados = "";
    private String descricao = "";

    public NcmSefaz(String ncm) {
        this.ncm = ncm.trim().replaceAll("[^0-9]", "");
    }

    public static NcmSefaz copy(NcmSefaz other) {
        NcmSefaz newNcmSefaz = new NcmSefaz(other.getNcm());
        newNcmSefaz.setObs(other.getObs());
        newNcmSefaz.setCst(other.getCst());
        return newNcmSefaz;
    }

    public void setCst(String cst) {
        this.cst = cst;
    }

    public void setObs(String obs) {
        this.obs = obs.replaceAll("[^a-zA-Z0-9áàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ ]", " ").trim();
        while (this.obs.contains("  ")) this.obs = this.obs.replaceAll("  ", " ");
    }

    public void setAliquota(String aliquota) {
        this.aliquota = aliquota;
    }

    public void setMvaOriginal(String mvaOriginal) {
        this.mvaOriginal = mvaOriginal;
    }

    public void setMva12(String mva12) {
        this.mva12 = mva12;
    }

    public void setMva4(String mva4) {
        this.mva4 = mva4;
    }

    public String getObs() {
        return obs;
    }

    public String getNcm() {
        return ncm;
    }

    public String getNcmFormatado() {
        return ncm.substring(0, 4) + "." + ncm.substring(4, 6) + "." + ncm.substring(6, 8);
    }

    public String getCst() {
        return cst;
    }

    public String getAliquota() {
        return aliquota;
    }

    public String getMvaOriginal() {
        return mvaOriginal;
    }

    public String getMva12() {
        return mva12;
    }

    public String getMva4() {
        return mva4;
    }

    public String getEstados() {
        return estados;
    }

    public void addInEstados(String add) {
        this.estados += add;
    }

    public void setEstados(String estados) {
        this.estados = estados;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao.replaceAll("[^a-zA-Z0-9áàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ ]", " ").trim();
        while (this.descricao.contains("  ")) this.descricao = this.descricao.replaceAll("  ", " ");
    }

}
