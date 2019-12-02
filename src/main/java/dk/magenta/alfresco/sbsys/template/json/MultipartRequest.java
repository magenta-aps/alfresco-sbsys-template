package dk.magenta.alfresco.sbsys.template.json;

public class MultipartRequest {

    private int caseId;
    private String name;
    private String filename;
    private String mimeType;
    private String token;
    private String contentStorePath;

    public MultipartRequest(int caseId, String name, String filename, String mimeType, String token, String contentStorePath) {
        this.caseId = caseId;
        this.name = name;
        this.filename = filename;
        this.mimeType = mimeType;
        this.token = token;
        this.contentStorePath = contentStorePath;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
