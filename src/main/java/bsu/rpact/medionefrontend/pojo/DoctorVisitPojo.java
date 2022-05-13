package bsu.rpact.medionefrontend.pojo;

import java.sql.Timestamp;

public class DoctorVisitPojo {
    private Integer patientId;
    private Timestamp datetime;
    private String diagnosis;
    private String comments;

    public DoctorVisitPojo(Timestamp datetime, String diagnosis, String comments) {
        this.datetime = datetime;
        this.diagnosis = diagnosis;
        this.comments = comments;
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
}
