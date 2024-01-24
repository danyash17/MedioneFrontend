package bsu.rpact.medionefrontend.service.medical;

import bsu.rpact.medionefrontend.adapter.medical.FhirBaseAdapter;
import bsu.rpact.medionefrontend.adapter.medical.ObservationFhirAdapter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ObservationService implements FhirBundleMapper<Observation>{

    @Autowired
    private ObservationFhirAdapter adapter;

    public List<Observation> search(Class linkedEntity, Integer id){
        Bundle bundle = adapter.search(linkedEntity,id);
        List<Observation> list = new LinkedList<>();
        for (Bundle.BundleEntryComponent component:bundle.getEntry()){
            list.add(map(component));
        }
        return list;
    }

    @Override
    public Observation map(Bundle.BundleEntryComponent component) {
        return (Observation) component.getResource();
    }
}
