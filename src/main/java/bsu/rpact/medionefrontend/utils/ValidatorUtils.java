package bsu.rpact.medionefrontend.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ValidatorUtils {

    public static boolean isValidPassword(String password)
    {
        String latin = "(?=.*[a-z])(?=\\S+$).{8,20}.*\\d+.*$";
        String cyrillic = "(?=.*[\\p{InCyrillic}])(?=\\S+$).{8,20}.*\\d+.*$";
        return doMatch(password, latin) || doMatch(password, cyrillic);
    }

    public static boolean isValidLiteral(String literal){
        String latin = "^[a-zA-Z][a-zA-Z]*$";
        String cyrillic = "^[\\p{InCyrillic}][\\p{InCyrillic}]*$";
        return doMatch(literal, latin) || doMatch(literal, cyrillic);
    }

    public static boolean isValidLogin(String login){
        String latin = "^\\d*[a-zA-Z][a-zA-Z\\d]*$";
        String cyrillic = "^\\d*[\\p{InCyrillic}][\\p{InCyrillic}\\d]*$";
        return doMatch(login, latin) || doMatch(login,cyrillic);
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
