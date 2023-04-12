package bsu.rpact.medionefrontend.pojo.medical;

import java.util.List;

public abstract class MedicationTimePeriodsStrings {

    public static final String MINUTES = "Minutes";
    public static final String HOURS = "Hours";
    public static final String DAYS = "Days";
    public static final String WEEKS = "Weeks";
    public static final String MONTHS = "Months";
    public static final String YEARS = "Years";

    public static List<String> getMedicationTimePeriods(){
        return List.of(MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS);
    }

}
