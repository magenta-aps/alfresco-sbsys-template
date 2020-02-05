package dk.magenta.sbsys.template.multipart;

public class Request {

    private String json;
    private String filename;
    private String mimeType;
    private String url;
    private String token;
    private String contentStorePath;

    public Request(String json, String filename, String mimeType, String url, String token, String contentStorePath) {
        this.json = json;
        this.filename = filename;
        this.mimeType = mimeType;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
