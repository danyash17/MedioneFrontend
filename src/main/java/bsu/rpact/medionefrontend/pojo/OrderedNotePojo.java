package bsu.rpact.medionefrontend.pojo;

public class OrderedNotePojo {
    private Integer order;
    private String name;

    public OrderedNotePojo(Integer order, String name) {
        this.order = order;
        this.name = name;
    }

    public OrderedNotePojo() {
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
