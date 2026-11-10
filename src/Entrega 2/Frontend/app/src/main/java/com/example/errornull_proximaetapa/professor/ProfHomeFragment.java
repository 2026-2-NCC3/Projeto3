package com.example.errornull_proximaetapa.professor;

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
import com.example.errornull_proximaetapa.ChatManager;
import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.UserSession;
import com.example.errornull_proximaetapa.aluno.AlunoCursosFragment;
import com.example.errornull_proximaetapa.auth.NotificationHelper;
import com.example.errornull_proximaetapa.curso.TelaAulaCurso;

import java.util.HashSet;
import java.util.Set;

public class ProfHomeFragment extends Fragment {

    private TextView txtNomeAluno, txtBoasVindas, txtNomeCompleto, txtIdade, txtEscola;
    private ImageView imgFotoAluno;
    private Button btnPostarNovaDisciplinaProfHome;
    private LinearLayout containerDisciplinasProfHome;
    private View btnChatProfAdmHome, btnSininhoNotificacoes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prof_home, container, false);
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

        btnPostarNovaDisciplinaProfHome = view.findViewById(R.id.btnPostarNovaDisciplinaProfHome);
        containerDisciplinasProfHome = view.findViewById(R.id.containerDisciplinasProfHome);
        btnChatProfAdmHome = view.findViewById(R.id.btnChatProfAdmHome);

        if (btnPostarNovaDisciplinaProfHome != null) {
            btnPostarNovaDisciplinaProfHome.setOnClickListener(v -> exibirDialogPostarCursoProf());
        }

        if (btnChatProfAdmHome != null) {
            btnChatProfAdmHome.setOnClickListener(v -> ChatManager.abrirJanelaChat(
                    requireContext(),
                    "chat_prof_adm",
                    "💬 Chat Docentes ↔ ADM",
                    "Comunicação direta em tempo real com a Direção"
            ));
        }

        btnSininhoNotificacoes = view.findViewById(R.id.btnSininhoNotificacoes);
        if (btnSininhoNotificacoes != null) {
            btnSininhoNotificacoes.setOnClickListener(v -> NotificationHelper.exibirCentralNotificacoes(requireContext(), () -> atualizarSininho(view)));
        }

        carregarDadosProfessor();
    }

    private void carregarDadosProfessor() {
        if (getContext() == null) return;
        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());

        String emailAtual = UserSession.getUsuarioAtual(requireContext());
        String nome = sp.getString("nome", "");
        if (nome.isEmpty() || emailAtual.isEmpty() || "aluno@exemplo.com".equalsIgnoreCase(emailAtual) || "usuario_convidado".equalsIgnoreCase(emailAtual)) {
            nome = "Convidado";
        }

        String escola = sp.getString("escola", sp.getString("faculdade", "Universidade de São Paulo (USP)"));
        String fotoUri = sp.getString("foto_perfil_uri", "");

        if (txtNomeAluno != null) txtNomeAluno.setText(nome);
        if (txtBoasVindas != null) txtBoasVindas.setText("bem vindo(a), professor(a)!");
        if (txtNomeCompleto != null) txtNomeCompleto.setText(nome);
        if (txtIdade != null) txtIdade.setText("Cargo: Docente / Leitor");
        if (txtEscola != null) txtEscola.setText("Instituição: " + escola);

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

        renderizarDisciplinasProfessor(sp);
    }

    private void renderizarDisciplinasProfessor(SharedPreferences sp) {
        if (containerDisciplinasProfHome == null || getContext() == null) return;
        containerDisciplinasProfHome.removeAllViews();

        Set<String> setCursos = sp.getStringSet("lista_cursos_ids", new HashSet<>());

        if (setCursos.isEmpty()) {
            TextView txtVazio = new TextView(getContext());
            txtVazio.setText("Nenhuma disciplina cadastrada por você ainda. Clique no botão acima para publicar uma nova matéria!");
            txtVazio.setTextColor(requireContext().getColor(R.color.dark_gray));
            txtVazio.setTextSize(13);
            txtVazio.setPadding(12, 12, 12, 12);
            containerDisciplinasProfHome.addView(txtVazio);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (String idCurso : setCursos) {
            String nome = sp.getString("nomeCurso_" + idCurso, "");
            String faculdade = sp.getString("faculdadeCurso_" + idCurso, "USP");
            String dias = sp.getString("diasSemana_" + idCurso, "Seg, Qua, Sex (14h - 16h)");
            String prazoReserva = sp.getString("prazoReserva_" + idCurso, "Até 15/05/2026");

            if (nome.isEmpty()) continue;

            View profView = inflater.inflate(R.layout.item_curso_professor, containerDisciplinasProfHome, false);

            TextView txtNome = profView.findViewById(R.id.txtNomeCursoProf);
            TextView txtFacul = profView.findViewById(R.id.txtCursoFaculdadeProf);
            TextView txtStatusInscricao = profView.findViewById(R.id.txtStatusInscricaoProf);
            Button btnAlunos = profView.findViewById(R.id.btnVerAlunosTurmaProf);
            Button btnChamada = profView.findViewById(R.id.btnCursoQrProf);

            if (txtNome != null) txtNome.setText(nome);
            if (txtFacul != null) txtFacul.setText(faculdade + " • " + dias);
            if (txtStatusInscricao != null) txtStatusInscricao.setText("Reserva: " + prazoReserva);

            int qtdInscritos = AlunoCursosFragment.getQtdInscritosNoCurso(requireContext(), idCurso);

            if (btnAlunos != null) {
                btnAlunos.setText("👥 ALUNOS (" + qtdInscritos + ")");
            }

            if (btnChamada != null) {
                btnChamada.setOnClickListener(v -> AttendanceManager.exibirDialogGestaoPresencaProf(requireContext(), idCurso, nome, () -> renderizarDisciplinasProfessor(sp)));
            }

            // Ao clicar no cartão da disciplina, abre a sala de aula digital
            final String finalIdCurso = idCurso;
            profView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), TelaAulaCurso.class);
                intent.putExtra("cursoId", finalIdCurso);
                startActivity(intent);
            });

            containerDisciplinasProfHome.addView(profView);
        }
    }

    private void exibirDialogPostarCursoProf() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_postar_curso, null);

        EditText inputNome = dialogView.findViewById(R.id.inputNomeCurso);
        EditText inputFaculdade = dialogView.findViewById(R.id.inputFaculdadeCurso);
        EditText inputCarga = dialogView.findViewById(R.id.inputPrazoReservaCurso);
        EditText inputDias = dialogView.findViewById(R.id.inputDiasSemanaCurso);
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
                String dias = inputDias != null ? inputDias.getText().toString().trim() : "Seg, Qua e Sex (14h - 16h)";

                if (nome.isEmpty()) {
                    NotificationHelper.exibirToastCustomizado(getContext(), "Digite o nome da disciplina!");
                    return;
                }

                String idCurso = "curso_prof_" + System.currentTimeMillis();
                SharedPreferences sp = UserSession.getScopedPreferences(requireContext());

                Set<String> setCursos = new HashSet<>(sp.getStringSet("lista_cursos_ids", new HashSet<>()));
                setCursos.add(idCurso);

                sp.edit()
                        .putStringSet("lista_cursos_ids", setCursos)
                        .putString("nomeCurso_" + idCurso, nome)
                        .putString("faculdadeCurso_" + idCurso, faculdade)
                        .putString("cargaHoraria_" + idCurso, carga)
                        .putString("diasSemana_" + idCurso, dias)
                        .apply();

                // Salva também no banco global
                SharedPreferences spGeralCursos = requireContext().getSharedPreferences("ListaGeralCursosPlataforma", Context.MODE_PRIVATE);
                Set<String> setGlobal = new HashSet<>(spGeralCursos.getStringSet("lista_cursos_publicos_ids", new HashSet<>()));
                setGlobal.add(idCurso);
                spGeralCursos.edit()
                        .putStringSet("lista_cursos_publicos_ids", setGlobal)
                        .putString("nomeCurso_" + idCurso, nome)
                        .putString("faculdadeCurso_" + idCurso, faculdade)
                        .putString("cargaHoraria_" + idCurso, carga)
                        .putString("diasSemana_" + idCurso, dias)
                        .apply();

                NotificationHelper.adicionarNotificacao(
                        requireContext(),
                        "Novo Curso Publicado",
                        "Você publicou a matéria " + nome + " (" + faculdade + ")!"
                );

                NotificationHelper.exibirToastCustomizado(getContext(), "📢 Disciplina " + nome + " publicada com sucesso!");
                renderizarDisciplinasProfessor(sp);
                dialog.dismiss();
            });
        }

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
        carregarDadosProfessor();
    }
}
