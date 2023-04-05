package bsu.rpact.medionefrontend.service.medical;

import bsu.rpact.medionefrontend.adapter.medical.MedicationRequestFhirAdapter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class MedicationRequestService implements FhirBundleMapper{

    @Autowired
    private MedicationRequestFhirAdapter adapter;

    public boolean isIdentifierUnique(String identifierValue) {
        return adapter.isIdentifierUnique(identifierValue);
    }

    @Override
    public MedicationRequest map(Bundle.BundleEntryComponent component) {
        return (MedicationRequest) component.getResource();
    }
}
