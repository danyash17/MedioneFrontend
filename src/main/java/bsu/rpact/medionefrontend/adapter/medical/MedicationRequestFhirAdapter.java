package bsu.rpact.medionefrontend.adapter.medical;

import ca.uhn.fhir.rest.gclient.StringClientParam;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Procedure;
import org.springframework.stereotype.Component;

@Component
public class MedicationRequestFhirAdapter extends FhirBaseAdapter{

    public boolean isIdentifierUnique(String identifierValue) {
        boolean isUnique = true;
        Bundle response = getClient().search()
                .forResource(MedicationRequest.class)
                .where(new StringClientParam("identifier").matchesExactly().value(identifierValue))
                .returnBundle(Bundle.class)
                .execute();
        int totalResults = response.getTotal();
        if (totalResults > 0) {
            isUnique = false;
        }
        return isUnique;
    }

    public void saveMedicationRequest(MedicationRequest medicationRequest) {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.TRANSACTION);
        bundle.addEntry().setResource(medicationRequest).getRequest().setMethod(Bundle.HTTPVerb.POST);
        Bundle responseBundle = getClient().transaction().withBundle(bundle).execute();
    }

    public Bundle search(Class linkedEntity, Integer id) {
        return getClient().search()
                .forResource(MedicationRequest.class)
                .where(MedicationRequest.IDENTIFIER.exactly().systemAndIdentifier(linkedEntity.getSimpleName(), id.toString()))
                .returnBundle(Bundle.class)
                .execute();
    }

}
