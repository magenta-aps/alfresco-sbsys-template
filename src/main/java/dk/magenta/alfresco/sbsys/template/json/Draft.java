package dk.magenta.alfresco.sbsys.template.json;

public class Draft {

    private String SagID;
    private String Navn;
    private CasePart SagsPart;

    public String getSagID() {
        return SagID;
    }

    public void setSagID(String sagID) {
        SagID = sagID;
    }

    public String getNavn() {
        return Navn;
    }

    public void setNavn(String navn) {
        Navn = navn;
    }

    public CasePart getSagsPart() {
        return SagsPart;
    }

    public void setSagsPart(CasePart sagsPart) {
        SagsPart = sagsPart;
    }
}
