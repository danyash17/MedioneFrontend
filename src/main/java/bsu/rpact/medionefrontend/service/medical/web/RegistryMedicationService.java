package bsu.rpact.medionefrontend.service.medical.web;

import bsu.rpact.medionefrontend.pojo.medical.RegistryMedication;
import bsu.rpact.medionefrontend.webparser.MedicationByNameStateRegistryWebParser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistryMedicationService {

    private final static MedicationByNameStateRegistryWebParser parser = new MedicationByNameStateRegistryWebParser();

    public List<RegistryMedication> searchMedicationInRegistryByName(String searchQuery){
        return parser.parse(searchQuery);
    }

}
