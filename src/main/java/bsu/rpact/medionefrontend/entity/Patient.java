package bsu.rpact.medionefrontend.entity;

import bsu.rpact.medionefrontend.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

@Entity
@Table(name="patient")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Patient implements User{

    private static final transient Role role = Role.PATIENT;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_credential_id")
    private Credentials credentials;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medcard_id")
    private Medcard medcard;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_visit_schedule_id")
    private VisitSchedule visitSchedule;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_document_folder_id")
    private DocumentFolder documentFolder;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_note_dashboard_id")
    private NoteDashboard noteDashboard;

    public Patient() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public Medcard getMedcard() {
        return medcard;
    }

    public void setMedcard(Medcard medcard) {
        this.medcard = medcard;
    }

    public VisitSchedule getVisitSchedule() {
        return visitSchedule;
    }

    public void setVisitSchedule(VisitSchedule visitSchedule) {
        this.visitSchedule = visitSchedule;
    }

    public DocumentFolder getDocumentFolder() {
        return documentFolder;
    }

    public void setDocumentFolder(DocumentFolder documentFolder) {
        this.documentFolder = documentFolder;
    }

    public NoteDashboard getNoteDashboard() {
        return noteDashboard;
    }

    public void setNoteDashboard(NoteDashboard noteDashboard) {
        this.noteDashboard = noteDashboard;
    }

    @PreRemove
    public void preRemove(){
        if(medcard != null) {
            medcard.setPatient(null);
        }
    }
}
