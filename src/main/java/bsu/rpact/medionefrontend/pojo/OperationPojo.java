package bsu.rpact.medionefrontend.pojo;

import java.time.LocalDateTime;

public class OperationPojo {
    private LocalDateTime operationDate;
    private String name;
    private String description;

    public OperationPojo(LocalDateTime operationDate, String name, String description) {
        this.operationDate = operationDate;
        this.name = name;
        this.description = description;
    }

    public OperationPojo() {
    }

    public LocalDateTime getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(LocalDateTime operationDate) {
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
