package com.example.errornull_proximaetapa.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.errornull_proximaetapa.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RequerimentosManager {

    private static final String PREF_REQUERIMENTOS = "lista_requerimentos_geral_string";
    private static String filtroReqAtual = "todos";

    public static void enviarRequerimento(Context context, String emailAluno, String nomeAluno, String tipoProblema, String relato) {
        if (context == null || relato == null || relato.trim().isEmpty()) return;

        SharedPreferences sp = context.getSharedPreferences("RequerimentosDataBase", Context.MODE_PRIVATE);
        String savedString = sp.getString(PREF_REQUERIMENTOS, "");

        List<String> lista = new ArrayList<>();
        if (!savedString.isEmpty()) {
            lista.addAll(Arrays.asList(savedString.split("###")));
        }

        String reqId = "req_" + System.currentTimeMillis();
        String dataHora = DateFormat.format("dd/MM/yyyy • HH:mm", new Date()).toString();

        // Format: reqId|||emailAluno|||nomeAluno|||tipoProblema|||relato|||dataHora|||status|||respostaAdm
        String itemReq = reqId + "|||" + emailAluno + "|||" + nomeAluno + "|||" + tipoProblema + "|||" + relato.trim() + "|||" + dataHora + "|||Pendente|||Sem resposta da Direção ainda";

        lista.add(0, itemReq);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            sb.append(lista.get(i));
            if (i < lista.size() - 1) sb.append("###");
        }

        sp.edit().putString(PREF_REQUERIMENTOS, sb.toString()).apply();

        NotificationHelper.adicionarNotificacao(
                context,
                "📬 Requerimento Enviado com Sucesso!",
                "Sua solicitação de '" + tipoProblema + "' foi recebida pela Direção/ADM."
        );
        NotificationHelper.exibirToastCustomizado(context, "📬 Requerimento enviado para a Direção!");
    }

    public static List<String> getListaRequerimentosGeral(Context context) {
        if (context == null) return new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences("RequerimentosDataBase", Context.MODE_PRIVATE);
        String savedString = sp.getString(PREF_REQUERIMENTOS, "");

        if (savedString.isEmpty()) {
            // Exemplos iniciais
            enviarRequerimento(context, "aluno@proximaetapa.com", "Bolo Fofo", "Solicitação de Atestado / Declaração", "Gostaria de solicitar a declaração de matrícula no curso de Dev Web.");
            savedString = sp.getString(PREF_REQUERIMENTOS, "");
        }

        return new ArrayList<>(Arrays.asList(savedString.split("###")));
    }

    public static void responderRequerimentoAdm(Context context, String reqIdTarget, String respostaAdmText) {
        if (context == null || reqIdTarget == null || respostaAdmText == null) return;

        SharedPreferences sp = context.getSharedPreferences("RequerimentosDataBase", Context.MODE_PRIVATE);
        List<String> lista = getListaRequerimentosGeral(context);

        StringBuilder sb = new StringBuilder();
        String emailAlunoTarget = "";

        for (int i = 0; i < lista.size(); i++) {
            String item = lista.get(i);
            String[] p = item.split("\\|\\|\\|");
            String reqId = p.length > 0 ? p[0] : "";

            if (reqId.equals(reqIdTarget) && p.length >= 7) {
                emailAlunoTarget = p[1];
                item = p[0] + "|||" + p[1] + "|||" + p[2] + "|||" + p[3] + "|||" + p[4] + "|||" + p[5] + "|||Respondido|||" + respostaAdmText;
            }

            sb.append(item);
            if (i < lista.size() - 1) sb.append("###");
        }

        sp.edit().putString(PREF_REQUERIMENTOS, sb.toString()).apply();

        if (!emailAlunoTarget.isEmpty()) {
            NotificationHelper.adicionarNotificacao(
                    context,
                    "📬 Resposta ao seu Requerimento!",
                    "A Direção respondeu à sua solicitação: " + respostaAdmText
            );
            NotificationHelper.exibirToastCustomizado(context, "📬 Resposta do ADM enviada ao estudante!");
        }
    }

    public static void concluirRequerimentoAdm(Context context, String reqIdTarget) {
        if (context == null || reqIdTarget == null) return;

        SharedPreferences sp = context.getSharedPreferences("RequerimentosDataBase", Context.MODE_PRIVATE);
        List<String> lista = getListaRequerimentosGeral(context);

        StringBuilder sb = new StringBuilder();
        String emailAlunoTarget = "";

        for (int i = 0; i < lista.size(); i++) {
            String item = lista.get(i);
            String[] p = item.split("\\|\\|\\|");
            String reqId = p.length > 0 ? p[0] : "";

            if (reqId.equals(reqIdTarget) && p.length >= 7) {
                emailAlunoTarget = p[1];
                String respExistente = p.length > 7 ? p[7] : "Solicitação concluída e deferida pela Direção";
                item = p[0] + "|||" + p[1] + "|||" + p[2] + "|||" + p[3] + "|||" + p[4] + "|||" + p[5] + "|||Concluído|||" + respExistente;
            }

            sb.append(item);
            if (i < lista.size() - 1) sb.append("###");
        }

        sp.edit().putString(PREF_REQUERIMENTOS, sb.toString()).apply();

        if (!emailAlunoTarget.isEmpty()) {
            NotificationHelper.adicionarNotificacao(
                    context,
                    "✅ Requerimento Concluído!",
                    "A Direção/ADM concluiu e finalizou a sua solicitação com sucesso!"
            );
            NotificationHelper.exibirToastCustomizado(context, "✅ Tarefa marcada como CONCLUÍDA!");
        }
    }

    public static void abrirPainelRequerimentosAdmin(Context context) {
        if (context == null) return;

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_painel_requerimentos_adm, null);

        View btnFechar = dialogView.findViewById(R.id.btnFecharCardDialog);
        LinearLayout containerLista = dialogView.findViewById(R.id.containerListaReqAdmin);

        TextView fTodos = dialogView.findViewById(R.id.filterReqTodos);
        TextView fPendentes = dialogView.findViewById(R.id.filterReqPendentes);
        TextView fRespondidos = dialogView.findViewById(R.id.filterReqRespondidos);
        TextView fDeclaracao = dialogView.findViewById(R.id.filterReqDeclaracao);
        TextView fTecnico = null;

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnFechar != null) btnFechar.setOnClickListener(v -> dialog.dismiss());

        Runnable renderizarLista = () -> {
            if (containerLista == null) return;
            containerLista.removeAllViews();

            List<String> lista = getListaRequerimentosGeral(context);
            int exibidos = 0;
            int hPx = (int) (40 * context.getResources().getDisplayMetrics().density);

            for (String itemStr : lista) {
                String[] p = itemStr.split("\\|\\|\\|");
                String reqId = p.length > 0 ? p[0] : "";
                String emailAluno = p.length > 1 ? p[1] : "";
                String nomeAluno = p.length > 2 ? p[2] : "Aluno";
                String tipoProblema = p.length > 3 ? p[3] : "Geral";
                String relato = p.length > 4 ? p[4] : "";
                String dataHora = p.length > 5 ? p[5] : "";
                String status = p.length > 6 ? p[6] : "Pendente";
                String resposta = p.length > 7 ? p[7] : "Sem resposta da Direção ainda";

                boolean isConcluido = "Concluído".equalsIgnoreCase(status) || "Respondido".equalsIgnoreCase(status);

                // Filtros do ADM
                if ("pendentes".equalsIgnoreCase(filtroReqAtual) && isConcluido) continue;
                if ("respondidos".equalsIgnoreCase(filtroReqAtual) && !isConcluido) continue;
                if ("declaracao".equalsIgnoreCase(filtroReqAtual) && !tipoProblema.contains("Declaração") && !tipoProblema.contains("Atestado")) continue;
                if ("tecnico".equalsIgnoreCase(filtroReqAtual) && !tipoProblema.contains("Técnico") && !tipoProblema.contains("Acesso")) continue;

                exibidos++;

                LinearLayout cardR = new LinearLayout(context);
                cardR.setOrientation(LinearLayout.VERTICAL);
                cardR.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
                cardR.setPadding(16, 14, 16, 14);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 4, 0, 10);
                cardR.setLayoutParams(lp);

                TextView txtHeader = new TextView(context);
                txtHeader.setText("👤 " + nomeAluno + " (" + emailAluno + ")\n📅 " + dataHora + " • Status: " + status.toUpperCase());
                txtHeader.setTextColor(context.getColor(isConcluido ? R.color.mint : R.color.purple));
                txtHeader.setTextSize(12);
                txtHeader.setTypeface(null, Typeface.BOLD);

                TextView txtTipo = new TextView(context);
                txtTipo.setText("📌 Assunto: " + tipoProblema);
                txtTipo.setTextColor(context.getColor(R.color.dark_indigo));
                txtTipo.setTextSize(12);
                txtTipo.setTypeface(null, Typeface.BOLD);

                TextView txtRelato = new TextView(context);
                txtRelato.setText("💬 Relato: " + relato);
                txtRelato.setTextColor(context.getColor(R.color.dark_gray));
                txtRelato.setTextSize(12);

                TextView txtResp = new TextView(context);
                txtResp.setText("✉️ Resposta ADM: " + resposta);
                txtResp.setTextColor(context.getColor(isConcluido ? R.color.dark_indigo : R.color.dark_gray));
                txtResp.setTextSize(12);

                // Barra de Botões de Ação do ADM
                LinearLayout actionRow = new LinearLayout(context);
                actionRow.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lpRow.topMargin = 12;
                actionRow.setLayoutParams(lpRow);

                Button btnResp = new Button(context);
                btnResp.setText("💬 Responder");
                btnResp.setBackgroundTintList(android.content.res.ColorStateList.valueOf(context.getColor(R.color.yellow)));
                btnResp.setTextColor(context.getColor(R.color.dark_indigo));
                btnResp.setTypeface(null, Typeface.BOLD);
                btnResp.setTextSize(11);
                btnResp.setPadding(0, 0, 0, 0);

                LinearLayout.LayoutParams lpBtn1 = new LinearLayout.LayoutParams(0, hPx, 1f);
                lpBtn1.rightMargin = 8;
                btnResp.setLayoutParams(lpBtn1);

                btnResp.setOnClickListener(v -> exibirDialogRespostaAdmin(context, reqId, nomeAluno, relato, () -> {
                    dialog.dismiss();
                    abrirPainelRequerimentosAdmin(context);
                }));

                Button btnConcluir = new Button(context);
                btnConcluir.setText(isConcluido ? "✓ Concluído" : "✅ Concluir Tarefa");
                btnConcluir.setBackgroundTintList(android.content.res.ColorStateList.valueOf(context.getColor(isConcluido ? R.color.dark_gray : R.color.mint)));
                btnConcluir.setTextColor(context.getColor(R.color.white));
                btnConcluir.setTypeface(null, Typeface.BOLD);
                btnConcluir.setTextSize(11);
                btnConcluir.setPadding(0, 0, 0, 0);

                LinearLayout.LayoutParams lpBtn2 = new LinearLayout.LayoutParams(0, hPx, 1f);
                btnConcluir.setLayoutParams(lpBtn2);

                btnConcluir.setOnClickListener(v -> {
                    concluirRequerimentoAdm(context, reqId);
                    dialog.dismiss();
                    abrirPainelRequerimentosAdmin(context);
                });

                actionRow.addView(btnResp);
                actionRow.addView(btnConcluir);

                cardR.addView(txtHeader);
                cardR.addView(txtTipo);
                cardR.addView(txtRelato);
                cardR.addView(txtResp);
                cardR.addView(actionRow);

                containerLista.addView(cardR);
            }

            if (exibidos == 0) {
                TextView txtVazio = new TextView(context);
                txtVazio.setText("Nenhum requerimento encontrado para o filtro '" + filtroReqAtual.toUpperCase() + "'.");
                txtVazio.setTextColor(context.getColor(R.color.dark_gray));
                txtVazio.setTextSize(12);
                txtVazio.setPadding(8, 8, 8, 8);
                containerLista.addView(txtVazio);
            }
        };

        View.OnClickListener filtroClickListener = v -> {
            int id = v.getId();
            resetarVisualFiltrosReq(context, fTodos, fPendentes, fRespondidos, fDeclaracao, fTecnico);

            if (id == R.id.filterReqPendentes) {
                filtroReqAtual = "pendentes";
                if (fPendentes != null) { fPendentes.setBackgroundResource(R.drawable.bg_pill_purple); fPendentes.setTextColor(Color.WHITE); }
            } else if (id == R.id.filterReqRespondidos) {
                filtroReqAtual = "respondidos";
                if (fRespondidos != null) { fRespondidos.setBackgroundResource(R.drawable.bg_pill_purple); fRespondidos.setTextColor(Color.WHITE); }
            } else if (id == R.id.filterReqDeclaracao) {
                filtroReqAtual = "declaracao";
                if (fDeclaracao != null) { fDeclaracao.setBackgroundResource(R.drawable.bg_pill_purple); fDeclaracao.setTextColor(Color.WHITE); }
            } else {
                filtroReqAtual = "todos";
                if (fTodos != null) { fTodos.setBackgroundResource(R.drawable.bg_pill_purple); fTodos.setTextColor(Color.WHITE); }
            }

            renderizarLista.run();
        };

        if (fTodos != null) fTodos.setOnClickListener(filtroClickListener);
        if (fPendentes != null) fPendentes.setOnClickListener(filtroClickListener);
        if (fRespondidos != null) fRespondidos.setOnClickListener(filtroClickListener);
        if (fDeclaracao != null) fDeclaracao.setOnClickListener(filtroClickListener);
        if (fTecnico != null) fTecnico.setOnClickListener(filtroClickListener);

        renderizarLista.run();
        dialog.show();
    }

    private static void resetarVisualFiltrosReq(Context context, TextView... textViews) {
        int darkIndigo = context.getColor(R.color.dark_indigo);
        for (TextView tv : textViews) {
            if (tv != null) {
                tv.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
                tv.setTextColor(darkIndigo);
            }
        }
    }

    private static void exibirDialogRespostaAdmin(Context context, String reqId, String nomeAluno, String relato, Runnable onSavedCallback) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(context);
        title.setText("💬 Resposta Oficial do ADM para " + nomeAluno);
        title.setTextColor(context.getColor(R.color.dark_indigo));
        title.setTextSize(15);
        title.setTypeface(null, Typeface.BOLD);

        TextView txtRel = new TextView(context);
        txtRel.setText("Solicitação do Aluno: " + relato);
        txtRel.setTextColor(context.getColor(R.color.dark_gray));
        txtRelatoSize(txtRel);

        EditText inputResposta = new EditText(context);
        inputResposta.setHint("Digite a resposta oficial do ADM aqui...");
        inputResposta.setMinLines(3);

        Button btnEnviarResp = new Button(context);
        btnEnviarResp.setText("Enviar Resposta");
        btnEnviarResp.setBackgroundTintList(android.content.res.ColorStateList.valueOf(context.getColor(R.color.mint)));
        btnEnviarResp.setTextColor(context.getColor(R.color.white));
        btnEnviarResp.setTypeface(null, Typeface.BOLD);

        layout.addView(title);
        layout.addView(txtRel);
        layout.addView(inputResposta);
        layout.addView(btnEnviarResp);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(layout)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnEnviarResp.setOnClickListener(v -> {
            String respText = inputResposta.getText().toString().trim();
            if (respText.isEmpty()) {
                NotificationHelper.exibirToastCustomizado(context, "Digite sua resposta!");
                return;
            }

            responderRequerimentoAdm(context, reqId, respText);
            dialog.dismiss();
            if (onSavedCallback != null) onSavedCallback.run();
        });

        dialog.show();
    }

    private static void txtRelatoSize(TextView tv) {
        if (tv != null) tv.setTextSize(12);
    }
}