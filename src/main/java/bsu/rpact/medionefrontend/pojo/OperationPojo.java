package bsu.rpact.medionefrontend.pojo;

import java.util.Date;

public class OperationPojo {
    private Date operationDate;
    private String name;
    private String description;

    public OperationPojo(Date operationDate, String name, String description) {
        this.operationDate = operationDate;
        this.name = name;
        this.description = description;
    }

    public OperationPojo() {
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
