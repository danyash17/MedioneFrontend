package bsu.rpact.medionefrontend.pojo;

import java.sql.Timestamp;

public class DoctorVisitPojo {
    private Integer patientId;
    private Timestamp datetime;
    private String diagnosis;
    private String comments;
    private String reason;
    private Boolean active;

    public DoctorVisitPojo(Timestamp datetime, String diagnosis, String comments, String reason, Boolean active) {
        this.datetime = datetime;
        this.diagnosis = diagnosis;
        this.comments = comments;
        this.reason = reason;
        this.active = active;
    }

    public DoctorVisitPojo() {
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

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
