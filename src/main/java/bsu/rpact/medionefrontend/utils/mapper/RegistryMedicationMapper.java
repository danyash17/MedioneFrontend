package bsu.rpact.medionefrontend.utils.mapper;

import bsu.rpact.medionefrontend.entity.medical.RegistryMedication;
import bsu.rpact.medionefrontend.pojo.other.Href;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class RegistryMedicationMapper implements HtmlMapper<List<RegistryMedication>>{

    @Override
    public List<RegistryMedication> map(Elements rows) {
        List<RegistryMedication> list = new ArrayList<>();
        for (Element row : rows) {
            RegistryMedication registryMedication = new RegistryMedication();
            Elements columns = row.select("td");
            registryMedication.setOrderNumber(columns.get(0).text());
            registryMedication.setTradeName(columns.get(1).text());
            Elements tdElements = columns.get(1).select("td");
            Elements hrefElements = tdElements.select("a[href]");
            for (Element hrefElement : hrefElements) {
                String label = hrefElement.text();
                String link = hrefElement.attr("href");
                registryMedication.getHrefs().add(new Href(label, link));
            }
            registryMedication.setInternationalName(columns.get(2).text());
            registryMedication.setManufacturer(columns.get(3).text());
            registryMedication.setApplicant(columns.get(4).text());
            registryMedication.setIdNumber(columns.get(5).text());
            registryMedication.setRegistrationDate(columns.get(5).text());
            registryMedication.setExpirationDate(columns.get(6).text());
            registryMedication.setOriginal(columns.get(7).text());
            list.add(registryMedication);
        }
        return list;
    }

}
