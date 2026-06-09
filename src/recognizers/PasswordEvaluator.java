package recognizers;

/*******
 * <p> Title: PasswordEvaluator Class. </p>
 *
 * <p> Description: This class evaluates whether a password satisfies a set of
 * strength requirements using a Finite State Machine approach. It checks that
 * the password does not exceed the maximum allowed length before evaluating
 * strength criteria including uppercase, lowercase, numeric digit, special
 * character, and minimum length requirements.</p>
 *
 * <p> Copyright: Sara Suarez © 2026 </p>
 *
 * @author Sara Suarez
 *
 * @version 1.00    2026-06-04 Initial version for CSE 360 TP1 Story 2 and Story 3
 *
 */

public class PasswordEvaluator {

	    public static String passwordErrorMessage = "";
	    public static int passwordIndexofError = -1;
	    public static boolean foundUpperCase = false;
	    public static boolean foundLowerCase = false;
	    public static boolean foundNumericDigit = false;
	    public static boolean foundSpecialChar = false;
	    public static boolean foundLongEnough = false;

	    // Maximum password length allowed
	    public static final int MAX_PASSWORD_LENGTH = 24;

	    public static String evaluatePassword(String input) {
	        // Reset all flags
	        foundUpperCase = false;
	        foundLowerCase = false;
	        foundNumericDigit = false;
	        foundSpecialChar = false;
	        foundLongEnough = false;
	        passwordIndexofError = 0;
	        passwordErrorMessage = "";

	        // Check empty
	        if (input.length() <= 0)
	            return "*** Error *** The password is empty!";

	        // Check max length FIRST before anything else
	        if (input.length() > MAX_PASSWORD_LENGTH) {
	            passwordIndexofError = MAX_PASSWORD_LENGTH;
	            return "*** Error *** Password is too long. Maximum is " 
	                + MAX_PASSWORD_LENGTH + " characters.";
	        }

	        // Evaluate each character
	        for (int i = 0; i < input.length(); i++) {
	            char c = input.charAt(i);
	            if (c >= 'A' && c <= 'Z') foundUpperCase = true;
	            else if (c >= 'a' && c <= 'z') foundLowerCase = true;
	            else if (c >= '0' && c <= '9') foundNumericDigit = true;
	            else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/"
	                    .indexOf(c) >= 0) foundSpecialChar = true;
	            else {
	                passwordIndexofError = i;
	                return "*** Error *** Invalid character found!";
	            }
	            if (i >= 7) foundLongEnough = true;
	        }

	        // Build error message for missing requirements
	        String errMessage = "";
	        if (!foundUpperCase) errMessage += "Upper case; ";
	        if (!foundLowerCase) errMessage += "Lower case; ";
	        if (!foundNumericDigit) errMessage += "Numeric digit; ";
	        if (!foundSpecialChar) errMessage += "Special character; ";
	        if (!foundLongEnough) errMessage += "At least 8 characters; ";

	        if (errMessage.isEmpty()) return "";
	        
	        passwordIndexofError = input.length();
	        return errMessage + "requirements not satisfied";
	    }
	}
