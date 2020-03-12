package dk.magenta.alfresco.sbsys.template.json.requests;

import java.util.Map;

public class Upload {

    private String preUploadId;
    private Map<String, String> token;

    public Upload(String preUploadId, Map<String, String> token) {
        this.preUploadId = preUploadId;
        this.token = token;
    }

    public String getPreUploadId() {
        return preUploadId;
    }

    public void setPreUploadId(String preUploadId) {
        this.preUploadId = preUploadId;
    }

    public Map<String, String> getToken() {
        return token;
    }

    public void setToken(Map<String, String> token) {
        this.token = token;
    }
}
