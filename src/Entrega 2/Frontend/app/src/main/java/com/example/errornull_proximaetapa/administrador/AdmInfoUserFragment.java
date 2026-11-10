package com.example.errornull_proximaetapa.administrador;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.SuspensaoManager;
import com.example.errornull_proximaetapa.UserSession;
import com.example.errornull_proximaetapa.auth.NotificationHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdmInfoUserFragment extends Fragment {

    private View btnAdminBlacklistTools, btnSininhoNotificacoes;
    private Button btnCadastrarAlunoAdmin;
    private LinearLayout containerListaAlunosAdmin;

    private String filtroAlunoAtual = "todos";
    private TextView filterAAll, filterAAtivos, filterAAvisos, filterA1Strike, filterA2Strikes, filterABanidos, filterADesativados;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adm_info_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
        String emailAtual = UserSession.getUsuarioAtual(requireContext());
        String nome = sp.getString("nome", "");
        if (nome.isEmpty() || emailAtual.isEmpty() || "aluno@exemplo.com".equalsIgnoreCase(emailAtual) || "usuario_convidado".equalsIgnoreCase(emailAtual)) {
            nome = "Convidado";
        }

        TextView txtNomeAdminMaster = view.findViewById(R.id.txtNomeAdminMaster);
        if (txtNomeAdminMaster != null) {
            txtNomeAdminMaster.setText(nome);
        }

        btnAdminBlacklistTools = view.findViewById(R.id.btnAdminBlacklistTools);
        btnCadastrarAlunoAdmin = view.findViewById(R.id.btnCadastrarAlunoAdmin);
        containerListaAlunosAdmin = view.findViewById(R.id.containerListaAlunosAdmin);

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

        configurarFiltrosAlunosAdmin(view);
        renderizarListaAlunosAdmin();
        atualizarSininho(view);
    }

    private void configurarFiltrosAlunosAdmin(View view) {
        filterAAll = view.findViewById(R.id.filterAlunoTodos);
        filterAAtivos = view.findViewById(R.id.filterAlunoAtivos);
        filterAAvisos = view.findViewById(R.id.filterAlunoAvisos);
        filterA1Strike = view.findViewById(R.id.filterAluno1Strike);
        filterA2Strikes = view.findViewById(R.id.filterAluno2Strikes);
        filterABanidos = view.findViewById(R.id.filterAlunoBanidos);
        filterADesativados = view.findViewById(R.id.filterAlunoDesativados);

        if (filterAAll != null) filterAAll.setOnClickListener(v -> aplicarFiltroAluno("todos"));
        if (filterAAtivos != null) filterAAtivos.setOnClickListener(v -> aplicarFiltroAluno("ativos"));
        if (filterAAvisos != null) filterAAvisos.setOnClickListener(v -> aplicarFiltroAluno("avisos"));
        if (filterA1Strike != null) filterA1Strike.setOnClickListener(v -> aplicarFiltroAluno("1strike"));
        if (filterA2Strikes != null) filterA2Strikes.setOnClickListener(v -> aplicarFiltroAluno("2strikes"));
        if (filterABanidos != null) filterABanidos.setOnClickListener(v -> aplicarFiltroAluno("banidos"));
        if (filterADesativados != null) filterADesativados.setOnClickListener(v -> aplicarFiltroAluno("desativados"));
    }

    private void aplicarFiltroAluno(String filtro) {
        filtroAlunoAtual = filtro;
        resetarVisualFiltrosAlunos();

        if ("ativos".equalsIgnoreCase(filtro) && filterAAtivos != null) {
            filterAAtivos.setBackgroundResource(R.drawable.bg_pill_purple);
            filterAAtivos.setTextColor(Color.WHITE);
        } else if ("avisos".equalsIgnoreCase(filtro) && filterAAvisos != null) {
            filterAAvisos.setBackgroundResource(R.drawable.bg_pill_purple);
            filterAAvisos.setTextColor(Color.WHITE);
        } else if ("1strike".equalsIgnoreCase(filtro) && filterA1Strike != null) {
            filterA1Strike.setBackgroundResource(R.drawable.bg_pill_purple);
            filterA1Strike.setTextColor(Color.WHITE);
        } else if ("2strikes".equalsIgnoreCase(filtro) && filterA2Strikes != null) {
            filterA2Strikes.setBackgroundResource(R.drawable.bg_pill_purple);
            filterA2Strikes.setTextColor(Color.WHITE);
        } else if ("banidos".equalsIgnoreCase(filtro) && filterABanidos != null) {
            filterABanidos.setBackgroundResource(R.drawable.bg_pill_purple);
            filterABanidos.setTextColor(Color.WHITE);
        } else if ("desativados".equalsIgnoreCase(filtro) && filterADesativados != null) {
            filterADesativados.setBackgroundResource(R.drawable.bg_pill_purple);
            filterADesativados.setTextColor(Color.WHITE);
        } else if (filterAAll != null) {
            filterAAll.setBackgroundResource(R.drawable.bg_pill_purple);
            filterAAll.setTextColor(Color.WHITE);
        }

        renderizarListaAlunosAdmin();
    }

    private void resetarVisualFiltrosAlunos() {
        if (getContext() == null) return;
        int darkIndigo = requireContext().getColor(R.color.dark_indigo);
        if (filterAAll != null) { filterAAll.setBackgroundResource(R.drawable.bg_card_sobreposicao2); filterAAll.setTextColor(darkIndigo); }
        if (filterAAtivos != null) { filterAAtivos.setBackgroundResource(R.drawable.bg_card_sobreposicao2); filterAAtivos.setTextColor(darkIndigo); }
        if (filterAAvisos != null) { filterAAvisos.setBackgroundResource(R.drawable.bg_card_sobreposicao2); filterAAvisos.setTextColor(darkIndigo); }
        if (filterA1Strike != null) { filterA1Strike.setBackgroundResource(R.drawable.bg_card_sobreposicao2); filterA1Strike.setTextColor(darkIndigo); }
        if (filterA2Strikes != null) { filterA2Strikes.setBackgroundResource(R.drawable.bg_card_sobreposicao2); filterA2Strikes.setTextColor(darkIndigo); }
        if (filterABanidos != null) { filterABanidos.setBackgroundResource(R.drawable.bg_card_sobreposicao2); filterABanidos.setTextColor(darkIndigo); }
        if (filterADesativados != null) { filterADesativados.setBackgroundResource(R.drawable.bg_card_sobreposicao2); filterADesativados.setTextColor(darkIndigo); }
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
            listaAlunos.add("Bolo Fofo;;;aluno@proximaetapa.com");
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

            SharedPreferences spAluno = UserSession.getScopedPreferencesForUser(requireContext(), emailAluno);
            int strikesCount = spAluno.getInt("strike_count", 0);
            boolean banido = spAluno.getBoolean("esta_banido", false) || strikesCount >= 3;
            boolean suspenso = spAluno.getBoolean("esta_suspenso", false);

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

            Button btnAbrirPainelSuspensoes = rowView.findViewById(R.id.btnAbrirPainelSuspensoes);
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

            if (btnAbrirPainelSuspensoes != null) {
                btnAbrirPainelSuspensoes.setOnClickListener(v -> {
                    SuspensaoManager.aplicarStrikeParaEmail(requireContext(), emailAluno, "Desrespeito às regras da plataforma", "1 minuto");
                    renderizarListaAlunosAdmin();
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

    private void exibirDialogCadastrarAluno() {
        if (getContext() == null) return;

        LinearLayout cardView = new LinearLayout(requireContext());
        cardView.setOrientation(LinearLayout.VERTICAL);
        cardView.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        cardView.setPadding(40, 40, 40, 40);

        TextView title = new TextView(requireContext());
        title.setText("Cadastrar Novo Aluno");
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
        renderizarListaAlunosAdmin();
    }
}
