package bsu.rpact.medionefrontend.session;

import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.springframework.stereotype.Component;

@Component
public class FhirCashingContainer {

    private Observation observation;
    private DiagnosticReport report;
    private Procedure procedure;

    public Observation getObservation() {
        return observation;
    }

    public void setObservation(Observation observation) {
        this.observation = observation;
    }

    public DiagnosticReport getReport() {
        return report;
    }

    public void setReport(DiagnosticReport report) {
        this.report = report;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }
}
