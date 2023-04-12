package bsu.rpact.medionefrontend.pojo.medical;

import java.util.List;

public abstract class MedicationMeasureUnitsStrings {

    public static final String MILIGRAMS = "Milligrams";
    public static final String MICROGRAMS = "Micrograms";
    public static final String GRAMS = "Grams";
    public static final String INTERNATIONAL_UNITS = "International Units";
    public static final String UNITS = "Units";
    public static final String MILLILITERS = "Milliliters";
    public static final String DROPS = "Drops";
    public static final String PUFFS = "Puffs";
    public static final String TABLETS_OR_CAPSULES = "Tablets or capsules";

    public static List<String> getMedicationMeasureUnits(){
        return List.of(MILIGRAMS, MICROGRAMS, GRAMS, INTERNATIONAL_UNITS, UNITS, MILLILITERS, DROPS, PUFFS, TABLETS_OR_CAPSULES);
    }

}
