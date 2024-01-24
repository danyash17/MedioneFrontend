package bsu.rpact.medionefrontend.utils.mapper;

import bsu.rpact.medionefrontend.pojo.medical.MedicationForm;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class SnomedMedicationFormMapper implements HtmlMapper<List<MedicationForm>>{

    @Override
    public List<MedicationForm> map(Elements tables) {
        List<MedicationForm> codes = new ArrayList<>();
        Element codeTable = tables.get(2);
        Elements rows = codeTable.select("tr");
        for (int i = 1; i < rows.size(); i++) { // skip header row
            Element row = rows.get(i);
            Elements cols = row.select("td");
            String code = cols.get(0).select("a").get(1).attr("href");
            String display = cols.get(2).text();
            codes.add(new MedicationForm(code, display));
        }
        return codes;
    }
}
