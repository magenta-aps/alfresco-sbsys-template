package dk.magenta.alfresco.sbsys.template.json;

public class Address {

    private int Id;
    private String Adresse1;
    private int PostNummer;
    private String PostDistrikt;
    private boolean ErBeskyttet;
    private String WorkTelefonNummer;
    private String PrivateEmailAdresse;
    private String PrivateMobilNummer;
    private String PrivateTelefonNummer;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getAdresse1() {
        return Adresse1;
    }

    public void setAdresse1(String adresse1) {
        Adresse1 = adresse1;
    }

    public int getPostNummer() {
        return PostNummer;
    }

    public void setPostNummer(int postNummer) {
        PostNummer = postNummer;
    }

    public String getPostDistrikt() {
        return PostDistrikt;
    }

    public void setPostDistrikt(String postDistrikt) {
        PostDistrikt = postDistrikt;
    }

    public boolean isErBeskyttet() {
        return ErBeskyttet;
    }

    public void setErBeskyttet(boolean erBeskyttet) {
        ErBeskyttet = erBeskyttet;
    }

    public String getWorkTelefonNummer() {
        return WorkTelefonNummer;
    }

    public void setWorkTelefonNummer(String workTelefonNummer) {
        WorkTelefonNummer = workTelefonNummer;
    }

    public String getPrivateEmailAdresse() {
        return PrivateEmailAdresse;
    }

    public void setPrivateEmailAdresse(String privateEmailAdresse) {
        PrivateEmailAdresse = privateEmailAdresse;
    }

    public String getPrivateMobilNummer() {
        return PrivateMobilNummer;
    }

    public void setPrivateMobilNummer(String privateMobilNummer) {
        PrivateMobilNummer = privateMobilNummer;
    }

    public String getPrivateTelefonNummer() {
        return PrivateTelefonNummer;
    }

    public void setPrivateTelefonNummer(String privateTelefonNummer) {
        PrivateTelefonNummer = privateTelefonNummer;
    }
}
