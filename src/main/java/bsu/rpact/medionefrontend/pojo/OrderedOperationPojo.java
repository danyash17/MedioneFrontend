package bsu.rpact.medionefrontend.pojo;

public class OrderedOperationPojo {
    private Integer order;
    private String name;

    public OrderedOperationPojo(Integer order, String name) {
        this.order = order;
        this.name = name;
    }

    public OrderedOperationPojo() {
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
