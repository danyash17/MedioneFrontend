package bsu.rpact.medionefrontend.vaadin.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

public class I18nProvider implements I18NProvider {

    public static final String BUNDLE_PREFIX = "translate";

    public static final Locale RUSSIAN = new Locale("ru", "RU");
    public static final Locale BELARUSIAN = new Locale("be", "BY");
    public static final Locale ENGLISH = new Locale("en","GB");

    @Override
    public List<Locale> getProvidedLocales() {
        return Collections.unmodifiableList(
                Arrays.asList(ENGLISH, BELARUSIAN, RUSSIAN));
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            LoggerFactory.getLogger(I18nProvider.class.getName())
                    .warn("Got lang request for key with null value!");
            return "";
        }

        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale);
        String value;
        try {
            value = bundle.getString(key);
        } catch (final MissingResourceException e) {
            LoggerFactory.getLogger(I18nProvider.class.getName())
                    .warn("Missing resource", e);
            return "!" + locale.getLanguage() + ": " + key;
        }
        if (params.length > 0) {
            value = MessageFormat.format(value, params);
        }
        return value;
    }
}
