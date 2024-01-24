package bsu.rpact.medionefrontend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "document_folder", schema = "medione")
public class DocumentFolder {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    public DocumentFolder() {
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "documentFolder")
    private List<Document> documentList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Document> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<Document> documentList) {
        this.documentList = documentList;
    }

    public void addDocumentToDocumentFolder(Document document){
        if(documentList == null){
            documentList = new ArrayList<>();
        }
        documentList.add(document);
        document.setDocumentFolder(this);
    }

}
