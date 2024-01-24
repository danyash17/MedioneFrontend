package bsu.rpact.medionefrontend.pojo;

public class NotePojo {
    private String name;
    private String text;

    public NotePojo(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public NotePojo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
