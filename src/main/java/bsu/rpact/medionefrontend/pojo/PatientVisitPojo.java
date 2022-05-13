package bsu.rpact.medionefrontend.pojo;

import java.sql.Timestamp;

public class PatientVisitPojo {
    private Integer doctorId;
    private Timestamp datetime;
    private String diagnosis;
    private String comments;

    public PatientVisitPojo(Timestamp datetime, String diagnosis, String comments) {
        this.datetime = datetime;
        this.diagnosis = diagnosis;
        this.comments = comments;
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
}
