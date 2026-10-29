package com.example.errornull_proximaetapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginInfo extends AppCompatActivity {

    private EditText inputEmail, inputSenha;
    private Button btnLogar, btnIrParaCadastro;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_login);

        inputEmail = findViewById(R.id.input_email);
        inputSenha = findViewById(R.id.input_senha);
        btnLogar = findViewById(R.id.btn_logar);
        btnIrParaCadastro = findViewById(R.id.btn_ir_cadastro);

        // Configuração do OkHttpClient com timeout estendido para o Render acordar
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

        btnLogar.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String senha = inputSenha.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(LoginInfo.this, "Preencha o e-mail e a senha!", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest request = new LoginRequest(email, senha);

            api.fazerLogin(request).enqueue(new Callback<TokenResponse>() {
                @Override
                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().getToken();

                        // ==========================================
                        // INTEGRAÇÃO DO USER SESSION MANAGER
                        // ==========================================
                        UserSessionManager session = new UserSessionManager(LoginInfo.this);
                        // Guarda o e-mail e dados básicos na sessão local do telemóvel
                        session.salvarDadosUtilizador(email, "Nome do Utilizador", "aluno");

                        Toast.makeText(LoginInfo.this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();

                        // Redireciona para a Tela de Perfil e fecha a tela de login
                        Intent intent = new Intent(LoginInfo.this, TelaPerfil.class);
                        // Se precisares de passar o token, podes usar:
                        // intent.putExtra("USER_TOKEN", token);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LoginInfo.this, "E-mail ou senha inválidos.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TokenResponse> call, Throwable t) {
                    Toast.makeText(LoginInfo.this, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERRO", "Falha: " + t.getMessage());
                }
            });
        });

        if (btnIrParaCadastro != null) {
            btnIrParaCadastro.setOnClickListener(v -> {
                startActivity(new Intent(LoginInfo.this, Cadastro.class));
            });
        }
    }
}