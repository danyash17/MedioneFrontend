package bsu.rpact.medionefrontend.pojo;


import bsu.rpact.medionefrontend.enums.DocumentType;

public class DocumentPojo {
    private String name;
    private String description;
    private byte[] image;
    private DocumentType documentType;

    public DocumentPojo(String name, String description, byte[] image, DocumentType documentType) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.documentType = documentType;
    }

    public DocumentPojo() {
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

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
