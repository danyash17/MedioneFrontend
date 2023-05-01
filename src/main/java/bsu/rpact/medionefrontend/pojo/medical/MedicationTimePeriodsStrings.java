package bsu.rpact.medionefrontend.pojo.medical;

import java.util.List;

public abstract class MedicationTimePeriodsStrings {

    public static final String MINUTES = "минут";
    public static final String HOURS = "часов";
    public static final String DAYS = "дней";
    public static final String WEEKS = "недель";
    public static final String MONTHS = "месяцев";
    public static final String YEARS = "лет";

    public static List<String> getMedicationTimePeriods(){
        return List.of(MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS);
    }

}
