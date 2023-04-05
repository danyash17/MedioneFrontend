package bsu.rpact.medionefrontend.pojo.request;

import bsu.rpact.medionefrontend.entity.Patient;

public class MedicationPrescriptionRq {
    private Patient patient;
    private Boolean preferential;

    public Boolean getPreferential() {
        return preferential;
    }

    public void setPreferential(Boolean preferential) {
        this.preferential = preferential;
    }

    public MedicationPrescriptionRq(Patient patient, Boolean preferential) {
        this.patient = patient;
        this.preferential = preferential;
    }

    public MedicationPrescriptionRq() {
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }


}
