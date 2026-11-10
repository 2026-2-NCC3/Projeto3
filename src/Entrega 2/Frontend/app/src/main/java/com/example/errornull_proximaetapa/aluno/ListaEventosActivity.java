package com.example.errornull_proximaetapa.aluno;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.errornull_proximaetapa.auth.NotificationHelper;
import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.auth.SessionManager;
import com.example.errornull_proximaetapa.UserSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListaEventosActivity extends AppCompatActivity {

    private static final String KEY_CUSTOM_EVENTOS = "lista_eventos_custom_string";

    private Button btnVoltar, btnCriarNovoEvento;
    private LinearLayout containerEventos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserSession.aplicarPreferenciasTema(this);
        setContentView(R.layout.activity_lista_eventos);

        btnVoltar = findViewById(R.id.btnVoltarListaEventosActivity);
        btnCriarNovoEvento = findViewById(R.id.btnCriarNovoEventoActivity);
        containerEventos = findViewById(R.id.containerTodosEventosBlocosActivity);

        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        }

        String role = SessionManager.getRole(this);
        boolean isAdmin = "admin".equalsIgnoreCase(role) || "professor".equalsIgnoreCase(role);

        if (btnCriarNovoEvento != null) {
            btnCriarNovoEvento.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            btnCriarNovoEvento.setOnClickListener(v -> exibirDialogCriarEvento());
        }

        carregarERenderizarEventos();
    }

    private void carregarERenderizarEventos() {
        if (containerEventos == null) return;
        containerEventos.removeAllViews();

        SharedPreferences sp = UserSession.getScopedPreferences(this);
        List<String> listaEventos = carregarEventosSp(sp);

        LayoutInflater inflater = LayoutInflater.from(this);

        int[] imagensExemplo = new int[]{
                R.drawable.proximaetapa1,
                R.drawable.proximaetapa2,
                R.drawable.proximaetapa3
        };

        for (int i = 0; i < listaEventos.size(); i++) {
            String eventoStr = listaEventos.get(i);
            String[] partes = eventoStr.split(";;;");
            String titulo = partes.length > 0 ? partes[0] : "Evento";
            String data = partes.length > 1 ? partes[1] : "Data A Definir";
            String desc = partes.length > 2 ? partes[2] : "";

            View blocoView = inflater.inflate(R.layout.item_eventos, containerEventos, false);
            ImageView imgCapa = blocoView.findViewById(R.id.imgEncontro);
            TextView txtData = blocoView.findViewById(R.id.txtData);
            TextView txtTitulo = blocoView.findViewById(R.id.txtTituloEvento);
            TextView txtDesc = blocoView.findViewById(R.id.txtDescricaoEncontro);

            if (imgCapa != null) {
                imgCapa.setImageResource(imagensExemplo[i % imagensExemplo.length]);
            }
            if (txtData != null) txtData.setText(data);
            if (txtTitulo != null) txtTitulo.setText(titulo);
            if (txtDesc != null) txtDesc.setText(desc.isEmpty() ? "Evento organizado pela plataforma Próxima Etapa." : desc);

            containerEventos.addView(blocoView);
        }
    }

    private List<String> carregarEventosSp(SharedPreferences sp) {
        String savedString = sp.getString(KEY_CUSTOM_EVENTOS, "");
        List<String> lista = new ArrayList<>();

        if (!savedString.isEmpty()) {
            String[] split = savedString.split("###");
            lista.addAll(Arrays.asList(split));
        } else {
            lista.add("Feira de Profissões Próxima Etapa 2026;;;15/04/2026;;;Conecte-se com universidades públicas e privadas e conheça opções de carreira.");
            lista.add("Workshop de Carreira & Mentoria USP;;;28/05/2026;;;Palestra prática sobre os primeiros passos no mercado de trabalho e universidade.");
            lista.add("Semana de Tecnologia & Inovação UNICAMP;;;10/06/2026;;;Demonstração de projetos de computação, robótica e inteligência artificial.");
            salvarEventosSp(sp, lista);
        }
        return lista;
    }

    private void salvarEventosSp(SharedPreferences sp, List<String> lista) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            sb.append(lista.get(i));
            if (i < lista.size() - 1) sb.append("###");
        }
        sp.edit().putString(KEY_CUSTOM_EVENTOS, sb.toString()).apply();
    }

    private void exibirDialogCriarEvento() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_criar_evento, null);

        EditText inputTitulo = dialogView.findViewById(R.id.inputTituloEvento);
        EditText inputData = dialogView.findViewById(R.id.inputDataEvento);
        EditText inputDesc = dialogView.findViewById(R.id.inputDescricaoEvento);

        Button btnSalvar = dialogView.findViewById(R.id.btnSalvarNovoEvento);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnSalvar != null) {
            btnSalvar.setOnClickListener(v -> {
                String titulo = inputTitulo != null ? inputTitulo.getText().toString().trim() : "";
                String data = inputData != null ? inputData.getText().toString().trim() : "";
                String desc = inputDesc != null ? inputDesc.getText().toString().trim() : "";

                if (titulo.isEmpty() || data.isEmpty()) {
                    NotificationHelper.exibirToastCustomizado(this, "Preencha o título e a data do evento!");
                    return;
                }

                SharedPreferences sp = UserSession.getScopedPreferences(this);
                List<String> lista = carregarEventosSp(sp);
                lista.add(0, titulo + ";;;" + data + ";;;" + desc);
                salvarEventosSp(sp, lista);

                NotificationHelper.adicionarNotificacao(this, "Novo Evento Cadastrado", "O evento '" + titulo + "' foi agendado para " + data + "!");
                NotificationHelper.exibirToastCustomizado(this, "Eventos atualizados com sucesso!");
                carregarERenderizarEventos();
                dialog.dismiss();
            });
        }

        dialog.show();
    }
}