package com.example.errornull_proximaetapa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Questionario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_questionario);

        View btnVoltar = findViewById(R.id.btnVoltar);
        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        }

        Button btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcularPerfil();
            }
        });
    }

    private void calcularPerfil() {
        int analitico = 0, social = 0, empreendedor = 0, criativo = 0, pratico = 0;

        // Pergunta 1: Ambiente
        RadioGroup rgAmbiente = findViewById(R.id.rgAmbiente);
        int idAmbiente = rgAmbiente.getCheckedRadioButtonId();
        if (idAmbiente == R.id.rbEscritorio) pratico++;
        else if (idAmbiente == R.id.rbHomeOffice) criativo++;
        else if (idAmbiente == R.id.rbExterno) empreendedor++;

        // Pergunta 2: Interesses (CheckBoxes)
        if (((CheckBox) findViewById(R.id.cbTecnologia)).isChecked()) analitico++;
        if (((CheckBox) findViewById(R.id.cbSaude)).isChecked()) social++;
        if (((CheckBox) findViewById(R.id.cbArtes)).isChecked()) criativo++;
        if (((CheckBox) findViewById(R.id.cbNegocios)).isChecked()) empreendedor++;

        // Pergunta 3: Desafios
        RadioGroup rgDesafios = findViewById(R.id.rgDesafios);
        int idDesafios = rgDesafios.getCheckedRadioButtonId();
        if (idDesafios == R.id.rbLogica) analitico++;
        else if (idDesafios == R.id.rbCriatividade) criativo++;

        // Pergunta 4: Motivação
        RadioGroup rgMotivacao = findViewById(R.id.rgMotivacao);
        int idMotivacao = rgMotivacao.getCheckedRadioButtonId();
        if (idMotivacao == R.id.rbImpacto) social++;
        else if (idMotivacao == R.id.rbCrescimento) empreendedor++;
        else if (idMotivacao == R.id.rbConhecimento) analitico++;

        // Pergunta 5: Rotina
        RadioGroup rgRotina = findViewById(R.id.rgRotina);
        int idRotina = rgRotina.getCheckedRadioButtonId();
        if (idRotina == R.id.rbAgenda) pratico++;
        else if (idRotina == R.id.rbAdaptavel) empreendedor++;
        else if (idRotina == R.id.rbNatural) criativo++;

        // Pergunta 6: Grupo
        RadioGroup rgGrupo = findViewById(R.id.rgGrupo);
        int idGrupo = rgGrupo.getCheckedRadioButtonId();
        if (idGrupo == R.id.rbLider) empreendedor++;
        else if (idGrupo == R.id.rbExecutor) pratico++;
        else if (idGrupo == R.id.rbApoio) social++;

        // Pergunta 7: Inovação
        RadioGroup rgTech = findViewById(R.id.rgTech);
        int idTech = rgTech.getCheckedRadioButtonId();
        if (idTech == R.id.rbEarlyAdopter) criativo++;
        else if (idTech == R.id.rbSegurancaTech) pratico++;

        // Pergunta 8: Público
        RadioGroup rgPublico = findViewById(R.id.rgPublico);
        int idPublico = rgPublico.getCheckedRadioButtonId();
        if (idPublico == R.id.rbFaloBem) social++;
        else if (idPublico == R.id.rbIndividual) analitico++;

        // Pergunta 9: Decisões
        RadioGroup rgDecisao = findViewById(R.id.rgDecisao);
        int idDecisao = rgDecisao.getCheckedRadioButtonId();
        if (idDecisao == R.id.rbFatos) analitico++;
        else if (idDecisao == R.id.rbIntuicao) criativo++;

        // Pergunta 10: Futuro
        RadioGroup rgFuturo = findViewById(R.id.rgFuturo);
        int idFuturo = rgFuturo.getCheckedRadioButtonId();
        if (idFuturo == R.id.rbReferencia) analitico++;
        else if (idFuturo == R.id.rbGestaoFuturo) empreendedor++;
        else if (idFuturo == R.id.rbDono) empreendedor++;

        // Pergunta 11: Energia
        RadioGroup rgEnergia = findViewById(R.id.rgEnergia);
        int idEnergia = rgEnergia.getCheckedRadioButtonId();
        if (idEnergia == R.id.rbAnalise) analitico++;
        else if (idEnergia == R.id.rbOrientacao) social++;
        else if (idEnergia == R.id.rbCriacao) criativo++;

        // Pergunta 12: Estilo Aprendizado
        RadioGroup rgEstiloAprender = findViewById(R.id.rgEstiloAprender);
        int idEstilo = rgEstiloAprender.getCheckedRadioButtonId();
        if (idEstilo == R.id.rbPratico) pratico++;
        else if (idEstilo == R.id.rbTeoria) analitico++;

        // Pergunta 13: Tempo Livre
        RadioGroup rgTempoLivre = findViewById(R.id.rgTempoLivre);
        int idTempo = rgTempoLivre.getCheckedRadioButtonId();
        if (idTempo == R.id.rbOtimiza) pratico++;
        else if (idTempo == R.id.rbAjuda) social++;
        else if (idTempo == R.id.rbPesquisa) analitico++;

        // Pergunta 14: Destaque
        RadioGroup rgDestaque = findViewById(R.id.rgDestaque);
        int idDestaque = rgDestaque.getCheckedRadioButtonId();
        if (idDestaque == R.id.rbPrecisao) analitico++;
        else if (idDestaque == R.id.rbOriginalidade) criativo++;
        else if (idDestaque == R.id.rbInfluencia) empreendedor++;

        // Pergunta 15: Prioridade
        RadioGroup rgPrioridade = findViewById(R.id.rgPrioridade);
        int idPrioridade = rgPrioridade.getCheckedRadioButtonId();
        if (idPrioridade == R.id.rbResultados) pratico++;
        else if (idPrioridade == R.id.rbInova) criativo++;
        else if (idPrioridade == R.id.rbBemEstar) social++;

        // Determinar Resultado
        String perfilVencedor = "Analítico";
        String descricao = "Você possui facilidade com dados, lógica e resolução de problemas complexos. Considere áreas como TI, Engenharia ou Ciência.";
        int max = analitico;

        if (social > max) {
            max = social;
            perfilVencedor = "Social";
            descricao = "Sua motivação está em ajudar pessoas e trabalhar em equipe. Considere áreas como Saúde, Educação ou RH.";
        }
        if (empreendedor > max) {
            max = empreendedor;
            perfilVencedor = "Empreendedor";
            descricao = "Você tem perfil de liderança e gosta de desafios de mercado. Considere Gestão, Marketing ou Negócios.";
        }
        if (criativo > max) {
            max = criativo;
            perfilVencedor = "Criativo";
            descricao = "Você valoriza a originalidade e inovação. Áreas de Design, Artes e Comunicação são ideais para você.";
        }
        if (pratico > max) {
            perfilVencedor = "Prático";
            descricao = "Você foca na execução técnica e organização. Logística, Administração e Operações combinam com você.";
        }

        mostrarResultado(perfilVencedor, descricao);
    }

    private void mostrarResultado(String perfil, String descricao) {
        android.content.SharedPreferences sp = UserSession.getScopedPreferences(this);
        sp.edit()
                .putString("resultado_perfil_vocacional", perfil)
                .putString("resultado_perfil_descricao", descricao)
                .putBoolean("questionario_concluido", true)
                .apply();

        com.example.errornull_proximaetapa.auth.NotificationHelper.adicionarNotificacao(
                this,
                "Questionário Vocacional Concluído!",
                "Seu perfil vocacional foi definido como " + perfil + "."
        );

        new AlertDialog.Builder(this)
                .setTitle("🎯 Perfil Vocacional: " + perfil)
                .setMessage(descricao + "\n\nO seu resultado foi salvo com sucesso no seu perfil!")
                .setPositiveButton("Voltar para a Tela Principal", (dialog, which) -> {
                    Intent data = new Intent();
                    data.putExtra("RESULTADO_PERFIL", perfil);
                    setResult(RESULT_OK, data);
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}
