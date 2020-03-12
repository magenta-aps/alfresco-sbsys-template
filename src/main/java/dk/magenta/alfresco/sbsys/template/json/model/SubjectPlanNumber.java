package dk.magenta.alfresco.sbsys.template.json.model;

import java.util.List;

public class SubjectPlanNumber {

    private int Id;
    private int EmneplanID;
    private String Nummer;
    private String Navn;
    private String Beskrivelse;
    private int Niveau;
    private String Oprettet;
    private boolean ErUdgaaet;
    private List<String> AfloserNumre;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getEmneplanID() {
        return EmneplanID;
    }

    public void setEmneplanID(int emneplanID) {
        EmneplanID = emneplanID;
    }

    public String getNummer() {
        return Nummer;
    }

    public void setNummer(String nummer) {
        Nummer = nummer;
    }

    public String getNavn() {
        return Navn;
    }

    public void setNavn(String navn) {
        Navn = navn;
    }

    public String getBeskrivelse() {
        return Beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        Beskrivelse = beskrivelse;
    }

    public int getNiveau() {
        return Niveau;
    }

    public void setNiveau(int niveau) {
        Niveau = niveau;
    }

    public String getOprettet() {
        return Oprettet;
    }

    public void setOprettet(String oprettet) {
        Oprettet = oprettet;
    }

    public boolean isErUdgaaet() {
        return ErUdgaaet;
    }

    public void setErUdgaaet(boolean erUdgaaet) {
        ErUdgaaet = erUdgaaet;
    }

    public List<String> getAfloserNumre() {
        return AfloserNumre;
    }

    public void setAfloserNumre(List<String> afloserNumre) {
        AfloserNumre = afloserNumre;
    }
}
