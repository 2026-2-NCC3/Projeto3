package com.example.errornull_proximaetapa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CursoInfo extends AppCompatActivity {
    public TextView cursoInfo;
    public ImageView imgCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.curso_info); // Layout da segunda tela

        Button btnVoltar = findViewById(R.id.btnVoltar);

        cursoInfo = findViewById(R.id.cursoInfo);
        imgCurso = findViewById(R.id.imgCurso);

        // Pegando o nome enviado da MainActivity
        String detalheRecebido = getIntent().getStringExtra("cursoInfo");
        int idImagemRecebida = getIntent().getIntExtra("cursoImagem", 0);

        if (idImagemRecebida != 0){
            imgCurso.setImageResource(idImagemRecebida);
        }

        cursoInfo.setText(detalheRecebido);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
    }
}
