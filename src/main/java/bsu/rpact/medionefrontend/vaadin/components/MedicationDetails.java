package bsu.rpact.medionefrontend.vaadin.components;

import bsu.rpact.medionefrontend.pojo.medical.*;
import com.vaadin.flow.component.details.Details;

public class MedicationDetails extends Details {

    private RegistryMedication registryMedication;
    private MedicationForm medicationForm;
    private OnceDosageMethod onceDosageMethod;
    private PeriodicalDosageMethod periodicalDosageMethod;
    private OnDemandDosageMethod onDemandDosageMethod;
    private TetrationDosageMethod tetrationDosageMethod;
    private String comment;

    public MedicationDetails() {
        onceDosageMethod = new OnceDosageMethod();
        periodicalDosageMethod = new PeriodicalDosageMethod();
        onDemandDosageMethod = new OnDemandDosageMethod();
        tetrationDosageMethod = new TetrationDosageMethod();
    }

    public MedicationDetails(RegistryMedication registryMedication, MedicationForm medicationForm, OnceDosageMethod onceDosageMethod, PeriodicalDosageMethod periodicalDosageMethod, OnDemandDosageMethod onDemandDosageMethod, TetrationDosageMethod tetrationDosageMethod, String comment) {
        this.registryMedication = registryMedication;
        this.medicationForm = medicationForm;
        this.onceDosageMethod = onceDosageMethod;
        this.periodicalDosageMethod = periodicalDosageMethod;
        this.onDemandDosageMethod = onDemandDosageMethod;
        this.tetrationDosageMethod = tetrationDosageMethod;
        this.comment = comment;
    }

    public RegistryMedication getRegistryMedication() {
        return registryMedication;
    }

    public void setRegistryMedication(RegistryMedication registryMedication) {
        this.registryMedication = registryMedication;
    }

    public OnceDosageMethod getOnceDosageMethod() {
        return onceDosageMethod;
    }

    public void setOnceDosageMethod(OnceDosageMethod onceDosageMethod) {
        this.onceDosageMethod = onceDosageMethod;
    }

    public PeriodicalDosageMethod getPeriodicalDosageMethod() {
        return periodicalDosageMethod;
    }

    public void setPeriodicalDosageMethod(PeriodicalDosageMethod periodicalDosageMethod) {
        this.periodicalDosageMethod = periodicalDosageMethod;
    }

    public OnDemandDosageMethod getOnDemandDosageMethod() {
        return onDemandDosageMethod;
    }

    public void setOnDemandDosageMethod(OnDemandDosageMethod onDemandDosageMethod) {
        this.onDemandDosageMethod = onDemandDosageMethod;
    }

    public TetrationDosageMethod getTetrationDosageMethod() {
        return tetrationDosageMethod;
    }

    public void setTetrationDosageMethod(TetrationDosageMethod tetrationDosageMethod) {
        this.tetrationDosageMethod = tetrationDosageMethod;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public MedicationForm getMedicationForm() {
        return medicationForm;
    }

    public void setMedicationForm(MedicationForm medicationForm) {
        this.medicationForm = medicationForm;
    }
}
