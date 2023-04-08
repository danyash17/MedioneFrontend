package bsu.rpact.medionefrontend.pojo.request;

import bsu.rpact.medionefrontend.entity.Patient;
import bsu.rpact.medionefrontend.entity.medical.RegistryMedication;

public class MedicationPrescriptionRq {
    private Patient patient;
    private Boolean preferential;
    private String serieNum;
    private RegistryMedication medication;

    public Boolean getPreferential() {
        return preferential;
    }

    public void setPreferential(Boolean preferential) {
        this.preferential = preferential;
    }

    public MedicationPrescriptionRq(Patient patient, Boolean preferential, String serieNum, RegistryMedication medication) {
        this.patient = patient;
        this.preferential = preferential;
        this.serieNum = serieNum;
        this.medication = medication;
    }

    public MedicationPrescriptionRq() {
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

    public RegistryMedication getMedication() {
        return medication;
    }

    public void setMedication(RegistryMedication medication) {
        this.medication = medication;
    }
}
