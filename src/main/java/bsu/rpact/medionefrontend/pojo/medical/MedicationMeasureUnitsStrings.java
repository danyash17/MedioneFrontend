package bsu.rpact.medionefrontend.pojo.medical;

import bsu.rpact.medionefrontend.utils.CachedTranslatorUtils;

import java.util.List;

public abstract class MedicationMeasureUnitsStrings {

    public static final String MILIGRAMS = "милиграмм";
    public static final String MICROGRAMS = "микрограмм";
    public static final String GRAMS = "грамм";
    public static final String INTERNATIONAL_UNITS = "международных единиц";
    public static final String UNITS = "единиц";
    public static final String MILLILITERS = "милилитров";
    public static final String DROPS = "капель";
    public static final String PUFFS = "впрыскиваний";
    public static final String TABLETS_OR_CAPSULES = "таблеток или капсул";

    public static List<String> getMedicationMeasureUnits(){
        return List.of(MILIGRAMS, MICROGRAMS, GRAMS, INTERNATIONAL_UNITS, UNITS, MILLILITERS, DROPS, PUFFS, TABLETS_OR_CAPSULES);
    }

}
