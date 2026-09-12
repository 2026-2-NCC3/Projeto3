package com.example.errornull_proximaetapa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Curso curso1, curso2, curso3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        curso1 = new Curso("Administração", "Terça-feira", "Sua próxima etapa será transformar suas ideias em resultados! Desenvolva habilidades essenciais de gestão, organize processos e torne-se o líder que o mercado de trabalho procura com nosso curso prático de Administração.");
        curso2 = new Curso("Logística", "quarta-feira", "Sua próxima etapa será conectar o mundo e entregar resultados! Desenvolva habilidades essenciais em controle de frotas, armazenamento e operações ágeis. Torne-se o profissional que o mercado logístico procura.");
        curso3 = new Curso("Administração3", "quinta-feira", "Sua próxima etapa será transformar sua criatividade em resultados! Desenvolva habilidades em mídias sociais, comunicação e vendas. Torne-se o profissional de marketing que sabe conectar grandes marcas a pessoas.");

        Button button1 = findViewById(R.id.btnC1);
        Button button2 = findViewById(R.id.btnC2);
        Button button3 = findViewById(R.id.btnC3);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent irParaSegundaTela = new Intent(MainActivity.this, CursoInfo.class);
                irParaSegundaTela.putExtra("cursoInfo", curso1.getInfo());
                startActivity(irParaSegundaTela);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent irParaSegundaTela = new Intent(MainActivity.this, CursoInfo.class);
                irParaSegundaTela.putExtra("cursoInfo", curso2.getInfo());
                startActivity(irParaSegundaTela);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent irParaSegundaTela = new Intent(MainActivity.this, CursoInfo.class);
                irParaSegundaTela.putExtra("cursoInfo", curso3.getInfo());
                startActivity(irParaSegundaTela);
            }
        });

    }
}