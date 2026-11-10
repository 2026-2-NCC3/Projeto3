package com.example.errornull_proximaetapa.administrador;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.UserSession;
import com.example.errornull_proximaetapa.auth.LoginInfo;
import com.example.errornull_proximaetapa.auth.NotificationHelper;
import com.example.errornull_proximaetapa.auth.SessionManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class AdmConfigFragment extends Fragment {

    private ImageView imgFotoAluno;
    private TextView txtNomeAluno;
    private View btnConfigConta, btnConfigSessaoMultiperfil, btnPrivacidadeAcessibilidade, btnSairPerfil;
    private View btnSininhoNotificacoes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adm_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgFotoAluno = view.findViewById(R.id.imgFotoAluno);
        txtNomeAluno = view.findViewById(R.id.txtNomeAluno);

        btnConfigConta = view.findViewById(R.id.btnConfigConta);
        btnConfigSessaoMultiperfil = view.findViewById(R.id.btnConfigSessaoMultiperfil);
        btnPrivacidadeAcessibilidade = view.findViewById(R.id.btnPrivacidadeAcessibilidade);
        btnSairPerfil = view.findViewById(R.id.btnSairPerfil);

        btnSininhoNotificacoes = view.findViewById(R.id.btnSininhoNotificacoes);
        if (btnSininhoNotificacoes != null) {
            btnSininhoNotificacoes.setOnClickListener(v -> NotificationHelper.exibirCentralNotificacoes(requireContext(), () -> atualizarSininho(view)));
        }

        if (btnConfigConta != null) {
            btnConfigConta.setOnClickListener(v -> exibirDialogConfigConta(view));
        }

        if (btnConfigSessaoMultiperfil != null) {
            btnConfigSessaoMultiperfil.setOnClickListener(v -> exibirDialogConfigSessao());
        }

        if (btnPrivacidadeAcessibilidade != null) {
            btnPrivacidadeAcessibilidade.setOnClickListener(v -> exibirDialogPrivacidadeAcessibilidade(view));
        }

        if (btnSairPerfil != null) {
            btnSairPerfil.setOnClickListener(v -> confirmarSairPerfil());
        }

        carregarDadosUsuario(view);
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

    private void carregarDadosUsuario(View view) {
        if (getContext() == null) return;
        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());

        String emailAtual = UserSession.getUsuarioAtual(requireContext());
        String nome = sp.getString("nome", "");
        if (nome.isEmpty() || emailAtual.isEmpty() || "aluno@exemplo.com".equalsIgnoreCase(emailAtual) || "usuario_convidado".equalsIgnoreCase(emailAtual)) {
            nome = "Convidado";
        }

        String fotoUri = sp.getString("foto_perfil_uri", "");

        if (txtNomeAluno != null) txtNomeAluno.setText(nome);

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

    private void exibirDialogConfigSessao() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_config_sessao, null);

        TextView txtEmail = dialogView.findViewById(R.id.txtEmailSessaoConfig);
        TextView txtRole = dialogView.findViewById(R.id.txtRoleSessaoConfig);
        TextView txtToken = dialogView.findViewById(R.id.txtTokenSessaoConfig);

        Button btnProf = dialogView.findViewById(R.id.btnTrocarRoleProf);
        Button btnAdmin = dialogView.findViewById(R.id.btnTrocarRoleAdmin);
        Button btnFechar = dialogView.findViewById(R.id.btnFecharSessaoConfig);

        String email = SessionManager.getUsuarioEmail(requireContext());
        String role = SessionManager.getRole(requireContext());
        String token = SessionManager.getToken(requireContext());

        if (txtEmail != null) txtEmail.setText("E-mail Conectado: " + email);
        if (txtRole != null) txtRole.setText("Modo Ativo: " + ("admin".equalsIgnoreCase(role) ? "ADMINISTRADOR" : "PROFESSOR"));
        if (txtToken != null) {
            String tokenShort = (token != null && token.length() > 20) ? token.substring(0, 20) + "..." : (token != null ? token : "Sem token");
            txtToken.setText("Token HTTP Bearer: " + tokenShort);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnFechar != null) btnFechar.setOnClickListener(v -> dialog.dismiss());

        View.OnClickListener trocarRoleListener = v -> {
            int id = v.getId();
            String novaRole = "professor";
            if (id == R.id.btnTrocarRoleAdmin) novaRole = "admin";

            SessionManager.saveUserSession(requireContext(), email, novaRole, token);
            NotificationHelper.exibirToastCustomizado(getContext(), "🔄 Modo de atuação alterado para " + ("admin".equalsIgnoreCase(novaRole) ? "ADMINISTRADOR" : "PROFESSOR") + "!");
            dialog.dismiss();

            if (getActivity() != null) {
                getActivity().recreate();
            }
        };

        if (btnProf != null) btnProf.setOnClickListener(trocarRoleListener);
        if (btnAdmin != null) btnAdmin.setOnClickListener(trocarRoleListener);

        dialog.show();
    }

    private void exibirDialogConfigConta(View mainView) {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_config_conta, null);

        EditText inputEmail = dialogView.findViewById(R.id.inputEmailConfig);
        EditText inputNovaSenha = dialogView.findViewById(R.id.inputNovaSenhaConfig);
        Button btnSalvar = dialogView.findViewById(R.id.btnSalvarConfigConta);

        String currentEmail = UserSession.getCurrentUserEmail(requireContext());
        if (inputEmail != null) inputEmail.setText(currentEmail);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnSalvar != null) {
            btnSalvar.setOnClickListener(v -> {
                String novoEmail = inputEmail != null ? inputEmail.getText().toString().trim() : "";
                String novaSenha = inputNovaSenha != null ? inputNovaSenha.getText().toString().trim() : "";

                if (novoEmail.isEmpty()) {
                    NotificationHelper.exibirToastCustomizado(getContext(), "Digite um e-mail válido!");
                    return;
                }

                SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("email", novoEmail);
                if (!novaSenha.isEmpty()) {
                    editor.putString("senha", novaSenha);
                }
                editor.apply();

                NotificationHelper.adicionarNotificacao(requireContext(), "Conta Atualizada", "Seus dados de acesso foram salvos com sucesso.");
                NotificationHelper.exibirToastCustomizado(getContext(), "Configurações da conta salvas!");
                atualizarSininho(mainView);
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void exibirDialogPrivacidadeAcessibilidade(View mainView) {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_config_privacidade, null);

        SwitchMaterial switchTemaEscuro = dialogView.findViewById(R.id.switchTemaEscuro);
        SwitchMaterial switchNotificacoes = dialogView.findViewById(R.id.switchNotificacoes);
        SwitchMaterial switchAltoContraste = dialogView.findViewById(R.id.switchAltoContraste);
        Button btnSalvar = dialogView.findViewById(R.id.btnSalvarConfigPrivacidade);

        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
        boolean temaEscuroAntigo = sp.getBoolean("config_tema_escuro", false);

        if (switchTemaEscuro != null) switchTemaEscuro.setChecked(temaEscuroAntigo);
        if (switchNotificacoes != null) switchNotificacoes.setChecked(sp.getBoolean("config_notificacoes_som", true));
        if (switchAltoContraste != null) switchAltoContraste.setChecked(sp.getBoolean("config_alto_contraste", false));

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnSalvar != null) {
            btnSalvar.setOnClickListener(v -> {
                try {
                    boolean temaEscuroNovo = switchTemaEscuro != null && switchTemaEscuro.isChecked();
                    boolean notifs = switchNotificacoes != null && switchNotificacoes.isChecked();
                    boolean contraste = switchAltoContraste != null && switchAltoContraste.isChecked();

                    sp.edit()
                            .putBoolean("config_tema_escuro", temaEscuroNovo)
                            .putBoolean("config_notificacoes_som", notifs)
                            .putBoolean("config_alto_contraste", contraste)
                            .apply();

                    AppCompatDelegate.setDefaultNightMode(
                            temaEscuroNovo ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                    );

                    if (notifs) {
                        NotificationHelper.adicionarNotificacao(
                                requireContext(),
                                "Preferências Salvas",
                                "Configurações de Privacidade e Acessibilidade salvas com sucesso!"
                        );
                        NotificationHelper.exibirToastCustomizado(getContext(), "Preferências salvas com sucesso!");
                    } else {
                        Toast.makeText(getContext(), "Preferências salvas com sucesso!", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();

                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
    }

    private void confirmarSairPerfil() {
        if (getContext() == null) return;

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Sair da Conta")
                .setMessage("Deseja realmente sair da sua conta?")
                .setPositiveButton("Sair", (d, which) -> {
                    UserSession.logout(requireContext());
                    Intent intent = new Intent(getActivity(), LoginInfo.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    if (getActivity() != null) getActivity().finish();
                })
                .setNegativeButton("Cancelar", null)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            carregarDadosUsuario(getView());
        }
    }
}
