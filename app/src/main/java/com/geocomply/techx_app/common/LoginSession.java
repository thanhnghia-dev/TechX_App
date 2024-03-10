package com.geocomply.techx_app.common;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginSession {
    private static final String PREF_NAME = "loginSession";
    private static final String ID_KEY = "id";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";

    private static SharedPreferences preferences;

    public LoginSession(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveLoginSession(String id, String email, String password) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ID_KEY, id);
        editor.putString(EMAIL_KEY, email);
        editor.putString(PASSWORD_KEY, password);
        editor.apply();
    }

    public static void saveIdKeySession(String id) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ID_KEY, id);
        editor.apply();
    }

    public static String getEmailKey() {
        return preferences.getString(EMAIL_KEY, null);
    }

    public static String getIdKey() {
        return preferences.getString(ID_KEY, null);
    }

    public static void clearSession() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
