package bsu.rpact.medionefrontend.vaadin.i18n.components;

import com.vaadin.flow.component.textfield.TextField;

import java.util.Locale;

public class SpecialityLocalizator {

    private static TextField hook = new TextField();

    public static String localize(String speciality){
        return hook.getTranslation(speciality.toLowerCase(Locale.ROOT));
    }

}
