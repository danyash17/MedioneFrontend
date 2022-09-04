package bsu.rpact.medionefrontend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "operation", schema = "medione")
public class Operation {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "operation_date", columnDefinition = "DATE")
    private Timestamp operationDate;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "description")
    private String description;

    public Operation() {
    }

    public Operation(Timestamp operationDate, String name, String description) {
        this.operationDate = operationDate;
        this.name = name;
        this.description = description;
    }

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "operation_medcard_id")
    @JsonBackReference(value = "medcard")
    private Medcard medcard;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Timestamp operationDate) {
        this.operationDate = operationDate;
    }

    public Medcard getMedcard() {
        return medcard;
    }

    public void setMedcard(Medcard medcard) {
        this.medcard = medcard;
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
