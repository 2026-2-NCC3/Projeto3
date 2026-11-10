package com.example.errornull_proximaetapa;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.errornull_proximaetapa.auth.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import java.util.List;
import java.util.Locale;


public class AttendanceManager {

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("AttendancePrefs", Context.MODE_PRIVATE);
    }

    public static boolean isSessaoAtiva(Context context, String cursoId) {
        if (context == null) return false;
        return getPrefs(context).getBoolean("sessao_ativa_" + cursoId, false);
    }

    public static boolean isEntradaLiberada(Context context, String cursoId) {
        if (context == null) return false;
        return getPrefs(context).getBoolean("entrada_liberada_" + cursoId, false);
    }

    public static boolean isSaidaLiberada(Context context, String cursoId) {
        if (context == null) return false;
        return getPrefs(context).getBoolean("saida_liberada_" + cursoId, false);
    }

    public static String getStatusSessaoTexto(Context context, String cursoId) {
        if (context == null) return "Sessão Fechada";
        if (isSessaoAtiva(context, cursoId)) {
            if (isEntradaLiberada(context, cursoId)) return "🟢 Entrada Liberada";
            if (isSaidaLiberada(context, cursoId)) return "🟡 Saída Liberada";
            return "🔵 Sessão Ativa (QR Bloqueado)";
        }
        return "🔴 Sessão Fechada";
    }

    public static boolean isModuloEntradaLiberada(Context context, String cursoId, int modIndex) {
        if (context == null) return false;
        return getPrefs(context).getBoolean("mod_entrada_" + cursoId + "_" + modIndex, false);
    }

    public static boolean isModuloSaidaLiberada(Context context, String cursoId, int modIndex) {
        if (context == null) return false;
        return getPrefs(context).getBoolean("mod_saida_" + cursoId + "_" + modIndex, false);
    }

    public static void toggleModuloEntrada(Context context, String cursoId, int modIndex, String nomeModulo) {
        if (context == null) return;
        boolean val = !isModuloEntradaLiberada(context, cursoId, modIndex);
        getPrefs(context).edit().putBoolean("mod_entrada_" + cursoId + "_" + modIndex, val).apply();
        NotificationHelper.exibirToastCustomizado(context, val ? "Entrada liberada para: " + nomeModulo : "Entrada bloqueada para: " + nomeModulo);
    }

    public static void toggleModuloSaida(Context context, String cursoId, int modIndex, String nomeModulo) {
        if (context == null) return;
        boolean val = !isModuloSaidaLiberada(context, cursoId, modIndex);
        getPrefs(context).edit().putBoolean("mod_saida_" + cursoId + "_" + modIndex, val).apply();
        NotificationHelper.exibirToastCustomizado(context, val ? "Saída liberada para: " + nomeModulo : "Saída bloqueada para: " + nomeModulo);
    }

    public static boolean validarEConfirmarPresencaQrCode(Context context, String courseId, String courseName, String qrTokenPayload, Runnable onComplete) {
        if (context == null || qrTokenPayload == null || qrTokenPayload.isEmpty()) {
            NotificationHelper.exibirToastCustomizado(context, "⚠️ Token do QR Code inválido ou vazio!");
            return false;
        }

        String[] partes = qrTokenPayload.trim().split("\\|\\|\\|");
        String studentEmail = partes.length > 1 ? partes[1] : "aluno@proximaetapa.com";
        String studentName = partes.length > 2 ? partes[2] : "Estudante";
        String targetCourseId = partes.length > 3 ? partes[3] : courseId;
        String tipoRegra = partes.length > 5 ? partes[5] : "ENTRADA";
        long timestamp = 0;
        long validadeMs = 0;

        if (partes.length > 6) {
            try { timestamp = Long.parseLong(partes[6]); } catch (Exception ignored) {}
        }
        if (partes.length > 7) {
            try { validadeMs = Long.parseLong(partes[7]); } catch (Exception ignored) {}
        }

        if (courseId != null && !courseId.equalsIgnoreCase("Geral") && !targetCourseId.equalsIgnoreCase(courseId)) {
            NotificationHelper.exibirToastCustomizado(context, "⚠️ Este QR Code pertence a outro curso!");
            return false;
        }

        if (timestamp > 0 && validadeMs > 0) {
            long decorrido = System.currentTimeMillis() - timestamp;
            if (decorrido > validadeMs) {
                NotificationHelper.exibirToastCustomizado(context, "⚠️ QR Code Expirado! Peça ao aluno para atualizar o código.");
                return false;
            }
        }

        String dataHoraHora = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        getPrefs(context).edit()
                .putBoolean("presenca_" + studentEmail + "_" + targetCourseId, true)
                .putString("presenca_tipo_" + studentEmail + "_" + targetCourseId, tipoRegra)
                .putString("presenca_data_" + studentEmail + "_" + targetCourseId, dataHoraHora)
                .apply();

        SharedPreferences spStudent = UserSession.getScopedPreferencesForUser(context, studentEmail);
        int progressoAtual = spStudent.getInt("progressoCurso_" + targetCourseId, 0);
        int novoProgresso = Math.min(100, progressoAtual + 25);

        spStudent.edit()
                .putBoolean("presenca_confirmada_" + targetCourseId, true)
                .putString("presenca_status_" + targetCourseId, "PRESENTE (" + tipoRegra + ")")
                .putString("presenca_data_" + targetCourseId, dataHoraHora)
                .putInt("progressoCurso_" + targetCourseId, novoProgresso)
                .apply();

        NotificationHelper.adicionarNotificacao(
                context,
                "Presença Confirmada!",
                "Sua presença de " + tipoRegra + " no curso " + courseName + " foi validada com sucesso!"
        );

        NotificationHelper.exibirToastCustomizado(context, "✅ Presença de " + tipoRegra + " CONFIRMADA para " + studentName + "!");

        if (onComplete != null) {
            onComplete.run();
        }

        return true;
    }

    public static void exibirDialogScannerQrCode(Context context, String courseId, String courseName, Runnable callback) {
        if (context == null) return;

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_scanner_qrcode, null);

        TextView txtTitulo = dialogView.findViewById(R.id.txtTituloScannerProf);
        EditText inputToken = dialogView.findViewById(R.id.inputTokenScanner);
        Button btnValidar = dialogView.findViewById(R.id.btnValidarToken);
        Button btnFechar = dialogView.findViewById(R.id.btnFecharScanner);

        if (txtTitulo != null) {
            txtTitulo.setText("📷 Scanner & Validação: " + (courseName != null ? courseName : "Disciplina"));
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnValidar != null) {
            btnValidar.setOnClickListener(v -> {
                String token = inputToken != null ? inputToken.getText().toString().trim() : "";
                if (token.isEmpty()) {
                    NotificationHelper.exibirToastCustomizado(context, "Digite ou escaneie o token do QR Code do aluno!");
                    return;
                }

                boolean ok = validarEConfirmarPresencaQrCode(context, courseId, courseName != null ? courseName : "Disciplina", token, callback);
                if (ok) {
                    dialog.dismiss();
                }
            });
        }

        if (btnFechar != null) {
            btnFechar.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }

    public static void exibirDialogListaPresencaTurma(Context context, String courseId, String courseName) {
        if (context == null) return;

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_lista_presenca_turma, null);

        TextView txtTitulo = dialogView.findViewById(R.id.txtTituloListaPresencaProf);
        ImageView btnFechar = dialogView.findViewById(R.id.btnFecharCardDialog);
        LinearLayout containerItens = dialogView.findViewById(R.id.containerItensPresencaTurma);

        if (txtTitulo != null) {
            txtTitulo.setText("📋 Lista de Presença: " + (courseName != null ? courseName : "Turma"));
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnFechar != null) {
            btnFechar.setOnClickListener(v -> dialog.dismiss());
        }

        if (containerItens != null) {
            containerItens.removeAllViews();

            SharedPreferences spGeneral = context.getSharedPreferences("ListaGeralAlunosAdmin", Context.MODE_PRIVATE);
            String savedString = spGeneral.getString("lista_alunos_cadastrados_string", "");
            List<String> listaAlunos = new ArrayList<>();

            if (!savedString.isEmpty()) {
                listaAlunos.addAll(Arrays.asList(savedString.split("###")));
            } else {
                listaAlunos.add("Bolo Fofo;;;aluno@proximaetapa.com");
            }

            for (String aStr : listaAlunos) {
                String[] partes = aStr.split(";;;");
                String nomeA = partes.length > 0 ? partes[0] : "Estudante";
                String emailA = partes.length > 1 ? partes[1] : "aluno@escola.com";

                boolean presente = getPrefs(context).getBoolean("presenca_" + emailA + "_" + courseId, false);
                String dataP = getPrefs(context).getString("presenca_data_" + emailA + "_" + courseId, "Pendente");

                LinearLayout rowCard = new LinearLayout(context);
                rowCard.setOrientation(LinearLayout.HORIZONTAL);
                rowCard.setBackgroundResource(R.drawable.bg_card_sobreposicao1);
                rowCard.setPadding(16, 12, 16, 12);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 10);
                rowCard.setLayoutParams(lp);

                LinearLayout colText = new LinearLayout(context);
                colText.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams lpCol = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                colText.setLayoutParams(lpCol);

                TextView tvNome = new TextView(context);
                tvNome.setText("👤 " + nomeA);
                tvNome.setTextColor(context.getColor(R.color.dark_indigo));
                tvNome.setTextSize(13);
                tvNome.setTypeface(null, android.graphics.Typeface.BOLD);

                TextView tvEmail = new TextView(context);
                tvEmail.setText(emailA + " • " + (presente ? ("Registrado às " + dataP) : "Aguardando leitura QR Code"));
                tvEmail.setTextColor(context.getColor(R.color.dark_gray));
                tvEmail.setTextSize(11);

                colText.addView(tvNome);
                colText.addView(tvEmail);

                Button btnStatus = new Button(context);
                btnStatus.setText(presente ? "🟢 PRESENTE" : "🔴 PENDENTE");
                btnStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        context.getColor(presente ? R.color.mint : R.color.yellow)
                ));
                btnStatus.setTextColor(context.getColor(R.color.dark_indigo));
                btnStatus.setTextSize(10);
                btnStatus.setTypeface(null, android.graphics.Typeface.BOLD);

                btnStatus.setOnClickListener(v -> {
                    boolean novoPresente = !presente;
                    getPrefs(context).edit()
                            .putBoolean("presenca_" + emailA + "_" + courseId, novoPresente)
                            .putString("presenca_data_" + emailA + "_" + courseId, new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()))
                            .apply();

                    NotificationHelper.exibirToastCustomizado(context, novoPresente ? "Presença de " + nomeA + " confirmada!" : "Presença alterada para pendente.");
                    dialog.dismiss();
                    exibirDialogListaPresencaTurma(context, courseId, courseName);
                });

                rowCard.addView(colText);
                rowCard.addView(btnStatus);

                containerItens.addView(rowCard);
            }
        }

        dialog.show();
    }

    public static void exibirDialogGestaoPresencaProf(Context context, String cursoId) {
        exibirDialogGestaoPresencaProf(context, cursoId, "Disciplina", null);
    }

    public static void exibirDialogGestaoPresencaProf(Context context, String cursoId, String cursoNome, Runnable callback) {
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_gestao_sessao_presenca, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView txtPill = dialogView.findViewById(R.id.txtSessaoStatusPill);
        TextView txtDetalhes = dialogView.findViewById(R.id.txtDetalhesSessaoAtiva);
        Button btnNovaSessao = dialogView.findViewById(R.id.btnAbrirNovaSessaoAulaProf);
        LinearLayout containerControles = dialogView.findViewById(R.id.containerControlesSessaoAtivaProf);
        Button btnEntrada = dialogView.findViewById(R.id.btnToggleLiberarEntradaProf);
        Button btnSaida = dialogView.findViewById(R.id.btnToggleLiberarSaidaProf);
        Button btnScanner = dialogView.findViewById(R.id.btnAbrirScannerQrProf);
        Button btnLista = dialogView.findViewById(R.id.btnVerListaPresencaTurmaProf);
        Button btnEncerrar = dialogView.findViewById(R.id.btnEncerrarSessaoAulaProf);
        Button btnFechar = dialogView.findViewById(R.id.btnFecharGestaoPresencaProf);

        Runnable atualizarUi = () -> {
            boolean ativa = isSessaoAtiva(context, cursoId);
            if (txtPill != null) txtPill.setText(ativa ? "Sessão Ativa" : "Sessão Fechada");
            if (txtDetalhes != null) txtDetalhes.setText(ativa ? "Sessão em andamento para " + cursoNome : "Nenhuma sessão em andamento.");
            if (containerControles != null) containerControles.setVisibility(ativa ? View.VISIBLE : View.GONE);
            if (btnNovaSessao != null) btnNovaSessao.setVisibility(ativa ? View.GONE : View.VISIBLE);
        };

        atualizarUi.run();

        if (btnNovaSessao != null) {
            btnNovaSessao.setOnClickListener(v -> {
                getPrefs(context).edit().putBoolean("sessao_ativa_" + cursoId, true).apply();
                atualizarUi.run();
                if (callback != null) callback.run();
            });
        }

        if (btnEntrada != null) {
            btnEntrada.setOnClickListener(v -> {
                boolean val = !isEntradaLiberada(context, cursoId);
                getPrefs(context).edit().putBoolean("entrada_liberada_" + cursoId, val).apply();
                Toast.makeText(context, val ? "Entrada por QR Code LIBERADA" : "Entrada BLOQUEADA", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnSaida != null) {
            btnSaida.setOnClickListener(v -> {
                boolean val = !isSaidaLiberada(context, cursoId);
                getPrefs(context).edit().putBoolean("saida_liberada_" + cursoId, val).apply();
                Toast.makeText(context, val ? "Saída por QR Code LIBERADA" : "Saída BLOQUEADA", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnScanner != null) {
            btnScanner.setOnClickListener(v -> {
                exibirDialogScannerQrCode(context, cursoId, cursoNome, callback);
            });
        }

        if (btnLista != null) {
            btnLista.setOnClickListener(v -> {
                exibirDialogListaPresencaTurma(context, cursoId, cursoNome);
            });
        }

        if (btnEncerrar != null) {
            btnEncerrar.setOnClickListener(v -> {
                getPrefs(context).edit()
                        .putBoolean("sessao_ativa_" + cursoId, false)
                        .putBoolean("entrada_liberada_" + cursoId, false)
                        .putBoolean("saida_liberada_" + cursoId, false)
                        .apply();
                atualizarUi.run();
                if (callback != null) callback.run();
            });
        }

        if (btnFechar != null) btnFechar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
