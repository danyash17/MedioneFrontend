package bsu.rpact.medionefrontend.service.medical.web;

import bsu.rpact.medionefrontend.pojo.medical.MedicationForm;
import bsu.rpact.medionefrontend.webparser.MedicationFormStaticSnomedWebParser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicationFormService {

    private final static MedicationFormStaticSnomedWebParser parser = new MedicationFormStaticSnomedWebParser();

    public List<MedicationForm> getMedicationFormsFromSnomed(){
        return parser.parse();
    }

}
