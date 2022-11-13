package bsu.rpact.medionefrontend;

import bsu.rpact.medionefrontend.adapter.medical.FhirBaseAdapter;
import bsu.rpact.medionefrontend.enums.FhirId;
import ca.uhn.fhir.context.FhirContext;
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
    void create(){
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

}
