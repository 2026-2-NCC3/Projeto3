package com.example.errornull_proximaetapa.curso;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.UserSession;
import com.example.errornull_proximaetapa.auth.NotificationHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class QrCodeHelper {

    public static Bitmap gerarQrCodeBitmap(String textoPayload, int larguraPx, int alturaPx, int corFore, int corBack) {
        if (textoPayload == null || textoPayload.isEmpty()) return null;

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(textoPayload, BarcodeFormat.QR_CODE, larguraPx, alturaPx);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];

            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? corFore : corBack;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void exibirDialogQrCodeAluno(Context context, String courseId, String nomeCurso, String tipoRegra) {
        if (context == null || courseId == null) return;

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_qrcode_aluno, null);

        TextView txtTitulo = dialogView.findViewById(R.id.txtTituloQrCodeDialog);
        ImageView imgQr = dialogView.findViewById(R.id.imgQrCodeDisplay);
        TextView txtTimer = dialogView.findViewById(R.id.txtTimerValidadeQrCode);
        TextView txtNomeAluno = dialogView.findViewById(R.id.txtNomeAlunoQrCode);

        Button btnAtualizar = dialogView.findViewById(R.id.btnAtualizarQrCode);
        Button btnFechar = dialogView.findViewById(R.id.btnFecharQrCodeDialog);

        SharedPreferences spUser = UserSession.getScopedPreferences(context);
        String studentId = spUser != null ? spUser.getString("userId", "12345") : "12345";
        String nomeAluno = spUser != null ? spUser.getString("nome", "Estudante") : "Estudante";
        String myEmail = UserSession.getCurrentUserEmail(context);

        if (txtTitulo != null) {
            txtTitulo.setText("📱 QR Code (" + tipoRegra.toUpperCase() + "): " + nomeCurso);
        }

        if (txtNomeAluno != null) {
            txtNomeAluno.setText("👤 " + nomeAluno + " (" + myEmail + ")");
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final CountDownTimer[] timerHolder = new CountDownTimer[1];

        Runnable gerarENibirCode = () -> {
            long timestamp = System.currentTimeMillis();
            long validadeMs = 5 * 60 * 1000L;
            String sessionId = "session_" + courseId;

            String payloadStr = studentId + "|||" + myEmail + "|||" + nomeAluno + "|||" + courseId + "|||" + sessionId + "|||" + tipoRegra + "|||" + timestamp + "|||" + validadeMs;

            int colorDarkIndigo = context.getColor(R.color.dark_indigo);
            Bitmap qrBitmap = gerarQrCodeBitmap(payloadStr, 400, 400, colorDarkIndigo, Color.WHITE);

            if (imgQr != null && qrBitmap != null) {
                imgQr.setImageBitmap(qrBitmap);
            }

            if (timerHolder[0] != null) {
                timerHolder[0].cancel();
            }

            timerHolder[0] = new CountDownTimer(validadeMs, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long mins = millisUntilFinished / 60000;
                    long secs = (millisUntilFinished % 60000) / 1000;
                    if (txtTimer != null) {
                        txtTimer.setText(String.format("⏳ Válido por: %02d:%02d min", mins, secs));
                    }
                }

                @Override
                public void onFinish() {
                    if (txtTimer != null) {
                        txtTimer.setText("⚠️ QR Code Expirado! Clique em 'Atualizar Code'");
                    }
                }
            }.start();
        };

        gerarENibirCode.run();

        if (btnAtualizar != null) {
            btnAtualizar.setOnClickListener(v -> {
                gerarENibirCode.run();
                NotificationHelper.exibirToastCustomizado(context, "🔄 QR Code de " + tipoRegra + " atualizado!");
            });
        }

        if (btnFechar != null) {
            btnFechar.setOnClickListener(v -> {
                if (timerHolder[0] != null) timerHolder[0].cancel();
                dialog.dismiss();
            });
        }

        dialog.setOnDismissListener(d -> {
            if (timerHolder[0] != null) timerHolder[0].cancel();
        });

        dialog.show();
    }
}