package dk.magenta.alfresco.sbsys.template.json;

import java.util.Map;

public class CaseNumber {

    private int Aarstal;
    private SubjectPlanNumber EmneplanNummer;
    private Map<String, String> Facet;
    private int SekvensNummer;

    public int getAarstal() {
        return Aarstal;
    }

    public void setAarstal(int aarstal) {
        Aarstal = aarstal;
    }

    public SubjectPlanNumber getEmneplanNummer() {
        return EmneplanNummer;
    }

    public void setEmneplanNummer(SubjectPlanNumber emneplanNummer) {
        EmneplanNummer = emneplanNummer;
    }

    public Map<String, String> getFacet() {
        return Facet;
    }

    public void setFacet(Map<String, String> facet) {
        Facet = facet;
    }

    public int getSekvensNummer() {
        return SekvensNummer;
    }

    public void setSekvensNummer(int sekvensNummer) {
        SekvensNummer = sekvensNummer;
    }
}
