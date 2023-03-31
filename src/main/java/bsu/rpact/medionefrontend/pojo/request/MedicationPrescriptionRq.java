package bsu.rpact.medionefrontend.pojo.request;

import bsu.rpact.medionefrontend.entity.Patient;

public class MedicationPrescriptionRq {
    private Patient patient;

    public MedicationPrescriptionRq(Patient patient) {
        this.patient = patient;
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
