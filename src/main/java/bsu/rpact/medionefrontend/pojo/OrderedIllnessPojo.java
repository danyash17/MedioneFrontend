package bsu.rpact.medionefrontend.pojo;

public class OrderedIllnessPojo {
    private Integer order;
    private String description;

    public OrderedIllnessPojo(Integer order, String description) {
        this.order = order;
        this.description = description;
    }

    public OrderedIllnessPojo() {
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
