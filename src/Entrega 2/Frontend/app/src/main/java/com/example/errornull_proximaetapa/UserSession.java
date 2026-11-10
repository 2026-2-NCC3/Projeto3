package com.example.errornull_proximaetapa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.errornull_proximaetapa.auth.LoginInfo;

public class UserSession {
    private static final String PREF_NAME = "ProximaEtapaPrefs";
    private static final String KEY_USER_EMAIL = "user_email";

    public static void setUsuarioAtual(Context context, String email) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public static String getUsuarioAtual(Context context) {
        if (context == null) return "";
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_EMAIL, "aluno@exemplo.com");
    }

    public static String getCurrentUserEmail(Context context) {
        return getUsuarioAtual(context);
    }

    public static SharedPreferences getScopedPreferences(Context context) {
        if (context == null) return null;
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getScopedPreferencesForUser(Context context, String email) {
        if (context == null) return null;
        if (email == null || email.isEmpty()) return getScopedPreferences(context);
        return context.getSharedPreferences("ProximaEtapaPrefs_" + email, Context.MODE_PRIVATE);
    }

    public static void aplicarPreferenciasTema(Context context) {
        if (context == null) return;
        SharedPreferences prefs = getScopedPreferences(context);
        boolean modoEscuro = prefs.getBoolean("modo_escuro", false);
        if (modoEscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static void logout(Context context) {
        if (context == null) return;
        SharedPreferences prefs = getScopedPreferences(context);
        prefs.edit().clear().apply();
        Intent intent = new Intent(context, LoginInfo.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
