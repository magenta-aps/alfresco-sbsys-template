package dk.magenta.alfresco.sbsys.template.json.requests;

public class Upload403 {

    private String msg;
    private int uploadStatus;

    public Upload403(String msg, int uploadStatus) {
        this.msg = msg;
        this.uploadStatus = uploadStatus;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }
}
