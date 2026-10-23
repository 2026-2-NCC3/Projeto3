package com.example.errornull_proximaetapa;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class TelaOportunidades extends AppCompatActivity {

    Curso curso1, curso2, curso3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tela_oportunidades);

        //butoes curso
        Button button1 = findViewById(R.id.btnC1);
        Button button2 = findViewById(R.id.btnC2);
        Button button3 = findViewById(R.id.btnC3);

        //butoes paginas
        Button btnPerfil = findViewById(R.id.btnPerfil);
        Button btnCurso = findViewById(R.id.btnCurso);
        Button btnOport = findViewById(R.id.btnOport);
        Button btnAvisos = findViewById(R.id.btnAvisos);
        Button btnConfig = findViewById(R.id.btnConfig);

        //onclicklistener paginas
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent irParaPerfil = new Intent(TelaOportunidades.this, TelaPerfil.class);
                startActivity(irParaPerfil);
            }
        });

        btnCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent irParaCurso = new Intent(TelaOportunidades.this, TelaCurso.class);
                startActivity(irParaCurso);
            }
        });
    }
}
