package bsu.rpact.medionefrontend.pojo.medical;

import bsu.rpact.medionefrontend.utils.CachedTranslatorUtils;

import java.util.List;

public abstract class DosageMethodsStrings {

    public static final String ONCE = "Единоразово";
    public static final String PERIODICALLY = "Периодично";
    public static final String ON_DEMAND = "По востребованию";
    public static final String TITRATION_METHOD = "Тетрационный метод";

    public static List<String> getDosageMethods(){
        return List.of(ONCE, PERIODICALLY, ON_DEMAND, TITRATION_METHOD);
    }
}
