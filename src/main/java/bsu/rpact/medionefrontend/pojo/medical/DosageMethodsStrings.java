package bsu.rpact.medionefrontend.pojo.medical;

import java.util.List;

public abstract class DosageMethodsStrings {

    public static final String ONCE = "Once";
    public static final String PERIODICALLY = "Periodically";
    public static final String ON_DEMAND = "On demand";
    public static final String TITRATION_METHOD = "Titration method";

    public static List<String> getDosageMethods(){
        return List.of(ONCE, PERIODICALLY, ON_DEMAND, TITRATION_METHOD);
    }
}
