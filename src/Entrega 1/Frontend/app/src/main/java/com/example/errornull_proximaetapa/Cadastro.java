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

public class Cadastro extends AppCompatActivity {

    private EditText nome;
    private EditText telefone;
    private EditText escola;
    private EditText cidade;
    private EditText grade;
    private EditText senha;

    private Button cadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tela_cadastro);

        nome = findViewById(R.id.nome);
        telefone = findViewById(R.id.telefone);
        escola = findViewById(R.id.escola);
        cidade = findViewById(R.id.cidade);
        grade = findViewById(R.id.grade);
        senha = findViewById(R.id.senha);
        cadastro = findViewById(R.id.cadastrar);

        cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String nome = nome.getText().toString();
                String telefone = telefone.getText().toString();
                String escola = escola.getText().toString();
                String cidade = cidade.getText().toString();
                String grade = grade.getText().toString();
                String senha = senha.getText().toString();*/

                Intent irParaSegundaTela = new Intent(Cadastro.this, LoginInfo.class);
                startActivity(irParaSegundaTela);

            }
        });

    }
}

