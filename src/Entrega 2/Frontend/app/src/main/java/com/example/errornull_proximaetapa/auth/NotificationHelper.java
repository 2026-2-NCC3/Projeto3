package com.example.errornull_proximaetapa.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.errornull_proximaetapa.ChatManager;
import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.curso.TelaAulaCurso;
import com.example.errornull_proximaetapa.UserSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class NotificationHelper {

    private static final String KEY_NOTIFICACOES = "lista_notificacoes_string";

    public static void exibirToastCustomizado(Context context, String mensagem) {
        if (context == null || mensagem == null || mensagem.isEmpty()) return;

        SharedPreferences sp = UserSession.getScopedPreferences(context);
        boolean sonsNotifs = sp.getBoolean("config_notificacoes_som", true);

        // Respeita a preferência do usuário do switch de Notificações
        if (!sonsNotifs) return;

        try {
            View layout = LayoutInflater.from(context).inflate(R.layout.layout_top_notification_banner, null);

            TextView txtMsg = layout.findViewById(R.id.txtBannerMensagem);
            if (txtMsg != null) {
                txtMsg.setText(mensagem);
            }

            Toast toast = new Toast(context.getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 90);
            toast.show();
        } catch (Exception e) {
            Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
        }
    }

    public static void adicionarNotificacao(Context context, String titulo, String mensagem) {
        if (context == null || titulo == null || mensagem == null) return;

        SharedPreferences sp = UserSession.getScopedPreferences(context);
        String savedString = sp.getString(KEY_NOTIFICACOES, "");

        List<String> lista = new ArrayList<>();
        if (!savedString.isEmpty()) {
            lista.addAll(Arrays.asList(savedString.split("###")));
        }

        String dataHora = DateFormat.format("dd/MM/yyyy • HH:mm", new Date()).toString();
        String itemNovaNotif = titulo + "|||" + mensagem + "|||" + dataHora + "|||nao_lida";

        lista.add(0, itemNovaNotif); // Adiciona a mais recente no topo

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            sb.append(lista.get(i));
            if (i < lista.size() - 1) sb.append("###");
        }

        sp.edit().putString(KEY_NOTIFICACOES, sb.toString()).apply();
    }

    public static List<String> getListaNotificacoes(Context context) {
        if (context == null) return new ArrayList<>();

        SharedPreferences sp = UserSession.getScopedPreferences(context);
        String savedString = sp.getString(KEY_NOTIFICACOES, "");

        if (savedString.isEmpty()) {
            // Notificações iniciais padrão com estado não lida para teste
            adicionarNotificacao(context, "🎓 Novo curso disponível!", "O curso de 'Intercâmbio e Gestão' acabou de ser publicado na plataforma. Reserve sua vaga agora!");
            adicionarNotificacao(context, "🤖 Testes de IA disponíveis!", "Você tem acesso a ferramentas de Inteligência Artificial: Teste Vocacional e Análise Comportamental DISC.");
            adicionarNotificacao(context, "Bem-vindo ao Próxima Etapa!", "Explore nossos cursos e conecte-se com as principais universidades do Brasil.");
            savedString = sp.getString(KEY_NOTIFICACOES, "");
        }

        return new ArrayList<>(Arrays.asList(savedString.split("###")));
    }

    public static int getQtdNaoLidas(Context context) {
        List<String> lista = getListaNotificacoes(context);
        int naoLidas = 0;
        for (String item : lista) {
            if (item.contains("|||nao_lida")) {
                naoLidas++;
            }
        }
        return naoLidas;
    }

    public static void marcarTodasComoLidas(Context context) {
        if (context == null) return;

        SharedPreferences sp = UserSession.getScopedPreferences(context);
        String savedString = sp.getString(KEY_NOTIFICACOES, "");

        if (!savedString.isEmpty()) {
            String atualizada = savedString.replace("|||nao_lida", "|||lida");
            sp.edit().putString(KEY_NOTIFICACOES, atualizada).apply();
        }
    }

    public static void marcarUmaComoLida(Context context, String tituloTarget, String dataHoraTarget) {
        if (context == null || tituloTarget == null) return;

        SharedPreferences sp = UserSession.getScopedPreferences(context);
        String savedString = sp.getString(KEY_NOTIFICACOES, "");

        if (savedString.isEmpty()) return;

        List<String> lista = new ArrayList<>(Arrays.asList(savedString.split("###")));
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < lista.size(); i++) {
            String item = lista.get(i);
            if (item.contains(tituloTarget) && item.contains("|||nao_lida")) {
                item = item.replace("|||nao_lida", "|||lida");
            }
            sb.append(item);
            if (i < lista.size() - 1) sb.append("###");
        }

        sp.edit().putString(KEY_NOTIFICACOES, sb.toString()).apply();
    }

    public static void exibirCentralNotificacoes(Context context, Runnable onDismissCallback) {
        if (context == null) return;

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_aba_notificacoes, null);

        TextView btnMarcarTodas = dialogView.findViewById(R.id.btnMarcarTodasLidas);
        ImageView btnFechar = dialogView.findViewById(R.id.btnFecharNotificacoes);
        LinearLayout container = dialogView.findViewById(R.id.containerListaNotificacoes);
        Button btnLimpar = dialogView.findViewById(R.id.btnLimparNotificacoes);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Runnable renderizarLista = () -> {
            if (container == null) return;
            container.removeAllViews();

            List<String> lista = getListaNotificacoes(context);

            if (lista.isEmpty()) {
                TextView txtVazio = new TextView(context);
                txtVazio.setText("Nenhuma notificação no momento.");
                txtVazio.setTextColor(context.getColor(R.color.dark_gray));
                txtVazio.setTextSize(13);
                txtVazio.setPadding(16, 16, 16, 16);
                container.addView(txtVazio);
            } else {
                LayoutInflater inflater = LayoutInflater.from(context);

                for (String itemStr : lista) {
                    String[] partes = itemStr.split("\\|\\|\\|");
                    String titulo = partes.length > 0 ? partes[0] : "Notificação";
                    String mensagem = partes.length > 1 ? partes[1] : "";
                    String dataHora = partes.length > 2 ? partes[2] : "";
                    String statusStr = partes.length > 3 ? partes[3] : "nao_lida";

                    View notifCard = inflater.inflate(R.layout.item_notificacao_extensivel, container, false);

                    TextView txtEmoji = notifCard.findViewById(R.id.txtIconeEmojiNotif);
                    TextView txtTitulo = notifCard.findViewById(R.id.txtTituloNotificacao);
                    TextView txtMensagem = notifCard.findViewById(R.id.txtMensagemNotificacao);
                    TextView txtTempo = notifCard.findViewById(R.id.txtTempoNotificacao);
                    TextView btnVer = notifCard.findViewById(R.id.btnVerAcaoNotificacao);
                    TextView txtStatus = notifCard.findViewById(R.id.txtStatusLida);

                    if (txtTitulo != null) txtTitulo.setText(titulo);
                    if (txtMensagem != null) txtMensagem.setText(mensagem);
                    if (txtTempo != null) txtTempo.setText(dataHora);

                    if (txtEmoji != null) {
                        if (titulo.contains("Curso") || titulo.contains("curso")) txtEmoji.setText("🎓");
                        else if (titulo.contains("Requerimento") || titulo.contains("ADM")) txtEmoji.setText("📬");
                        else if (titulo.contains("XP") || titulo.contains("Nível")) txtEmoji.setText("🎮");
                        else if (titulo.contains("IA") || titulo.contains("Teste")) txtEmoji.setText("🤖");
                        else txtEmoji.setText("🔔");
                    }

                    boolean isLida = "lida".equalsIgnoreCase(statusStr);
                    if (txtStatus != null) {
                        txtStatus.setText(isLida ? "✓ lida" : "🔔 nova");
                        txtStatus.setTextColor(context.getColor(isLida ? R.color.dark_gray : R.color.purple));
                    }

                    // AÇÃO REAL E NAVEGAÇÃO AO CLICAR EM "Ver →"
                    View.OnClickListener abrirAcaoListener = v -> {
                        marcarUmaComoLida(context, titulo, dataHora);
                        dialog.dismiss();

                        if (titulo.contains("Requerimento") || titulo.contains("ADM") || titulo.contains("Resposta")) {
                            SharedPreferences spUser = UserSession.getScopedPreferences(context);
                            String tipo = spUser.getString("tipoUsuario", "aluno");

                            if ("admin".equalsIgnoreCase(tipo)) {
                                RequerimentosManager.abrirPainelRequerimentosAdmin(context);
                            } else {
                                String myEmail = UserSession.getCurrentUserEmail(context);
                                String channelId = "requerimento_aluno_adm_" + myEmail.replaceAll("[^a-zA-Z0-9]", "_");
                                ChatManager.abrirJanelaChat(context, channelId, "📬 Requerimento Direto com o ADM", "Acompanhe as respostas da Direção em tempo real");
                            }
                        } else if (titulo.contains("Curso") || titulo.contains("curso") || titulo.contains("Inscrição")) {
                            Intent intent = new Intent(context, TelaAulaCurso.class);
                            intent.putExtra("cursoId", "adm");
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Aviso: " + titulo, Toast.LENGTH_SHORT).show();
                        }

                        if (onDismissCallback != null) {
                            onDismissCallback.run();
                        }
                    };

                    if (btnVer != null) btnVer.setOnClickListener(abrirAcaoListener);
                    notifCard.setOnClickListener(abrirAcaoListener);

                    container.addView(notifCard);
                }
            }
        };

        if (btnMarcarTodas != null) {
            btnMarcarTodas.setOnClickListener(v -> {
                marcarTodasComoLidas(context);
                renderizarLista.run();
                Toast.makeText(context, "Todas as notificações foram marcadas como lidas!", Toast.LENGTH_SHORT).show();
                if (onDismissCallback != null) {
                    onDismissCallback.run();
                }
            });
        }

        if (btnFechar != null) {
            btnFechar.setOnClickListener(v -> {
                dialog.dismiss();
                if (onDismissCallback != null) {
                    onDismissCallback.run();
                }
            });
        }

        if (btnLimpar != null) {
            btnLimpar.setOnClickListener(v -> {
                SharedPreferences sp = UserSession.getScopedPreferences(context);
                sp.edit().remove(KEY_NOTIFICACOES).apply();
                if (container != null) container.removeAllViews();
                Toast.makeText(context, "Histórico de notificações limpo!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                if (onDismissCallback != null) {
                    onDismissCallback.run();
                }
            });
        }

        renderizarLista.run();
        dialog.show();
    }
}