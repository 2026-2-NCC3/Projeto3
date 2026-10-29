package com.example.errornull_proximaetapa;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {
    private static final String PREF_NAME = "ProximaEtapaPrefs";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public UserSessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void salvarDadosUtilizador(String email, String nome, String tipoUsuario) {
        editor.putString("EMAIL", email);
        editor.putString("NOME", nome);
        editor.putString("TIPO_USUARIO", tipoUsuario);
        editor.putBoolean("LOGGED_IN", true);
        editor.apply(); // Aplica de forma assíncrona (rápida)
    }

    public boolean estaLogado() {
        return prefs.getBoolean("LOGGED_IN", false);
    }

    public String getEmail() {
        return prefs.getString("EMAIL", "");
    }

    public String getTipoUsuario() {
        return prefs.getString("TIPO_USUARIO", "");
    }

    public void limparSessao() {
        editor.clear();
        editor.apply();
    }
}