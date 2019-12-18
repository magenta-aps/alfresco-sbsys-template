package dk.magenta.alfresco.sbsys.template.json;

import java.util.Map;

public class Merge {

    private String id;
    private Draft kladde;
    private Map<String, String> urls;
    private Map<String, String> token;

    public Merge(String id, Draft kladde, Map<String, String> urls, Map<String, String> token) {
        this.id = id;
        this.kladde = kladde;
        this.urls = urls;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Draft getKladde() {
        return kladde;
    }

    public void setKladde(Draft kladde) {
        this.kladde = kladde;
    }

    public Map<String, String> getUrls() {
        return urls;
    }

    public void setUrls(Map<String, String> urls) {
        this.urls = urls;
    }

    public Map<String, String> getToken() {
        return token;
    }

    public void setToken(Map<String, String> token) {
        this.token = token;
    }
}
