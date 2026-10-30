package com.example.errornull_proximaetapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Cadastro extends AppCompatActivity {

    private EditText nome_aluno;
    private EditText cpf;
    private EditText email;
    private EditText telefone_aluno;
    private EditText escola;
    private EditText cidade;
    private EditText grade;
    private EditText senha;
    private Button cadastro;

    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tela_cadastro);

        nome_aluno = findViewById(R.id.nome);
        cpf = findViewById(R.id.cpf);
        email = findViewById(R.id.email);
        telefone_aluno = findViewById(R.id.telefone);
        escola = findViewById(R.id.escola);
        cidade = findViewById(R.id.cidade);
        grade = findViewById(R.id.grade);
        senha = findViewById(R.id.senha);
        cadastro = findViewById(R.id.cadastrar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pi3-kc5f.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ApiService.class);

        cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtNome = nome_aluno.getText().toString();
                String txtCPF = cpf.getText().toString();
                String txtEmail = email.getText().toString();
                String txtTelefone = telefone_aluno.getText().toString();
                String txtEscola = escola.getText().toString();
                String txtCidade = cidade.getText().toString();
                String txtGrade = grade.getText().toString();
                String txtSenha = senha.getText().toString();

                if (txtNome.isEmpty() || txtCPF.isEmpty() || txtEmail.isEmpty() || txtSenha.isEmpty()) {
                    Toast.makeText(Cadastro.this, "Preencha ao menos Nome, CPF, E-mail e Senha!", Toast.LENGTH_SHORT).show();
                    return;
                }

                CadastroRequest request = new CadastroRequest(txtNome, txtCPF, txtEmail,txtTelefone, txtEscola, txtCidade, txtGrade, txtSenha);

                api.fazerCadastro(request).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(Cadastro.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();

                            Intent irParaSegundaTela = new Intent(Cadastro.this, LoginInfo.class);
                            startActivity(irParaSegundaTela);
                            finish();
                        } else {
                            Toast.makeText(Cadastro.this, "Erro ao cadastrar. Tente novamente.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(Cadastro.this, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show();
                        Log.e("API_ERRO", "Falha no cadastro: " + t.getMessage());
                    }
                });
            }
        });

        Button btnIrLogin = findViewById(R.id.btn_ir_login);
        btnIrLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Cadastro.this, LoginInfo.class);
                startActivity(intent);
            }
        });
    }
}