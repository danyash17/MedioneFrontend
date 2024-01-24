package bsu.rpact.medionefrontend.pojo;

import java.sql.Timestamp;

public class PatientVisitPojo {
    private Integer doctorId;
    private Timestamp datetime;
    private String diagnosis;
    private String comments;
    private String reason;
    private Boolean active;

    public PatientVisitPojo(Timestamp datetime, String diagnosis, String comments, String reason, Boolean active) {
        this.datetime = datetime;
        this.diagnosis = diagnosis;
        this.comments = comments;
        this.reason = reason;
        this.active = active;
    }

    public PatientVisitPojo() {
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

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
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
