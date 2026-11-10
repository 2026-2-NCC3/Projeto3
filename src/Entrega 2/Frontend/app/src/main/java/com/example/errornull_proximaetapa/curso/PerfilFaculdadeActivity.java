package com.example.errornull_proximaetapa.curso;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.UserSession;
import com.example.errornull_proximaetapa.auth.NotificationHelper;

import java.util.HashSet;
import java.util.Set;

public class PerfilFaculdadeActivity extends AppCompatActivity {

    private Button btnVoltar, btnSeguir, btnAlterarCapaAdmin;
    private ImageView imgCapa;
    private TextView txtNome, txtBadge, txtDesc, txtSeguidores, txtCursos, txtDocentes;
    private LinearLayout containerCursos;

    private String sigla = "USP";
    private String nomeCompleto = "USP - Universidade de São Paulo";
    private String desc = "Excelente ensino superior, pesquisa e tecnologia.";
    private int capaResId = R.drawable.proximaetapa1;

    // Launcher para alterar capa da faculdade (Exclusivo ADM)
    private final ActivityResultLauncher<String> changeImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                    } catch (Exception ignored) {}

                    SharedPreferences sp = UserSession.getScopedPreferences(this);
                    sp.edit().putString("foto_capa_" + sigla, uri.toString()).apply();
                    if (imgCapa != null) imgCapa.setImageURI(uri);
                    NotificationHelper.exibirToastCustomizado(this, "Capa da " + sigla + " atualizada com sucesso!");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_faculdade);

        btnVoltar = findViewById(R.id.btnVoltarPerfilFaculdade);
        btnSeguir = findViewById(R.id.btnSeguirFaculdadePage);
        btnAlterarCapaAdmin = findViewById(R.id.btnAlterarFotoFaculdadeAdmin);
        imgCapa = findViewById(R.id.imgCapaFaculdadePage);
        txtNome = findViewById(R.id.txtNomeFaculdadePage);
        txtBadge = findViewById(R.id.txtBadgeFaculdadePage);
        txtDesc = findViewById(R.id.txtDescFaculdadePage);
        txtSeguidores = findViewById(R.id.txtSeguidoresPage);
        txtCursos = findViewById(R.id.txtCursosPage);
        txtDocentes = findViewById(R.id.txtDocentesPage);
        containerCursos = findViewById(R.id.containerCursosFaculdadePage);

        String siglaExtra = getIntent().getStringExtra("sigla");
        String nomeExtra = getIntent().getStringExtra("nomeCompleto");
        String descExtra = getIntent().getStringExtra("descricao");
        int capaExtra = getIntent().getIntExtra("capaResId", 0);

        if (siglaExtra != null) sigla = siglaExtra;
        if (nomeExtra != null) nomeCompleto = nomeExtra;
        if (descExtra != null) desc = descExtra;
        if (capaExtra != 0) capaResId = capaExtra;

        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        }

        SharedPreferences sp = UserSession.getScopedPreferences(this);
        String tipoUsuario = sp.getString("tipoUsuario", "aluno");

        if ("admin".equalsIgnoreCase(tipoUsuario)) {
            if (btnAlterarCapaAdmin != null) {
                btnAlterarCapaAdmin.setVisibility(View.VISIBLE);
                btnAlterarCapaAdmin.setOnClickListener(v -> exibirOpcoesCapaFaculdade());
            }
        } else {
            if (btnAlterarCapaAdmin != null) {
                btnAlterarCapaAdmin.setVisibility(View.GONE);
            }
        }

        carregarDadosEPerfil();
    }

    private void exibirOpcoesCapaFaculdade() {
        CharSequence[] opcoes = {
                "📁 Galeria do Dispositivo",
                "🏛️ Capa Campus USP",
                "🔬 Capa Unicamp Inovação",
                "🎓 Capa Unesp Formatura"
        };

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Alterar Capa da " + sigla)
                .setItems(opcoes, (d, which) -> {
                    if (which == 0) {
                        changeImageLauncher.launch("image/*");
                    } else {
                        int resId = R.drawable.proximaetapa1;
                        if (which == 2) resId = R.drawable.proximaetapa2;
                        if (which == 3) resId = R.drawable.proximaetapa3;

                        SharedPreferences sp = UserSession.getScopedPreferences(this);
                        sp.edit().putString("foto_capa_" + sigla, "preset:" + resId).apply();
                        if (imgCapa != null) imgCapa.setImageResource(resId);
                        NotificationHelper.exibirToastCustomizado(this, "Capa da " + sigla + " atualizada!");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    private void carregarDadosEPerfil() {
        SharedPreferences sp = UserSession.getScopedPreferences(this);
        String capaSalva = sp.getString("foto_capa_" + sigla, "");

        if (imgCapa != null) {
            if (!capaSalva.isEmpty()) {
                if (capaSalva.startsWith("preset:")) {
                    try {
                        int resId = Integer.parseInt(capaSalva.replace("preset:", ""));
                        imgCapa.setImageResource(resId);
                    } catch (Exception e) {
                        imgCapa.setImageResource(capaResId);
                    }
                } else {
                    try {
                        imgCapa.setImageURI(Uri.parse(capaSalva));
                    } catch (Exception e) {
                        imgCapa.setImageResource(capaResId);
                    }
                }
            } else {
                imgCapa.setImageResource(capaResId);
            }
        }

        if (txtNome != null) txtNome.setText(nomeCompleto);
        if (txtBadge != null) txtBadge.setText("✓ Faculdade Pública • " + sigla);
        if (txtDesc != null) txtDesc.setText(desc);

        boolean seguindo = sp.getBoolean("seguindo_" + sigla, false);

        // Calcula seguidores reais
        Set<String> cursosInscritos = sp.getStringSet("lista_cursos_ids", new HashSet<>());
        int seguidoresCount = 0;
        if (seguindo) seguidoresCount++;

        boolean temInscricaoNestaFacul = false;
        for (String idC : cursosInscritos) {
            String faculCurso = sp.getString("faculdadeCurso_" + idC, "");
            if (faculCurso.equalsIgnoreCase(sigla) || faculCurso.contains(sigla)) {
                temInscricaoNestaFacul = true;
                break;
            }
        }

        if (temInscricaoNestaFacul && !seguindo) {
            seguidoresCount++;
        }

        if (txtSeguidores != null) {
            txtSeguidores.setText(seguidoresCount + "\nSeguidores");
        }

        if (btnSeguir != null) {
            btnSeguir.setText(seguindo ? "✓ Acompanhando Faculdade" : "Acompanhar Faculdade");
            btnSeguir.setOnClickListener(v -> {
                boolean novoSeguindo = !sp.getBoolean("seguindo_" + sigla, false);
                sp.edit().putBoolean("seguindo_" + sigla, novoSeguindo).apply();
                getSharedPreferences("ProximaEtapaPrefs", MODE_PRIVATE).edit().putBoolean("seguindo_" + sigla, novoSeguindo).apply();
                NotificationHelper.exibirToastCustomizado(this, novoSeguindo ? "Você começou a acompanhar a " + sigla + "!" : "Você deixou de acompanhar a " + sigla + ".");
                carregarDadosEPerfil();
            });
        }

        renderizarCursosFaculdadeClicaveis();
    }

    private void renderizarCursosFaculdadeClicaveis() {
        if (containerCursos == null) return;
        containerCursos.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        if ("USP".equalsIgnoreCase(sigla)) {
            adicionarCardCursoClicavel(inflater, "Administração & Gestão", "USP • Liderança & Negócios", "80 horas", "Seg, Qua e Sex (14h - 16h)", R.drawable.adm_logo, "adm", "USP", "Graduação", "Negócios & Gestão");
            adicionarCardCursoClicavel(inflater, "Desenvolvimento Web & TI", "USP • Programação & Software", "100 horas", "Ter, Qui e Sáb (14h - 17h)", R.drawable.logo_proxima_etapa, "web", "USP", "Graduação", "Tecnologia & TI");
            if (txtCursos != null) txtCursos.setText("2\nCursos");
        } else if ("UNICAMP".equalsIgnoreCase(sigla)) {
            adicionarCardCursoClicavel(inflater, "Logística & Suprimentos", "UNICAMP • Cadeia Global", "60 horas", "Ter, Qui e Sáb (09h - 11h)", R.drawable.logistica_logo, "logistica", "UNICAMP", "Graduação", "Engenharia & Logística");
            if (txtCursos != null) txtCursos.setText("1\nCurso");
        } else if ("UNESP".equalsIgnoreCase(sigla)) {
            adicionarCardCursoClicavel(inflater, "Marketing Digital", "UNESP • Mídias & Vendas", "50 horas", "Seg, Qua e Sex (16h - 18h)", R.drawable.marketing_logo, "marketing", "UNESP", "Graduação", "Comunicação & Mídias");
            if (txtCursos != null) txtCursos.setText("1\nCurso");
        }
    }

    private void adicionarCardCursoClicavel(LayoutInflater inflater, String nome, String resumo, String carga, String dias, int imgResId, String cursoId, String faculdade, String tag1, String tag2) {
        View cardItem = inflater.inflate(R.layout.item_cursos, containerCursos, false);

        TextView txtResumo = cardItem.findViewById(R.id.txtTituloCurso);
        TextView txtBadge = cardItem.findViewById(R.id.txtStatusCurso);
        TextView txtDiasAulas = cardItem.findViewById(R.id.txtDescricaoCurso1);
        View pbProgresso = null;
        TextView txtPorcentagem = cardItem.findViewById(R.id.txtDescricaoCurso2);

        if (txtResumo != null) txtResumo.setText(nome + "\n🏷️ [" + tag1 + "] [" + tag2 + "]");
        if (txtBadge != null) txtBadge.setText(faculdade);
        if (txtDiasAulas != null) txtDiasAulas.setText("📅 " + dias + " • " + carga);

        if (pbProgresso != null) pbProgresso.setVisibility(View.GONE);
        if (txtPorcentagem != null) txtPorcentagem.setVisibility(View.GONE);

        cardItem.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilFaculdadeActivity.this, CursoInfo.class);
            intent.putExtra("nomeCurso", nome);
            intent.putExtra("cursoInfo", "Curso oferecido pela " + faculdade + ". Aprenda com metodologia prática e certificado de conclusão.");
            intent.putExtra("cursoImagem", imgResId);
            intent.putExtra("faculdadePertencente", faculdade);
            intent.putExtra("cursoId", cursoId);
            intent.putExtra("cargaHoraria", carga);
            intent.putExtra("diasSemana", dias);
            startActivity(intent);
        });

        containerCursos.addView(cardItem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDadosEPerfil();
    }
}