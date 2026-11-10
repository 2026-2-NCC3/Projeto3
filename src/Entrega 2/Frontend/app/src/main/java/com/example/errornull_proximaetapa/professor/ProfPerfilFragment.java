package com.example.errornull_proximaetapa.professor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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

import com.example.errornull_proximaetapa.AttendanceManager;
import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.SuspensaoManager;
import com.example.errornull_proximaetapa.UserSession;
import com.example.errornull_proximaetapa.auth.NotificationHelper;

import java.util.HashSet;
import java.util.Set;

public class ProfPerfilFragment extends Fragment {

    private ImageView imgFotoAluno;
    private TextView txtNomeAluno, txtBoasVindas, txtNomeCompleto, txtIdade, txtEscola, txtCursosCompletos, txtBioPerfil;
    private View btnSininhoNotificacoes, btnAvisosSuspensoesHome;
    private Button btnVerAvisos;

    private View opcReservas, opcCursos, opcTestes, opcEditPerfil;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prof_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgFotoAluno = view.findViewById(R.id.imgFotoAluno);
        txtNomeAluno = view.findViewById(R.id.txtNomeAluno);
        txtBoasVindas = view.findViewById(R.id.txtBoasVindas);
        txtNomeCompleto = view.findViewById(R.id.txtNomeCompleto);
        txtIdade = view.findViewById(R.id.txtIdade);
        txtEscola = view.findViewById(R.id.txtEscola);
        txtCursosCompletos = view.findViewById(R.id.txtCursosCompletos);
        txtBioPerfil = view.findViewById(R.id.txtBioPerfil);

        btnAvisosSuspensoesHome = view.findViewById(R.id.btnAvisosSuspensoesHome);
        btnVerAvisos = view.findViewById(R.id.btnVerAvisos);

        opcReservas = view.findViewById(R.id.opcReservas);
        opcCursos = view.findViewById(R.id.opcCursos);
        opcTestes = view.findViewById(R.id.opcTestes);
        opcEditPerfil = view.findViewById(R.id.opcEditPerfil);

        btnSininhoNotificacoes = view.findViewById(R.id.btnSininhoNotificacoes);
        if (btnSininhoNotificacoes != null) {
            btnSininhoNotificacoes.setOnClickListener(v -> NotificationHelper.exibirCentralNotificacoes(requireContext(), () -> atualizarSininho(view)));
        }

        if (btnVerAvisos != null) {
            btnVerAvisos.setOnClickListener(v -> SuspensaoManager.exibirPainelAdminSuspensaoDialog(requireContext()));
        } else if (btnAvisosSuspensoesHome != null) {
            btnAvisosSuspensoesHome.setOnClickListener(v -> SuspensaoManager.exibirPainelAdminSuspensaoDialog(requireContext()));
        }

        if (opcReservas != null) opcReservas.setOnClickListener(v -> exibirDialogGestaoPresencaMenu());
        if (opcCursos != null) opcCursos.setOnClickListener(v -> exibirDialogMinhasDisciplinasProf());
        if (opcTestes != null) opcTestes.setOnClickListener(v -> exibirDialogTestesProf());
        if (opcEditPerfil != null) opcEditPerfil.setOnClickListener(v -> exibirDialogEditarPerfilProf());

        carregarDadosPerfil(view);
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

    private void carregarDadosPerfil(View view) {
        if (getContext() == null) return;

        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());

        String emailAtual = UserSession.getUsuarioAtual(requireContext());
        String nome = sp.getString("nome", "");
        if (nome.isEmpty() || emailAtual.isEmpty() || "aluno@exemplo.com".equalsIgnoreCase(emailAtual) || "usuario_convidado".equalsIgnoreCase(emailAtual)) {
            nome = "Convidado";
        }

        String escola = sp.getString("escola", sp.getString("faculdade", "Universidade de São Paulo (USP)"));
        String bio = sp.getString("bio", "Professor e lecionador parceiro na plataforma Próxima Etapa.");
        String fotoUri = sp.getString("foto_perfil_uri", "");

        Set<String> setCursos = sp.getStringSet("lista_cursos_ids", new HashSet<>());

        if (txtNomeAluno != null) txtNomeAluno.setText(nome);
        if (txtBoasVindas != null) txtBoasVindas.setText("seja bem-vindo(a), professor(a)!");
        if (txtNomeCompleto != null) txtNomeCompleto.setText(nome);
        if (txtIdade != null) txtIdade.setText("Docente & Leitor");
        if (txtEscola != null) txtEscola.setText("Instituição: " + escola);
        if (txtCursosCompletos != null) txtCursosCompletos.setText("Disciplinas Postadas: " + setCursos.size() + " disciplina(s)");
        if (txtBioPerfil != null) txtBioPerfil.setText(bio.isEmpty() ? "Nenhuma biografia informada ainda." : bio);

        if (imgFotoAluno != null && !fotoUri.isEmpty()) {
            if (fotoUri.startsWith("preset:")) {
                String presetName = fotoUri.replace("preset:", "");
                if ("baseline_account".equals(presetName)) {
                    imgFotoAluno.setImageResource(R.drawable.baseline_account);
                } else if ("logo_proxima_etapa".equals(presetName)) {
                    imgFotoAluno.setImageResource(R.drawable.logo_proxima_etapa);
                } else {
                    imgFotoAluno.setImageResource(R.drawable.round_person);
                }
            } else {
                try {
                    imgFotoAluno.setImageURI(Uri.parse(fotoUri));
                } catch (Exception e) {
                    imgFotoAluno.setImageResource(R.drawable.round_person);
                }
            }
        }

        atualizarSininho(view);
    }

    private void exibirDialogGestaoPresencaMenu() {
        if (getContext() == null) return;
        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
        Set<String> setCursos = sp.getStringSet("lista_cursos_ids", new HashSet<>());

        if (setCursos.isEmpty()) {
            NotificationHelper.exibirToastCustomizado(getContext(), "Nenhuma disciplina cadastrada para gerenciar presença.");
            return;
        }

        String idCurso = setCursos.iterator().next();
        String nomeCurso = sp.getString("nomeCurso_" + idCurso, "Disciplina");
        AttendanceManager.exibirDialogGestaoPresencaProf(requireContext(), idCurso, nomeCurso, () -> carregarDadosPerfil(getView()));
    }

    private void exibirDialogMinhasDisciplinasProf() {
        if (getContext() == null) return;
        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
        Set<String> setCursos = sp.getStringSet("lista_cursos_ids", new HashSet<>());

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(requireContext());
        title.setText("📚 Minhas Disciplinas Publicadas");
        title.setTextColor(requireContext().getColor(R.color.dark_indigo));
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        layout.addView(title);

        if (setCursos.isEmpty()) {
            TextView txtVazio = new TextView(requireContext());
            txtVazio.setText("Você ainda não publicou disciplinas. Clique na aba 'Postar' para publicar!");
            txtVazio.setTextColor(requireContext().getColor(R.color.dark_gray));
            txtVazio.setTextSize(13);
            txtVazio.setPadding(0, 16, 0, 16);
            layout.addView(txtVazio);
        } else {
            for (String idC : setCursos) {
                String nome = sp.getString("nomeCurso_" + idC, "Disciplina");
                String faculdade = sp.getString("faculdadeCurso_" + idC, "USP");

                TextView itemC = new TextView(requireContext());
                itemC.setText("📚 " + nome + " (" + faculdade + ")");
                itemC.setTextColor(requireContext().getColor(R.color.dark_indigo));
                itemC.setTextSize(13);
                itemC.setTypeface(null, Typeface.BOLD);
                itemC.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
                itemC.setPadding(16, 12, 16, 12);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 8, 0, 8);
                itemC.setLayoutParams(lp);

                layout.addView(itemC);
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(layout)
                .setPositiveButton("Fechar", null)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    private void exibirDialogTestesProf() {
        if (getContext() == null) return;

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(requireContext());
        title.setText("🧠 Avaliações & Testes Docentes");
        title.setTextColor(requireContext().getColor(R.color.purple));
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        layout.addView(title);

        TextView t1 = new TextView(requireContext());
        t1.setText("📝 Avaliação Contínua de Aulas Práticas (Ativo)");
        t1.setTextColor(requireContext().getColor(R.color.dark_indigo));
        t1.setTextSize(13);
        t1.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        t1.setPadding(16, 12, 16, 12);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.setMargins(0, 12, 0, 8);
        t1.setLayoutParams(lp1);

        layout.addView(t1);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(layout)
                .setPositiveButton("Fechar", null)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    private void exibirDialogEditarPerfilProf() {
        if (getContext() == null) return;

        LinearLayout cardView = new LinearLayout(requireContext());
        cardView.setOrientation(LinearLayout.VERTICAL);
        cardView.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        cardView.setPadding(40, 40, 40, 40);

        TextView title = new TextView(requireContext());
        title.setText("✏️ Editar Biografia & Perfil do Professor");
        title.setTextColor(requireContext().getColor(R.color.dark_indigo));
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);

        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());

        EditText inputNome = new EditText(requireContext());
        inputNome.setHint("Nome do Professor");
        inputNome.setText(sp.getString("nome", ""));

        EditText inputBio = new EditText(requireContext());
        inputBio.setHint("Biografia / Especialidades");
        inputBio.setText(sp.getString("bio", ""));

        Button btnSalvar = new Button(requireContext());
        btnSalvar.setText("Salvar Dados");
        btnSalvar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(requireContext().getColor(R.color.yellow)));
        btnSalvar.setTextColor(requireContext().getColor(R.color.dark_indigo));
        btnSalvar.setTypeface(null, Typeface.BOLD);

        cardView.addView(title);
        cardView.addView(inputNome);
        cardView.addView(inputBio);
        cardView.addView(btnSalvar);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(cardView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnSalvar.setOnClickListener(v -> {
            String nome = inputNome.getText().toString().trim();
            String bio = inputBio.getText().toString().trim();

            if (nome.isEmpty()) {
                NotificationHelper.exibirToastCustomizado(getContext(), "Digite seu nome!");
                return;
            }

            sp.edit()
                    .putString("nome", nome)
                    .putString("bio", bio)
                    .apply();

            NotificationHelper.exibirToastCustomizado(getContext(), "Perfil do professor atualizado!");
            if (getView() != null) carregarDadosPerfil(getView());
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            carregarDadosPerfil(getView());
        }
    }
}
