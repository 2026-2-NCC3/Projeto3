package com.example.errornull_proximaetapa;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.errornull_proximaetapa.auth.NotificationHelper;

public class SuspensaoManager {

    public static int getQtdStrikes(Context context) {
        if (context == null) return 0;
        SharedPreferences sp = UserSession.getScopedPreferences(context);
        return sp.getInt("qtd_strikes", 0);
    }

    public static boolean isSuspensoOuBanido(Context context) {
        if (context == null) return false;
        SharedPreferences sp = UserSession.getScopedPreferences(context);
        boolean banido = sp.getBoolean("aluno_banido", false);
        long tempoSuspensaoAte = sp.getLong("tempo_suspensao_ate", 0);
        boolean suspenso = System.currentTimeMillis() < tempoSuspensaoAte;
        return banido || suspenso;
    }

    public static void aplicarStrikeParaEmail(Context context, String email, String motivo, String duracao) {
        if (context == null) return;
        SharedPreferences sp = UserSession.getScopedPreferencesForUser(context, email);
        if (sp == null) sp = UserSession.getScopedPreferences(context);

        int strikes = sp.getInt("qtd_strikes", 0) + 1;
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("qtd_strikes", strikes);

        if (strikes >= 3) {
            ed.putBoolean("aluno_banido", true);
            Toast.makeText(context, "Aluno " + email + " foi BANIDO", Toast.LENGTH_LONG).show();
        } else {
            long duracaoMs = 60 * 1000L;
            if ("1 dia".equalsIgnoreCase(duracao)) duracaoMs = 24 * 60 * 60 * 1000L;
            ed.putLong("tempo_suspensao_ate", System.currentTimeMillis() + duracaoMs);
            Toast.makeText(context, "Strike #" + strikes + " aplicado a " + email + " (" + motivo + ")", Toast.LENGTH_SHORT).show();
        }
        ed.apply();
        NotificationHelper.exibirToastCustomizado(context, "Infração gravada para " + email);
    }

    public static void enviarAvisoProfessorParaEmail(Context context, String email, String mensagem, String remetente) {
        if (context == null) return;
        NotificationHelper.exibirToastCustomizado(context, "Aviso enviado para " + email + ": " + mensagem);
    }

    public static void LiberarAlunoParaEmail(Context context, String email) {
        if (context == null) return;
        SharedPreferences sp = UserSession.getScopedPreferencesForUser(context, email);
        if (sp == null) sp = UserSession.getScopedPreferences(context);

        sp.edit()
                .putInt("qtd_strikes", 0)
                .putBoolean("aluno_banido", false)
                .putLong("tempo_suspensao_ate", 0)
                .apply();
        Toast.makeText(context, "Strikes removidos e conta liberada para " + email, Toast.LENGTH_SHORT).show();
    }

    public static void exibirPainelAdminSuspensaoDialog(Context context) {
        exibirPainelAdminSuspensaoDialog(context, null);
    }

    public static void exibirPainelAdminSuspensaoDialog(Context context, Runnable callback) {
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_suspensoes_admin, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        View btnFechar = dialogView.findViewById(R.id.btnVoltarSuspensao);
        if (btnFechar != null) btnFechar.setOnClickListener(v -> dialog.dismiss());

        Button btnAplicar = dialogView.findViewById(R.id.btnAplicarStrikeAdmin);
        Button btnAviso = dialogView.findViewById(R.id.btnEnviarAvisoAdmin);
        EditText inputEmail = dialogView.findViewById(R.id.inputEmailAlunoStrike);
        EditText inputMotivo = dialogView.findViewById(R.id.inputMotivoStrike);
        EditText inputPrazo = dialogView.findViewById(R.id.inputPrazoStrike);

        if (btnAplicar != null) {
            btnAplicar.setOnClickListener(v -> {
                String e = inputEmail != null ? inputEmail.getText().toString().trim() : "";
                String m = inputMotivo != null ? inputMotivo.getText().toString().trim() : "";
                String p = inputPrazo != null ? inputPrazo.getText().toString().trim() : "";
                if (!e.isEmpty()) {
                    aplicarStrikeParaEmail(context, e, m.isEmpty() ? "Advertência" : m, p.isEmpty() ? "1 minuto" : p);
                    if (callback != null) callback.run();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Informe o e-mail do aluno!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnAviso != null) {
            btnAviso.setOnClickListener(v -> {
                String e = inputEmail != null ? inputEmail.getText().toString().trim() : "";
                String m = inputMotivo != null ? inputMotivo.getText().toString().trim() : "";
                if (!e.isEmpty()) {
                    enviarAvisoProfessorParaEmail(context, e, m, "ADM");
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Informe o e-mail do aluno!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        dialog.show();
    }

    public static void exibirSuspensaoAlunoDialog(Context context) {
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_suspensoes_aluno, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        View btnFechar = dialogView.findViewById(R.id.btnVoltarSuspensao);
        if (btnFechar != null) btnFechar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public static void exibirTelaBloqueioCurso(Context context) {
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_curso_bloqueado, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        View btnEntendi = dialogView.findViewById(R.id.btnEntendiLock);
        if (btnEntendi != null) btnEntendi.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
