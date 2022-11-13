package bsu.rpact.medionefrontend.session;

import org.hl7.fhir.r4.model.Observation;
import org.springframework.stereotype.Component;

@Component
public class FhirCashingContainer {

    private Observation observation;

    public Observation getObservation() {
        return observation;
    }

    public void setObservation(Observation observation) {
        this.observation = observation;
    }
}
