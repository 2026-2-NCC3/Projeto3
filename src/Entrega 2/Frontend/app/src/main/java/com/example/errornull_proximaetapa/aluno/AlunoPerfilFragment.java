package com.example.errornull_proximaetapa.aluno;

import android.content.Context;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.errornull_proximaetapa.CardImageSaver;
import com.example.errornull_proximaetapa.auth.NotificationHelper;
import com.example.errornull_proximaetapa.curso.PerfilFaculdadeActivity;
import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.UserSession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlunoPerfilFragment extends Fragment {

    private ImageView imgFotoPerfil;
    private TextView txtNomePerfil, txtTipoPerfil, txtTituloEquipadoPerfil;
    private TextView txtIdadePerfil, txtFaculdadePerfil;
    private TextView txtBioPerfil, txtPreferenciasPerfil;
    private View containerFotoPerfil, btnAlterarFoto;
    private Button btnVerCardPerfil;
    private LinearLayout containerCertificadosPerfil;
    private View btnSininhoNotificacoes;

    // Botões do Menu de Atalhos do Estudante
    private Button btnMenuCertificados, btnMenuReservas, btnMenuCursos, btnMenuTestes, btnMenuEditarPerfil;

    private View headerFaculdadesSeguidasPerfil, headerCertificadosPerfil;
    private LinearLayout containerListaFaculdadesSeguidasPerfil;

    private String avatarSelecionadoTemp = "preset:round_person";

    // Launcher para trocar a foto diretamente no Perfil
    private final ActivityResultLauncher<String> changeImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try {
                        requireContext().getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                    } catch (Exception ignored) {}

                    if (imgFotoPerfil != null) {
                        imgFotoPerfil.setImageURI(uri);
                    }

                    if (getContext() != null) {
                        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
                        sp.edit().putString("foto_perfil_uri", uri.toString()).apply();
                        NotificationHelper.adicionarNotificacao(requireContext(), "Perfil Atualizado", "Sua foto de perfil foi atualizada com sucesso!");
                        if (getView() != null) atualizarSininho(getView());
                    }

                    Toast.makeText(getContext(), "Foto de perfil atualizada!", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aluno_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgFotoPerfil = view.findViewById(R.id.imgFotoAluno);
        txtNomePerfil = view.findViewById(R.id.txtNomeCompleto);
        txtTipoPerfil = view.findViewById(R.id.txtNomeAluno);
        txtTituloEquipadoPerfil = view.findViewById(R.id.txtBoasVindas);
        txtIdadePerfil = view.findViewById(R.id.txtIdade);
        txtFaculdadePerfil = view.findViewById(R.id.txtEscola);
        txtBioPerfil = view.findViewById(R.id.txtBioPerfil);
        txtPreferenciasPerfil = view.findViewById(R.id.txtPreferenciasPerfil);
        containerFotoPerfil = view.findViewById(R.id.containerFotoPerfil);
        btnAlterarFoto = view.findViewById(R.id.btnAlterarFoto);
        btnVerCardPerfil = view.findViewById(R.id.btnVerCardHome);
        containerCertificadosPerfil = view.findViewById(R.id.containerCertificadosPerfil);

        // Bindings do Menu de Atalhos do Estudante
        View opcCertific = view.findViewById(R.id.opcCertificados);
        View opcReserv = view.findViewById(R.id.opcReservas);
        View opcCur = view.findViewById(R.id.opcCursos);
        View opcTes = view.findViewById(R.id.opcTestes);
        View opcEdit = view.findViewById(R.id.opcEditPerfil);

        View.OnClickListener certifsListener = v -> exibirDialogCertificadosMenu();
        View.OnClickListener reservsListener = v -> exibirDialogMinhasReservasMenu();
        View.OnClickListener cursosListener = v -> exibirDialogMeusCursosMenu();
        View.OnClickListener testesListener = v -> exibirDialogMeusTestesMenu();
        View.OnClickListener editPerfilListener = v -> exibirDialogEditarPerfilAluno();

        if (opcCertific != null) opcCertific.setOnClickListener(certifsListener);
        if (opcReserv != null) opcReserv.setOnClickListener(reservsListener);
        if (opcCur != null) opcCur.setOnClickListener(cursosListener);
        if (opcTes != null) opcTes.setOnClickListener(testesListener);
        if (opcEdit != null) opcEdit.setOnClickListener(editPerfilListener);

        View txtCertific = view.findViewById(R.id.txtCerfificados);
        View txtReserv = view.findViewById(R.id.txtReservas);
        View txtCur = view.findViewById(R.id.txtMeusCursos);
        View txtTes = view.findViewById(R.id.txtMeusTestes);
        View txtEd = view.findViewById(R.id.txtEditPerfil);

        if (txtCertific != null) txtCertific.setOnClickListener(certifsListener);
        if (txtReserv != null) txtReserv.setOnClickListener(reservsListener);
        if (txtCur != null) txtCur.setOnClickListener(cursosListener);
        if (txtTes != null) txtTes.setOnClickListener(testesListener);
        if (txtEd != null) txtEd.setOnClickListener(editPerfilListener);


        headerFaculdadesSeguidasPerfil = view.findViewById(R.id.headerFaculdadesSeguidasPerfil);
        headerCertificadosPerfil = view.findViewById(R.id.headerCertificadosPerfil);
        containerListaFaculdadesSeguidasPerfil = view.findViewById(R.id.containerListaFaculdadesSeguidasPerfil);

        btnSininhoNotificacoes = view.findViewById(R.id.btnSininhoNotificacoes);
        if (btnSininhoNotificacoes != null) {
            btnSininhoNotificacoes.setOnClickListener(v -> NotificationHelper.exibirCentralNotificacoes(requireContext(), () -> atualizarSininho(view)));
        }

        View.OnClickListener listenerFoto = v -> exibirDialogEditarPerfilAluno();
        if (containerFotoPerfil != null) containerFotoPerfil.setOnClickListener(listenerFoto);
        if (btnAlterarFoto != null) btnAlterarFoto.setOnClickListener(listenerFoto);

        if (btnVerCardPerfil != null) {
            btnVerCardPerfil.setOnClickListener(v -> CardImageSaver.exibirCardEstudanteDialog(requireContext()));
        }


        carregarDadosPerfil(view);
    }

    private void exibirDialogCertificadosMenu() {
        if (getContext() == null) return;
        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
        Set<String> setCursos = sp.getStringSet("lista_cursos_ids", new HashSet<>());

        if (setCursos.isEmpty()) {
            NotificationHelper.exibirToastCustomizado(getContext(), "Você ainda não possui certificados concluídos.");
            return;
        }

        String cursoExemplo = setCursos.iterator().next();
        String nomeCurso = sp.getString("nomeCurso_" + cursoExemplo, "Desenvolvimento Web & TI");
        String faculdade = sp.getString("faculdadeCurso_" + cursoExemplo, "USP");
        CardImageSaver.exibirCertificadoDialog(requireContext(), nomeCurso, faculdade);
    }

    private void exibirDialogMinhasReservasMenu() {
        if (getContext() == null) return;

        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
        Set<String> setCursos = sp.getStringSet("lista_cursos_ids", new HashSet<>());

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(requireContext());
        title.setText("📖 Minhas Reservas de Cursos");
        title.setTextColor(requireContext().getColor(R.color.dark_indigo));
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        layout.addView(title);

        if (setCursos.isEmpty()) {
            TextView txtVazio = new TextView(requireContext());
            txtVazio.setText("Nenhuma reserva de curso ativa no momento.");
            txtVazio.setTextColor(requireContext().getColor(R.color.dark_gray));
            txtVazio.setTextSize(13);
            txtVazio.setPadding(0, 16, 0, 16);
            layout.addView(txtVazio);
        } else {
            for (String idC : setCursos) {
                String nome = sp.getString("nomeCurso_" + idC, "Curso");
                String faculdade = sp.getString("faculdadeCurso_" + idC, "USP");
                String prazo = sp.getString("prazoReserva_" + idC, "Até 15/05/2026");

                TextView itemR = new TextView(requireContext());
                itemR.setText("📖 " + nome + " (" + faculdade + ")\n• Período de Reserva: " + prazo + "\nStatus: Vaga Garantida ✓");
                itemR.setTextColor(requireContext().getColor(R.color.dark_indigo));
                itemR.setTextSize(13);
                itemR.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
                itemR.setPadding(16, 12, 16, 12);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 8, 0, 8);
                itemR.setLayoutParams(lp);

                layout.addView(itemR);
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

    private void exibirDialogMeusCursosMenu() {
        if (getContext() == null) return;

        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
        Set<String> setCursos = sp.getStringSet("lista_cursos_ids", new HashSet<>());

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(requireContext());
        title.setText("📚 Meus Cursos (Concluídos e Em Progresso)");
        title.setTextColor(requireContext().getColor(R.color.dark_indigo));
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        layout.addView(title);

        if (setCursos.isEmpty()) {
            TextView txtVazio = new TextView(requireContext());
            txtVazio.setText("Você não está matriculado em nenhum curso ainda.");
            txtVazio.setTextColor(requireContext().getColor(R.color.dark_gray));
            txtVazio.setTextSize(13);
            txtVazio.setPadding(0, 16, 0, 16);
            layout.addView(txtVazio);
        } else {
            for (String idC : setCursos) {
                String nome = sp.getString("nomeCurso_" + idC, "Curso");
                String faculdade = sp.getString("faculdadeCurso_" + idC, "USP");
                int progresso = sp.getInt("progressoCurso_" + idC, 0);

                boolean concluido = progresso >= 100;

                TextView itemC = new TextView(requireContext());
                itemC.setText((concluido ? "🎓 [CONCLUÍDO] " : "⏳ [EM PROGRESSO " + progresso + "%] ") + nome + " (" + faculdade + ")");
                itemC.setTextColor(requireContext().getColor(concluido ? R.color.mint : R.color.dark_indigo));
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

    private void exibirDialogMeusTestesMenu() {
        if (getContext() == null) return;

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        layout.setPadding(40, 40, 40, 40);

        TextView title = new TextView(requireContext());
        title.setText("🧠 Meus Testes & Questionários Externos");
        title.setTextColor(requireContext().getColor(R.color.purple));
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        layout.addView(title);

        TextView t1 = new TextView(requireContext());
        t1.setText("📝 Questionário Vocacional - Lógica & TI (Concluído)");
        t1.setTextColor(requireContext().getColor(R.color.dark_indigo));
        t1.setTextSize(13);
        t1.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        t1.setPadding(16, 12, 16, 12);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.setMargins(0, 12, 0, 8);
        t1.setLayoutParams(lp1);

        TextView t2 = new TextView(requireContext());
        t2.setText("📝 Teste de Aptidão Acadêmica (Pendente)");
        t2.setTextColor(requireContext().getColor(R.color.dark_indigo));
        t2.setTextSize(13);
        t2.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        t2.setPadding(16, 12, 16, 12);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.setMargins(0, 0, 0, 8);
        t2.setLayoutParams(lp2);

        layout.addView(t1);
        layout.addView(t2);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(layout)
                .setPositiveButton("Fechar", null)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

    private void exibirDialogEditarPerfilAluno() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_perfil_aluno, null);

        ImageView imgPreview = dialogView.findViewById(R.id.imgAvatarPreviewEditarDialog);
        Button btnEnviarGaleria = dialogView.findViewById(R.id.btnEnviarFotoGaleriaEditarDialog);

        ImageView opt1 = dialogView.findViewById(R.id.avatarOpt1Editar);
        ImageView opt2 = dialogView.findViewById(R.id.avatarOpt2Editar);
        ImageView opt3 = dialogView.findViewById(R.id.avatarOpt3Editar);

        EditText inputNome = dialogView.findViewById(R.id.inputNomeEditar);
        EditText inputIdade = dialogView.findViewById(R.id.inputIdadeEditar);
        EditText inputBio = dialogView.findViewById(R.id.inputBioEditar);
        EditText inputPreferencias = dialogView.findViewById(R.id.inputPreferenciasEditar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarEditarPerfil);
        Button btnSalvar = dialogView.findViewById(R.id.btnSalvarEditarPerfil);

        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());

        String fotoUriAtual = sp.getString("foto_perfil_uri", "preset:round_person");
        avatarSelecionadoTemp = fotoUriAtual;
        carregarImagemElemento(imgPreview, fotoUriAtual);

        if (inputNome != null) inputNome.setText(sp.getString("nome", ""));
        if (inputIdade != null) inputIdade.setText(sp.getString("idade", "17 anos"));
        if (inputBio != null) inputBio.setText(sp.getString("bio", ""));
        if (inputPreferencias != null) inputPreferencias.setText(sp.getString("preferencias", ""));

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnEnviarGaleria != null) {
            btnEnviarGaleria.setOnClickListener(v -> {
                changeImageLauncher.launch("image/*");
                dialog.dismiss();
            });
        }

        if (opt1 != null) {
            opt1.setOnClickListener(v -> {
                avatarSelecionadoTemp = "preset:round_person";
                if (imgPreview != null) imgPreview.setImageResource(R.drawable.round_person);
            });
        }

        if (opt2 != null) {
            opt2.setOnClickListener(v -> {
                avatarSelecionadoTemp = "preset:baseline_account";
                if (imgPreview != null) imgPreview.setImageResource(R.drawable.baseline_account);
            });
        }

        if (opt3 != null) {
            opt3.setOnClickListener(v -> {
                avatarSelecionadoTemp = "preset:logo_proxima_etapa";
                if (imgPreview != null) imgPreview.setImageResource(R.drawable.logo_proxima_etapa);
            });
        }

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> dialog.dismiss());
        }

        if (btnSalvar != null) {
            btnSalvar.setOnClickListener(v -> {
                String novoNome = inputNome != null ? inputNome.getText().toString().trim() : "";
                String novaIdade = inputIdade != null ? inputIdade.getText().toString().trim() : "17 anos";
                String novaBio = inputBio != null ? inputBio.getText().toString().trim() : "";
                String novasPreferencias = inputPreferencias != null ? inputPreferencias.getText().toString().trim() : "";

                if (novoNome.isEmpty()) {
                    NotificationHelper.exibirToastCustomizado(getContext(), "Digite seu nome!");
                    return;
                }

                sp.edit()
                        .putString("nome", novoNome)
                        .putString("idade", novaIdade)
                        .putString("bio", novaBio)
                        .putString("preferencias", novasPreferencias)
                        .putString("foto_perfil_uri", avatarSelecionadoTemp)
                        .apply();

                NotificationHelper.exibirToastCustomizado(getContext(), "Perfil e foto atualizados!");
                if (getView() != null) carregarDadosPerfil(getView());
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void aplicarFotoPreset(String presetKey, int drawableResId) {
        if (imgFotoPerfil != null) {
            imgFotoPerfil.setImageResource(drawableResId);
        }
        if (getContext() != null) {
            SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
            sp.edit().putString("foto_perfil_uri", presetKey).apply();
            NotificationHelper.adicionarNotificacao(requireContext(), "Perfil Atualizado", "Avatar do perfil alterado.");
            if (getView() != null) atualizarSininho(getView());
        }
        Toast.makeText(getContext(), "Avatar de perfil atualizado!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            carregarDadosPerfil(getView());
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

        String tipoUsuario = sp.getString("tipoUsuario", "aluno");
        String idade = sp.getString("idade", "17 anos");
        String pretensaoFaculdade = sp.getString("pretensaoFaculdade", "Ciência da Computação / USP");
        String bio = sp.getString("bio", "Nenhuma biografia informada ainda.");
        String preferencias = sp.getString("preferencias", "Nenhuma preferência cadastrada.");
        String fotoUri = sp.getString("foto_perfil_uri", "");

        int countCursosInscritos = sp.getInt("cursosInscritosCount", 0);

        if (txtNomePerfil != null) txtNomePerfil.setText(nome);
        if (txtTituloEquipadoPerfil != null) txtTituloEquipadoPerfil.setVisibility(View.GONE);

        // Ajuste das Informações do Perfil conforme o papel (Aluno vs Professor vs ADM)
        if ("admin".equalsIgnoreCase(tipoUsuario)) {
            if (txtTipoPerfil != null) {

            }
            if (txtTituloEquipadoPerfil != null) txtTituloEquipadoPerfil.setVisibility(View.GONE);
            if (txtIdadePerfil != null) txtIdadePerfil.setText("Cargo: Administrador Geral da Plataforma");
            if (txtFaculdadePerfil != null) txtFaculdadePerfil.setVisibility(View.GONE);
            if (btnVerCardPerfil != null) btnVerCardPerfil.setVisibility(View.GONE);
            if (headerCertificadosPerfil != null) headerCertificadosPerfil.setVisibility(View.GONE);
            if (containerCertificadosPerfil != null) containerCertificadosPerfil.setVisibility(View.GONE);

            if (headerFaculdadesSeguidasPerfil != null) headerFaculdadesSeguidasPerfil.setVisibility(View.GONE);
            if (containerListaFaculdadesSeguidasPerfil != null) containerListaFaculdadesSeguidasPerfil.setVisibility(View.GONE);

        } else if ("professor".equalsIgnoreCase(tipoUsuario)) {
            if (txtTipoPerfil != null) {
                txtTipoPerfil.setText("👨‍🏫 ÁREA DO PROFESSOR");
                txtTipoPerfil.setBackgroundResource(R.drawable.bg_pill_green);
                txtTipoPerfil.setTextColor(requireContext().getColor(R.color.white));
            }
            if (txtTituloEquipadoPerfil != null) txtTituloEquipadoPerfil.setVisibility(View.GONE);
            if (txtIdadePerfil != null) txtIdadePerfil.setText("Cargo: Docente / Gestor Acadêmico");
            if (txtFaculdadePerfil != null) txtFaculdadePerfil.setVisibility(View.GONE);
            if (btnVerCardPerfil != null) btnVerCardPerfil.setVisibility(View.GONE);
            if (headerCertificadosPerfil != null) headerCertificadosPerfil.setVisibility(View.GONE);
            if (containerCertificadosPerfil != null) containerCertificadosPerfil.setVisibility(View.GONE);

            if (headerFaculdadesSeguidasPerfil != null) headerFaculdadesSeguidasPerfil.setVisibility(View.GONE);
            if (containerListaFaculdadesSeguidasPerfil != null) containerListaFaculdadesSeguidasPerfil.setVisibility(View.GONE);

        } else {
            // ALUNO EXCLUSIVO COM SISTEMA DE GAMIFICAÇÃO & CONQUISTAS
            if (txtTipoPerfil != null) {
                txtTipoPerfil.setText("🎓 ÁREA DO ALUNO");
                txtTipoPerfil.setBackgroundResource(R.drawable.bg_pill_yellow);
                txtTipoPerfil.setTextColor(requireContext().getColor(R.color.dark_indigo));
            }

            // Exibe o Título Equipado pelo Aluno no Topo do Perfil

            if (txtIdadePerfil != null) txtIdadePerfil.setText("Idade: " + idade);

            if (txtFaculdadePerfil != null) {
                txtFaculdadePerfil.setVisibility(View.VISIBLE);
                String textoInscricoes = countCursosInscritos > 0
                        ? "Cursos Cadastrados/Inscritos: " + countCursosInscritos + " curso(s)"
                        : "Pretensão / Meta: " + pretensaoFaculdade;
                txtFaculdadePerfil.setText(textoInscricoes);
            }

            if (btnVerCardPerfil != null) {
                btnVerCardPerfil.setText("✏️ Editar Informações do Perfil");
                btnVerCardPerfil.setVisibility(View.VISIBLE);
            }

            if (headerCertificadosPerfil != null) headerCertificadosPerfil.setVisibility(View.VISIBLE);
            if (containerCertificadosPerfil != null) containerCertificadosPerfil.setVisibility(View.VISIBLE);

            if (headerFaculdadesSeguidasPerfil != null) headerFaculdadesSeguidasPerfil.setVisibility(View.VISIBLE);
            if (containerListaFaculdadesSeguidasPerfil != null) containerListaFaculdadesSeguidasPerfil.setVisibility(View.VISIBLE);

            renderizarFaculdadesSeguidas(sp);
        }

        if (txtBioPerfil != null) txtBioPerfil.setText(bio.isEmpty() ? "Nenhuma biografia informada." : bio);
        if (txtPreferenciasPerfil != null) txtPreferenciasPerfil.setText(preferencias.isEmpty() ? "Nenhuma preferência cadastrada." : preferencias);

        carregarImagemElemento(imgFotoPerfil, fotoUri);

        if ("aluno".equalsIgnoreCase(tipoUsuario)) {
            renderizarCertificadosPerfil(sp);
        }

        atualizarSininho(view);
    }

    private void renderizarFaculdadesSeguidas(SharedPreferences sp) {
        if (containerListaFaculdadesSeguidasPerfil == null || getContext() == null) return;
        containerListaFaculdadesSeguidasPerfil.removeAllViews();

        SharedPreferences spGlobal = requireContext().getSharedPreferences("ProximaEtapaPrefs", Context.MODE_PRIVATE);

        boolean segueUsp = sp.getBoolean("seguindo_USP", spGlobal.getBoolean("seguindo_USP", false));
        boolean segueUnicamp = sp.getBoolean("seguindo_UNICAMP", spGlobal.getBoolean("seguindo_UNICAMP", false));
        boolean segueUnesp = sp.getBoolean("seguindo_UNESP", spGlobal.getBoolean("seguindo_UNESP", false));

        int cont = 0;

        if (segueUsp) {
            cont++;
            adicionarCardFaculdadeSeguida("USP", "USP - Universidade de São Paulo", "Líder em ensino e pesquisa", R.drawable.proximaetapa1);
        }

        if (segueUnicamp) {
            cont++;
            adicionarCardFaculdadeSeguida("UNICAMP", "UNICAMP - Universidade Estadual de Campinas", "Referência em tecnologia e inovação", R.drawable.proximaetapa2);
        }

        if (segueUnesp) {
            cont++;
            adicionarCardFaculdadeSeguida("UNESP", "UNESP - Universidade Estadual Paulista", "Excelência acadêmica em todo o estado de SP", R.drawable.proximaetapa3);
        }

        SharedPreferences spGeralFacul = requireContext().getSharedPreferences("ListaGeralFaculdadesPlataforma", Context.MODE_PRIVATE);
        String savedFaculs = spGeralFacul.getString("lista_faculdades_cadastradas_string", "");
        if (!savedFaculs.isEmpty()) {
            String[] split = savedFaculs.split("###");
            for (String fStr : split) {
                String[] p = fStr.split(";;;");
                String sigla = p.length > 0 ? p[0] : "";
                String nomeComp = p.length > 1 ? p[1] : sigla;
                String desc = p.length > 2 ? p[2] : "";
                int imgResId = R.drawable.proximaetapa1;
                if (p.length > 3) {
                    try { imgResId = Integer.parseInt(p[3]); } catch (Exception ignored) {}
                }

                if (!sigla.isEmpty() && (sp.getBoolean("seguindo_" + sigla, false) || spGlobal.getBoolean("seguindo_" + sigla, false))) {
                    cont++;
                    adicionarCardFaculdadeSeguida(sigla, nomeComp, desc, imgResId);
                }
            }
        }

        if (cont == 0) {
            TextView txtVazio = new TextView(getContext());
            txtVazio.setText("Você ainda não segue nenhuma faculdade. Acesse a aba de Cursos para seguir suas faculdades preferidas!");
            txtVazio.setTextColor(requireContext().getColor(R.color.dark_gray));
            txtVazio.setTextSize(12);
            txtVazio.setPadding(8, 8, 8, 8);
            containerListaFaculdadesSeguidasPerfil.addView(txtVazio);
        }
    }

    private void adicionarCardFaculdadeSeguida(String sigla, String nomeCompleto, String desc, int caparesId) {
        if (getContext() == null || containerListaFaculdadesSeguidasPerfil == null) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View cardView = inflater.inflate(R.layout.item_faculdade_seguida_perfil, containerListaFaculdadesSeguidasPerfil, false);

        TextView txtNome = cardView.findViewById(R.id.txtNomeFaculdadeSeguidaPerfil);
        TextView txtDesc = cardView.findViewById(R.id.txtDescFaculdadeSeguidaPerfil);
        Button btnVerPerfil = cardView.findViewById(R.id.btnVerPerfilFaculdadeItem);

        if (txtNome != null) txtNome.setText(nomeCompleto);
        if (txtDesc != null) txtDesc.setText("Parceira Oficial & Próxima Etapa");

        if (btnVerPerfil != null) {
            btnVerPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), PerfilFaculdadeActivity.class);
                intent.putExtra("sigla", sigla);
                intent.putExtra("nomeCompleto", nomeCompleto);
                intent.putExtra("descricao", desc);
                intent.putExtra("capaResId", caparesId);
                startActivity(intent);
            });
        }

        containerListaFaculdadesSeguidasPerfil.addView(cardView);
    }

    private void renderizarCertificadosPerfil(SharedPreferences sp) {
        if (containerCertificadosPerfil == null || getContext() == null) return;

        containerCertificadosPerfil.removeAllViews();

        Set<String> setCursosOriginal = sp.getStringSet("lista_cursos_ids", new HashSet<>());
        Set<String> setCursos = new HashSet<>(setCursosOriginal);

        if (setCursos.isEmpty()) {
            TextView txtVazio = new TextView(getContext());
            txtVazio.setText("Nenhum curso concluído ou certificado disponível ainda.");
            txtVazio.setTextColor(requireContext().getColor(R.color.dark_gray));
            txtVazio.setTextSize(12);
            txtVazio.setPadding(8, 8, 8, 8);
            containerCertificadosPerfil.addView(txtVazio);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (String idCurso : setCursos) {
            String nomeCurso = sp.getString("nomeCurso_" + idCurso, "");
            String faculdade = sp.getString("faculdadeCurso_" + idCurso, "USP");

            if (nomeCurso.isEmpty()) continue;

            View certView = inflater.inflate(R.layout.item_certificado_aluno, containerCertificadosPerfil, false);

            TextView txtCursoCert = certView.findViewById(R.id.txtNomeCursoCertificado);
            TextView txtFaculCert = certView.findViewById(R.id.txtFaculdadeCertificado);
            Button btnVerCert = certView.findViewById(R.id.btnVerCertificadoItem);

            if (txtCursoCert != null) txtCursoCert.setText(nomeCurso);
            if (txtFaculCert != null) txtFaculCert.setText("Realização: " + faculdade + " & Próxima Etapa");

            if (btnVerCert != null) {
                btnVerCert.setOnClickListener(v -> CardImageSaver.exibirCertificadoDialog(requireContext(), nomeCurso, faculdade));
            }

            containerCertificadosPerfil.addView(certView);
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
}