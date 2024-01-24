package bsu.rpact.medionefrontend.utils;

import java.sql.Date;
import java.time.LocalDate;

public class CalculatorUtils {

    public static int getAge(java.sql.Date birthDate){
        return LocalDate.now().getYear() - birthDate.toLocalDate().getYear();
    }

}
