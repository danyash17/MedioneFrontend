package bsu.rpact.medionefrontend.pojo.medical;

import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.ResourceType;

import java.util.List;

public class DiagnosticReportContainer extends DomainResource {

    private DiagnosticReport report;
    private List<Observation> observationList;

    public List<Observation> getObservationList() {
        return observationList;
    }

    public void setObservationList(List<Observation> observationList) {
        this.observationList = observationList;
    }

    public DiagnosticReport getReport() {
        return report;
    }

    public void setReport(DiagnosticReport report) {
        this.report = report;
    }

    @Override
    public DomainResource copy() {
        return null;
    }

    @Override
    public ResourceType getResourceType() {
        return null;
    }
}
