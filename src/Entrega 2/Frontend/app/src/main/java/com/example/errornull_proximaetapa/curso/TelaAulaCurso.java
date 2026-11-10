package com.example.errornull_proximaetapa.curso;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.errornull_proximaetapa.AttendanceManager;
import com.example.errornull_proximaetapa.ChatManager;
import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.UserSession;
import com.example.errornull_proximaetapa.auth.NotificationHelper;
import com.example.errornull_proximaetapa.auth.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TelaAulaCurso extends AppCompatActivity {

    private TextView txtNomeCursoAula, txtFaculdadeAula, txtPorcentagemAula;
    private ProgressBar pbProgressoAula;
    private Button btnIniciarContinuar, btnVoltarAula;
    private View btnAbrirForumDuvidas;

    // Componentes de Presença (Etapa 1 & 2)
    private TextView txtStatusSessaoAlunoPill, txtStatusSessaoAlunoText;
    private View containerBotoesQrCodeAluno;
    private Button btnGerarQrCodeEntradaAluno, btnGerarQrCodeSaidaAluno;

    // Container de Módulos Dinâmicos
    private LinearLayout containerModulosAulasDinamico;

    private int progressoAtual = 0;
    private String cursoId = "web";
    private String nomeCurso = "Desenvolvimento Web";
    private String faculdade = "USP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_aula_curso);

        txtNomeCursoAula = findViewById(R.id.txtNomeCursoAula);
        txtFaculdadeAula = findViewById(R.id.txtFaculdadeAula);
        txtPorcentagemAula = findViewById(R.id.txtPorcentagemAula);
        pbProgressoAula = findViewById(R.id.pbProgressoAula);

        btnIniciarContinuar = findViewById(R.id.btnIniciarContinuar);
        btnVoltarAula = findViewById(R.id.btnVoltarAula);

        btnAbrirForumDuvidas = findViewById(R.id.btnAbrirForumDuvidas);

        txtStatusSessaoAlunoPill = findViewById(R.id.txtStatusSessaoAlunoPill);
        txtStatusSessaoAlunoText = findViewById(R.id.txtStatusSessaoAlunoText);
        containerBotoesQrCodeAluno = findViewById(R.id.containerBotoesQrCodeAluno);
        btnGerarQrCodeEntradaAluno = findViewById(R.id.btnGerarQrCodeEntradaAluno);
        btnGerarQrCodeSaidaAluno = findViewById(R.id.btnGerarQrCodeSaidaAluno);

        containerModulosAulasDinamico = findViewById(R.id.containerModulosAulasDinamico);

        String idExtra = getIntent().getStringExtra("cursoId");
        if (idExtra != null && !idExtra.isEmpty()) {
            cursoId = idExtra;
        }

        carregarDados();
        atualizarStatusSessaoPresenca();
        renderizarModulosAulasDinamicos();

        if (btnGerarQrCodeEntradaAluno != null) {
            btnGerarQrCodeEntradaAluno.setOnClickListener(v -> {
                if (!AttendanceManager.isEntradaLiberada(this, cursoId)) {
                    NotificationHelper.exibirToastCustomizado(this, "Aguarde o professor liberar o registro de ENTRADA!");
                    return;
                }
                QrCodeHelper.exibirDialogQrCodeAluno(this, cursoId, nomeCurso, "ENTRADA");
            });
        }

        if (btnGerarQrCodeSaidaAluno != null) {
            btnGerarQrCodeSaidaAluno.setOnClickListener(v -> {
                if (!AttendanceManager.isSaidaLiberada(this, cursoId)) {
                    NotificationHelper.exibirToastCustomizado(this, "Aguarde o professor liberar o registro de SAÍDA!");
                    return;
                }
                QrCodeHelper.exibirDialogQrCodeAluno(this, cursoId, nomeCurso, "SAÍDA");
            });
        }

        if (btnAbrirForumDuvidas != null) {
            btnAbrirForumDuvidas.setOnClickListener(v -> ChatManager.abrirJanelaChat(
                    TelaAulaCurso.this,
                    "forum_curso_" + cursoId,
                    "💬 Fórum de Dúvidas: " + nomeCurso,
                    "Perguntas, respostas e discussões em tempo real"
            ));
        }

        if (btnVoltarAula != null) {
            btnVoltarAula.setOnClickListener(v -> finish());
        }

        if (btnIniciarContinuar != null) {
            btnIniciarContinuar.setOnClickListener(v -> {
                if (progressoAtual == 0) {
                    atualizarProgresso(20);
                    NotificationHelper.exibirToastCustomizado(this, "Você começou o curso!");
                } else {
                    NotificationHelper.exibirToastCustomizado(this, "Continuando o curso...");
                }
            });
        }
    }

    private void atualizarStatusSessaoPresenca() {
        boolean ativa = AttendanceManager.isSessaoAtiva(this, cursoId);
        boolean entrada = AttendanceManager.isEntradaLiberada(this, cursoId);
        boolean saida = AttendanceManager.isSaidaLiberada(this, cursoId);

        if (txtStatusSessaoAlunoPill != null) {
            txtStatusSessaoAlunoPill.setText(ativa ? "SESSÃO EM ANDAMENTO" : "SESSÃO FECHADA");
            txtStatusSessaoAlunoPill.setBackgroundResource(ativa ? R.drawable.bg_pill_green : R.drawable.bg_pill_purple);
        }

        if (txtStatusSessaoAlunoText != null) {
            txtStatusSessaoAlunoText.setText(AttendanceManager.getStatusSessaoTexto(this, cursoId));
        }

        if (containerBotoesQrCodeAluno != null) {
            containerBotoesQrCodeAluno.setVisibility(ativa ? View.VISIBLE : View.GONE);
        }

        if (btnGerarQrCodeEntradaAluno != null) {
            btnGerarQrCodeEntradaAluno.setEnabled(entrada);
            btnGerarQrCodeEntradaAluno.setAlpha(entrada ? 1.0f : 0.5f);
        }

        if (btnGerarQrCodeSaidaAluno != null) {
            btnGerarQrCodeSaidaAluno.setEnabled(saida);
            btnGerarQrCodeSaidaAluno.setAlpha(saida ? 1.0f : 0.5f);
        }
    }

    private void renderizarModulosAulasDinamicos() {
        if (containerModulosAulasDinamico == null) return;
        containerModulosAulasDinamico.removeAllViews();

        String roleUser = SessionManager.getRole(this);
        boolean isProfOuAdmin = "professor".equalsIgnoreCase(roleUser) || "admin".equalsIgnoreCase(roleUser);

        SharedPreferences spGeral = getSharedPreferences("ListaGeralCursosPlataforma", MODE_PRIVATE);
        SharedPreferences spUser = UserSession.getScopedPreferences(this);

        String rawModulos = spGeral.getString("modulos_" + cursoId, spUser.getString("modulos_" + cursoId, ""));

        List<String> listaModulos = new ArrayList<>();
        if (!rawModulos.isEmpty()) {
            listaModulos.addAll(Arrays.asList(rawModulos.split("###")));
        } else {
            listaModulos.add("Módulo 1: Fundamentos & Teoria Básica;;;35;;;Aulas introdutórias e conceitos chave do curso.");
            listaModulos.add("Módulo 2: Prática e Projetos de Campo;;;35;;;Exercícios práticos com mentoria.");
            listaModulos.add("Módulo 3: Projeto Final & Certificação;;;30;;;Apresentação do projeto final e certificação.");
        }

        // Botão para o Professor/ADM Adicionar Novo Módulo
        if (isProfOuAdmin) {
            Button btnAddNovoModulo = new Button(this);
            btnAddNovoModulo.setText("➕ Adicionar Novo Módulo nesta Disciplina");
            btnAddNovoModulo.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.yellow)));
            btnAddNovoModulo.setTextColor(getColor(R.color.dark_indigo));
            btnAddNovoModulo.setTypeface(null, android.graphics.Typeface.BOLD);
            btnAddNovoModulo.setTextSize(12);

            LinearLayout.LayoutParams lpAdd = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (int) (44 * getResources().getDisplayMetrics().density));
            lpAdd.bottomMargin = 14;
            btnAddNovoModulo.setLayoutParams(lpAdd);

            btnAddNovoModulo.setOnClickListener(v -> exibirDialogCriarNovoModulo(listaModulos));
            containerModulosAulasDinamico.addView(btnAddNovoModulo);
        }

        final int totalModulosCount = listaModulos.size();

        for (int i = 0; i < totalModulosCount; i++) {
            final int modIndex = i + 1;
            final int listIndex = i;

            String modStr = listaModulos.get(i);
            String[] p = modStr.split(";;;");
            String nomeM = p.length > 0 ? p[0] : ("Módulo " + modIndex);
            String pontosM = p.length > 1 ? p[1] : "35";
            String detalheM = p.length > 2 ? p[2] : "Conteúdo programático da disciplina.";

            final int pesoIncremento = Math.max(1, 100 / totalModulosCount);

            LinearLayout cardMod = new LinearLayout(this);
            cardMod.setOrientation(LinearLayout.VERTICAL);
            cardMod.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
            cardMod.setPadding(16, 14, 16, 14);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 14);
            cardMod.setLayoutParams(lp);

            // Header do Módulo com Titulo e Botão Excluir (para Professor)
            LinearLayout headerMod = new LinearLayout(this);
            headerMod.setOrientation(LinearLayout.HORIZONTAL);
            headerMod.setGravity(android.view.Gravity.CENTER_VERTICAL);

            TextView txtTitleM = new TextView(this);
            txtTitleM.setText(nomeM + " (" + pontosM + " pts)");
            txtTitleM.setTextColor(getColor(R.color.purple));
            txtTitleM.setTextSize(14);
            txtTitleM.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams lpTitle = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            txtTitleM.setLayoutParams(lpTitle);
            headerMod.addView(txtTitleM);

            if (isProfOuAdmin) {
                Button btnExcluirMod = new Button(this);
                btnExcluirMod.setText("🗑️ Excluir");
                btnExcluirMod.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.purple)));
                btnExcluirMod.setTextColor(getColor(R.color.white));
                btnExcluirMod.setTextSize(10);
                LinearLayout.LayoutParams lpDel = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, (int) (32 * getResources().getDisplayMetrics().density));
                btnExcluirMod.setLayoutParams(lpDel);

                btnExcluirMod.setOnClickListener(v -> {
                    listaModulos.remove(listIndex);
                    salvarModulosDisciplina(listaModulos);
                    renderizarModulosAulasDinamicos();
                    NotificationHelper.exibirToastCustomizado(this, "Módulo '" + nomeM + "' excluído com sucesso!");
                });

                headerMod.addView(btnExcluirMod);
            }

            cardMod.addView(headerMod);

            TextView txtDetailM = new TextView(this);
            txtDetailM.setText(detalheM);
            txtDetailM.setTextColor(getColor(R.color.dark_gray));
            txtDetailM.setTextSize(12);
            txtDetailM.setPadding(0, 4, 0, 6);
            cardMod.addView(txtDetailM);

            // INTEGRAÇÃO DO QR CODE DE PRESENÇA DENTRO DO MÓDULO
            boolean sessaoAtiva = AttendanceManager.isSessaoAtiva(this, cursoId);
            boolean modEntradaLib = AttendanceManager.isModuloEntradaLiberada(this, cursoId, modIndex);
            boolean modSaidaLib = AttendanceManager.isModuloSaidaLiberada(this, cursoId, modIndex);

            LinearLayout containerQrModulo = new LinearLayout(this);
            containerQrModulo.setOrientation(LinearLayout.VERTICAL);
            containerQrModulo.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
            containerQrModulo.setPadding(12, 10, 12, 10);
            LinearLayout.LayoutParams lpQr = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lpQr.topMargin = 6;
            containerQrModulo.setLayoutParams(lpQr);

            TextView txtStatusQrMod = new TextView(this);
            txtStatusQrMod.setText(sessaoAtiva ? (modEntradaLib ? "🟢 ENTRADA LIBERADA NESTE MÓDULO" : (modSaidaLib ? "🟢 SAÍDA LIBERADA NESTE MÓDULO" : "🟡 SESSÃO ABERTA NO MÓDULO")) : "🔒 CHAMADA INDISPONÍVEL NESTE MÓDULO");
            txtStatusQrMod.setTextColor(getColor(sessaoAtiva && (modEntradaLib || modSaidaLib) ? R.color.mint : R.color.dark_gray));
            txtStatusQrMod.setTextSize(11);
            txtStatusQrMod.setTypeface(null, android.graphics.Typeface.BOLD);
            containerQrModulo.addView(txtStatusQrMod);

            // Botões de Geração de QR Code do Aluno no Módulo
            LinearLayout rowBtnsQrAluno = new LinearLayout(this);
            rowBtnsQrAluno.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lpRow.topMargin = 6;
            rowBtnsQrAluno.setLayoutParams(lpRow);

            Button btnQrEntradaMod = new Button(this);
            btnQrEntradaMod.setText("📱 QR Entrada (" + nomeM + ")");
            btnQrEntradaMod.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.mint)));
            btnQrEntradaMod.setTextColor(getColor(R.color.white));
            btnQrEntradaMod.setTextSize(10);
            btnQrEntradaMod.setEnabled(modEntradaLib);
            btnQrEntradaMod.setAlpha(modEntradaLib ? 1.0f : 0.5f);
            LinearLayout.LayoutParams lpB1 = new LinearLayout.LayoutParams(0, (int) (36 * getResources().getDisplayMetrics().density), 1.0f);
            lpB1.rightMargin = 4;
            btnQrEntradaMod.setLayoutParams(lpB1);

            btnQrEntradaMod.setOnClickListener(v -> QrCodeHelper.exibirDialogQrCodeAluno(this, cursoId, nomeM, "ENTRADA"));

            Button btnQrSaidaMod = new Button(this);
            btnQrSaidaMod.setText("📱 QR Saída (" + nomeM + ")");
            btnQrSaidaMod.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.purple)));
            btnQrSaidaMod.setTextColor(getColor(R.color.white));
            btnQrSaidaMod.setTextSize(10);
            btnQrSaidaMod.setEnabled(modSaidaLib);
            btnQrSaidaMod.setAlpha(modSaidaLib ? 1.0f : 0.5f);
            LinearLayout.LayoutParams lpB2 = new LinearLayout.LayoutParams(0, (int) (36 * getResources().getDisplayMetrics().density), 1.0f);
            lpB2.leftMargin = 4;
            btnQrSaidaMod.setLayoutParams(lpB2);

            btnQrSaidaMod.setOnClickListener(v -> QrCodeHelper.exibirDialogQrCodeAluno(this, cursoId, nomeM, "SAÍDA"));

            rowBtnsQrAluno.addView(btnQrEntradaMod);
            rowBtnsQrAluno.addView(btnQrSaidaMod);
            containerQrModulo.addView(rowBtnsQrAluno);

            // Controles de Liberação do Professor para este Módulo
            if (isProfOuAdmin && sessaoAtiva) {
                LinearLayout rowControlesProfMod = new LinearLayout(this);
                rowControlesProfMod.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams lpProfRow = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lpProfRow.topMargin = 6;
                rowControlesProfMod.setLayoutParams(lpProfRow);

                Button btnToggleEntradaM = new Button(this);
                btnToggleEntradaM.setText(modEntradaLib ? "🔒 Fechar Entrada" : "🔓 Liberar Entrada");
                btnToggleEntradaM.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(modEntradaLib ? R.color.purple : R.color.mint)));
                btnToggleEntradaM.setTextColor(getColor(R.color.white));
                btnToggleEntradaM.setTextSize(9);
                LinearLayout.LayoutParams lpP1 = new LinearLayout.LayoutParams(0, (int) (34 * getResources().getDisplayMetrics().density), 1.0f);
                lpP1.rightMargin = 4;
                btnToggleEntradaM.setLayoutParams(lpP1);

                btnToggleEntradaM.setOnClickListener(v -> {
                    AttendanceManager.toggleModuloEntrada(this, cursoId, modIndex, nomeM);
                    renderizarModulosAulasDinamicos();
                });

                Button btnToggleSaidaM = new Button(this);
                btnToggleSaidaM.setText(modSaidaLib ? "🔒 Fechar Saída" : "🔓 Liberar Saída");
                btnToggleSaidaM.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(modSaidaLib ? R.color.purple : R.color.mint)));
                btnToggleSaidaM.setTextColor(getColor(R.color.white));
                btnToggleSaidaM.setTextSize(9);
                LinearLayout.LayoutParams lpP2 = new LinearLayout.LayoutParams(0, (int) (34 * getResources().getDisplayMetrics().density), 1.0f);
                lpP2.leftMargin = 4;
                btnToggleSaidaM.setLayoutParams(lpP2);

                btnToggleSaidaM.setOnClickListener(v -> {
                    AttendanceManager.toggleModuloSaida(this, cursoId, modIndex, nomeM);
                    renderizarModulosAulasDinamicos();
                });

                rowControlesProfMod.addView(btnToggleEntradaM);
                rowControlesProfMod.addView(btnToggleSaidaM);
                containerQrModulo.addView(rowControlesProfMod);
            }

            cardMod.addView(containerQrModulo);

            // Botão Concluir Módulo
            Button btnConcluirMod = new Button(this);
            btnConcluirMod.setText("Concluir " + nomeM + " (+" + pesoIncremento + "%)");
            btnConcluirMod.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.dark_indigo)));
            btnConcluirMod.setTextColor(getColor(R.color.white));
            btnConcluirMod.setTypeface(null, android.graphics.Typeface.BOLD);
            btnConcluirMod.setTextSize(11);

            LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, (int) (40 * getResources().getDisplayMetrics().density));
            lpBtn.topMargin = 10;
            btnConcluirMod.setLayoutParams(lpBtn);

            btnConcluirMod.setOnClickListener(v -> {
                int proximoProgresso = Math.min(100, progressoAtual + pesoIncremento);
                if (modIndex == totalModulosCount) proximoProgresso = 100;
                atualizarProgresso(proximoProgresso);
            });

            cardMod.addView(btnConcluirMod);
            containerModulosAulasDinamico.addView(cardMod);
        }
    }

    private void exibirDialogCriarNovoModulo(List<String> listaModulos) {
        LinearLayout cardView = new LinearLayout(this);
        cardView.setOrientation(LinearLayout.VERTICAL);
        cardView.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        cardView.setPadding(40, 40, 40, 40);

        TextView title = new TextView(this);
        title.setText("➕ Criar Novo Módulo na Disciplina");
        title.setTextColor(getColor(R.color.dark_indigo));
        title.setTextSize(16);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        EditText inputNome = new EditText(this);
        inputNome.setHint("Nome do Módulo (ex: Módulo 4: Prática)");

        EditText inputPontos = new EditText(this);
        inputPontos.setHint("Pontos do Módulo (ex: 30)");

        EditText inputDesc = new EditText(this);
        inputDesc.setHint("Descrição / Atividades do Módulo");

        Button btnSalvar = new Button(this);
        btnSalvar.setText("Salvar e Adicionar Módulo");
        btnSalvar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.yellow)));
        btnSalvar.setTextColor(getColor(R.color.dark_indigo));
        btnSalvar.setTypeface(null, android.graphics.Typeface.BOLD);

        cardView.addView(title);
        cardView.addView(inputNome);
        cardView.addView(inputPontos);
        cardView.addView(inputDesc);
        cardView.addView(btnSalvar);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(cardView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnSalvar.setOnClickListener(v -> {
            String nome = inputNome.getText().toString().trim();
            String pontos = inputPontos.getText().toString().trim();
            String desc = inputDesc.getText().toString().trim();

            if (nome.isEmpty()) {
                NotificationHelper.exibirToastCustomizado(this, "Digite o nome do módulo!");
                return;
            }

            if (pontos.isEmpty()) pontos = "30";
            if (desc.isEmpty()) desc = "Conteúdo prático e avaliações da disciplina.";

            listaModulos.add(nome + ";;;" + pontos + ";;;" + desc);
            salvarModulosDisciplina(listaModulos);
            renderizarModulosAulasDinamicos();
            NotificationHelper.exibirToastCustomizado(this, "📢 Módulo '" + nome + "' adicionado com sucesso!");
            dialog.dismiss();
        });

        dialog.show();
    }

    private void salvarModulosDisciplina(List<String> listaModulos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listaModulos.size(); i++) {
            sb.append(listaModulos.get(i));
            if (i < listaModulos.size() - 1) sb.append("###");
        }

        String rawStr = sb.toString();

        SharedPreferences spGeral = getSharedPreferences("ListaGeralCursosPlataforma", MODE_PRIVATE);
        spGeral.edit().putString("modulos_" + cursoId, rawStr).apply();

        SharedPreferences spUser = UserSession.getScopedPreferences(this);
        spUser.edit().putString("modulos_" + cursoId, rawStr).apply();
    }

    private void carregarDados() {
        SharedPreferences sp = UserSession.getScopedPreferences(this);

        nomeCurso = sp.getString("nomeCurso_" + cursoId, sp.getString("nomeCursoInscrito", "Desenvolvimento Web"));
        faculdade = sp.getString("faculdadeCurso_" + cursoId, sp.getString("faculdadeCursoInscrito", "USP"));
        progressoAtual = sp.getInt("progressoCurso_" + cursoId, sp.getInt("progressoCursoInscrito", 0));

        if (txtNomeCursoAula != null) txtNomeCursoAula.setText(nomeCurso);
        if (txtFaculdadeAula != null) txtFaculdadeAula.setText(faculdade);

        renderizarProgresso();
    }

    private void atualizarProgresso(int novoProgresso) {
        progressoAtual = novoProgresso;

        SharedPreferences sp = UserSession.getScopedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt("progressoCurso_" + cursoId, progressoAtual);
        editor.putInt("progressoCursoInscrito", progressoAtual);

        if (progressoAtual >= 100) {
            Set<String> concluidosSet = new HashSet<>(sp.getStringSet("lista_cursos_concluidos_100", new HashSet<>()));
            concluidosSet.add(cursoId);
            editor.putStringSet("lista_cursos_concluidos_100", concluidosSet);
            editor.putBoolean("concluido_100_" + cursoId, true);

            NotificationHelper.adicionarNotificacao(
                    this,
                    "🎓 Curso Concluído 100%!",
                    "Parabéns! Você concluiu 100% das aulas do curso " + nomeCurso + ". Seu certificado já está disponível no perfil!"
            );
        } else {
            NotificationHelper.adicionarNotificacao(
                    this,
                    "Progresso de Aulas",
                    "Seu progresso no curso " + nomeCurso + " foi atualizado para " + progressoAtual + "%."
            );
        }

        editor.apply();

        renderizarProgresso();
        NotificationHelper.exibirToastCustomizado(this, "Progresso atualizado para " + progressoAtual + "%!");
    }

    private void renderizarProgresso() {
        if (pbProgressoAula != null) pbProgressoAula.setProgress(progressoAtual);
        if (txtPorcentagemAula != null) txtPorcentagemAula.setText(progressoAtual + "%");

        if (btnIniciarContinuar != null) {
            if (progressoAtual == 0) {
                btnIniciarContinuar.setText("Começar Curso");
            } else {
                btnIniciarContinuar.setText("Continuar Curso");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarStatusSessaoPresenca();
        renderizarModulosAulasDinamicos();
    }
}