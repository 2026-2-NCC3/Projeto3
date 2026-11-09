package com.example.errornull_proximaetapa.curso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.UserSession;
import com.example.errornull_proximaetapa.auth.NotificationHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CursoInfo extends AppCompatActivity {

    private TextView txtNomeCursoTitle, txtFaculdadeHeaderPill;
    private TextView txtDiasAulasDetalhe, txtCargaHorariaDetalhe, txtLocalDetalhe;
    private TextView txtVagasStatus, txtPontosParticipar;
    private ProgressBar pbVagasCurso;
    private TextView cursoInfo;
    private ImageView imgCurso;
    private Button btnInscrever;
    private Button btnVoltar;

    private String nomeCurso = "Curso Selecionado";
    private String faculdadePertencente = "USP";
    private String cursoId = "adm";
    private String cargaHoraria = "80 horas";
    private String diasSemana = "Segunda, Quarta e Sexta (14h - 16h)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.curso_info);

        txtNomeCursoTitle = findViewById(R.id.txtNomeCursoTitle);
        txtFaculdadeHeaderPill = findViewById(R.id.txtFaculdadeHeaderPill);
        txtDiasAulasDetalhe = findViewById(R.id.txtDiasAulasDetalhe);
        txtCargaHorariaDetalhe = findViewById(R.id.txtCargaHorariaDetalhe);
        txtLocalDetalhe = findViewById(R.id.txtLocalDetalhe);
        txtVagasStatus = findViewById(R.id.txtVagasStatus);
        txtPontosParticipar = findViewById(R.id.txtPontosParticipar);
        pbVagasCurso = findViewById(R.id.pbVagasCurso);
        cursoInfo = findViewById(R.id.cursoInfo);
        imgCurso = findViewById(R.id.imgCurso);
        btnInscrever = findViewById(R.id.btnInscrever);
        btnVoltar = findViewById(R.id.btnVoltar);

        String detalheRecebido = getIntent().getStringExtra("cursoInfo");
        int idImagemRecebida = getIntent().getIntExtra("cursoImagem", 0);
        String nomeRecebido = getIntent().getStringExtra("nomeCurso");
        String faculdadeRecebida = getIntent().getStringExtra("faculdadePertencente");
        String cursoIdRecebido = getIntent().getStringExtra("cursoId");
        String cargaRecebida = getIntent().getStringExtra("cargaHoraria");
        String diasRecebidos = getIntent().getStringExtra("diasSemana");

        if (nomeRecebido != null && !nomeRecebido.isEmpty()) {
            nomeCurso = nomeRecebido;
        }

        if (faculdadeRecebida != null && !faculdadeRecebida.isEmpty()) {
            faculdadePertencente = faculdadeRecebida;
        }

        if (cursoIdRecebido != null && !cursoIdRecebido.isEmpty()) {
            cursoId = cursoIdRecebido;
        }

        if (cargaRecebida != null && !cargaRecebida.isEmpty()) {
            cargaHoraria = cargaRecebida;
        }

        if (diasRecebidos != null && !diasRecebidos.isEmpty()) {
            diasSemana = diasRecebidos;
        }

        if (txtNomeCursoTitle != null) {
            txtNomeCursoTitle.setText(nomeCurso);
        }

        if (txtFaculdadeHeaderPill != null) {
            txtFaculdadeHeaderPill.setText("Oferecido por " + faculdadePertencente);
        }

        if (txtDiasAulasDetalhe != null) {
            txtDiasAulasDetalhe.setText("Dias de Aula: " + diasSemana);
        }

        if (txtCargaHorariaDetalhe != null) {
            txtCargaHorariaDetalhe.setText("Horário e Carga Horária: " + cargaHoraria);
        }

        if (txtLocalDetalhe != null) {
            txtLocalDetalhe.setText("Localização: Campus " + faculdadePertencente + " & Plataforma Online");
        }

        if (idImagemRecebida != 0 && imgCurso != null) {
            imgCurso.setImageResource(idImagemRecebida);
        }

        if (cursoInfo != null) {
            if (detalheRecebido != null && !detalheRecebido.isEmpty()) {
                cursoInfo.setText(detalheRecebido);
            } else {
                cursoInfo.setText("• Conteúdo prático ministrado por profissionais de referência\n• Networking com outros estudantes e profissionais da área\n• Experiência em uma das melhores faculdades do Brasil\n• Certificado de participação para adicionar ao seu currículo");
            }
        }

        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        }

        // CÁLCULO REAL E DINÂMICO DOS DADOS DA TURMA NO APLICATIVO
        SharedPreferences spGeneral = getSharedPreferences("ListaGeralAlunosAdmin", Context.MODE_PRIVATE);
        String savedString = spGeneral.getString("lista_alunos_cadastrados_string", "");
        List<String> listaAlunos = new ArrayList<>();

        if (!savedString.isEmpty()) {
            listaAlunos.addAll(Arrays.asList(savedString.split("###")));
        }

        int qtdInscritosReais = 0;
        for (String aStr : listaAlunos) {
            String[] p = aStr.split(";;;");
            String emailA = p.length > 1 ? p[1] : "";
            if (emailA.isEmpty()) continue;

            SharedPreferences spAluno = getSharedPreferences("PerfilUsuario_" + emailA.replaceAll("[^a-zA-Z0-9]", "_"), Context.MODE_PRIVATE);
            Set<String> cursosInscritosAluno = spAluno.getStringSet("lista_cursos_ids", new HashSet<>());
            if (cursosInscritosAluno.contains(cursoId)) {
                qtdInscritosReais++;
            }
        }

        int totalVagas = 30; // Capacidade da turma no aplicativo
        int vagasDisponiveis = Math.max(0, totalVagas - qtdInscritosReais);

        if (txtVagasStatus != null) {
            txtVagasStatus.setText(vagasDisponiveis + " disponíveis (" + qtdInscritosReais + "/" + totalVagas + ")");
        }

        if (pbVagasCurso != null) {
            pbVagasCurso.setMax(totalVagas);
            pbVagasCurso.setProgress(qtdInscritosReais);
        }

        if (txtPontosParticipar != null) {
            txtPontosParticipar.setText("🏆 Pontos ao participar: +100 Pontos");
        }

        SharedPreferences sp = UserSession.getScopedPreferences(CursoInfo.this);
        String tipoUsuario = sp.getString("tipoUsuario", "aluno");

        Set<String> meusCursosInscritos = sp.getStringSet("lista_cursos_ids", new HashSet<>());
        boolean jaInscrito = meusCursosInscritos.contains(cursoId);

        View containerProgressoAluno = findViewById(R.id.containerProgressoAlunoCurso);
        TextView txtPorcentagemProgresso = findViewById(R.id.txtPorcentagemProgressoCursoInfo);
        ProgressBar pbProgressoAulas = findViewById(R.id.pbProgressoAulasCursoInfo);

        int progressoSalvo = sp.getInt("progressoCurso_" + cursoId, 0);

        if (jaInscrito) {
            if (containerProgressoAluno != null) containerProgressoAluno.setVisibility(View.VISIBLE);
            if (txtPorcentagemProgresso != null) txtPorcentagemProgresso.setText(progressoSalvo + "% Concluído");
            if (pbProgressoAulas != null) pbProgressoAulas.setProgress(progressoSalvo);
        } else {
            if (containerProgressoAluno != null) containerProgressoAluno.setVisibility(View.GONE);
        }

        // Administrador e Professor não se inscrevem em cursos de aluno!
        if ("admin".equalsIgnoreCase(tipoUsuario) || "professor".equalsIgnoreCase(tipoUsuario)) {
            if (btnInscrever != null) {
                btnInscrever.setVisibility(View.GONE);
            }
        } else if (btnInscrever != null) {
            btnInscrever.setVisibility(View.VISIBLE);

            if (jaInscrito) {
                btnInscrever.setText("✓ VOCÊ JÁ ESTÁ INSCRITO (Acessar Aulas)");
                btnInscrever.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.dark_indigo)));
                btnInscrever.setOnClickListener(v -> {
                    Intent intent = new Intent(CursoInfo.this, TelaAulaCurso.class);
                    intent.putExtra("cursoId", cursoId);
                    startActivity(intent);
                    finish();
                });
            } else if (vagasDisponiveis <= 0) {
                btnInscrever.setText("🚫 Vagas Esgotadas");
                btnInscrever.setEnabled(false);
                btnInscrever.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.dark_gray)));
            } else {
                btnInscrever.setText("Reservar Vaga Grátis");
                btnInscrever.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.mint)));
                btnInscrever.setOnClickListener(v -> {
                    String emailAtual = UserSession.getUsuarioAtual(CursoInfo.this);
                    String cpfAluno = sp.getString("cpf", "");
                    if (emailAtual.isEmpty() || "aluno@exemplo.com".equalsIgnoreCase(emailAtual) || "usuario_convidado".equalsIgnoreCase(emailAtual)) {
                        NotificationHelper.exibirToastCustomizado(getApplicationContext(), "🔒 Faça login ou cadastre-se para se inscrever nos cursos!");
                        Intent loginIntent = new Intent(CursoInfo.this, com.example.errornull_proximaetapa.auth.LoginInfo.class);
                        startActivity(loginIntent);
                        return;
                    }


                if (cpfAluno == null || cpfAluno.trim().isEmpty()) {
                    NotificationHelper.exibirToastCustomizado(
                        getApplicationContext(),
                        "CPF não encontrado. Verifique seu cadastro!"
                    );
                    return;
                }


                InscricaoRequest request = new InscricaoRequest(cpfAluno, cursoId);

                btnInscrever.setEnabled(false);

                

                RetrofitClient.getApiService().inscreverAluno(request)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {


                    Set<String> setCursos = new HashSet<>(sp.getStringSet("lista_cursos_ids", new HashSet<>()));
                    setCursos.add(cursoId);

                    sp.edit()
                            .putBoolean("inscrito", true)
                            .putStringSet("lista_cursos_ids", setCursos)
                            .putString("nomeCurso_" + cursoId, nomeCurso)
                            .putString("faculdadeCurso_" + cursoId, faculdadePertencente)
                            .putString("cargaHoraria_" + cursoId, cargaHoraria)
                            .putString("diasSemana_" + cursoId, diasSemana)
                            .putInt("progressoCurso_" + cursoId, 0)
                            .putInt("cursosInscritosCount", setCursos.size())
                            .apply();

                    String msgConfirmacao = "Inscrição realizada no curso " + nomeCurso + " (" + faculdadePertencente + ")!";

                    NotificationHelper.adicionarNotificacao(
                            CursoInfo.this,
                            "Inscrição Confirmada!",
                            msgConfirmacao
                    );

                    NotificationHelper.exibirToastCustomizado(getApplicationContext(), "🎓 " + msgConfirmacao);

                    
                new Handler(Looper.getMainLooper()).postDelayed(
                    CursoInfo.this::finish,
                    400
                );



            } else {
                btnInscrever.setEnabled(true);
                NotificationHelper.exibirToastCustomizado(
                    getApplicationContext(),
                    "Erro ao inscrever. Código HTTP: " + response.code()
                );
            }
        }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        btnInscrever.setEnabled(true);
                        android.util.Log.e("API_INSCRICAO", "Falha na inscrição", t);
                        NotificationHelper.exibirToastCustomizado(
                            getApplicationContext(),
                            "Não foi possível conectar ao servidor."
                        );
                    }
                });

                });
            }
        }
    }
}
