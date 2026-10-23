package com.example.errornull_proximaetapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Cadastro extends AppCompatActivity {

    private RadioGroup rgTipoUsuario;
    private LinearLayout layoutAluno, layoutProfessor, layoutAdmin;
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

        inputCpf = findViewById(R.id.input_cpf);
        inputNome = findViewById(R.id.input_nome);
        inputEmail = findViewById(R.id.input_email);
        inputSenha = findViewById(R.id.input_senha);

        inputTelefoneAluno = findViewById(R.id.input_telefone_aluno);
        inputEscola = findViewById(R.id.input_escola);
        inputCidade = findViewById(R.id.input_cidade);
        inputGrade = findViewById(R.id.input_grade);

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
            String cpf = inputCpf.getText().toString().trim();
            String nome = inputNome.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String senha = inputSenha.getText().toString().trim();

            if (cpf.isEmpty() || nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(Cadastro.this, "Preencha os campos obrigatórios!", Toast.LENGTH_SHORT).show();
                return;
            }

            String telAluno = null, escola = null, cidade = null, grade = null;
            String telProf = null, especialidade = null, cargo = null;

            if ("aluno".equals(tipoUsuarioSelecionado)) {
                telAluno = inputTelefoneAluno.getText().toString().trim();
                escola = inputEscola.getText().toString().trim();
                cidade = inputCidade.getText().toString().trim();
                grade = inputGrade.getText().toString().trim();
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
                        Toast.makeText(Cadastro.this, "Erro ao cadastrar. Verifique os dados.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(Cadastro.this, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERRO", "Falha: " + t.getMessage());
                }
            });
        });
    }
}