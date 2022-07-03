package bsu.rpact.medionefrontend.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ValidatorUtils {

    public static boolean isValidPassword(String password)
    {
        String regex = "(?=.*[a-z])"
                + "(?=\\S+$).{8,20}$";
        return doMatch(password, regex);
    }

    public static boolean isValidLiteral(String literal){
        String regex = "^[a-zA-Z][a-zA-Z]*$";
        return doMatch(literal, regex);
    }

    public static boolean isValidLogin(String login){
        String regex = "^\\d*[a-zA-Z][a-zA-Z\\d]*$";
        return doMatch(login, regex);
    }

    public static boolean isValidPhone(String phone){
        String regex = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$";
        return doMatch(phone, regex);
    }

    private static boolean doMatch(String login, String regex) {
        Pattern p = Pattern.compile(regex);
        if (login == null) {
            return false;
        }
        Matcher m = p.matcher(login);
        return m.matches();
    }

}
