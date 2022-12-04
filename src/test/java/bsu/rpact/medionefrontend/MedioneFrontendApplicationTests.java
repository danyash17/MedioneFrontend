package bsu.rpact.medionefrontend;

import bsu.rpact.medionefrontend.adapter.medical.FhirBaseAdapter;
import bsu.rpact.medionefrontend.enums.FhirId;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Period;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@SpringBootTest
class MedioneFrontendApplicationTests {

    public static final Date DATETIME = new Date();
    @Value("${fhir.serverbase}")
    private String fhirServer;
    @Autowired
    private FhirBaseAdapter fhirBaseAdapter;

    @Test
    void contextLoads() {
    }

    @Test
    void createObservation(){
        Observation observation = new Observation();
        observation.setStatus(Observation.ObservationStatus.FINAL);
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setSystem("http://loinc.org");
        coding.setCode("11557-6");
        coding.setDisplay("Carbon dioxide in blood");
        codeableConcept.addCoding(coding);
        observation.setCode(codeableConcept);
        Quantity quantity = new Quantity();
        quantity.setCode("kPa");
        quantity.setSystem("http://unitsofmeasure.org");
        quantity.setUnit("kPa");
        quantity.setValue(6.2);
        observation.setValue(quantity);
        Identifier businessIdentifier = new Identifier();
        businessIdentifier.setValue("19");
        businessIdentifier.setSystem(FhirId.Patient.name());
        Identifier frontendIdentifier = new Identifier();
        String firstWordLetters = Arrays.stream(observation.getCode().getCodingFirstRep()
                        .getDisplay().split(" "))
                .filter(s -> s.matches("[a-zA-Z0-9]*"))
                .map(s -> s.substring(0, 1))
                .collect(Collectors.joining());
        String identifierFirstWordLettersAndTime = firstWordLetters+DATETIME.getTime();
        frontendIdentifier.setValue(identifierFirstWordLettersAndTime);
        frontendIdentifier.setSystem(FhirId.Frontend.name());
        observation.setIdentifier(Arrays.asList(businessIdentifier,frontendIdentifier));
        observation.setIssued(DATETIME);
        Period period = new Period();
        period.setStart(DATETIME);
        period.setEnd(Date.from(LocalDate.ofInstant(DATETIME.toInstant(), ZoneId.systemDefault()).plusDays(365).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        observation.setEffective(period);
        CodeableConcept interpret = new CodeableConcept();
        Coding interpretCoding = new Coding();
        interpretCoding.setDisplay("High");
        interpretCoding.setSystem("http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation");
        interpretCoding.setCode("H");
        interpret.addCoding(interpretCoding);
        observation.setInterpretation(Arrays.asList(interpret));
        Observation.ObservationReferenceRangeComponent referenceRange = new Observation.ObservationReferenceRangeComponent();
        Quantity low = new Quantity();
        low.setValue(4.8);
        low.setSystem("http://unitsofmeasure.org");
        low.setCode("kPa");
        low.setUnit("kPa");
        Quantity high = new Quantity();
        high.setValue(6);
        high.setSystem("http://unitsofmeasure.org");
        high.setCode("kPa");
        high.setUnit("kPa");
        referenceRange.setHigh(high);
        referenceRange.setLow(low);
        observation.setReferenceRange(Arrays.asList(referenceRange));
        MethodOutcome outcome = fhirBaseAdapter.getClient()
                .create()
                .resource(observation)
                .execute();
        IIdType id = outcome.getId();
        System.out.println(id);
    }

    @Test
    void createProcedure(){
        Procedure procedure = new Procedure();
        procedure.setStatus(Procedure.ProcedureStatus.COMPLETED);
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setSystem("http://snomed.info/sct");
        coding.setCode("25267002");
        coding.setDisplay("Insertion of intracardiac pacemaker (procedure)");
        codeableConcept.addCoding(coding);
        procedure.setCode(codeableConcept);
        DateTimeType dateType = new DateTimeType();
        dateType.setValue(DATETIME);
        procedure.setPerformed(dateType);
        Identifier businessIdentifier = new Identifier();
        businessIdentifier.setValue("19");
        businessIdentifier.setSystem(FhirId.Patient.name());
        Identifier performerIdentifier = new Identifier();
        performerIdentifier.setValue("1");
        performerIdentifier.setSystem(FhirId.Doctor.name());
        Identifier frontendIdentifier = new Identifier();
        String firstWordLetters = Arrays.stream(procedure.getCode().getCodingFirstRep()
                        .getDisplay().split(" "))
                .filter(s -> s.matches("[a-zA-Z0-9]*"))
                .map(s -> s.substring(0, 1))
                .collect(Collectors.joining());
        String identifierFirstWordLettersAndTime = firstWordLetters+DATETIME.getTime();
        frontendIdentifier.setValue(identifierFirstWordLettersAndTime);
        frontendIdentifier.setSystem(FhirId.Frontend.name());
        procedure.setIdentifier(Arrays.asList(businessIdentifier,frontendIdentifier, performerIdentifier));
        Reference reason = new Reference();
        reason.setDisplay("Carcenoma");
        procedure.setReasonReference(Arrays.asList(reason));
        CodeableConcept bodySite = new CodeableConcept();
        Coding bodyCode = new Coding();
        bodyCode.setCode("272676008");
        bodyCode.setDisplay("Sphenoid bone");
        bodyCode.setSystem("http://snomed.info/sct");
        bodySite.setCoding(Arrays.asList(bodyCode));
        bodySite.setText("Left forearm");
        procedure.setBodySite(Arrays.asList(bodySite));
        CodeableConcept complication = new CodeableConcept();
        Coding complicationCoding = new Coding();
        complicationCoding.setCode("367336001");
        complicationCoding.setDisplay("Chemotherapy");
        complicationCoding.setSystem("http://snomed.info/sct");
        complication.setCoding(Arrays.asList(complicationCoding));
        complication.setText("Ineffective airway clearance");
        procedure.setComplication(Arrays.asList(complication));
        CodeableConcept followUp = new CodeableConcept();
        followUp.setText("Review in clinic");
        procedure.setFollowUp(Arrays.asList(followUp));
        Annotation annotation = new Annotation();
        annotation.setText("-");
        procedure.setNote(Arrays.asList(annotation));
        CodeableConcept usedConcept = new CodeableConcept();
        Coding used = new Coding();
        used.setSystem("http://snomed.info/sct");
        used.setDisplay("Needle, device (physical object)");
        used.setCode("79068005");
        usedConcept.setCoding(Arrays.asList(used));
        usedConcept.setText("30-guage needle");
        procedure.setUsedCode(Arrays.asList(usedConcept));
        MethodOutcome outcome = fhirBaseAdapter.getClient()
                .create()
                .resource(procedure)
                .execute();
        IIdType id = outcome.getId();
        System.out.println(id);
    }

    @Test
    void createDiagnosticReport(){
        DiagnosticReport report = new DiagnosticReport();
        report.setStatus(DiagnosticReport.DiagnosticReportStatus.FINAL);
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setSystem("http://snomed.info/sct");
        coding.setCode("252275004");
        coding.setDisplay("Haematology test");
        codeableConcept.addCoding(coding);
        report.setCode(codeableConcept);
        report.setIssued(DATETIME);
        Identifier businessIdentifier = new Identifier();
        businessIdentifier.setValue("19");
        businessIdentifier.setSystem(FhirId.Patient.name());
        Identifier performerIdentifier = new Identifier();
        performerIdentifier.setValue("1");
        performerIdentifier.setSystem(FhirId.Doctor.name());
        Identifier frontendIdentifier = new Identifier();
        String firstWordLetters = Arrays.stream(report.getCode().getCodingFirstRep()
                        .getDisplay().split(" "))
                .filter(s -> s.matches("[a-zA-Z0-9]*"))
                .map(s -> s.substring(0, 1))
                .collect(Collectors.joining());
        String identifierFirstWordLettersAndTime = firstWordLetters+DATETIME.getTime();
        frontendIdentifier.setValue(identifierFirstWordLettersAndTime);
        frontendIdentifier.setSystem(FhirId.Frontend.name());
        report.setIdentifier(Arrays.asList(businessIdentifier,frontendIdentifier, performerIdentifier));
        Reference observation = new Reference();
        observation.setReference("Observation/155");
        observation.setDisplay("Carbon dioxide in blood");
        report.setResult(Arrays.asList(observation));
        MethodOutcome outcome = fhirBaseAdapter.getClient()
                .create()
                .resource(report)
                .execute();
        IIdType id = outcome.getId();
        System.out.println(id);
    }

    @Test
    void delete(){
        fhirBaseAdapter.getClient().delete().resourceById(new IdType("Observation","104")).execute();
        fhirBaseAdapter.getClient().delete().resourceById(new IdType("Observation","105")).execute();
    }

    @Test
    void createPatient() {
        Patient newPatient = new Patient();
        newPatient
                .addName()
                .setFamily("DevDays2015")
                .addGiven("John")
                .addGiven("Q");
        newPatient
                .addIdentifier()
                .setSystem("http://acme.org/mrn")
                .setValue("1234567");
        newPatient.setGender(Enumerations.AdministrativeGender.MALE);
        newPatient.setBirthDateElement(new DateType("2015-11-18"));
        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);
        MethodOutcome outcome = client
                .create()
                .resource(newPatient)
                .execute();
        IIdType id = outcome.getId();
        System.out.println("Created patient, got ID: " + id);
    }

    @Test
    void search(){
        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);
        Bundle response = client.search()
                .forResource(Observation.class)
                .where(Observation.IDENTIFIER.exactly().systemAndIdentifier("Patient","19"))
                .returnBundle(Bundle.class)
                .execute();
        String string = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(response);
        System.out.println(string);
    }

    @Test
    void searchMultiple(){
        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);
        Bundle response = client.search()
                .forResource(DiagnosticReport.class)
                .where(DiagnosticReport.RESULT.hasAnyOfIds("Observation/155"))
                .include(new Include(DiagnosticReport.class.getSimpleName() + ":" + DiagnosticReport.RESULT.getParamName()))
                .returnBundle(Bundle.class)
                .execute();
        String string = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(response);
        System.out.println(string);
    }

    @Test
    void updateReport(){
        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);
        Bundle response = client.search()
                .forResource(DiagnosticReport.class)
                .where(DiagnosticReport.RESULT.hasAnyOfIds("Observation/155"))
                .include(new Include(DiagnosticReport.class.getSimpleName() + ":" + DiagnosticReport.RESULT.getParamName()))
                .returnBundle(Bundle.class)
                .execute();
        Bundle.BundleEntryComponent entryComponent = response.getEntryFirstRep();
        DiagnosticReport report = (DiagnosticReport) entryComponent.getResource();
        CodeableConcept icdC = new CodeableConcept();
        Coding icd = new Coding();
        icd.setSystem("https://icd.who.int/browse11/l-m/en#/http%3A%2F%2Fid.who.int%2Ficd%2Fentity%2F");
        icd.setCode("457240160");
        icd.setDisplay("Carcinoma of breast, specialised type");
        icdC.setCoding(Arrays.asList(icd));
        icdC.setText("ICD-11");
        CodeableConcept orpanetC = new CodeableConcept();
        Coding orpanet = new Coding();
        orpanet.setSystem("https://www.orpha.net/consor/cgi-bin/Disease_Search_Simple.php?lng=EN&diseaseGroup=");
        orpanet.setCode("Rare adenocarcinoma of the breast");
        orpanet.setDisplay("Rare adenocarcinoma of the breast");
        orpanetC.setCoding(Arrays.asList(orpanet));
        orpanetC.setText("Orpanet");
        report.addConclusionCode(icdC);
        report.addConclusionCode(orpanetC);
        MethodOutcome outcome = client
                .update()
                .resource(report)
                .execute();
        IIdType id = outcome.getId();
        System.out.println("Updated report, ID: " + id);
    }

}
