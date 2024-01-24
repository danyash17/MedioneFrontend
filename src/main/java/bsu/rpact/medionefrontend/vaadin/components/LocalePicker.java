package bsu.rpact.medionefrontend.vaadin.components;

import bsu.rpact.medionefrontend.pojo.other.Country;
import bsu.rpact.medionefrontend.vaadin.i18n.I18nProvider;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;

public class LocalePicker extends ComboBox<Country> {

    public LocalePicker() {
        List<Country> countryList = new ArrayList<>();
        Country eng = new Country("English", "EN", "Europe", "images/flags/gb.png");
        countryList.add(eng);
        Country blr = new Country("Belarusian", "BY", "Europe", "images/flags/by.png");
        countryList.add(blr);
        Country rus = new Country("Russian", "RU", "Asia", "images/flags/ru.png");
        countryList.add(rus);
        setItems(countryList);
        setRenderer(createRenderer());
        setItemLabelGenerator(country -> country.getCode());
        setAllowCustomValue(false);
        setMaxWidth("100px");
        switch (VaadinSession.getCurrent().getLocale().getISO3Country()){
            case "GBR":{
                setValue(eng);
                break;
            }
            case "BLR":{
                setValue(blr);
                break;
            }
            case "RUS":{
                setValue(rus);
                break;
            }
        }
        addValueChangeListener(e -> {
            switch (e.getValue().getCode()){
                case "EN":{
                    VaadinSession.getCurrent().setLocale(I18nProvider.ENGLISH);
                    break;
                }
                case "BY":{
                    VaadinSession.getCurrent().setLocale(I18nProvider.BELARUSIAN);
                    break;
                }
                case "RU":{
                    VaadinSession.getCurrent().setLocale(I18nProvider.RUSSIAN);
                    break;
                }
            }
        });
    }

    private Renderer<Country> createRenderer() {
        StringBuilder tpl = new StringBuilder();
        tpl.append("<div style=\"display: flex;\">");
        tpl.append("  <img style=\"height: var(--lumo-size-m);\" src=\"${item.flag}\"/>");
        tpl.append("</div>");

        return LitRenderer.<Country>of(tpl.toString()).withProperty("flag", Country::getFlag);
    }
}
