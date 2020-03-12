package dk.magenta.alfresco.sbsys.template.json.model;

public class CasePart {

    private int PartId;
    private String PartType;

    public CasePart(int partId, String partType) {
        PartId = partId;
        PartType = partType;
    }

    public int getPartId() {
        return PartId;
    }

    public void setPartId(int partId) {
        PartId = partId;
    }

    public String getPartType() {
        return PartType;
    }

    public void setPartType(String partType) {
        PartType = partType;
    }
}
