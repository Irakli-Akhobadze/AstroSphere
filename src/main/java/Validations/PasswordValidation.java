package Validations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidation {
	private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }

}
