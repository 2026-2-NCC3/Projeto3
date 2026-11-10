package com.example.errornull_proximaetapa;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class ChatManager {

    public static void abrirJanelaChat(Context context, String channelId, String title, String subtitle) {
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_chat, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView txtTitulo = dialogView.findViewById(R.id.txtTituloChat);
        TextView txtSubtitulo = dialogView.findViewById(R.id.txtSubtituloChat);
        View btnFechar = dialogView.findViewById(R.id.btnFecharChat);
        LinearLayout containerMsgs = dialogView.findViewById(R.id.containerMensagens);
        EditText inputMsg = dialogView.findViewById(R.id.inputTextoMensagem);
        Button btnEnviar = dialogView.findViewById(R.id.btnEnviarMensagem);

        if (txtTitulo != null && title != null) txtTitulo.setText(title);
        if (txtSubtitulo != null && subtitle != null) txtSubtitulo.setText(subtitle);

        if (btnFechar != null) btnFechar.setOnClickListener(v -> dialog.dismiss());

        SharedPreferences sp = context.getSharedPreferences("ChatDataBase_" + channelId, Context.MODE_PRIVATE);

        Runnable carregarMsgs = () -> {
            if (containerMsgs == null) return;
            containerMsgs.removeAllViews();
            String saved = sp.getString("mensagens", "");
            if (!saved.isEmpty()) {
                String[] msgs = saved.split("###");
                for (String m : msgs) {
                    TextView tv = new TextView(context);
                    tv.setText(m);
                    tv.setTextColor(Color.BLACK);
                    tv.setTextSize(13);
                    tv.setPadding(8, 8, 8, 8);
                    containerMsgs.addView(tv);
                }
            } else {
                TextView tv = new TextView(context);
                tv.setText("Inicie a conversa digitando abaixo...");
                tv.setTextColor(Color.GRAY);
                tv.setTextSize(12);
                tv.setPadding(8, 8, 8, 8);
                containerMsgs.addView(tv);
            }
        };

        carregarMsgs.run();

        if (btnEnviar != null) {
            btnEnviar.setOnClickListener(v -> {
                String msg = inputMsg != null ? inputMsg.getText().toString().trim() : "";
                if (!msg.isEmpty()) {
                    String user = UserSession.getUsuarioAtual(context);
                    String novaMsg = user + ": " + msg;
                    String ex = sp.getString("mensagens", "");
                    String total = ex.isEmpty() ? novaMsg : ex + "###" + novaMsg;
                    sp.edit().putString("mensagens", total).apply();
                    if (inputMsg != null) inputMsg.setText("");
                    carregarMsgs.run();
                }
            });
        }

        dialog.show();
    }
}
