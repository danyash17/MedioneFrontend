package bsu.rpact.medionefrontend.service.medical.web;

import bsu.rpact.medionefrontend.pojo.medical.MedicationForm;
import bsu.rpact.medionefrontend.utils.CachedTranslatorUtils;
import bsu.rpact.medionefrontend.webparser.MedicationFormStaticSnomedWebParser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicationFormService {

    private final static MedicationFormStaticSnomedWebParser parser = new MedicationFormStaticSnomedWebParser();

    public List<MedicationForm> getMedicationFormsFromSnomed(){
        return parser.parse();
    }

    public List<MedicationForm> getTranslatedMedicationFormsFromSnomed(){
        List<MedicationForm> list = getMedicationFormsFromSnomed();
        list.stream().forEach(item -> {
            if (CachedTranslatorUtils.translations.containsKey(item.getDisplay())){
                item.setDisplay(CachedTranslatorUtils.translations.get(item.getDisplay()));
            }
        });
        return list;
    }

}
