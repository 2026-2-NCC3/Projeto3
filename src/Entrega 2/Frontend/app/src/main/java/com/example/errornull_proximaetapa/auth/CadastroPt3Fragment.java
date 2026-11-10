package com.example.errornull_proximaetapa.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.UserSession;

public class CadastroPt3Fragment extends Fragment {

    private LinearLayout layoutEscolarAluno, layoutEscolarProfessor, layoutEscolarAdmin;
    private View btnEscolherFoto, btnEscolherFotoProf, btnEscolherFotoAdm;
    private ImageView imgPerfilPreview, imgPerfilPreviewProf, imgPerfilPreviewAdm;
    private EditText inputBio, inputPreferencias, inputBioProf;
    private Button btnVoltarEtapa3, btnCadastrar;

    private CadastroViewModel viewModel;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try {
                        requireContext().getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                    } catch (Exception ignored) {}

                    if (imgPerfilPreview != null) {
                        imgPerfilPreview.setImageURI(uri);
                    }
                    if (imgPerfilPreviewProf != null) {
                        imgPerfilPreviewProf.setImageURI(uri);
                    }
                    if (imgPerfilPreviewAdm != null) {
                        imgPerfilPreviewAdm.setImageURI(uri);
                    }

                    if (viewModel != null) {
                        viewModel.setFotoUri(uri.toString());
                    }

                    if (getContext() != null) {
                        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
                        sp.edit().putString("foto_perfil_uri", uri.toString()).apply();
                    }

                    Toast.makeText(getContext(), "Foto da galeria selecionada!", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cadastro_pt3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            viewModel = new ViewModelProvider(requireActivity()).get(CadastroViewModel.class);
            ((Cadastro) getActivity()).bloquearTabs(true);
        }

        // Sub-layouts
        layoutEscolarAluno = view.findViewById(R.id.layout_campos_aluno3);
        layoutEscolarProfessor = view.findViewById(R.id.layout_campos_professor3);
        layoutEscolarAdmin = view.findViewById(R.id.layout_campos_admin3);

        // Aluno
        inputBio = view.findViewById(R.id.input_bio);
        imgPerfilPreview = view.findViewById(R.id.imgPerfilPreview);
        inputPreferencias = view.findViewById(R.id.input_preferencias);
        btnEscolherFoto = view.findViewById(R.id.btnEscolherFoto);

        // Professor
        inputBioProf = view.findViewById(R.id.input_bioProf);
        imgPerfilPreviewProf = view.findViewById(R.id.imgPerfilPreview2);
        btnEscolherFotoProf = view.findViewById(R.id.btnEscolherFotoProf);

        // Adm
        imgPerfilPreviewAdm = view.findViewById(R.id.imgPerfilPreview3);
        btnEscolherFotoAdm = view.findViewById(R.id.btnEscolherFotoAdm);

        btnVoltarEtapa3 = view.findViewById(R.id.btnVoltarEtapa3);
        btnCadastrar = view.findViewById(R.id.btn_cadastrar);

        if (viewModel != null) {
            if (inputBio != null) inputBio.setText(viewModel.getBio());
            if (inputPreferencias != null) inputPreferencias.setText(viewModel.getPreferencias());
            if (inputBioProf != null) inputBioProf.setText(viewModel.getBio());

            atualizarVisibilidadeTipo(viewModel.getTipoUsuario());
        }

        if (btnEscolherFoto != null) {
            btnEscolherFoto.setOnClickListener(v -> exibirOpcoesFoto());
        }
        if (btnEscolherFotoProf != null) {
            btnEscolherFotoProf.setOnClickListener(v -> exibirOpcoesFoto());
        }
        if (btnEscolherFotoAdm != null) {
            btnEscolherFotoAdm.setOnClickListener(v -> exibirOpcoesFoto());
        }

        if (btnVoltarEtapa3 != null) {
            btnVoltarEtapa3.setOnClickListener(v -> {
                salvarDados();
                if (getActivity() instanceof Cadastro) {
                    ((Cadastro) getActivity()).navegarParaEtapa(2);
                }
            });
        }

        if (btnCadastrar != null) {
            btnCadastrar.setOnClickListener(v -> {
                salvarDados();
                if (getActivity() instanceof Cadastro) {
                    ((Cadastro) getActivity()).executarCadastroFinal();
                }
            });
        }
    }

    public void atualizarVisibilidadeTipo(String tipo) {
        if (layoutEscolarAluno == null || layoutEscolarProfessor == null || layoutEscolarAdmin == null) return;

        layoutEscolarAluno.setVisibility(View.GONE);
        layoutEscolarProfessor.setVisibility(View.GONE);
        layoutEscolarAdmin.setVisibility(View.GONE);

        if ("professor".equalsIgnoreCase(tipo)) {
            layoutEscolarProfessor.setVisibility(View.VISIBLE);
        } else if ("admin".equalsIgnoreCase(tipo)) {
            layoutEscolarAdmin.setVisibility(View.VISIBLE);
        } else {
            layoutEscolarAluno.setVisibility(View.VISIBLE);
        }
    }

    private void exibirOpcoesFoto() {
        if (getContext() == null) return;

        CharSequence[] opcoes = {
                "📁 Galeria do Dispositivo",
                "👤 Avatar Clássico",
                "🎓 Avatar Estudante",
                "🚀 Logo Próxima Etapa"
        };

        new AlertDialog.Builder(requireContext())
                .setTitle("Escolher Foto de Perfil")
                .setItems(opcoes, (dialog, which) -> {
                    if (which == 0) {
                        pickImageLauncher.launch("image/*");
                    } else if (which == 1) {
                        aplicarFotoPreset("preset:round_person", R.drawable.icon_round_person);
                    } else if (which == 2) {
                        aplicarFotoPreset("preset:baseline_account", R.drawable.icon_round_lock);
                    } else if (which == 3) {
                        aplicarFotoPreset("preset:logo_proxima_etapa", R.drawable.logo_proxima_etapa);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void aplicarFotoPreset(String presetKey, int drawableResId) {
        if (imgPerfilPreview != null) {
            imgPerfilPreview.setImageResource(drawableResId);
        }
        if (imgPerfilPreviewProf != null) {
            imgPerfilPreviewProf.setImageResource(drawableResId);
        }
        if (imgPerfilPreviewAdm != null) {
            imgPerfilPreviewAdm.setImageResource(drawableResId);
        }

        if (viewModel != null) {
            viewModel.setFotoUri(presetKey);
        }
        if (getContext() != null) {
            SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
            sp.edit().putString("foto_perfil_uri", presetKey).apply();
        }
        Toast.makeText(getContext(), "Avatar selecionado com sucesso!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        salvarDados();
    }

    private void salvarDados() {
        if (viewModel == null) return;
        String tipo = viewModel.getTipoUsuario();
        if ("professor".equalsIgnoreCase(tipo)) {
            if (inputBioProf != null) viewModel.setBio(inputBioProf.getText().toString().trim());
        } else {
            if (inputBio != null) viewModel.setBio(inputBio.getText().toString().trim());
            if (inputPreferencias != null) viewModel.setPreferencias(inputPreferencias.getText().toString().trim());
        }
    }
}
