package bsu.rpact.medionefrontend.utils.mapper;

import bsu.rpact.medionefrontend.pojo.medical.MedicationTimePeriodsStrings;
import bsu.rpact.medionefrontend.pojo.request.MedicationPrescriptionRq;
import bsu.rpact.medionefrontend.vaadin.components.MedicationDetails;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class FhirMedicationRequestMapper {

    private static final String SERIAL_NUMBER = "http://uiip.bas-net.by/hl7/fhir/serial-number";
    private static final String RCETH = "https://www.rceth.by/Refbank/reestr_lekarstvennih_sredstv";
    private static final String DRUGS = "https://www.drugs.com/";

    public MedicationRequest map(MedicationPrescriptionRq rq){
        MedicationRequest medicationRequest = new MedicationRequest();
        initIdentifiers(rq, medicationRequest);
        initExtra(rq, medicationRequest);
        initPatientRef(rq, medicationRequest);
        initDoctorRef(rq,medicationRequest);
        initMedications(rq,medicationRequest);
        return medicationRequest;
    }

    private void initMedications(MedicationPrescriptionRq rq, MedicationRequest medicationRequest) {
        List<Resource> resources = new ArrayList<>();
        for (MedicationDetails medicationDetails:rq.getMedicationDetails()){
            mapMedicationDetails(medicationDetails,medicationRequest,resources);
        }
        medicationRequest.setContained(resources);
    }

    private void mapMedicationDetails(MedicationDetails details, MedicationRequest medicationRequest, List<Resource> resources) {
        Medication medication = new Medication();
        Coding tradeName = new Coding();
        tradeName.setSystem(RCETH);
        tradeName.setDisplay(details.getRegistryMedication().getTradeName());
        Coding internationalName = new Coding();
        internationalName.setSystem(DRUGS);
        internationalName.setDisplay(details.getRegistryMedication().getInternationalName());
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding(tradeName);
        codeableConcept.addCoding(internationalName);
        medication.setCode(codeableConcept);
        Coding form = new Coding();
        form.setDisplay(details.getMedicationForm().getDisplay());
        form.setCode(details.getMedicationForm().getCode());
        medication.setForm(new CodeableConcept(form));
        Reference manufacturer = new Reference();
        manufacturer.setDisplay(details.getRegistryMedication().getManufacturer());
        medication.setManufacturer(manufacturer);
        if (!details.getOnceDosageMethod().isEmpty()){
            Dosage dosage = new Dosage();
            Coding once = new Coding();
            once.setDisplay("Once");
            dosage.setMethod(new CodeableConcept(once));
            Quantity quantity = new Quantity();
            quantity.setValue(details.getOnceDosageMethod().getAmount());
            quantity.setUnit(details.getOnceDosageMethod().getUnit());
            Dosage.DosageDoseAndRateComponent dosageDoseAndRateComponent = new Dosage.DosageDoseAndRateComponent();
            dosageDoseAndRateComponent.setType(new CodeableConcept().setText("Dose"));
            dosageDoseAndRateComponent.setDose(quantity);
            dosage.setDoseAndRate(Collections.singletonList(dosageDoseAndRateComponent));
            dosage.setText(details.getOnceDosageMethod().toString());
            Coding comment = new Coding();
            comment.setSystem("Comment/");
            comment.setDisplay(details.getComment());
            dosage.getAdditionalInstruction().add(new CodeableConcept(comment));
            Extension extension = new Extension();
            extension.setUrl("Dosage/");
            extension.setValue(dosage);
            medication.getExtension().add(extension);
        }
        if (!details.getOnDemandDosageMethod().isEmpty()){
            Dosage dosage = new Dosage();
            Coding onDemand = new Coding();
            onDemand.setDisplay("On demand");
            dosage.setMethod(new CodeableConcept(onDemand));
            Quantity quantity = new Quantity();
            quantity.setValue(details.getOnDemandDosageMethod().getAmount());
            quantity.setUnit(details.getOnDemandDosageMethod().getUnit());
            Dosage.DosageDoseAndRateComponent dosageDoseAndRateComponent = new Dosage.DosageDoseAndRateComponent();
            dosageDoseAndRateComponent.setType(new CodeableConcept().setText("Dose"));
            dosageDoseAndRateComponent.setDose(quantity);
            dosage.setDoseAndRate(Collections.singletonList(dosageDoseAndRateComponent));
            dosage.setText(details.getOnDemandDosageMethod().toString());
            Coding comment = new Coding();
            comment.setSystem("Comment/");
            comment.setDisplay(details.getComment());
            dosage.getAdditionalInstruction().add(new CodeableConcept(comment));
            Extension extension = new Extension();
            extension.setUrl("Dosage/");
            extension.setValue(dosage);
            medication.getExtension().add(extension);
        }
        if (!details.getPeriodicalDosageMethod().isEmpty()){
            Dosage dosage = new Dosage();
            Coding periodical = new Coding();
            periodical.setDisplay("Periodical");
            dosage.setMethod(new CodeableConcept(periodical));
            Quantity quantity = new Quantity();
            quantity.setValue(details.getPeriodicalDosageMethod().getAmount());
            quantity.setUnit(details.getPeriodicalDosageMethod().getUnit());
            Timing timing = new Timing();
            Timing.TimingRepeatComponent timingRepeatComponent = new Timing.TimingRepeatComponent();
            timingRepeatComponent.setCount(details.getPeriodicalDosageMethod().getTimes());
            timingRepeatComponent.setPeriod(details.getPeriodicalDosageMethod().getTimePeriodQuantity());
            timingRepeatComponent.setPeriodUnit(mapToTimingUnit(details.getPeriodicalDosageMethod().getTimePeriod()));
            timing.setRepeat(timingRepeatComponent);
            dosage.setTiming(timing);
            Dosage.DosageDoseAndRateComponent dosageDoseAndRateComponent = new Dosage.DosageDoseAndRateComponent();
            dosageDoseAndRateComponent.setType(new CodeableConcept().setText("Dose"));
            dosageDoseAndRateComponent.setDose(quantity);
            dosage.setDoseAndRate(Collections.singletonList(dosageDoseAndRateComponent));
            Coding comment = new Coding();
            comment.setSystem("Comment/");
            comment.setDisplay(details.getComment());
            dosage.getAdditionalInstruction().add(new CodeableConcept(comment));
            Extension extension = new Extension();
            dosage.setText(details.getPeriodicalDosageMethod().toString());
            extension.setUrl("Dosage/");
            extension.setValue(dosage);
            medication.getExtension().add(extension);
        }
        if (!details.getTetrationDosageMethod().isEmpty()){
            Dosage dosage = new Dosage();
            Coding tetration = new Coding();
            tetration.setDisplay("Tetration Method");
            dosage.setMethod(new CodeableConcept(tetration));
            Quantity quantity = new Quantity();
            quantity.setValue(details.getTetrationDosageMethod().getAmount());
            quantity.setUnit(details.getTetrationDosageMethod().getUnit());
            Extension tetrationCoef = new Extension();
            tetrationCoef.setUrl("TetrationCoefficient/");
            tetrationCoef.setValue(new DecimalType(details.getTetrationDosageMethod().getCoefficient()));
            Extension tetrationCoefTrend = new Extension();
            tetrationCoefTrend.setUrl("TetrationCoefficientTrend/");
            tetrationCoefTrend.setValue(new StringType(details.getTetrationDosageMethod().getCoefTrend()));
            quantity.getExtension().add(tetrationCoef);
            quantity.getExtension().add(tetrationCoefTrend);
            Timing timing = new Timing();
            Timing.TimingRepeatComponent timingRepeatComponent = new Timing.TimingRepeatComponent();
            timingRepeatComponent.setCount(details.getTetrationDosageMethod().getTimes());
            timingRepeatComponent.setPeriod(details.getTetrationDosageMethod().getTimePeriodQuantity());
            timingRepeatComponent.setPeriodUnit(mapToTimingUnit(details.getTetrationDosageMethod().getTimePeriod()));
            timing.setRepeat(timingRepeatComponent);
            dosage.setTiming(timing);
            Dosage.DosageDoseAndRateComponent dosageDoseAndRateComponent = new Dosage.DosageDoseAndRateComponent();
            dosageDoseAndRateComponent.setType(new CodeableConcept().setText("Dose"));
            dosageDoseAndRateComponent.setDose(quantity);
            dosage.setDoseAndRate(Collections.singletonList(dosageDoseAndRateComponent));
            dosage.setText(details.getTetrationDosageMethod().toString());
            Coding comment = new Coding();
            comment.setSystem("Comment/");
            comment.setDisplay(details.getComment());
            dosage.getAdditionalInstruction().add(new CodeableConcept(comment));
            Extension extension = new Extension();
            extension.setUrl("Dosage/");
            extension.setValue(dosage);
            medication.getExtension().add(extension);
        }
        resources.add(medication);
    }

    public static Timing.UnitsOfTime mapToTimingUnit(String timePeriod) {
        if (timePeriod.equalsIgnoreCase(MedicationTimePeriodsStrings.MINUTES)) {
            return Timing.UnitsOfTime.MIN;
        } else if (timePeriod.equalsIgnoreCase(MedicationTimePeriodsStrings.HOURS)) {
            return Timing.UnitsOfTime.H;
        } else if (timePeriod.equalsIgnoreCase(MedicationTimePeriodsStrings.DAYS)) {
            return Timing.UnitsOfTime.D;
        } else if (timePeriod.equalsIgnoreCase(MedicationTimePeriodsStrings.WEEKS)) {
            return Timing.UnitsOfTime.WK;
        } else if (timePeriod.equalsIgnoreCase(MedicationTimePeriodsStrings.MONTHS)) {
            return Timing.UnitsOfTime.MO;
        } else if (timePeriod.equalsIgnoreCase(MedicationTimePeriodsStrings.YEARS)) {
            return Timing.UnitsOfTime.A;
        }
        return null;
    }

    private void initExtra(MedicationPrescriptionRq rq, MedicationRequest medicationRequest) {
        if (rq.getAuthoredOn() != null) {
            medicationRequest.setAuthoredOn(Date.from(rq.getAuthoredOn().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        }
        medicationRequest.setStatus(MedicationRequest.MedicationRequestStatus.ACTIVE);

    }

    private void initPatientRef(MedicationPrescriptionRq rq, MedicationRequest medicationRequest) {
        Reference patRef = new Reference();
        patRef.setReference("Patient/");
        patRef.setDisplay(rq.getPatient().getCredentials().getFirstName() + " " + rq.getPatient().getCredentials().getLastName()
                + " " + rq.getPatient().getCredentials().getPatronymic());
        medicationRequest.setSubject(patRef);
    }

    private void initDoctorRef(MedicationPrescriptionRq rq, MedicationRequest medicationRequest) {
        Reference docRef = new Reference();
        docRef.setReference("Doctor/");
        docRef.setDisplay(rq.getDoctor().getCredentials().getFirstName() + " " + rq.getDoctor().getCredentials().getLastName()
                + " " + rq.getDoctor().getCredentials().getPatronymic());
        medicationRequest.setPerformer(docRef);
    }

    private void initIdentifiers(MedicationPrescriptionRq rq, MedicationRequest medicationRequest) {
        Date datetime = new Date();
        Identifier patient = new Identifier();
        if (rq.getPatient() != null) {
            patient.setValue(String.valueOf(rq.getPatient().getId()));
            patient.setSystem("Patient");
        }
        Identifier doctor = new Identifier();
        if (rq.getDoctor() != null) {
            doctor.setValue(String.valueOf(rq.getDoctor().getId()));
            doctor.setSystem("Doctor");
        }
        Identifier serialNumber = new Identifier();
        if (rq.getSerieNum() != null) {
            serialNumber.setValue(rq.getSerieNum());
            serialNumber.setSystem(SERIAL_NUMBER);
            Period period = new Period();
            if (rq.getActiveAfter() != null) {
                Date start = Date.from(rq.getActiveAfter().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                period.setStart(start);
                if (rq.getValidity() != null) {
                    Date end = Date.from(rq.getActiveAfter().plusDays(rq.getValidity()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                    period.setEnd(end);
                }
                serialNumber.setPeriod(period);
            }
        }
        Identifier frontendIdentifier = new Identifier();
        String prescription = "MedicationPrescription" + datetime.getTime();
        frontendIdentifier.setValue(prescription);
        frontendIdentifier.setSystem("Frontend");
        medicationRequest.setIdentifier(List.of(patient, doctor, serialNumber, frontendIdentifier));
    }

}
