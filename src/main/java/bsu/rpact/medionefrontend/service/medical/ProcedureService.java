package bsu.rpact.medionefrontend.service.medical;

import bsu.rpact.medionefrontend.adapter.medical.ProcedureFhirAdapter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Procedure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ProcedureService implements FhirBundleMapper<Procedure>{

    @Autowired
    private ProcedureFhirAdapter adapter;

    public List<Procedure> search(Class linkedEntity, Integer id){
        Bundle bundle = adapter.search(linkedEntity,id);
        List<Procedure> list = new LinkedList<>();
        for (Bundle.BundleEntryComponent component:bundle.getEntry()){
            list.add(map(component));
        }
        return list;
    }

    @Override
    public Procedure map(Bundle.BundleEntryComponent component) {
        return (Procedure) component.getResource();
    }
}
