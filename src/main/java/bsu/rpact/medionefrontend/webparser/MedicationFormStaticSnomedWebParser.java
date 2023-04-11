package bsu.rpact.medionefrontend.webparser;

import bsu.rpact.medionefrontend.pojo.medical.MedicationForm;
import bsu.rpact.medionefrontend.utils.mapper.SnomedMedicationFormMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

public class MedicationFormStaticSnomedWebParser implements QuerylessWebParser<List<MedicationForm>>{

    @Override
    public List<MedicationForm> parse() {
        Document doc = null;
        try {
            doc = Jsoup.connect("https://build.fhir.org/valueset-medication-form-codes.html").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SnomedMedicationFormMapper().map(doc.select("table"));
    }
}
