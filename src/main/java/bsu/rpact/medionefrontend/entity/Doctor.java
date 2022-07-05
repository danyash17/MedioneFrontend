package bsu.rpact.medionefrontend.entity;

import bsu.rpact.medionefrontend.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctor", schema = "medione")
public class Doctor implements User {

    private static final transient Role role = Role.DOCTOR;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "hospital")
    private String hospital;
    @Basic
    @Column(name = "available")
    private Boolean available;
    @Basic
    @Column(name = "common_info")
    private String commonInfo;
    @Basic
    @Column(name = "doctor_photo")
    private byte[] doctorPhoto;

    public Doctor() {
    }

    public Doctor(String hospital, Boolean available, String commonInfo, byte[] doctorPhoto) {
        this.hospital = hospital;
        this.available = available;
        this.commonInfo = commonInfo;
        this.doctorPhoto = doctorPhoto;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "doctor_credential_id")
    private Credentials credentials;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "doctor_visit_schedule_id")
    private VisitSchedule visitSchedule;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "doctor_document_folder_id")
    private DocumentFolder documentFolder;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "doctor_note_dashboard_id")
    private NoteDashboard noteDashboard;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "doctor_specialities",
            joinColumns = @JoinColumn(name = "ds_doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "ds_speciality_id")
    )
    private List<Speciality> specialityList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getCommonInfo() {
        return commonInfo;
    }

    public void setCommonInfo(String commonInfo) {
        this.commonInfo = commonInfo;
    }

    public byte[] getDoctorPhoto() {
        return doctorPhoto;
    }

    public void setDoctorPhoto(byte[] doctorPhoto) {
        this.doctorPhoto = doctorPhoto;
    }

    public List<Speciality> getSpecialityList() {
        return specialityList;
    }

    public void setSpecialityList(List<Speciality> specialityList) {
        this.specialityList = specialityList;
    }

    public void addSpecialityToSpecialityList(Speciality speciality){
        if(specialityList == null){
            specialityList = new ArrayList<>();
        }
        specialityList.add(speciality);
        speciality.getDoctorList().add(this);
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
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
}
