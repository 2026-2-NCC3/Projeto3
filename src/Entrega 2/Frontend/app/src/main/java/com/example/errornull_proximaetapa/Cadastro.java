package com.example.errornull_proximaetapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Cadastro extends AppCompatActivity {

    private RadioGroup rgTipoUsuario;
    private LinearLayout layoutAluno, layoutProfessor, layoutAdmin;
    private TextInputLayout tilCpf, tilNome, tilEmail, textInputLayoutSenha;
    private EditText inputCpf, inputNome, inputEmail, inputSenha;
    private EditText inputTelefoneAluno, inputEscola, inputCidade, inputGrade;
    private EditText inputTelefoneProfessor, inputEspecialidade;
    private EditText inputCargo;
    private Button btnCadastrar, btnIrLogin;
    private ApiService api;
    private String tipoUsuarioSelecionado = "aluno";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_cadastro);

        rgTipoUsuario = findViewById(R.id.rg_tipo_usuario);
        layoutAluno = findViewById(R.id.layout_campos_aluno);
        layoutProfessor = findViewById(R.id.layout_campos_professor);
        layoutAdmin = findViewById(R.id.layout_campos_admin);

        // Referências dos TextInputLayouts para exibir o erro visual com .setError()
        tilCpf = findViewById(R.id.tilCpf);
        tilNome = findViewById(R.id.tilNome);
        tilEmail = findViewById(R.id.tilEmail);
        textInputLayoutSenha = findViewById(R.id.textInputLayoutSenha);

        inputCpf = findViewById(R.id.input_cpf);
        inputNome = findViewById(R.id.input_nome);
        inputEmail = findViewById(R.id.input_email);
        inputSenha = findViewById(R.id.input_senha);

        inputTelefoneAluno = findViewById(R.id.input_telefone_aluno);
        inputEscola = findViewById(R.id.input_escola);
        inputCidade = findViewById(R.id.input_cidade);
        spinnerGrade = findViewById(R.id.spinner_grade);

        String[] niveisAcademicos = new String[]{
            "Selecione sua escolaridade...",
            "EM 1° ano",
            "EM 2° ano",
            "EM 3° ano",
            "Graduação",
            "Pós-Graduação",
            "Mestrado",
            "Doutorado",
            "Pós-Doutorado"
        };

        ArrayAdapter<String> adapterGrade = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            niveisAcademicos
        );
        spinnerGrade.setAdapter(adapterGrade);

        inputTelefoneProfessor = findViewById(R.id.input_telefone_professor);
        inputEspecialidade = findViewById(R.id.input_especialidade);

        inputCargo = findViewById(R.id.input_cargo);
        btnCadastrar = findViewById(R.id.btn_cadastrar);
        btnIrLogin = findViewById(R.id.btn_ir_login);

        if (btnIrLogin != null) {
            btnIrLogin.setOnClickListener(v -> {
                startActivity(new Intent(Cadastro.this, LoginInfo.class));
                finish();
            });
        }

        // Altera a visibilidade dos blocos e reposiciona o botão de cadastro dinamicamente
        rgTipoUsuario.setOnCheckedChangeListener((group, checkedId) -> {
            layoutAluno.setVisibility(View.GONE);
            layoutProfessor.setVisibility(View.GONE);
            layoutAdmin.setVisibility(View.GONE);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) btnCadastrar.getLayoutParams();

            if (checkedId == R.id.rb_aluno) {
                tipoUsuarioSelecionado = "aluno";
                layoutAluno.setVisibility(View.VISIBLE);
                params.topToBottom = R.id.layout_campos_aluno;
            } else if (checkedId == R.id.rb_professor) {
                tipoUsuarioSelecionado = "professor";
                layoutProfessor.setVisibility(View.VISIBLE);
                params.topToBottom = R.id.layout_campos_professor;
            } else if (checkedId == R.id.rb_admin) {
                tipoUsuarioSelecionado = "admin";
                layoutAdmin.setVisibility(View.VISIBLE);
                params.topToBottom = R.id.layout_campos_admin;
            }
            btnCadastrar.setLayoutParams(params);
        });

        // Configuração do OkHttpClient com timeout estendido para o Render
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pi3-kc5f.onrender.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ApiService.class);

        btnCadastrar.setOnClickListener(v -> {
            // Limpa erros anteriores visuais
            if (tilCpf != null) tilCpf.setError(null);
            if (tilNome != null) tilNome.setError(null);
            if (tilEmail != null) tilEmail.setError(null);
            if (textInputLayoutSenha != null) textInputLayoutSenha.setError(null);

            String cpf = inputCpf.getText().toString().trim();
            String nome = inputNome.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String senha = inputSenha.getText().toString().trim();

            // Validações do Front-end
            if (cpf.isEmpty()) {
                if (tilCpf != null) tilCpf.setError("O CPF é obrigatório");
                return;
            }
            if (nome.isEmpty()) {
                if (tilNome != null) tilNome.setError("O nome é obrigatório");
                return;
            }
            if (!validarEmail(email)) {
                if (tilEmail != null) tilEmail.setError("Insira um e-mail válido (ex: teste@email.com)");
                return;
            }
            if (!validarSenha(senha)) {
                if (textInputLayoutSenha != null) {
                    textInputLayoutSenha.setError("Mín. 6 carateres, 1 maiúscula, 1 minúscula, 1 número e 1 '@'");
                }
                return;
            }

            String telAluno = null, escola = null, cidade = null, grade = null;
            String telProf = null, especialidade = null, cargo = null;

            if ("aluno".equals(tipoUsuarioSelecionado)) {
                telAluno = inputTelefoneAluno.getText().toString().trim();
                escola = inputEscola.getText().toString().trim();
                cidade = inputCidade.getText().toString().trim();
                grade = spinnerGrade.getSelectedItem().toString();
                if (grade.equals("Selecione sua escolaridade...")) {
                    Toast.makeText(Cadastro.this, "Por favor, selecione sua escolaridade!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else if ("professor".equals(tipoUsuarioSelecionado)) {
                telProf = inputTelefoneProfessor.getText().toString().trim();
                especialidade = inputEspecialidade.getText().toString().trim();
            } else if ("admin".equals(tipoUsuarioSelecionado)) {
                cargo = inputCargo.getText().toString().trim();
            }

            CadastroRequest request = new CadastroRequest(
                    cpf, nome, email, senha, tipoUsuarioSelecionado,
                    telAluno, escola, cidade, grade,
                    telProf, especialidade, cargo
            );

            api.fazerCadastro(request).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(Cadastro.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Cadastro.this, LoginInfo.class));
                        finish();
                    } else {
                        // Tratamento inteligente de duplicidade vindo do Back-end
                        try {
                            String erroMsg = response.errorBody() != null ? response.errorBody().string() : "";
                            
                            if (erroMsg.contains("CPF")) {
                                if (tilCpf != null) tilCpf.setError("Este CPF já está cadastrado!");
                            } else if (erroMsg.contains("E-mail") || erroMsg.contains("email")) {
                                if (tilEmail != null) tilEmail.setError("Este E-mail já está em uso!");
                            } else {
                                Toast.makeText(Cadastro.this, "Erro ao cadastrar. Verifique os dados.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(Cadastro.this, "Erro: E-mail ou CPF já cadastrados no sistema!", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(Cadastro.this, "Erro: " + t.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
                    Log.e("API_ERRO", "Detalhe completo da falha: ", t);
                }
            });
        });
    }

    // Método para validar formato do e-mail
    private boolean validarEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Método para validar regras de segurança da senha
    private boolean validarSenha(String senha) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{6,}$";
        return Pattern.matches(regex, senha);
    }
}