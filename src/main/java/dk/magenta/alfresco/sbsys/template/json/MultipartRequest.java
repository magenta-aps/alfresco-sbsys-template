package dk.magenta.alfresco.sbsys.template.json;

public class MultipartRequest {

    private String json;
    private String filename;
    private String mimeType;
    private String token;
    private String contentStorePath;

    public MultipartRequest(String json, String filename, String mimeType, String token, String contentStorePath) {
        this.json = json;
        this.filename = filename;
        this.mimeType = mimeType;
        this.token = token;
        this.contentStorePath = contentStorePath;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getContentStorePath() {
        return contentStorePath;
    }

    public void setContentStorePath(String contentStorePath) {
        this.contentStorePath = contentStorePath;
    }

}
