package com.example.errornull_proximaetapa;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

public class CardImageSaver {

    public static void exibirCardEstudanteDialog(Context context) {
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_card_aluno, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        View btnFechar = dialogView.findViewById(R.id.btnFecharCardDialog);
        if (btnFechar != null) btnFechar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public static void exibirCertificadoDialog(Context context, String nomeCurso, String faculdade) {
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_certificado, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        View btnFechar = dialogView.findViewById(R.id.btnFecharCertificadoDialog);
        if (btnFechar != null) btnFechar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
