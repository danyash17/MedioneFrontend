package bsu.rpact.medionefrontend.pojo.request;

import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.entity.Patient;
import bsu.rpact.medionefrontend.vaadin.components.MedicationDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicationPrescriptionRq {
    private Patient patient;
    private Doctor doctor;
    private Boolean preferential;
    private String serieNum;
    private Integer validity;
    private LocalDate activeAfter;
    private List<MedicationDetails> medicationDetails;
    private LocalDate authoredOn;

    public Boolean getPreferential() {
        return preferential;
    }

    public void setPreferential(Boolean preferential) {
        this.preferential = preferential;
    }

    public MedicationPrescriptionRq(Patient patient, Doctor doctor, Boolean preferential, String serieNum, Integer validity, LocalDate activeAfter, LocalDate authoredOn) {
        this.patient = patient;
        this.doctor = doctor;
        this.preferential = preferential;
        this.serieNum = serieNum;
        this.validity = validity;
        this.activeAfter = activeAfter;
        this.authoredOn = authoredOn;
        this.medicationDetails = new ArrayList<>();
    }

    public MedicationPrescriptionRq() {
        this.medicationDetails = new ArrayList<>();
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getSerieNum() {
        return serieNum;
    }

    public void setSerieNum(String serieNum) {
        this.serieNum = serieNum;
    }

    public List<MedicationDetails> getMedicationDetails() {
        return medicationDetails;
    }

    public void setMedicationDetails(List<MedicationDetails> medicationDetails) {
        this.medicationDetails = medicationDetails;
    }

    public Integer getValidity() {
        return validity;
    }

    public void setValidity(Integer validity) {
        this.validity = validity;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDate getActiveAfter() {
        return activeAfter;
    }

    public void setActiveAfter(LocalDate activeAfter) {
        this.activeAfter = activeAfter;
    }

    public LocalDate getAuthoredOn() {
        return authoredOn;
    }

    public void setAuthoredOn(LocalDate authoredOn) {
        this.authoredOn = authoredOn;
    }
}
