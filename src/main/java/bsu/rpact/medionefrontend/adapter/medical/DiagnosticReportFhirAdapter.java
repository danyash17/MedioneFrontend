package bsu.rpact.medionefrontend.adapter.medical;

import ca.uhn.fhir.model.api.Include;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.springframework.stereotype.Component;

@Component
public class DiagnosticReportFhirAdapter extends FhirBaseAdapter {

    public Bundle searchIncluded(Class linkedEntity, Integer id) {
        return getClient().search()
                .forResource(DiagnosticReport.class)
                .where(DiagnosticReport.IDENTIFIER.exactly().systemAndIdentifier(linkedEntity.getSimpleName(), id.toString()))
                .include(new Include(getIncludeParameterAsString(DiagnosticReport.class, DiagnosticReport.RESULT)))
                .returnBundle(Bundle.class)
                .execute();
    }

}
