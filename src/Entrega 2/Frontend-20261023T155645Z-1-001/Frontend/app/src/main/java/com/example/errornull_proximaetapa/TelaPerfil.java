package com.example.errornull_proximaetapa;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class TelaPerfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tela_perfil);

        Button btnPerfil = findViewById(R.id.btnPerfil);
        Button btnCurso = findViewById(R.id.btnCurso);
        Button btnOport = findViewById(R.id.btnOport);
        Button btnAvisos = findViewById(R.id.btnAvisos);
        Button btnConfig = findViewById(R.id.btnConfig);
        Button btnCard = findViewById(R.id.btnCard);

        btnCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent irParaCurso = new Intent(TelaPerfil.this, TelaCurso.class);
                startActivity(irParaCurso);
            }
        });

        btnOport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent irParaOport = new Intent(TelaPerfil.this, TelaOportunidades.class);
                startActivity(irParaOport);
            }
        });
    }
}