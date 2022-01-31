package com.theappwelt.vhelp.utilities;

import android.app.Application;

public class NW_URL extends Application {
    public static String ip = "http://13.127.49.54/vhelp/apiv1/";

    public static String RequestOTP() {
        if (ip != null) {
            return ip + "/user/send_user_otp";
        } else {
            return "";
        }
    }

    public static String RegisterUser() {
        if (ip != null) {
            return ip + "/user/insert_user";
        } else {
            return "";
        }
    }


    public static String ResendOTP() {
        if (ip != null) {
            return ip + "/user/resend_user_otp";
        } else {
            return "";
        }
    }

    public static String VerifyOTP() {
        if (ip != null) {
            return ip + "/user/verify_user_otp";
        } else {
            return "";
        }
    }

    public static String InsertContact() {
        if (ip != null) {
            return ip + "/contact/insert_contact";
        } else {
            return "";
        }
    }


}