package dk.magenta.alfresco.sbsys.template;

import java.util.List;
import java.util.Map;

public class Case {

    private String SagsTitel;
    private List<Object> Personer;
    private List<Object> Firmaer;
    private int Id;
    private String SagIdentity;
    private String Opstaaet;
    private String Nummer;
    private Map<String, String> Fagomraade;
    private int BevaringId;
    private String SenesteStatusAendringKommentar;
    private String SenesteStatusAendring;
    private String Oprettet;
    private String SenestAendret;
    private boolean ErBeskyttet;
    private boolean ErBesluttet;
    private boolean BeslutningHarDeadline;
    private int SagsNummerId;
    private Map<String, String> Ansaettelsessted;
    private Map<String, String> Behandler;
    private Map<String, String> SagsStatus;
    private int ArkivAfklaringStatusId;
    private Map<String, String> OprettetAf;
    private Map<String, String> SenestAendretAf;
    private Map<String, String> StyringsreolHylde;
    private int SecuritySetId;

    public String getSagsTitel() {
        return SagsTitel;
    }

    public void setSagsTitel(String sagsTitel) {
        SagsTitel = sagsTitel;
    }

    public List<Object> getPersoner() {
        return Personer;
    }

    public void setPersoner(List<Object> personer) {
        Personer = personer;
    }

    public List<Object> getFirmaer() {
        return Firmaer;
    }

    public void setFirmaer(List<Object> firmaer) {
        Firmaer = firmaer;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getSagIdentity() {
        return SagIdentity;
    }

    public void setSagIdentity(String sagIdentity) {
        SagIdentity = sagIdentity;
    }

    public String getOpstaaet() {
        return Opstaaet;
    }

    public void setOpstaaet(String opstaaet) {
        Opstaaet = opstaaet;
    }

    public String getNummer() {
        return Nummer;
    }

    public void setNummer(String nummer) {
        Nummer = nummer;
    }

    public Map<String, String> getFagomraade() {
        return Fagomraade;
    }

    public void setFagomraade(Map<String, String> fagomraade) {
        Fagomraade = fagomraade;
    }

    public int getBevaringId() {
        return BevaringId;
    }

    public void setBevaringId(int bevaringId) {
        BevaringId = bevaringId;
    }

    public String getSenesteStatusAendringKommentar() {
        return SenesteStatusAendringKommentar;
    }

    public void setSenesteStatusAendringKommentar(String senesteStatusAendringKommentar) {
        SenesteStatusAendringKommentar = senesteStatusAendringKommentar;
    }

    public String getSenesteStatusAendring() {
        return SenesteStatusAendring;
    }

    public void setSenesteStatusAendring(String senesteStatusAendring) {
        SenesteStatusAendring = senesteStatusAendring;
    }

    public String getOprettet() {
        return Oprettet;
    }

    public void setOprettet(String oprettet) {
        Oprettet = oprettet;
    }

    public String getSenestAendret() {
        return SenestAendret;
    }

    public void setSenestAendret(String senestAendret) {
        SenestAendret = senestAendret;
    }

    public boolean isErBeskyttet() {
        return ErBeskyttet;
    }

    public void setErBeskyttet(boolean erBeskyttet) {
        ErBeskyttet = erBeskyttet;
    }

    public boolean isErBesluttet() {
        return ErBesluttet;
    }

    public void setErBesluttet(boolean erBesluttet) {
        ErBesluttet = erBesluttet;
    }

    public boolean isBeslutningHarDeadline() {
        return BeslutningHarDeadline;
    }

    public void setBeslutningHarDeadline(boolean beslutningHarDeadline) {
        BeslutningHarDeadline = beslutningHarDeadline;
    }

    public int getSagsNummerId() {
        return SagsNummerId;
    }

    public void setSagsNummerId(int sagsNummerId) {
        SagsNummerId = sagsNummerId;
    }

    public Map<String, String> getAnsaettelsessted() {
        return Ansaettelsessted;
    }

    public void setAnsaettelsessted(Map<String, String> ansaettelsessted) {
        Ansaettelsessted = ansaettelsessted;
    }

    public Map<String, String> getBehandler() {
        return Behandler;
    }

    public void setBehandler(Map<String, String> behandler) {
        Behandler = behandler;
    }

    public Map<String, String> getSagsStatus() {
        return SagsStatus;
    }

    public void setSagsStatus(Map<String, String> sagsStatus) {
        SagsStatus = sagsStatus;
    }

    public int getArkivAfklaringStatusId() {
        return ArkivAfklaringStatusId;
    }

    public void setArkivAfklaringStatusId(int arkivAfklaringStatusId) {
        ArkivAfklaringStatusId = arkivAfklaringStatusId;
    }

    public Map<String, String> getOprettetAf() {
        return OprettetAf;
    }

    public void setOprettetAf(Map<String, String> oprettetAf) {
        OprettetAf = oprettetAf;
    }

    public Map<String, String> getSenestAendretAf() {
        return SenestAendretAf;
    }

    public void setSenestAendretAf(Map<String, String> senestAendretAf) {
        SenestAendretAf = senestAendretAf;
    }

    public Map<String, String> getStyringsreolHylde() {
        return StyringsreolHylde;
    }

    public void setStyringsreolHylde(Map<String, String> styringsreolHylde) {
        StyringsreolHylde = styringsreolHylde;
    }

    public int getSecuritySetId() {
        return SecuritySetId;
    }

    public void setSecuritySetId(int securitySetId) {
        SecuritySetId = securitySetId;
    }
}