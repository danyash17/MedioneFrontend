package bsu.rpact.medionefrontend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medcard", schema = "medione")
public class Medcard {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "date_created")
    private Date dateCreated;
    @Basic
    @Column(name = "valid_to")
    private Date validTo;
    @Basic
    @Column(name = "residental_address")
    private String residentalAddress;

    public Medcard() {
    }

    public Medcard(Date dateCreated, Date validTo, String residentalAddress) {
        this.dateCreated = dateCreated;
        this.validTo = validTo;
        this.residentalAddress = residentalAddress;
    }

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH},
            mappedBy = "medcard")
    @JsonBackReference(value = "pat")
    private Patient patient;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "medcard")
    private List<Illness> illnessList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "medcard")
    private List<Operation> operationList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getResidentalAddress() {
        return residentalAddress;
    }

    public void setResidentalAddress(String residentalAddress) {
        this.residentalAddress = residentalAddress;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public List<Illness> getIllnessList() {
        return illnessList;
    }

    public void setIllnessList(List<Illness> illnessList) {
        this.illnessList = illnessList;
    }

    public List<Operation> getOperationList() {
        return operationList;
    }

    public void setOperationList(List<Operation> operationList) {
        this.operationList = operationList;
    }

    public void addIllnessToIllnessList(Illness illness){
        if(illnessList == null){
            illnessList = new ArrayList<>();
        }
        illnessList.add(illness);
        illness.setMedcard(this);
    }

    public void addOperationToOperationList(Operation operation){
        if(operationList == null){
            operationList = new ArrayList<>();
        }
        operationList.add(operation);
        operation.setMedcard(this);
    }

    @PreRemove
    public void preRemove(){
        if(patient != null) {
            patient.setMedcard(null);
        }
    }

}
