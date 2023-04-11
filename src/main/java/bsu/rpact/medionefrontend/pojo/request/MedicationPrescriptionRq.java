package bsu.rpact.medionefrontend.pojo.request;

import bsu.rpact.medionefrontend.entity.Patient;
import bsu.rpact.medionefrontend.pojo.medical.MedicationDetails;
import bsu.rpact.medionefrontend.pojo.medical.RegistryMedication;

import java.util.ArrayList;
import java.util.List;

public class MedicationPrescriptionRq {
    private Patient patient;
    private Boolean preferential;
    private String serieNum;
    private List<MedicationDetails> medicationDetails;

    public Boolean getPreferential() {
        return preferential;
    }

    public void setPreferential(Boolean preferential) {
        this.preferential = preferential;
    }

    public MedicationPrescriptionRq(Patient patient, Boolean preferential, String serieNum) {
        this.patient = patient;
        this.preferential = preferential;
        this.serieNum = serieNum;
        this.medicationDetails = new ArrayList<>();
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

    public List<MedicationDetails> getMedicationDetails() {
        return medicationDetails;
    }

    public void setMedicationDetails(List<MedicationDetails> medicationDetails) {
        this.medicationDetails = medicationDetails;
    }
}
