package com.example.errornull_proximaetapa.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.errornull_proximaetapa.R;

public class CadastroPt1Fragment extends Fragment {

    private EditText inputNome, inputIdade, inputCpf, inputEstado, inputEmail, inputSenha;
    private Button btnAvancarEtapa1;
    private CadastroViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cadastro_pt1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            viewModel = new ViewModelProvider(requireActivity()).get(CadastroViewModel.class);
        }

        inputNome = view.findViewById(R.id.input_nome);
        inputIdade = view.findViewById(R.id.input_idade);
        inputCpf = view.findViewById(R.id.input_cpf);
        inputEstado = view.findViewById(R.id.input_cidade);
        inputEmail = view.findViewById(R.id.input_email);
        inputSenha = view.findViewById(R.id.input_senha);
        btnAvancarEtapa1 = view.findViewById(R.id.btnAvancarEtapa1);

        if (viewModel != null) {
            if (inputNome != null) inputNome.setText(viewModel.getNome());
            if (inputIdade != null) inputIdade.setText(viewModel.getIdade());
            if (inputCpf != null) inputCpf.setText(viewModel.getCpf());
            if (inputEstado != null) inputEstado.setText(viewModel.getCidade());
            if (inputEmail != null) inputEmail.setText(viewModel.getEmail());
            if (inputSenha != null) inputSenha.setText(viewModel.getSenha());
        }

        TextWatcher lockTabsWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && getActivity() instanceof Cadastro) {
                    ((Cadastro) getActivity()).bloquearTabs(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        if (inputNome != null) inputNome.addTextChangedListener(lockTabsWatcher);
        if (inputCpf != null) inputCpf.addTextChangedListener(lockTabsWatcher);
        if (inputEmail != null) inputEmail.addTextChangedListener(lockTabsWatcher);

        if (btnAvancarEtapa1 != null) {
            btnAvancarEtapa1.setOnClickListener(v -> {
                String nome = inputNome != null ? inputNome.getText().toString().trim() : "";
                String idade = inputIdade != null ? inputIdade.getText().toString().trim() : "";
                String cpf = inputCpf != null ? inputCpf.getText().toString().trim() : "";
                String estado = inputEstado != null ? inputEstado.getText().toString().trim() : "";
                String email = inputEmail != null ? inputEmail.getText().toString().trim() : "";
                String senha = inputSenha != null ? inputSenha.getText().toString().trim() : "";

                // 1. Validação do Nome: Mínimo 2 letras, SEM NÚMEROS
                if (nome.length() < 2) {
                    Toast.makeText(getContext(), "O nome deve ter no mínimo 2 letras!", Toast.LENGTH_SHORT).show();
                    if (inputNome != null) inputNome.setError("Mínimo de 2 letras!");
                    return;
                }
                if (nome.matches(".*\\d.*")) {
                    Toast.makeText(getContext(), "O nome NÃO pode conter números!", Toast.LENGTH_SHORT).show();
                    if (inputNome != null) inputNome.setError("Números não são permitidos no nome!");
                    return;
                }

                // 2. Validação de Idade: Mínimo 2 dígitos, APENAS NÚMEROS (Sem letras)
                if (idade.length() < 2) {
                    Toast.makeText(getContext(), "A idade deve ter no mínimo 2 dígitos (ex: 17)!", Toast.LENGTH_SHORT).show();
                    if (inputIdade != null) inputIdade.setError("Mínimo de 2 dígitos!");
                    return;
                }
                if (!idade.matches("\\d+")) {
                    Toast.makeText(getContext(), "A idade deve conter APENAS números!", Toast.LENGTH_SHORT).show();
                    if (inputIdade != null) inputIdade.setError("Apenas números são permitidos!");
                    return;
                }

                // 3. Validação de CPF: APENAS NÚMEROS (Sem letras), Mínimo 11 números
                if (cpf.matches(".*[a-zA-Z].*")) {
                    Toast.makeText(getContext(), "O CPF NÃO pode conter letras!", Toast.LENGTH_SHORT).show();
                    if (inputCpf != null) inputCpf.setError("Apenas números!");
                    return;
                }
                String cpfDigitos = cpf.replaceAll("[^0-9]", "");
                if (cpfDigitos.length() < 11) {
                    Toast.makeText(getContext(), "O CPF deve ter no mínimo 11 dígitos numéricos!", Toast.LENGTH_SHORT).show();
                    if (inputCpf != null) inputCpf.setError("Mínimo de 11 números!");
                    return;
                }

                // 4. Validação de E-mail: Ter @, mínimo 2 caracteres antes do @, ter .com ou .br
                if (email.length() < 5 || !email.contains("@")) {
                    Toast.makeText(getContext(), "E-mail inválido! Deve conter '@' e no mínimo 2 caracteres antes do '@'.", Toast.LENGTH_SHORT).show();
                    if (inputEmail != null) inputEmail.setError("Informe um e-mail válido!");
                    return;
                }
                String[] partesEmail = email.split("@");
                if (partesEmail[0].length() < 2) {
                    Toast.makeText(getContext(), "O e-mail deve ter no mínimo 2 caracteres antes do '@'!", Toast.LENGTH_SHORT).show();
                    if (inputEmail != null) inputEmail.setError("Mínimo de 2 caracteres antes do '@'!");
                    return;
                }
                String dominio = partesEmail.length > 1 ? partesEmail[1].toLowerCase() : "";
                if (!dominio.contains(".com") && !dominio.contains(".br")) {
                    Toast.makeText(getContext(), "O e-mail deve conter um domínio válido com .com ou .br (ex: usuario@email.com, usuario@gmail.com)!", Toast.LENGTH_SHORT).show();
                    if (inputEmail != null) inputEmail.setError("Domínio deve conter .com ou .br!");
                    return;
                }

                // 5. Validação de Senha: Mínimo 2 caracteres
                if (senha.length() < 2) {
                    Toast.makeText(getContext(), "A senha deve ter no mínimo 2 caracteres!", Toast.LENGTH_SHORT).show();
                    if (inputSenha != null) inputSenha.setError("Mínimo de 2 caracteres!");
                    return;
                }

                if (viewModel != null) {
                    viewModel.setNome(nome);
                    viewModel.setIdade(idade);
                    viewModel.setCpf(cpf);
                    viewModel.setCidade(estado);
                    viewModel.setEmail(email);
                    viewModel.setSenha(senha);
                }

                if (getActivity() instanceof Cadastro) {
                    ((Cadastro) getActivity()).navegarParaEtapa(2);
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        salvarDados();
    }

    private void salvarDados() {
        if (viewModel == null) return;
        if (inputNome != null) viewModel.setNome(inputNome.getText().toString().trim());
        if (inputIdade != null) viewModel.setIdade(inputIdade.getText().toString().trim());
        if (inputCpf != null) viewModel.setCpf(inputCpf.getText().toString().trim());
        if (inputEstado != null) viewModel.setCidade(inputEstado.getText().toString().trim());
        if (inputEmail != null) viewModel.setEmail(inputEmail.getText().toString().trim());
        if (inputSenha != null) viewModel.setSenha(inputSenha.getText().toString().trim());
    }
}
