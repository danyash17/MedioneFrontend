package bsu.rpact.medionefrontend.session;

import bsu.rpact.medionefrontend.pojo.medical.DiagnosticReportContainer;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.springframework.stereotype.Component;

@Component
public class FhirCashingContainer {

    private Observation observation;
    private DiagnosticReportContainer reportContainer;
    private Procedure procedure;

    public Observation getObservation() {
        return observation;
    }

    public void setObservation(Observation observation) {
        this.observation = observation;
    }

    public DiagnosticReportContainer getReportContainer() {
        return reportContainer;
    }

    public void setReportContainer(DiagnosticReportContainer reportContainer) {
        this.reportContainer = reportContainer;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }
}
