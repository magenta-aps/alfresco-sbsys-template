package dk.magenta.alfresco.sbsys.template.json;

import java.util.Map;

public class PreviewRequest {

    private String filnavn;
    private String kladdeID;
    private Map<String, String> token;

    public PreviewRequest(String filnavn, String kladdeID, Map<String, String> token) {
        this.filnavn = filnavn;
        this.kladdeID = kladdeID;
        this.token = token;
    }

    public String getKladdeID() {
        return kladdeID;
    }

    public void setFilnavn(String filnavn) {
        this.filnavn = filnavn;
    }

    public String getFilnavn() {
        return filnavn;
    }

    public void setKladdeID(String kladdeID) {
        this.kladdeID = kladdeID;
    }

    public Map<String, String> getToken() {
        return token;
    }

    public void setToken(Map<String, String> token) {
        this.token = token;
    }
}
