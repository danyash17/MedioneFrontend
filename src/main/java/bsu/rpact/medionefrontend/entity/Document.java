package bsu.rpact.medionefrontend.entity;

import bsu.rpact.medionefrontend.enums.DocumentType;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "document", schema = "medione")
public class Document {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "image")
    private byte[] image;
    @Basic
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private DocumentType type;
    @Basic
    @Column(name = "name")
    private String name;

    public Document() {
    }

    public Document(String description, byte[] image, DocumentType type, String name) {
        this.description = description;
        this.image = image;
        this.type = type;
        this.name = name;
    }

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "folder_id")
    @JsonBackReference
    private DocumentFolder documentFolder;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public DocumentFolder getDocumentFolder() {
        return documentFolder;
    }

    public void setDocumentFolder(DocumentFolder documentFolder) {
        this.documentFolder = documentFolder;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PreRemove
    public void preRemove(){
        if(documentFolder!=null){
            documentFolder.getDocumentList().remove(this);
            documentFolder = null;
        }
    }
}
