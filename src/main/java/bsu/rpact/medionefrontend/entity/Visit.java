package bsu.rpact.medionefrontend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "visit", schema = "medione")
public class Visit {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic
    @Column(name = "datetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Timestamp datetime;
    @Basic
    @Column(name = "diagnosis")
    private String diagnosis;
    @Basic
    @Column(name = "comments")
    private String comments;

    public Visit() {
    }

    public Visit(Timestamp datetime, String diagnosis, String comments) {
        this.datetime = datetime;
        this.diagnosis = diagnosis;
        this.comments = comments;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "schedule_assert",
            joinColumns = @JoinColumn(name = "visit_id"),
            inverseJoinColumns = @JoinColumn(name = "schedule_id")
    )
    @JsonBackReference(value = "visitScheds")
    private Set<VisitSchedule> visitSchedules;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    @JsonBackReference(value = "pat")
    private Patient patient;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "doctor_id")
    @JsonBackReference(value = "doc")
    private Doctor doctor;

    public Set<VisitSchedule> getVisitSchedules() {
        return visitSchedules;
    }

    public void setVisitSchedules(Set<VisitSchedule> visitScheduleList) {
        this.visitSchedules = visitScheduleList;
    }

    public void addScheduleToScheduleList(VisitSchedule schedule) {
        if (visitSchedules == null) {
            visitSchedules = new LinkedHashSet<>();
        }
        visitSchedules.add(schedule);
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Visit visit = (Visit) o;
        return Objects.equals(id, visit.id) && Objects.equals(datetime, visit.datetime) && Objects.equals(diagnosis, visit.diagnosis) && Objects.equals(comments, visit.comments) && Objects.equals(visitSchedules, visit.visitSchedules) && Objects.equals(patient, visit.patient) && Objects.equals(doctor, visit.doctor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, datetime, diagnosis, comments, visitSchedules, patient, doctor);
    }

    @PreRemove
    public void preRemove() {
        if(visitSchedules!=null) {
            visitSchedules.forEach(item ->
                    item.getVisitList().remove(this));
            visitSchedules = null;
        }
    }
}
