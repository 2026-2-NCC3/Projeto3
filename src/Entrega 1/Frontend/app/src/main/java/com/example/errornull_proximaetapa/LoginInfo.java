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

public class LoginInfo extends AppCompatActivity {

    private EditText entradaNome;
    private EditText entradaPassword;
    private Button botaoConfirmar;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tela_login);

        entradaNome = findViewById(R.id.entrada_nome);
        entradaPassword = findViewById(R.id.entrada_password);
        botaoConfirmar = findViewById(R.id.button);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ApiService.class);

        botaoConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginDigitado = entradaNome.getText().toString();
                String senhaDigitada = entradaPassword.getText().toString();

                if(loginDigitado.isEmpty() || senhaDigitada.isEmpty()) {
                    Toast.makeText(LoginInfo.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginRequest request = new LoginRequest(loginDigitado, senhaDigitada);

                api.fazerLogin(request).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.isSuccessful()) {
                            Intent intent = new Intent(LoginInfo.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginInfo.this, "Usuário ou senha incorretos!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(LoginInfo.this, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show();
                        Log.e("API_ERRO", "Falha: " + t.getMessage());
                    }
                });
            }
        });
    }
}