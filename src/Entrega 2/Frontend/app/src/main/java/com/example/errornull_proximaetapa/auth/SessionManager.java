package com.example.errornull_proximaetapa.auth;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.errornull_proximaetapa.UserSession;

public class SessionManager {
    private static final String PREF_NAME = "ProximaEtapaPrefs";
    private static final String KEY_ROLE = "tipoUsuario";
    private static final String KEY_TOKEN = "user_token";

    public static String getRole(Context context) {
        if (context == null) return "aluno";
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ROLE, "aluno");
    }

    public static void setRole(Context context, String role) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_ROLE, role).apply();
    }

    public static String getUsuarioEmail(Context context) {
        return UserSession.getUsuarioAtual(context);
    }

    public static String getToken(Context context) {
        if (context == null) return "";
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, "");
    }

    public static void saveUserSession(Context context, String email, String role, String token) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString("email", email)
                .putString(KEY_ROLE, role)
                .putString(KEY_TOKEN, token)
                .apply();
        UserSession.setUsuarioAtual(context, email);
    }
}
