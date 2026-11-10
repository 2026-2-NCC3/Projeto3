package com.example.errornull_proximaetapa.administrador;

import android.content.Context;
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

import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.SuspensaoManager;
import com.example.errornull_proximaetapa.UserSession;
import com.example.errornull_proximaetapa.aluno.AlunoCursosFragment;
import com.example.errornull_proximaetapa.auth.NotificationHelper;
import com.example.errornull_proximaetapa.auth.RequerimentosManager;

import java.util.HashSet;
import java.util.Set;

public class AdmModeradorFragment extends Fragment {

    private ImageView imgFotoAluno;
    private TextView txtNomeAluno, txtBoasVindas, txtNomeCompleto, txtIdade, txtEscola, txtCursosCompletos;
    private View btnSininhoNotificacoes, btnAvisosSuspensoesHome;
    private Button btnVerAvisos;

    private View opcReservas, opcCursos, opcTestes, opcEditPerfil;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adm_moderador, container, false);
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

        if (opcReservas != null) opcReservas.setOnClickListener(v -> RequerimentosManager.abrirPainelRequerimentosAdmin(requireContext()));
        if (opcCursos != null) opcCursos.setOnClickListener(v -> exibirDialogTodasDisciplinasAdmin());
        if (opcTestes != null) opcTestes.setOnClickListener(v -> exibirDialogTestesAdmin());
        if (opcEditPerfil != null) opcEditPerfil.setOnClickListener(v -> exibirDialogEditarPerfilAdmin());

        carregarDadosPerfilAdmin(view);
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

    private void carregarDadosPerfilAdmin(View view) {
        if (getContext() == null) return;

        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());

        String emailAtual = UserSession.getUsuarioAtual(requireContext());
        String nome = sp.getString("nome", "");
        if (nome.isEmpty() || emailAtual.isEmpty() || "aluno@exemplo.com".equalsIgnoreCase(emailAtual) || "usuario_convidado".equalsIgnoreCase(emailAtual)) {
            nome = "Convidado";
        }

        String fotoUri = sp.getString("foto_perfil_uri", "");

        AlunoCursosFragment.sincronizarECarregarCursosProfessores(requireContext());
        SharedPreferences spGeralCursos = requireContext().getSharedPreferences("ListaGeralCursosPlataforma", Context.MODE_PRIVATE);
        Set<String> setGlobalCursos = spGeralCursos.getStringSet("lista_cursos_publicos_ids", new HashSet<>());

        if (txtNomeAluno != null) txtNomeAluno.setText(nome);
        if (txtBoasVindas != null) txtBoasVindas.setText("seja bem-vindo(a), administrador(a)!");
        if (txtNomeCompleto != null) txtNomeCompleto.setText(nome);
        if (txtIdade != null) txtIdade.setText("Cargo: Direção & Moderação");
        if (txtEscola != null) txtEscola.setText("Acesso: Painel de Controle Global");
        if (txtCursosCompletos != null) txtCursosCompletos.setText("Cursos Validados no Sistema: " + setGlobalCursos.size());

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

    private void exibirDialogTodasDisciplinasAdmin() {
        if (getContext() == null) return;

        AlunoCursosFragment.sincronizarECarregarCursosProfessores(requireContext());
        SharedPreferences spGeralCursos = requireContext().getSharedPreferences("ListaGeralCursosPlataforma", Context.MODE_PRIVATE);
        Set<String> setGlobalCursos = spGeralCursos.getStringSet("lista_cursos_publicos_ids", new HashSet<>());

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(requireContext());
        title.setText("📚 Catálogo Global de Cursos & Disciplinas (ADM)");
        title.setTextColor(requireContext().getColor(R.color.dark_indigo));
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        layout.addView(title);

        if (setGlobalCursos.isEmpty()) {
            TextView txtVazio = new TextView(requireContext());
            txtVazio.setText("Nenhum curso cadastrado na plataforma ainda.");
            txtVazio.setTextColor(requireContext().getColor(R.color.dark_gray));
            txtVazio.setTextSize(13);
            txtVazio.setPadding(0, 16, 0, 16);
            layout.addView(txtVazio);
        } else {
            for (String idC : setGlobalCursos) {
                String nome = spGeralCursos.getString("nomeCurso_" + idC, "Curso");
                String faculdade = spGeralCursos.getString("faculdadeCurso_" + idC, "USP");
                String carga = spGeralCursos.getString("cargaHoraria_" + idC, "100 Pontos");

                TextView itemC = new TextView(requireContext());
                itemC.setText("📚 " + nome + " (" + faculdade + " • " + carga + ")");
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

    private void exibirDialogTestesAdmin() {
        if (getContext() == null) return;

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(requireContext());
        title.setText("🧠 Gestão de Testes Vocacionais & Avaliações");
        title.setTextColor(requireContext().getColor(R.color.purple));
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        layout.addView(title);

        TextView t1 = new TextView(requireContext());
        t1.setText("📝 Questionário Vocacional Oficial (15 Questões - Ativo)");
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

    private void exibirDialogEditarPerfilAdmin() {
        if (getContext() == null) return;

        LinearLayout cardView = new LinearLayout(requireContext());
        cardView.setOrientation(LinearLayout.VERTICAL);
        cardView.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        cardView.setPadding(40, 40, 40, 40);

        TextView title = new TextView(requireContext());
        title.setText("✏️ Editar Informações do Administrador");
        title.setTextColor(requireContext().getColor(R.color.dark_indigo));
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);

        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());

        EditText inputNome = new EditText(requireContext());
        inputNome.setHint("Nome do Administrador");
        inputNome.setText(sp.getString("nome", ""));

        Button btnSalvar = new Button(requireContext());
        btnSalvar.setText("Salvar Dados");
        btnSalvar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(requireContext().getColor(R.color.yellow)));
        btnSalvar.setTextColor(requireContext().getColor(R.color.dark_indigo));
        btnSalvar.setTypeface(null, Typeface.BOLD);

        cardView.addView(title);
        cardView.addView(inputNome);
        cardView.addView(btnSalvar);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(cardView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnSalvar.setOnClickListener(v -> {
            String nome = inputNome.getText().toString().trim();

            if (nome.isEmpty()) {
                NotificationHelper.exibirToastCustomizado(getContext(), "Digite seu nome!");
                return;
            }

            sp.edit().putString("nome", nome).apply();

            NotificationHelper.exibirToastCustomizado(getContext(), "Nome de Administrador atualizado!");
            if (getView() != null) carregarDadosPerfilAdmin(getView());
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            carregarDadosPerfilAdmin(getView());
        }
    }
}
