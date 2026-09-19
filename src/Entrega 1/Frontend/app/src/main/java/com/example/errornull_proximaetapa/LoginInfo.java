package com.example.errornull_proximaetapa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginInfo extends AppCompatActivity {

    private EditText entradaNome;
    private EditText entradaPassword;
    private Button botaoConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tela_login);

        entradaNome = findViewById(R.id.entrada_nome);
        entradaPassword = findViewById(R.id.entrada_password);
        botaoConfirmar = findViewById(R.id.button);

        botaoConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginDigitado = entradaNome.getText().toString();
                String senhaDigitada = entradaPassword.getText().toString();

                if (loginDigitado.equals("aluno123") && senhaDigitada.equals("12345678")) {

                    // Se correto, vai para a MainActivity
                    Intent intent = new Intent(LoginInfo.this, MainActivity.class);
                    startActivity(intent);

                    // Finaliza a tela de login para que o usuário não volte pra cá ao apertar o botão "Voltar" do celular
                    finish();

                } else {
                    // Se incorreto, exibe uma mensagem de erro na tela
                    Toast.makeText(LoginInfo.this, "Login ou senha incorretos!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
