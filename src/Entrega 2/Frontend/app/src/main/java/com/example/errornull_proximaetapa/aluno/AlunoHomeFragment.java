package com.example.errornull_proximaetapa.aluno;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.errornull_proximaetapa.SuspensaoManager;
import com.example.errornull_proximaetapa.CardImageSaver;
import com.example.errornull_proximaetapa.ChatManager;
import com.example.errornull_proximaetapa.auth.NotificationHelper;
import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.curso.TelaAulaCurso;
import com.example.errornull_proximaetapa.UserSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlunoHomeFragment extends Fragment {

    private TextView txtNomeAluno, txtBoasVindas, txtNomeCompleto, txtIdade, txtEscola;
    private ImageView imgFotoAluno;
    private Button btnVerCardHome;
    private View btnAvisosSuspensoesHome;


    private LinearLayout containerHomeAdmin, containerHomeAluno, containerListaAlunosAdmin;

    private View btnAdminBlacklistTools;
    private Button btnCadastrarAlunoAdmin;
    private View btnChatProfAdmHome;


    private String filtroAlunoAtual = "todos";
    private TextView filterAdmAll, filterAdmAtivos, filterAdmAvisos, filterAdm1Strike, filterAdm2Strikes, filterAdmBanidos, filterAdmDesativados;

    // Componentes dos Questionários
    private LinearLayout areaQuestionario1;
    private LinearLayout areaQuestionario2;

    // Componentes do Progresso de Cursos
    private LinearLayout containerCursosInscritosHome;
    private View layoutSemCursos;
    private View btnSininhoNotificacoes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aluno_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtNomeAluno = view.findViewById(R.id.txtNomeAluno);
        txtBoasVindas = view.findViewById(R.id.txtBoasVindas);
        txtNomeCompleto = view.findViewById(R.id.txtNomeCompleto);
        txtIdade = view.findViewById(R.id.txtIdade);
        txtEscola = view.findViewById(R.id.txtEscola);
        imgFotoAluno = view.findViewById(R.id.imgFotoAluno);
        btnVerCardHome = view.findViewById(R.id.btnVerCardHome);

        btnAvisosSuspensoesHome = view.findViewById(R.id.btnAvisosSuspensoesHome);
        if (btnAvisosSuspensoesHome != null) {
            btnAvisosSuspensoesHome.setOnClickListener(v -> SuspensaoManager.exibirSuspensaoAlunoDialog(requireContext()));
        }

        containerHomeAdmin = view.findViewById(R.id.containerHomeAdmin);
        containerHomeAluno = view.findViewById(R.id.containerHomeAluno);
        btnAdminBlacklistTools = view.findViewById(R.id.btnAdminBlacklistTools);

        btnCadastrarAlunoAdmin = view.findViewById(R.id.btnCadastrarAlunoAdmin);
        containerListaAlunosAdmin = view.findViewById(R.id.containerListaAlunosAdmin);
        btnChatProfAdmHome = view.findViewById(R.id.btnChatProfAdmHome);

        if (btnChatProfAdmHome != null) {
            btnChatProfAdmHome.setOnClickListener(v -> ChatManager.abrirJanelaChat(
                    requireContext(),
                    "chat_prof_adm",
                    "💬 Chat Docentes ↔ ADM",
                    "Comunicação direta em tempo real entre Professores e Direção"
            ));
        }

        configurarFiltrosAlunosAdmin(view);

        if (btnAdminBlacklistTools != null) {
            btnAdminBlacklistTools.setOnClickListener(v -> SuspensaoManager.exibirPainelAdminSuspensaoDialog(requireContext()));
        }

        if (btnCadastrarAlunoAdmin != null) {
            btnCadastrarAlunoAdmin.setOnClickListener(v -> exibirDialogCadastrarAluno());
        }

        btnSininhoNotificacoes = view.findViewById(R.id.btnSininhoNotificacoes);
        if (btnSininhoNotificacoes != null) {
            btnSininhoNotificacoes.setOnClickListener(v -> NotificationHelper.exibirCentralNotificacoes(requireContext(), () -> atualizarSininho(view)));
        }

        View btnVerAvisos = view.findViewById(R.id.btnVerAvisos);
        if (btnVerAvisos != null) {
            btnVerAvisos.setOnClickListener(v -> SuspensaoManager.exibirSuspensaoAlunoDialog(requireContext()));
        }

        View btnNovoRequerimento = view.findViewById(R.id.btnNovoRequerimento);
        if (btnNovoRequerimento != null) {
            btnNovoRequerimento.setOnClickListener(v -> exibirDialogCriarRequerimento());
        }

        areaQuestionario1 = view.findViewById(R.id.areaQuestionario1);
        areaQuestionario2 = view.findViewById(R.id.areaQuestionario2);

        View.OnClickListener questClickListener = v -> {
            Intent intent = new Intent(getActivity(), com.example.errornull_proximaetapa.Questionario.class);
            startActivity(intent);
        };

        if (areaQuestionario1 != null) areaQuestionario1.setOnClickListener(questClickListener);
        if (areaQuestionario2 != null) areaQuestionario2.setOnClickListener(questClickListener);

        View cardQ1 = view.findViewById(R.id.cardQuestionario1);
        View cardQ2 = view.findViewById(R.id.cardQuestionario2);
        if (cardQ1 != null) cardQ1.setOnClickListener(questClickListener);
        if (cardQ2 != null) cardQ2.setOnClickListener(questClickListener);

        containerCursosInscritosHome = view.findViewById(R.id.containerCursosInscritosHome);
        layoutSemCursos = view.findViewById(R.id.layoutSemCursos);

        carregarDadosSalvos(view);
    }

    private void configurarFiltrosAlunosAdmin(View view) {
        filterAdmAll = view.findViewById(R.id.filterAlunoTodos);
        filterAdmAtivos = view.findViewById(R.id.filterAlunoAtivos);
        filterAdmAvisos = view.findViewById(R.id.filterAlunoAvisos);
        filterAdm1Strike = view.findViewById(R.id.filterAluno1Strike);
        filterAdm2Strikes = view.findViewById(R.id.filterAluno2Strikes);
        filterAdmBanidos = view.findViewById(R.id.filterAlunoBanidos);
        filterAdmDesativados = view.findViewById(R.id.filterAlunoDesativados);

        if (filterAdmAll != null) filterAdmAll.setOnClickListener(v -> aplicarFiltroAluno("todos"));
        if (filterAdmAtivos != null) filterAdmAtivos.setOnClickListener(v -> aplicarFiltroAluno("ativos"));
        if (filterAdmAvisos != null) filterAdmAvisos.setOnClickListener(v -> aplicarFiltroAluno("avisos"));
        if (filterAdm1Strike != null) filterAdm1Strike.setOnClickListener(v -> aplicarFiltroAluno("1strike"));
        if (filterAdm2Strikes != null) filterAdm2Strikes.setOnClickListener(v -> aplicarFiltroAluno("2strikes"));
        if (filterAdmBanidos != null) filterAdmBanidos.setOnClickListener(v -> aplicarFiltroAluno("banidos"));
        if (filterAdmDesativados != null) filterAdmDesativados.setOnClickListener(v -> aplicarFiltroAluno("desativados"));
    }

    private void aplicarFiltroAluno(String filtro) {
        filtroAlunoAtual = filtro;
        resetarVisualFiltrosAlunos();

        if ("ativos".equalsIgnoreCase(filtro) && filterAdmAtivos != null) {
            filterAdmAtivos.setBackgroundResource(R.drawable.bg_pill_purple);
            filterAdmAtivos.setTextColor(Color.WHITE);
        } else if ("avisos".equalsIgnoreCase(filtro) && filterAdmAvisos != null) {
            filterAdmAvisos.setBackgroundResource(R.drawable.bg_pill_purple);
            filterAdmAvisos.setTextColor(Color.WHITE);
        } else if ("1strike".equalsIgnoreCase(filtro) && filterAdm1Strike != null) {
            filterAdm1Strike.setBackgroundResource(R.drawable.bg_pill_purple);
            filterAdm1Strike.setTextColor(Color.WHITE);
        } else if ("2strikes".equalsIgnoreCase(filtro) && filterAdm2Strikes != null) {
            filterAdm2Strikes.setBackgroundResource(R.drawable.bg_pill_purple);
            filterAdm2Strikes.setTextColor(Color.WHITE);
        } else if ("banidos".equalsIgnoreCase(filtro) && filterAdmBanidos != null) {
            filterAdmBanidos.setBackgroundResource(R.drawable.bg_pill_purple);
            filterAdmBanidos.setTextColor(Color.WHITE);
        } else if ("desativados".equalsIgnoreCase(filtro) && filterAdmDesativados != null) {
            filterAdmDesativados.setBackgroundResource(R.drawable.bg_pill_purple);
            filterAdmDesativados.setTextColor(Color.WHITE);
        } else if (filterAdmAll != null) {
            filterAdmAll.setBackgroundResource(R.drawable.bg_pill_purple);
            filterAdmAll.setTextColor(Color.WHITE);
        }

        renderizarListaAlunosAdmin();
    }

    private void resetarVisualFiltrosAlunos() {
        if (getContext() == null) return;
        int darkIndigo = requireContext().getColor(R.color.dark_indigo);
        if (filterAdmAll != null) { filterAdmAll.setBackgroundResource(R.drawable.bg_card_sobreposicao2); filterAdmAll.setTextColor(darkIndigo); }
        if (filterAdmAtivos != null) { filterAdmAtivos.setBackgroundResource(R.drawable.bg_card_sobreposicao2); filterAdmAtivos.setTextColor(darkIndigo); }
        if (filterAdmAvisos != null) { filterAdmAvisos.setBackgroundResource(R.drawable.bg_card_sobreposicao2); filterAdmAvisos.setTextColor(darkIndigo); }
        if (filterAdm1Strike != null) { filterAdm1Strike.setBackgroundResource(R.drawable.bg_card_sobreposicao2); filterAdm1Strike.setTextColor(darkIndigo); }
        if (filterAdm2Strikes != null) { filterAdm2Strikes.setBackgroundResource(R.drawable.bg_card_sobreposicao2); filterAdm2Strikes.setTextColor(darkIndigo); }
        if (filterAdmBanidos != null) { filterAdmBanidos.setBackgroundResource(R.drawable.bg_card_sobreposicao2); filterAdmBanidos.setTextColor(darkIndigo); }
        if (filterAdmDesativados != null) { filterAdmDesativados.setBackgroundResource(R.drawable.bg_card_sobreposicao2); filterAdmDesativados.setTextColor(darkIndigo); }
    }

    private void atualizarSininho(View view) {
        if (view == null || getContext() == null) return;
        TextView badge = view.findViewById(R.id.badgeSininhoQtd);
        int qtd = NotificationHelper.getQtdNaoLidas(requireContext());
        if (badge != null) {
            if (qtd > 0) {
                badge.setText(String.valueOf(qtd));
                badge.setVisibility(View.VISIBLE);
            } else {
                badge.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            carregarDadosSalvos(getView());
        }
    }

    private void carregarDadosSalvos(View view) {
        if (getContext() == null) return;

        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());

        String emailAtual = UserSession.getUsuarioAtual(requireContext());
        String nome = sp.getString("nome", "");
        if (nome.isEmpty() || emailAtual.isEmpty() || "aluno@exemplo.com".equalsIgnoreCase(emailAtual) || "usuario_convidado".equalsIgnoreCase(emailAtual)) {
            nome = "Convidado";
        }

        String idade = sp.getString("idade", "17 anos");
        String escola = sp.getString("escola", "E.E. Próxima Etapa");
        String fotoUri = sp.getString("foto_perfil_uri", "");
        String tipo = sp.getString("tipoUsuario", "aluno");

        atualizarDadosAluno(nome, nome, idade, escola, tipo);

        if (imgFotoAluno != null && !fotoUri.isEmpty()) {
            carregarImagemElemento(imgFotoAluno, fotoUri);
        }

        // Atualiza a badge de strikes no botão de avisos e suspensões da Home do Aluno
        int qtdStrikes = SuspensaoManager.getQtdStrikes(requireContext());
        TextView badgeStrikes = view.findViewById(R.id.badgeStrikesHome);
        if (badgeStrikes != null) {
            if (qtdStrikes > 0) {
                badgeStrikes.setText(qtdStrikes + " Strike(s)");
                badgeStrikes.setBackgroundResource(R.drawable.bg_strike_block_red);
                badgeStrikes.setTextColor(requireContext().getColor(R.color.white));
            } else {
                badgeStrikes.setText("Ver Blacklist");
                badgeStrikes.setBackgroundResource(R.drawable.bg_pill_yellow);
                badgeStrikes.setTextColor(requireContext().getColor(R.color.dark_indigo));
            }
        }

        configurarVisibilidadeAba(tipo);
        renderizarCursosInscritos(sp);
        atualizarSininho(view);
    }

    private void configurarVisibilidadeAba(String tipoUsuario) {
        if ("admin".equalsIgnoreCase(tipoUsuario)) {
            if (containerHomeAdmin != null) containerHomeAdmin.setVisibility(View.VISIBLE);
            if (containerHomeAluno != null) containerHomeAluno.setVisibility(View.GONE);
            renderizarListaAlunosAdmin();
        } else {
            if (containerHomeAdmin != null) containerHomeAdmin.setVisibility(View.GONE);
            if (containerHomeAluno != null) containerHomeAluno.setVisibility(View.VISIBLE);

            if (btnVerCardHome != null) {
                if ("professor".equalsIgnoreCase(tipoUsuario)) {
                    btnVerCardHome.setText("➕ Postar Novo Curso");
                    btnVerCardHome.setOnClickListener(v -> exibirDialogPostarCurso());
                    if (areaQuestionario1 != null) areaQuestionario1.setVisibility(View.GONE);
                    if (areaQuestionario2 != null) areaQuestionario2.setVisibility(View.GONE);
                    if (btnAvisosSuspensoesHome != null) btnAvisosSuspensoesHome.setVisibility(View.GONE);
                } else {
                    btnVerCardHome.setText("Ver Card do Estudante");
                    btnVerCardHome.setOnClickListener(v -> CardImageSaver.exibirCardEstudanteDialog(requireContext()));
                    if (areaQuestionario1 != null) areaQuestionario1.setVisibility(View.VISIBLE);
                    if (areaQuestionario2 != null) areaQuestionario2.setVisibility(View.VISIBLE);
                    if (btnAvisosSuspensoesHome != null) btnAvisosSuspensoesHome.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void renderizarListaAlunosAdmin() {
        if (containerListaAlunosAdmin == null || getContext() == null) return;
        containerListaAlunosAdmin.removeAllViews();

        SharedPreferences spGeneral = requireContext().getSharedPreferences("ListaGeralAlunosAdmin", Context.MODE_PRIVATE);
        String savedString = spGeneral.getString("lista_alunos_cadastrados_string", "");
        List<String> listaAlunos = new ArrayList<>();

        if (!savedString.isEmpty()) {
            listaAlunos.addAll(Arrays.asList(savedString.split("###")));
        } else {
            // Alunos cadastrados padrão
            listaAlunos.add("Bolo Fofo;;;aluno@proximaetapa.com");
            listaAlunos.add("João Silva;;;joao@escola.com");
            listaAlunos.add("Maria Souza;;;maria@escola.com");
            salvarAlunosAdminSp(spGeneral, listaAlunos);
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        int exibidosCount = 0;

        for (int i = 0; i < listaAlunos.size(); i++) {
            final int index = i;
            String alunoStr = listaAlunos.get(i);
            String[] partes = alunoStr.split(";;;");
            String nomeAluno = partes.length > 0 ? partes[0] : "Aluno";
            String emailAluno = partes.length > 1 ? partes[1] : "aluno@escola.com";

            // Verifica o status do aluno para os filtros sem modificar a sessão do usuário conectado
            SharedPreferences spAluno = UserSession.getScopedPreferencesForUser(requireContext(), emailAluno);
            int strikesCount = spAluno.getInt("strike_count", 0);
            boolean banido = spAluno.getBoolean("esta_banido", false) || strikesCount >= 3;
            boolean suspenso = spAluno.getBoolean("esta_suspenso", false);

            // APLICAÇÃO DOS FILTROS POR CATEGORIA DE ALUNOS
            if ("ativos".equalsIgnoreCase(filtroAlunoAtual) && (banido || suspenso)) continue;
            if ("1strike".equalsIgnoreCase(filtroAlunoAtual) && (strikesCount != 1 || banido)) continue;
            if ("2strikes".equalsIgnoreCase(filtroAlunoAtual) && (strikesCount != 2 || banido)) continue;
            if ("banidos".equalsIgnoreCase(filtroAlunoAtual) && !banido) continue;
            if ("avisos".equalsIgnoreCase(filtroAlunoAtual)) {
                String savedAvisos = spAluno.getString("lista_avisos_professor", "");
                if (savedAvisos.isEmpty()) continue;
            }
            if ("desativados".equalsIgnoreCase(filtroAlunoAtual)) {
                boolean desativada = spAluno.getBoolean("conta_desativada", false);
                if (!desativada) continue;
            }

            exibidosCount++;

            View rowView = inflater.inflate(R.layout.item_adm_info_user, containerListaAlunosAdmin, false);

            TextView txtNome = rowView.findViewById(R.id.txtNomeAluno);
            TextView txtEmail = rowView.findViewById(R.id.txtEmailAluno);
            TextView txtStatus = rowView.findViewById(R.id.txtStatusAluno);

            Button btnAbrirPainelSuspensao = rowView.findViewById(R.id.btnAbrirPainelSuspensoes);
            Button btnLiberar = rowView.findViewById(R.id.btnLiberarAluno);
            ImageView btnExcluir = rowView.findViewById(R.id.btnExcluirAluno);

            if (txtNome != null) txtNome.setText(nomeAluno);
            if (txtEmail != null) txtEmail.setText(emailAluno);

            if (txtStatus != null) {
                if (banido) {
                    txtStatus.setText("BANIDO (3/3)");
                    txtStatus.setBackgroundResource(R.drawable.bg_pill_purple);
                } else if (suspenso) {
                    txtStatus.setText("SUSPENSO (" + strikesCount + "/3)");
                    txtStatus.setBackgroundResource(R.drawable.bg_strike_block_red);
                } else {
                    txtStatus.setText("Regular");
                    txtStatus.setBackgroundResource(R.drawable.bg_pill_green);
                }
            }

            if (btnAbrirPainelSuspensao != null) {
                btnAbrirPainelSuspensao.setOnClickListener(v -> {

                });
            }


            if (btnLiberar != null) {
                btnLiberar.setOnClickListener(v -> {
                    SuspensaoManager.LiberarAlunoParaEmail(requireContext(), emailAluno);
                    renderizarListaAlunosAdmin();
                });
            }

            if (btnExcluir != null) {
                btnExcluir.setOnClickListener(v -> {
                    listaAlunos.remove(index);
                    salvarAlunosAdminSp(spGeneral, listaAlunos);
                    renderizarListaAlunosAdmin();
                    NotificationHelper.exibirToastCustomizado(getContext(), "Aluno " + nomeAluno + " removido da lista!");
                });
            }

            containerListaAlunosAdmin.addView(rowView);
        }

        if (exibidosCount == 0) {
            TextView txtVazio = new TextView(getContext());
            txtVazio.setText("Nenhum aluno encontrado para o filtro '" + filtroAlunoAtual.toUpperCase() + "'.");
            txtVazio.setTextColor(requireContext().getColor(R.color.dark_gray));
            txtVazio.setTextSize(12);
            txtVazio.setPadding(8, 8, 8, 8);
            containerListaAlunosAdmin.addView(txtVazio);
        }
    }

    private void salvarAlunosAdminSp(SharedPreferences spGeneral, List<String> lista) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            sb.append(lista.get(i));
            if (i < lista.size() - 1) sb.append("###");
        }
        spGeneral.edit().putString("lista_alunos_cadastrados_string", sb.toString()).apply();
    }

    private void exibirDialogCriarRequerimento() {
        if (getContext() == null) return;
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_form_requerimento_aluno, null);
        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipoProblemaRequerimento);
        EditText inputRelato = dialogView.findViewById(R.id.inputRelatoProblemaRequerimento);
        Button btnCancelar = dialogView.findViewById(R.id.btnFecharRequerimento);
        Button btnEnviar = dialogView.findViewById(R.id.btnEnviarRequerimento);

        if (spinnerTipo != null) {
            String[] tiposArr = {
                    "Solicitação de Atestado / Declaração",
                    "Dúvidas sobre Matrícula / Cursos",
                    "Problema Técnico / Acesso à Plataforma",
                    "Denúncia ou Outro Assunto"
            };
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, tiposArr);
            spinnerTipo.setAdapter(adapter);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(dialogView).create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (btnCancelar != null) btnCancelar.setOnClickListener(v -> dialog.dismiss());
        if (btnEnviar != null && inputRelato != null && spinnerTipo != null) {
            btnEnviar.setOnClickListener(v -> {
                String email = UserSession.getUsuarioAtual(requireContext());
                if (email.isEmpty() || "aluno@exemplo.com".equalsIgnoreCase(email) || "usuario_convidado".equalsIgnoreCase(email)) {
                    NotificationHelper.exibirToastCustomizado(getContext(), "🔒 Faça login ou cadastre-se para enviar requerimentos!");
                    Intent loginIntent = new Intent(getActivity(), com.example.errornull_proximaetapa.auth.LoginInfo.class);
                    startActivity(loginIntent);
                    dialog.dismiss();
                    return;
                }

                String relato = inputRelato.getText().toString().trim();
                String tipoProb = spinnerTipo.getSelectedItem() != null ? spinnerTipo.getSelectedItem().toString() : "Geral";
                if (relato.isEmpty()) {
                    NotificationHelper.exibirToastCustomizado(getContext(), "Descreva o seu relato/solicitação!");
                    return;
                }
                SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
                String nome = sp.getString("nome", "Estudante");
                com.example.errornull_proximaetapa.auth.RequerimentosManager.enviarRequerimento(requireContext(), email, nome, tipoProb, relato);
                NotificationHelper.exibirToastCustomizado(getContext(), "Requerimento enviado com sucesso!");
                dialog.dismiss();
            });
        }
        dialog.show();
    }

    private void exibirDialogCadastrarAluno() {
        if (getContext() == null) return;

        LinearLayout cardView = new LinearLayout(requireContext());
        cardView.setOrientation(LinearLayout.VERTICAL);
        cardView.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        cardView.setPadding(40, 40, 40, 40);

        TextView title = new TextView(requireContext());
        title.setText("➕ Cadastrar Novo Aluno");
        title.setTextColor(requireContext().getColor(R.color.dark_indigo));
        title.setTextSize(16);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        EditText inputNome = new EditText(requireContext());
        inputNome.setHint("Nome do Aluno");

        EditText inputEmail = new EditText(requireContext());
        inputEmail.setHint("E-mail do Aluno");

        Button btnAdd = new Button(requireContext());
        btnAdd.setText("Cadastrar Aluno");
        btnAdd.setBackgroundTintList(android.content.res.ColorStateList.valueOf(requireContext().getColor(R.color.yellow)));
        btnAdd.setTextColor(requireContext().getColor(R.color.dark_indigo));
        btnAdd.setTypeface(null, android.graphics.Typeface.BOLD);

        cardView.addView(title);
        cardView.addView(inputNome);
        cardView.addView(inputEmail);
        cardView.addView(btnAdd);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(cardView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnAdd.setOnClickListener(v -> {
            String nome = inputNome.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();

            if (nome.isEmpty() || email.isEmpty()) {
                NotificationHelper.exibirToastCustomizado(getContext(), "Preencha o nome e o e-mail!");
                return;
            }

            SharedPreferences spGeneral = requireContext().getSharedPreferences("ListaGeralAlunosAdmin", Context.MODE_PRIVATE);
            String savedString = spGeneral.getString("lista_alunos_cadastrados_string", "");
            List<String> listaAlunos = new ArrayList<>();
            if (!savedString.isEmpty()) {
                listaAlunos.addAll(Arrays.asList(savedString.split("###")));
            }

            listaAlunos.add(nome + ";;;" + email);
            salvarAlunosAdminSp(spGeneral, listaAlunos);


            SharedPreferences spNovo = UserSession.getScopedPreferencesForUser(requireContext(), email);
            spNovo.edit()
                    .putString("nome", nome)
                    .putString("email", email)
                    .putString("tipoUsuario", "aluno")
                    .apply();

            NotificationHelper.exibirToastCustomizado(getContext(), "Aluno " + nome + " cadastrado com sucesso!");
            renderizarListaAlunosAdmin();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void exibirDialogPostarCurso() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_postar_curso, null);

        EditText inputNome = dialogView.findViewById(R.id.inputNomeCurso);
        EditText inputFaculdade = dialogView.findViewById(R.id.inputFaculdadeCurso);
        EditText inputCarga = dialogView.findViewById(R.id.inputPrazoReservaCurso);
        EditText inputDias = dialogView.findViewById(R.id.inputPrazoTerminoCurso);
        Button btnPublicar = dialogView.findViewById(R.id.btnPublicarCursoProfFinal);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnPublicar != null) {
            btnPublicar.setOnClickListener(v -> {
                String nome = inputNome != null ? inputNome.getText().toString().trim() : "";
                String faculdade = inputFaculdade != null ? inputFaculdade.getText().toString().trim() : "USP";
                String carga = inputCarga != null ? inputCarga.getText().toString().trim() : "80 horas";
                String dias = inputDias != null ? inputDias.getText().toString().trim() : "Seg, Qua, Sex (14h - 16h)";

                if (nome.isEmpty()) {
                    NotificationHelper.exibirToastCustomizado(getContext(), "Digite o nome do curso!");
                    return;
                }

                String idCurso = "curso_prof_" + System.currentTimeMillis();
                SharedPreferences sp = UserSession.getScopedPreferences(requireContext());

                Set<String> setCursos = new HashSet<>(sp.getStringSet("lista_cursos_ids", new HashSet<>()));
                setCursos.add(idCurso);

                sp.edit()
                        .putBoolean("inscrito", true)
                        .putStringSet("lista_cursos_ids", setCursos)
                        .putString("nomeCurso_" + idCurso, nome)
                        .putString("faculdadeCurso_" + idCurso, faculdade)
                        .putString("cargaHoraria_" + idCurso, carga)
                        .putString("diasSemana_" + idCurso, dias)
                        .putInt("progressoCurso_" + idCurso, 0)
                        .apply();

                NotificationHelper.adicionarNotificacao(
                        requireContext(),
                        "Novo Curso Publicado",
                        "Você publicou a disciplina de " + nome + " (" + faculdade + ") para os alunos!"
                );

                NotificationHelper.exibirToastCustomizado(getContext(), "📢 Curso " + nome + " publicado com sucesso!");
                carregarDadosSalvos(getView());
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void renderizarCursosInscritos(SharedPreferences sp) {
        if (containerCursosInscritosHome == null || layoutSemCursos == null || getContext() == null) return;

        containerCursosInscritosHome.removeAllViews();

        Set<String> setCursosOriginal = sp.getStringSet("lista_cursos_ids", new HashSet<>());
        Set<String> setCursos = new HashSet<>(setCursosOriginal);

        if (setCursos.isEmpty()) {
            containerCursosInscritosHome.setVisibility(View.GONE);
            layoutSemCursos.setVisibility(View.VISIBLE);
            return;
        }

        containerCursosInscritosHome.setVisibility(View.VISIBLE);
        layoutSemCursos.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (String idCurso : setCursos) {
            String nomeCurso = sp.getString("nomeCurso_" + idCurso, "");
            String faculdade = sp.getString("faculdadeCurso_" + idCurso, "USP");
            String diasSemana = sp.getString("diasSemana_" + idCurso, "Segunda, Quarta e Sexta (14h - 16h)");
            int progresso = sp.getInt("progressoCurso_" + idCurso, 0);

            if (nomeCurso.isEmpty()) continue;

            View cardItem = inflater.inflate(R.layout.item_cursos, containerCursosInscritosHome, false);

            TextView txtResumo = cardItem.findViewById(R.id.txtTituloCurso);
            TextView txtBadge = cardItem.findViewById(R.id.txtStatusCurso);
            TextView txtDiasAulas = cardItem.findViewById(R.id.txtDescricaoCurso1);
            ProgressBar pbProgresso = null;
            TextView txtPorcentagem = cardItem.findViewById(R.id.txtDescricaoCurso2);

            if (txtResumo != null) txtResumo.setText(nomeCurso);
            if (txtBadge != null) txtBadge.setText(faculdade);
            if (txtDiasAulas != null) txtDiasAulas.setText("📅 " + diasSemana);
            if (pbProgresso != null) pbProgresso.setProgress(progresso);
            if (txtPorcentagem != null) txtPorcentagem.setText(progresso + "%");

            cardItem.setOnClickListener(v -> {
                if (SuspensaoManager.isSuspensoOuBanido(requireContext())) {
                    SuspensaoManager.exibirTelaBloqueioCurso(requireContext());
                    return;
                }
                Intent intent = new Intent(getActivity(), TelaAulaCurso.class);
                intent.putExtra("cursoId", idCurso);
                startActivity(intent);
            });

            containerCursosInscritosHome.addView(cardItem);
        }
    }

    private void carregarImagemElemento(ImageView imageView, String fotoUriStr) {
        if (imageView == null || fotoUriStr == null || fotoUriStr.isEmpty()) return;

        if (fotoUriStr.startsWith("preset:")) {
            String presetName = fotoUriStr.replace("preset:", "");
            if ("baseline_account".equals(presetName)) {
                imageView.setImageResource(R.drawable.baseline_account);
            } else if ("logo_proxima_etapa".equals(presetName)) {
                imageView.setImageResource(R.drawable.logo_proxima_etapa);
            } else {
                imageView.setImageResource(R.drawable.round_person);
            }
        } else {
            try {
                imageView.setImageURI(Uri.parse(fotoUriStr));
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.round_person);
            }
        }
    }

    public LinearLayout getAreaQuestionario1() {
        return areaQuestionario1;
    }

    public LinearLayout getAreaQuestionario2() {
        return areaQuestionario2;
    }

    public ImageView getImgFotoAluno() {
        return imgFotoAluno;
    }

    public void atualizarDadosAluno(String nome, String nomeCompleto, String idade, String escola, String tipoUsuario) {
        if (txtNomeAluno != null) txtNomeAluno.setText(nome);
        if (txtBoasVindas != null) {
            if ("professor".equalsIgnoreCase(tipoUsuario)) {
                txtBoasVindas.setText("bem vindo(a), professor(a)!");
            } else if ("admin".equalsIgnoreCase(tipoUsuario)) {
                txtBoasVindas.setText("bem vindo(a), administrador(a)!");
            } else {
                String mensagem = getString(R.string.boas_vindas_format, nome);
                txtBoasVindas.setText(mensagem);
            }
        }
        if (txtNomeCompleto != null) txtNomeCompleto.setText(nomeCompleto);
        if (txtIdade != null) txtIdade.setText("professor".equalsIgnoreCase(tipoUsuario) || "admin".equalsIgnoreCase(tipoUsuario) ? "Docente / Gestor" : idade);
        if (txtEscola != null) txtEscola.setText(escola);
    }
}