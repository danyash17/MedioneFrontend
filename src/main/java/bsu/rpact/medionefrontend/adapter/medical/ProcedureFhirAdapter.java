package bsu.rpact.medionefrontend.adapter.medical;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Procedure;
import org.springframework.stereotype.Component;

@Component
public class ProcedureFhirAdapter extends FhirBaseAdapter{

    public Bundle search(Class linkedEntity, Integer id) {
        return getClient().search()
                .forResource(Procedure.class)
                .where(Procedure.IDENTIFIER.exactly().systemAndIdentifier(linkedEntity.getSimpleName(), id.toString()))
                .returnBundle(Bundle.class)
                .execute();
    }

}
