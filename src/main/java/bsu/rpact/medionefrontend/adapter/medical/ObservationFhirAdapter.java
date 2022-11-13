package bsu.rpact.medionefrontend.adapter.medical;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.stereotype.Component;

@Component
public class ObservationFhirAdapter extends FhirBaseAdapter {

    public Bundle search(Class linkedEntity, Integer id) {
        return getClient().search()
                .forResource(Observation.class)
                .where(Observation.IDENTIFIER.exactly().systemAndIdentifier(linkedEntity.getSimpleName(), id.toString()))
                .returnBundle(Bundle.class)
                .execute();
    }

}
