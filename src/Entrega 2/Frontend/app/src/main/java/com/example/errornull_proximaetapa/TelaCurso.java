package com.example.errornull_proximaetapa;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class TelaCurso extends AppCompatActivity {

    Curso curso1, curso2, curso3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tela_curso);

        curso1 = new Curso("Administração", "Terça-feira", "Sua próxima etapa será transformar suas ideias em resultados! Desenvolva habilidades essenciais de gestão, organize processos e torne-se o líder que o mercado de trabalho procura com nosso curso prático de Administração.", R.drawable.adm_logo);
        curso2 = new Curso("Logística", "quarta-feira", "Sua próxima etapa será conectar o mundo e entregar resultados! Desenvolva habilidades essenciais em controle de frotas, armazenamento e operações ágeis. Torne-se o profissional que o mercado logístico procura.", R.drawable.logistica_logo);
        curso3 = new Curso("Marketing", "quinta-feira", "Sua próxima etapa será transformar sua criatividade em resultados! Desenvolva habilidades em mídias sociais, comunicação e vendas. Torne-se o profissional de marketing que sabe conectar grandes marcas a pessoas.", R.drawable.marketing_logo);

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


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent irParaSegundaTela = new Intent(TelaCurso.this, CursoInfo.class);
                irParaSegundaTela.putExtra("cursoInfo", curso1.getInfo());
                irParaSegundaTela.putExtra("cursoImagem", curso1.getImageId());
                startActivity(irParaSegundaTela);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent irParaSegundaTela = new Intent(TelaCurso.this, CursoInfo.class);
                irParaSegundaTela.putExtra("cursoInfo", curso2.getInfo());
                irParaSegundaTela.putExtra("cursoImagem", curso2.getImageId());
                startActivity(irParaSegundaTela);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent irParaSegundaTela = new Intent(TelaCurso.this, CursoInfo.class);
                irParaSegundaTela.putExtra("cursoInfo", curso3.getInfo());
                irParaSegundaTela.putExtra("cursoImagem", curso3.getImageId());
                startActivity(irParaSegundaTela);
            }
        });

        //onclicklistener paginas
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent irParaPerfil = new Intent(TelaCurso.this, TelaPerfil.class);
                startActivity(irParaPerfil);
            }
        });

        btnOport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent irParaOport = new Intent(TelaCurso.this, TelaOportunidades.class);
                startActivity(irParaOport);
            }
        });

    }
}
