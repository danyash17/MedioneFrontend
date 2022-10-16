package bsu.rpact.medionefrontend;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;

@SpringBootTest
class MedioneFrontendApplicationTests {

    @Value("${fhir.serverbase}")
    private String fhirServer;

    @Test
    void contextLoads() {
    }

    @Test
    void fhirTest(){
        Observation observation = new Observation();
        observation.setStatus(Observation.ObservationStatus.FINAL);


    }

    @Test
    void createPatient() {
        // Create a patient
        Patient newPatient = new Patient();

        // Populate the patient with fake information
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

        // Create a client
        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);

        // Create the resource on the server
        MethodOutcome outcome = client
                .create()
                .resource(newPatient)
                .execute();

        // Log the ID that the server assigned
        IIdType id = outcome.getId();
        System.out.println("Created patient, got ID: " + id);
    }

    @Test
    void searchPatient(){
        FhirContext ctx = FhirContext.forR4();
        IGenericClient client = ctx.newRestfulGenericClient(fhirServer);
        Bundle response = client.search()
                .forResource(Patient.class)
                .where(Patient.BIRTHDATE.afterOrEquals().day("2011-01-01"))
                .returnBundle(Bundle.class)
                .execute();
        String string = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(response);
        System.out.println(string);
    }

}
