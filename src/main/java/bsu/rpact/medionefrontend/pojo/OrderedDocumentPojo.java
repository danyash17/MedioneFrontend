package bsu.rpact.medionefrontend.pojo;

import bsu.rpact.medionefrontend.enums.DocumentType;

public class OrderedDocumentPojo {
    private Integer order;
    private String name;
    private DocumentType documentType;

    public OrderedDocumentPojo(Integer order, String name, DocumentType documentType) {
        this.order = order;
        this.name = name;
        this.documentType = documentType;
    }

    public OrderedDocumentPojo() {
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

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
}
