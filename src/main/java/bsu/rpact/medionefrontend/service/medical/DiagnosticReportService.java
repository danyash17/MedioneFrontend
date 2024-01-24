package bsu.rpact.medionefrontend.service.medical;

import bsu.rpact.medionefrontend.adapter.medical.DiagnosticReportFhirAdapter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DiagnosticReportService implements FhirBundleMapper{

    @Autowired
    private DiagnosticReportFhirAdapter adapter;

    public Map<DiagnosticReport, Observation> searchIncluded(Class linkedEntity, Integer id){
        Bundle bundle = adapter.searchIncluded(linkedEntity,id);
        Map<DiagnosticReport, Observation> map = new HashMap<>();
        DiagnosticReport currentReport = new DiagnosticReport();
        for (Bundle.BundleEntryComponent component:bundle.getEntry()){
            DomainResource resource = map(component);
            if (resource instanceof DiagnosticReport) {
                DiagnosticReport report = (DiagnosticReport) resource;
                currentReport = report;
            }
            if (resource instanceof Observation) {
                Observation observation = (Observation) resource;
                map.put(currentReport, observation);
            }
        }
        return map;
    }

    @Override
    public DomainResource map(Bundle.BundleEntryComponent component) {
        if (component.getResource() instanceof DiagnosticReport) {
            return (DiagnosticReport) component.getResource();
        }
        if (component.getResource() instanceof Observation) {
            return (Observation) component.getResource();
        }
        return null;
    }
}
