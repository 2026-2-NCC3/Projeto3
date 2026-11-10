package com.example.errornull_proximaetapa.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.errornull_proximaetapa.R;

public class CadastroPt2Fragment extends Fragment {

    private LinearLayout layoutEscolarAluno, layoutEscolarProfessor, layoutEscolarAdmin;

    // Campos Aluno
    private EditText inputEscola, inputTelefoneAluno;
    private Spinner spinnerGrade;

    // Campos Professor
    private EditText inputTelefoneProfessor, inputEspecialidade;

    // Campos Admin
    private EditText inputCargo;

    // Botões
    private Button btnVoltarEtapa2, btnAvancarEtapa2;

    private CadastroViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cadastro_pt2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            viewModel = new ViewModelProvider(requireActivity()).get(CadastroViewModel.class);
            ((Cadastro) getActivity()).bloquearTabs(true);
        }

        // Sub-layouts
        layoutEscolarAluno = view.findViewById(R.id.layout_campos_aluno2);
        layoutEscolarProfessor = view.findViewById(R.id.layout_campos_professor2);
        layoutEscolarAdmin = view.findViewById(R.id.layout_campos_admin2);

        // Aluno
        inputEscola = view.findViewById(R.id.input_escola);
        spinnerGrade = view.findViewById(R.id.spinner_grade);
        inputTelefoneAluno = view.findViewById(R.id.input_telefone_aluno);

        // Configurar Spinner de Grade se existir
        if (spinnerGrade != null && getContext() != null) {
            String[] grades = {"1º Ano Ensino Médio", "2º Ano Ensino Médio", "3º Ano Ensino Médio", "Cursinho / Outros"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_spinner_item,
                    grades
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGrade.setAdapter(adapter);
        }

        // Professor
        inputTelefoneProfessor = view.findViewById(R.id.input_telefone_professor);
        inputEspecialidade = view.findViewById(R.id.input_especialidade);

        // Admin
        inputCargo = view.findViewById(R.id.input_cargo);

        // Botões
        btnVoltarEtapa2 = view.findViewById(R.id.btnVoltarEtapa2);
        btnAvancarEtapa2 = view.findViewById(R.id.btnAvancarEtapa2);

        // Preencher dados salvos
        if (viewModel != null) {
            if (inputEscola != null) inputEscola.setText(viewModel.getEscola());
            if (inputTelefoneAluno != null) inputTelefoneAluno.setText(viewModel.getTelefoneAluno());

            if (inputTelefoneProfessor != null) inputTelefoneProfessor.setText(viewModel.getTelefoneProfessor());
            if (inputEspecialidade != null) inputEspecialidade.setText(viewModel.getEspecialidade());

            if (inputCargo != null) inputCargo.setText(viewModel.getCargo());

            atualizarVisibilidadeTipo(viewModel.getTipoUsuario());
        }

        if (btnVoltarEtapa2 != null) {
            btnVoltarEtapa2.setOnClickListener(v -> {
                salvarDados();
                if (getActivity() instanceof Cadastro) {
                    ((Cadastro) getActivity()).navegarParaEtapa(1);
                }
            });
        }

        if (btnAvancarEtapa2 != null) {
            btnAvancarEtapa2.setOnClickListener(v -> {
                String tipo = viewModel != null ? viewModel.getTipoUsuario() : "aluno";

                if ("professor".equalsIgnoreCase(tipo) || "admin".equalsIgnoreCase(tipo)) {
                    String telProf = inputTelefoneProfessor != null ? inputTelefoneProfessor.getText().toString().trim() : "";
                    String espec = inputEspecialidade != null ? inputEspecialidade.getText().toString().trim() : "";

                    if (telProf.matches(".*[a-zA-Z].*")) {
                        Toast.makeText(getContext(), "O telefone NÃO pode conter letras!", Toast.LENGTH_SHORT).show();
                        if (inputTelefoneProfessor != null) inputTelefoneProfessor.setError("Apenas números!");
                        return;
                    }
                    String telDigitos = telProf.replaceAll("[^0-9]", "");
                    if (telDigitos.length() < 8) {
                        Toast.makeText(getContext(), "O telefone deve ter no mínimo 8 dígitos numéricos!", Toast.LENGTH_SHORT).show();
                        if (inputTelefoneProfessor != null) inputTelefoneProfessor.setError("Mínimo de 8 números!");
                        return;
                    }
                    if (espec.length() < 2) {
                        Toast.makeText(getContext(), "A especialidade deve ter pelo menos 2 caracteres!", Toast.LENGTH_SHORT).show();
                        if (inputEspecialidade != null) inputEspecialidade.setError("Mínimo de 2 caracteres!");
                        return;
                    }
                } else {
                    String escola = inputEscola != null ? inputEscola.getText().toString().trim() : "";
                    String telAluno = inputTelefoneAluno != null ? inputTelefoneAluno.getText().toString().trim() : "";

                    if (escola.length() < 2) {
                        Toast.makeText(getContext(), "O nome da escola deve ter no mínimo 2 caracteres!", Toast.LENGTH_SHORT).show();
                        if (inputEscola != null) inputEscola.setError("Mínimo de 2 caracteres!");
                        return;
                    }
                    if (telAluno.matches(".*[a-zA-Z].*")) {
                        Toast.makeText(getContext(), "O telefone NÃO pode conter letras!", Toast.LENGTH_SHORT).show();
                        if (inputTelefoneAluno != null) inputTelefoneAluno.setError("Apenas números!");
                        return;
                    }
                    String telDigitos = telAluno.replaceAll("[^0-9]", "");
                    if (telDigitos.length() < 8) {
                        Toast.makeText(getContext(), "O telefone deve ter no mínimo 8 dígitos numéricos!", Toast.LENGTH_SHORT).show();
                        if (inputTelefoneAluno != null) inputTelefoneAluno.setError("Mínimo de 8 números!");
                        return;
                    }
                }

                salvarDados();
                if (getActivity() instanceof Cadastro) {
                    ((Cadastro) getActivity()).navegarParaEtapa(3);
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

    @Override
    public void onPause() {
        super.onPause();
        salvarDados();
    }

    private void salvarDados() {
        if (viewModel == null) return;

        if (inputEscola != null) viewModel.setEscola(inputEscola.getText().toString().trim());
        if (spinnerGrade != null && spinnerGrade.getSelectedItem() != null) {
            viewModel.setGrade(spinnerGrade.getSelectedItem().toString());
        }
        if (inputTelefoneAluno != null) viewModel.setTelefoneAluno(inputTelefoneAluno.getText().toString().trim());

        if (inputTelefoneProfessor != null) viewModel.setTelefoneProfessor(inputTelefoneProfessor.getText().toString().trim());
        if (inputEspecialidade != null) viewModel.setEspecialidade(inputEspecialidade.getText().toString().trim());

        if (inputCargo != null) viewModel.setCargo(inputCargo.getText().toString().trim());
    }
}
