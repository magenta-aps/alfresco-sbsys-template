package dk.magenta.alfresco.sbsys.template.json;

public class CompanyAddress {

    private int Id;
    private boolean ErUdlandsadresse;
    private String Adresse1;
    private String Adresse2;
    private String Adresse3;
    private String Adresse4;
    private int PostNummer;
    private String LandeKode;
    private boolean ErBeskyttet;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public boolean isErUdlandsadresse() {
        return ErUdlandsadresse;
    }

    public void setErUdlandsadresse(boolean erUdlandsadresse) {
        ErUdlandsadresse = erUdlandsadresse;
    }

    public String getAdresse1() {
        return Adresse1;
    }

    public void setAdresse1(String adresse1) {
        Adresse1 = adresse1;
    }

    public String getAdresse2() {
        return Adresse2;
    }

    public void setAdresse2(String adresse2) {
        Adresse2 = adresse2;
    }

    public String getAdresse3() {
        return Adresse3;
    }

    public void setAdresse3(String adresse3) {
        Adresse3 = adresse3;
    }

    public String getAdresse4() {
        return Adresse4;
    }

    public void setAdresse4(String adresse4) {
        Adresse4 = adresse4;
    }

    public int getPostNummer() {
        return PostNummer;
    }

    public void setPostNummer(int postNummer) {
        PostNummer = postNummer;
    }

    public String getLandeKode() {
        return LandeKode;
    }

    public void setLandeKode(String landeKode) {
        LandeKode = landeKode;
    }

    public boolean isErBeskyttet() {
        return ErBeskyttet;
    }

    public void setErBeskyttet(boolean erBeskyttet) {
        ErBeskyttet = erBeskyttet;
    }
}
