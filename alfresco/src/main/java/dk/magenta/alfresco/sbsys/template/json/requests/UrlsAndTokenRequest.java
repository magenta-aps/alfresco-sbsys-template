package dk.magenta.alfresco.sbsys.template.json.requests;

import java.util.Map;

public class UrlsAndTokenRequest {

    private Map<String, String> token;
    private Map<String, String> urls;

    public UrlsAndTokenRequest(Map<String, String> token, Map<String, String> urls) {
        this.token = token;
        this.urls = urls;
    }

    public Map<String, String> getToken() {
        return token;
    }

    public void setToken(Map<String, String> token) {
        this.token = token;
    }

    public Map<String, String> getUrls() {
        return urls;
    }

    public void setUrls(Map<String, String> urls) {
        this.urls = urls;
    }
}
