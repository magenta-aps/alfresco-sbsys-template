package dk.magenta.alfresco.sbsys.template.upload;

public class CheckoutJsonBuilderStrategy implements JsonBuilderStrategy {
    @Override
    public String build(UploadDocument uploadDocument) {
        return "{}";
    }
}
