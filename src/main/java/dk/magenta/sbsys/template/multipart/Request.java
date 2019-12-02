package dk.magenta.sbsys.template.multipart;

public class Request {

    private int caseId;
    private String name;
    private String mimeType;
    private String token;
    private String contentStorePath;

    public Request(int caseId, String name, String mimeType, String token, String contentStorePath) {
        this.caseId = caseId;
        this.name = name;
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
