package bsu.rpact.medionefrontend.service.medical;


import org.hl7.fhir.r4.model.Bundle;

public interface FhirBundleMapper<T> {
    T map(Bundle.BundleEntryComponent component);
}
