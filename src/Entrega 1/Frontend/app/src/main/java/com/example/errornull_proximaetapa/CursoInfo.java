package com.example.errornull_proximaetapa;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CursoInfo extends AppCompatActivity {
    public TextView cursoInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.curso_info); // Layout da segunda tela

        cursoInfo = findViewById(R.id.cursoInfo);


        // Pegando o nome enviado da MainActivity
        String detalheRecebido = getIntent().getStringExtra("cursoInfo");

        cursoInfo.setText(detalheRecebido);
    }
}
