package com.theappwelt.vhelp.utilities;

import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

public class Validation {
    // Regular Expression
    // you can change the expression based on your need
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "^[6-9]\\d{9}$";
    // private static final String PHONE_REGEX = " ^((\\+)?(\\d{2}))?(\\d{10}){1}?$";
    // ^((\+)?(\d{2}[-]))?(\d{10}){1}?$
    // private static final String PHONE_REGEX = "^([0]|\\+91)?[6789]\\d{9}$";
    private static final String TEXT_REGEX = "^[a-z]|[A-Z]|[a-zA-Z]$";
    private static final String Password_REGEX = "";
    private static final String PANCARD_REGEX = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
    private static final String OTP_REGEX = "\\d{1}\\d{1}\\d{1}\\d{1}\\d{1}\\d{1}";
    private static final String CHEQUE_REGEX = "";

    private static final String DATE_REGEX = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
    // private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d~@#$^*()_+={}|,.?: -]{8,}$";//"^[a-zA-Z0-9]{8,}$";//
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d~@#$^*()_+={}|,.?: -]{8,}$";//"^[a-zA-Z0-9]{8,}$";//
    private static final String URL_REGEX = "^(http|https):\\/\\/[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
    // Error Messages
    private static final String REQUIRED_MSG = "Required";
    private static final String EMAIL_MSG = "Invalid email";
    private static final String CHEQUE_MSG = "Please enter 8 digit cheque no.";
    private static final String TEXT_MSG = "Invalid name";
    private static final String DATE_MSG = "Invalid date format";
    private static final String PHONE_MSG = "Please Enter Valid Mobile Number";
    private static final String LANDLINE_MSG = "Please Enter Valid Phone Number";
    private static final String Valid_10digitNo_MSG = "Please enter 10 digit mobile  mobile number ";
    private static final String Valid_13digitNo_MSG = "Please enter mobile no like +91 9999999999";
    private static final String PANCARD_MSG = "Please Enter Correct PAN number !";
    private static final String AADHAR_MSG = "Please Enter Correct Aadhar number !";
    private static final String PASSWORD_MSG = "Password must have a length between 6 and 12";//"Password Must contain 1 Number and 6 Character !";
    private static final String OTP_MSG = "Please Enter Correct OTP !";

    // call this method when you need to check email validation
    public static boolean isEmailAddress(EditText editText, boolean required) {
        return isValidemail(editText, EMAIL_REGEX, EMAIL_MSG, required);
    }

    public static boolean isTextOnly(EditText editText, boolean required) {
        return isValid(editText, TEXT_REGEX, TEXT_MSG, required);
    }


    public static boolean isDate(EditText editText) {
        return isValidbutnotcompulsory(editText, DATE_REGEX, DATE_MSG);
    }


    // call this method when you need to check pan number validation
    public static boolean isPANNUMBER(EditText editText, boolean required) {
        return isValidpan(editText, PANCARD_MSG, required);
    }

    public static boolean isAdhar(EditText editText, boolean required) {
        return isValidAdhar(editText, AADHAR_MSG, required);
    }

    // call this method when you need to check phone number validation
    public static boolean isPhoneNumber(EditText editText, boolean required) {
        return isValid(editText, PHONE_REGEX, PHONE_MSG, required);
    }

    public static boolean isChequeNumber(EditText editText, boolean required) {
        return isValidChequeNo(editText, CHEQUE_REGEX, CHEQUE_MSG, required);
    }

    // call this method when you need to check phone number validation
    public static boolean isLandlineNumber(EditText editText, boolean required) {
        return isValidLandline(editText, PHONE_REGEX, LANDLINE_MSG, required);
    }

    private static boolean isValidLandline(EditText editText, String phone, String errMsg, boolean required) {
        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false;
        //if(required && !has10digit(editText))return false;
       /* if(!PhoneNumberUtils.isGlobalPhoneNumber(text))
        {
            editText.setError(errMsg);
            return false;
        }*/
        String number = "";
        if (text.startsWith("+")) {
            String numb = text.replace("+", "");
            if (numb.contains("-")) {
                number = numb.replaceAll("-", "");
            } else {
                number = numb.replaceAll("(?<=\\d)\\s+(?=\\d+)", "");
            }

        } else {
            if (text.contains("-")) {
                number = text.replaceAll("-", "");
            } else {
                number = text.replaceAll("(?<=\\d)\\s+(?=\\d+)", "");
            }
        }

        if (required && number.length() != 11) {

            editText.setError(errMsg);
            return false;

        }
        return true;
    }

    public static boolean isOTPNumber(EditText editText, boolean required) {
        return isValid(editText, OTP_REGEX, OTP_MSG, required);
    }

    public static boolean isPassword(EditText editText, boolean required) {
        return isValidPasswordd(editText, Password_REGEX, PASSWORD_MSG, required);
    }

    private static boolean isValidPasswordd(EditText editText, String password_regex, String passMsg, boolean required) {
        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);
        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false;
        if (required && text.length() < 6) {
            editText.setError(passMsg);
            return false;
        }

        return true;
    }


    public static boolean isValiedURL(String url) {
        if (!url.startsWith("http"))
            url = "http://" + url;

        if (!Pattern.matches(URL_REGEX, url)) {
            return false;
        }
        return true;
    }

    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false;
        //if(required && !has10digit(editText))return false;
       /* if(!PhoneNumberUtils.isGlobalPhoneNumber(text))
        {
            editText.setError(errMsg);
            return false;
        }*/
        String number = "";
        if (text.startsWith("+")) {
            String numb = text.replace("+", "");
            if (numb.contains("-")) {
                number = numb.replaceAll("\\D", "");
            } else {
                number = numb.replaceAll("(?<=\\d)\\s+(?=\\d+)", "");
            }

        } else if (text.startsWith("0")) {
            String numb = text.replaceFirst("0", "");
            if (numb.contains("-")) {
                number = numb.replaceAll("\\D", "");
            } else {
                number = numb.replaceAll("(?<=\\d)\\s+(?=\\d+)", "");
            }

        } else {
            if (text.contains("-")) {
                number = text.replaceAll("\\D", "");
            } else {
                number = text.replaceAll("(?<=\\d)\\s+(?=\\d+)", "");
            }

        }

        if (required && number.length() != 12) {
            if (number.length() != 10) {
                editText.setError(errMsg);
                return false;
            }
        }

       /* // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex,text)){

            return false;
        }*/


        return true;
    }

    public static boolean isValidemail(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }


        return true;
    }


    public static boolean isValidReferralCode(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }

        if (text.length() != 6) {
            editText.setError(errMsg);
            return false;
        }

        return true;
    }

    public static boolean isValidGPayNo(EditText editText) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        if (text.length() != 10) {
            editText.setError("Invalid Mobile No.");
            return false;
        }

        return true;
    }


    public static boolean isValidChequeNo(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false;

        // pattern doesn't match so returning false

        if (required && text.length() != 8) {
            editText.setError(errMsg);
            return false;
        }


        return true;
    }

    public static boolean isValidbutnotcompulsory(EditText editText, String regex, String errMsg) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        // editText.setError(null);

        // text required and editText is blank, so return false
        if (!hasText(editText)) return false;

        // pattern doesn't match so returning false
        if (!Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }


        return true;
    }


    public static boolean isValidpan(EditText editText, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false;

        // pattern doesn't match so returning false
       /* if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }*/

        if (required && text.length() != 10) {
            editText.setError(errMsg);
            return false;
        }

        return true;
    }

    public static boolean isValidAdhar(EditText editText, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText)) return false;

        // pattern doesn't match so returning false
       /* if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }*/

        if (required && text.length() != 12) {
            editText.setError(errMsg);
            return false;
        }

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        return true;
    }

    public static boolean hasTextview(TextView view, TextView view2) {

        String text = view.getText().toString().trim();
        view2.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            view2.setError(REQUIRED_MSG);
            return false;
        }

        return true;
    }

    public static boolean has10digit(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text

        if (text.length() != 10) {
            editText.setError(Valid_10digitNo_MSG);
            return false;
        }


        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean isPasswordSame(EditText editText, EditText editText1) {

        String text = editText.getText().toString().trim();
        String text1 = editText1.getText().toString().trim();

        // length 0 means there is no text
        if (!text.equalsIgnoreCase(text1)) {
            editText.setError("New Password doesn't match");
            editText1.setError("New Password doesn't match");
            return false;
        }

        return true;
    }


    /*public static boolean isValiedDate(String duration) {
        if (!Pattern.matches(DATE_REGEX, duration)) {
            return false;
        }
        return true;
    }*/
}
