package bsu.rpact.medionefrontend.utils.mapper;

import bsu.rpact.medionefrontend.pojo.medical.MedicationForm;
import bsu.rpact.medionefrontend.pojo.medical.MedicationTimePeriodsStrings;
import bsu.rpact.medionefrontend.pojo.medical.RegistryMedication;
import bsu.rpact.medionefrontend.pojo.request.MedicationPrescriptionRq;
import bsu.rpact.medionefrontend.service.DoctorService;
import bsu.rpact.medionefrontend.service.PatientService;
import bsu.rpact.medionefrontend.vaadin.components.MedicationDetails;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class MedicationPrescriptionRqMapper {

    @Autowired
    private PatientService patientService;
    @Autowired
    private DoctorService doctorService;

    public static final String SERIAL_NUMBER = "http://uiip.bas-net.by/hl7/fhir/serial-number";

    public MedicationPrescriptionRq map(MedicationRequest medicationRequest) {
        MedicationPrescriptionRq rq = new MedicationPrescriptionRq();
        initFromIdentifiers(medicationRequest, rq);
        initFromExtra(medicationRequest, rq);
        initFromMedications(medicationRequest,rq);
        return rq;
    }

    private void initFromMedications(MedicationRequest medicationRequest, MedicationPrescriptionRq rq) {
        for (Resource resource:medicationRequest.getContained()){
            if (!(resource instanceof Medication)) return;
            Medication medication = (Medication) resource;
            MedicationDetails details = new MedicationDetails();
            RegistryMedication registryMedication = new RegistryMedication();
            registryMedication.setTradeName(medication.getCode().getCodingFirstRep().getDisplay());
            registryMedication.setInternationalName(medication.getCode().getCoding().get(1).getDisplay());
            registryMedication.setManufacturer(medication.getManufacturer().getDisplay());
            details.setRegistryMedication(registryMedication);
            details.setMedicationForm(new MedicationForm(medication.getForm().getCodingFirstRep().getCode(), medication.getForm().getCodingFirstRep().getDisplay()));
            initFromDosage(medication, rq, details);
            rq.getMedicationDetails().add(details);
        }
    }

    private void initFromDosage(Medication medication, MedicationPrescriptionRq rq, MedicationDetails details) {
        Dosage dosage = (Dosage) medication.getExtension().get(0).getValue();
        switch (dosage.getMethod().getCodingFirstRep().getDisplay()){
            case "Once":{
                mapOnce(dosage, details);
                break;
            }
            case "On demand":{
                mapOnDemand(dosage, details);
                break;
            }
            case "Periodical":{
                mapPeriodical(dosage,rq, details);
                break;
            }
            case "Tetration Method":{
                mapTetration(dosage,rq, details);
                break;
            }
        }
    }

    private void mapTetration(Dosage dosage, MedicationPrescriptionRq rq, MedicationDetails details) {
        details.getTetrationDosageMethod().setAmount(dosage.getDoseAndRate().get(0).getDoseQuantity().getValue().doubleValue());
        details.getTetrationDosageMethod().setUnit(dosage.getDoseAndRate().get(0).getDoseQuantity().getUnit());
        details.getTetrationDosageMethod().setTimes(dosage.getTiming().getRepeat().getCount());
        details.getTetrationDosageMethod().setTimePeriod(mapToTimingUnitString(dosage.getTiming().getRepeat().getPeriodUnit()));
        details.getTetrationDosageMethod().setTimePeriodQuantity(dosage.getTiming().getRepeat().getPeriod().intValue());
        DecimalType decimalType = (DecimalType) dosage.getDoseAndRate().get(0).getDoseQuantity().getExtension().get(0).getValue();
        details.getTetrationDosageMethod().setCoefficient(decimalType.getValueAsNumber().doubleValue());
        StringType stringType = (StringType) dosage.getDoseAndRate().get(0).getDoseQuantity().getExtension().get(1).getValue();
        details.getTetrationDosageMethod().setCoefTrend(stringType.getValue());
        details.setComment(dosage.getAdditionalInstructionFirstRep().getCoding().get(0).getDisplay());
    }

    private void mapPeriodical(Dosage dosage, MedicationPrescriptionRq rq, MedicationDetails details) {
        details.getPeriodicalDosageMethod().setAmount(dosage.getDoseAndRate().get(0).getDoseQuantity().getValue().doubleValue());
        details.getPeriodicalDosageMethod().setUnit(dosage.getDoseAndRate().get(0).getDoseQuantity().getUnit());
        details.getPeriodicalDosageMethod().setTimes(dosage.getTiming().getRepeat().getCount());
        details.getPeriodicalDosageMethod().setTimePeriod(mapToTimingUnitString(dosage.getTiming().getRepeat().getPeriodUnit()));
        details.getPeriodicalDosageMethod().setTimePeriodQuantity(dosage.getTiming().getRepeat().getPeriod().intValue());
        details.setComment(dosage.getAdditionalInstructionFirstRep().getCoding().get(0).getDisplay());
    }

    private String mapToTimingUnitString(Timing.UnitsOfTime unitsOfTime) {
        if (unitsOfTime == Timing.UnitsOfTime.MIN) {
            return MedicationTimePeriodsStrings.MINUTES;
        } else if (unitsOfTime == Timing.UnitsOfTime.H) {
            return MedicationTimePeriodsStrings.HOURS;
        } else if (unitsOfTime == Timing.UnitsOfTime.D) {
            return MedicationTimePeriodsStrings.DAYS;
        } else if (unitsOfTime == Timing.UnitsOfTime.WK) {
            return MedicationTimePeriodsStrings.WEEKS;
        } else if (unitsOfTime == Timing.UnitsOfTime.MO) {
            return MedicationTimePeriodsStrings.MONTHS;
        } else if (unitsOfTime == Timing.UnitsOfTime.A) {
            return MedicationTimePeriodsStrings.YEARS;
        }
        return null;
    }

    private void mapOnDemand(Dosage dosage, MedicationDetails details) {
        details.getOnDemandDosageMethod().setAmount(dosage.getDoseAndRate().get(0).getDoseQuantity().getValue().doubleValue());
        details.getOnDemandDosageMethod().setUnit(dosage.getDoseAndRate().get(0).getDoseQuantity().getUnit());
        details.setComment(dosage.getAdditionalInstructionFirstRep().getCoding().get(0).getDisplay());
    }

    private void mapOnce(Dosage dosage, MedicationDetails details) {
        details.getOnceDosageMethod().setAmount(dosage.getDoseAndRate().get(0).getDoseQuantity().getValue().doubleValue());
        details.getOnceDosageMethod().setUnit(dosage.getDoseAndRate().get(0).getDoseQuantity().getUnit());
        details.setComment(dosage.getAdditionalInstructionFirstRep().getCoding().get(0).getDisplay());
    }

    private void initFromExtra(MedicationRequest medicationRequest, MedicationPrescriptionRq rq) {
        rq.setAuthoredOn(medicationRequest.getAuthoredOn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        rq.setPreferential(false);
    }

    private void initFromIdentifiers(MedicationRequest medicationRequest, MedicationPrescriptionRq rq) {
        rq.setPatient(patientService.getById(Integer.valueOf(medicationRequest.getIdentifier().stream().filter(identifier -> identifier.getSystem().equals("Patient")).findAny().get().getValue())).get());
        rq.setDoctor(doctorService.getDoctorById(Integer.valueOf(medicationRequest.getIdentifier().stream().filter(identifier -> identifier.getSystem().equals("Doctor")).findAny().get().getValue())));
        Identifier serieNum = medicationRequest.getIdentifier().stream().filter(identifier -> identifier.getSystem().equals(SERIAL_NUMBER)).findAny().get();
        rq.setActiveAfter(serieNum.getPeriod().getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        rq.setValidity(Math.abs(rq.getActiveAfter().getDayOfYear() - serieNum.getPeriod().getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfYear()));
        rq.setSerieNum(serieNum.getValue());
    }

}
