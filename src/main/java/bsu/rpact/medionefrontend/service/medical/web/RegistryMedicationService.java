package bsu.rpact.medionefrontend.service.medical.web;

import bsu.rpact.medionefrontend.webparser.MedicationByNameStateRegistryWebParser;
import org.springframework.stereotype.Service;

@Service
public class RegistryMedicationService {

    private final static MedicationByNameStateRegistryWebParser parser = new MedicationByNameStateRegistryWebParser();

    public void searchMedicationInRegistryByName(String searchQuery){
        parser.parse(searchQuery);
    }

}
