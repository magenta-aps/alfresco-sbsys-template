package dk.magenta.alfresco.sbsys.template.json.model;

public class Company {

    private boolean Reklamebeskyttelse;
    private String  CvrNummer;
    private CompanyAddress Adresse;
    private boolean ErJuridiskEnhed;
    private boolean TilmeldtDigitalPost;
    private String Uuid;
    private int Id;
    private String Navn;

    public boolean isReklamebeskyttelse() {
        return Reklamebeskyttelse;
    }

    public void setReklamebeskyttelse(boolean reklamebeskyttelse) {
        Reklamebeskyttelse = reklamebeskyttelse;
    }

    public String getCvrNummer() {
        return CvrNummer;
    }

    public void setCvrNummer(String cvrNummer) {
        CvrNummer = cvrNummer;
    }

    public CompanyAddress getAdresse() {
        return Adresse;
    }

    public void setAdresse(CompanyAddress adresse) {
        Adresse = adresse;
    }

    public boolean isErJuridiskEnhed() {
        return ErJuridiskEnhed;
    }

    public void setErJuridiskEnhed(boolean erJuridiskEnhed) {
        ErJuridiskEnhed = erJuridiskEnhed;
    }

    public boolean isTilmeldtDigitalPost() {
        return TilmeldtDigitalPost;
    }

    public void setTilmeldtDigitalPost(boolean tilmeldtDigitalPost) {
        TilmeldtDigitalPost = tilmeldtDigitalPost;
    }

    public String getUuid() {
        return Uuid;
    }

    public void setUuid(String uuid) {
        Uuid = uuid;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNavn() {
        return Navn;
    }

    public void setNavn(String navn) {
        Navn = navn;
    }
}
