package bsu.rpact.medionefrontend.service.medical;

import bsu.rpact.medionefrontend.adapter.medical.MedicationRequestFhirAdapter;
import bsu.rpact.medionefrontend.entity.Patient;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class MedicationRequestService implements FhirBundleMapper{

    @Autowired
    private MedicationRequestFhirAdapter adapter;

    public boolean isIdentifierUnique(String identifierValue) {
        return adapter.isIdentifierUnique(identifierValue);
    }

    public void saveMedicationRequest(MedicationRequest medicationRequest){
        adapter.saveMedicationRequest(medicationRequest);
    }

    @Override
    public MedicationRequest map(Bundle.BundleEntryComponent component) {
        return (MedicationRequest) component.getResource();
    }

    public List<MedicationRequest> search(Class<Patient> linkedEntity, Integer id) {
        Bundle bundle = adapter.search(linkedEntity,id);
        List<MedicationRequest> list = new LinkedList<>();
        for (Bundle.BundleEntryComponent component:bundle.getEntry()){
            list.add(map(component));
        }
        return list;
    }
}
