package bsu.rpact.medionefrontend.pojo.other;

public class Href {

    private String label;
    private String link;

    public Href(String label, String link) {
        this.label = label;
        this.link = link;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
