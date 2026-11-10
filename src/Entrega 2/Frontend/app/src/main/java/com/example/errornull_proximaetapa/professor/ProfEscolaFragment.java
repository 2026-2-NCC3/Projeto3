package com.example.errornull_proximaetapa.professor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.errornull_proximaetapa.CarouselAdapter;
import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.aluno.AlunoCursosFragment;
import com.example.errornull_proximaetapa.auth.NotificationHelper;
import com.example.errornull_proximaetapa.curso.PerfilFaculdadeActivity;
import com.example.errornull_proximaetapa.curso.TelaAulaCurso;

import java.util.HashSet;
import java.util.Set;

public class ProfEscolaFragment extends Fragment {

    private ViewPager2 viewPagerCursosCarousel;
    private final Handler carouselHandler = new Handler(Looper.getMainLooper());
    private Runnable carouselRunnable;
    private int currentCarouselPosition = 0;

    private View dot1, dot2, dot3;
    private View btnToggleBackdropFilter;
    private TextView txtFiltroSelecionadoBackdrop;
    private final Set<String> categoriasMultiSelecionadas = new HashSet<>();

    private LinearLayout contFacul;
    private LinearLayout contCursosPublicados;
    private View btnSininhoNotificacoes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prof_escola, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dot1 = view.findViewById(R.id.pontoImg1);
        dot2 = view.findViewById(R.id.pontoImg2);
        dot3 = view.findViewById(R.id.pontoImg3);

        btnToggleBackdropFilter = view.findViewById(R.id.btnToggleBackdropFilter);
        txtFiltroSelecionadoBackdrop = view.findViewById(R.id.txtFiltroSelecionadoBackdrop);

        contFacul = view.findViewById(R.id.contFacul);
        contCursosPublicados = view.findViewById(R.id.contCursosPublicados);

        btnSininhoNotificacoes = view.findViewById(R.id.btnSininhoNotificacoes);
        if (btnSininhoNotificacoes != null) {
            btnSininhoNotificacoes.setOnClickListener(v -> NotificationHelper.exibirCentralNotificacoes(requireContext(), () -> atualizarSininho(view)));
        }

        if (btnToggleBackdropFilter != null) {
            btnToggleBackdropFilter.setOnClickListener(v -> abrirBackdropFilterDialog());
        }

        inicializarCarrossel(view);
        renderizarFaculdades();
        renderizarPrincipaisCursos();
        atualizarSininho(view);
    }

    private void atualizarSininho(View view) {
        if (view == null || getContext() == null) return;
        TextView badge = view.findViewById(R.id.badgeSininhoQtd);
        int qtd = NotificationHelper.getQtdNaoLidas(requireContext());
        if (badge != null) {
            if (qtd > 0) {
                badge.setText(String.valueOf(qtd));
                badge.setVisibility(View.VISIBLE);
            } else {
                badge.setVisibility(View.GONE);
            }
        }
    }

    private void inicializarCarrossel(View view) {
        int[] carouselImages = new int[]{
                R.drawable.proximaetapa1,
                R.drawable.proximaetapa2,
                R.drawable.proximaetapa3
        };

        viewPagerCursosCarousel = view.findViewById(R.id.viewPagerCursosCarousel);
        if (viewPagerCursosCarousel != null) {
            viewPagerCursosCarousel.setAdapter(new CarouselAdapter(carouselImages));

            viewPagerCursosCarousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    currentCarouselPosition = position;
                    atualizarDots(position);
                }
            });

            carouselRunnable = new Runnable() {
                @Override
                public void run() {
                    if (viewPagerCursosCarousel != null) {
                        currentCarouselPosition = (currentCarouselPosition + 1) % carouselImages.length;
                        viewPagerCursosCarousel.setCurrentItem(currentCarouselPosition, true);
                        carouselHandler.postDelayed(this, 6000);
                    }
                }
            };

            carouselHandler.postDelayed(carouselRunnable, 6000);
        }
    }

    private void atualizarDots(int position) {
        if (dot1 == null || dot2 == null || dot3 == null) return;

        dot1.setBackgroundResource(position == 0 ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
        dot2.setBackgroundResource(position == 1 ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
        dot3.setBackgroundResource(position == 2 ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
    }

    private void renderizarFaculdades() {
        if (contFacul == null || getContext() == null) return;
        contFacul.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());

        adicionarCardFaculdade(inflater, "USP", "USP - Universidade de São Paulo", "Líder em ensino e extensão acadêmica pública.", R.drawable.proximaetapa1);
        adicionarCardFaculdade(inflater, "UNICAMP", "UNICAMP - Univ. Estadual de Campinas", "Referência em tecnologia, engenharias e inovação.", R.drawable.proximaetapa2);
        adicionarCardFaculdade(inflater, "UNESP", "UNESP - Univ. Estadual Paulista", "Excelência em ciências humanas, exatas e mídias.", R.drawable.proximaetapa3);
    }

    private void adicionarCardFaculdade(LayoutInflater inflater, String sigla, String nomeCompleto, String desc, int imgResId) {
        View fView = inflater.inflate(R.layout.item_faculdade, contFacul, false);
        TextView txtNome = fView.findViewById(R.id.tvNomeFacul);
        ImageView imgLogo = fView.findViewById(R.id.cursoFacul);

        if (txtNome != null) txtNome.setText(sigla);
        if (imgLogo != null) imgLogo.setImageResource(imgResId);

        fView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PerfilFaculdadeActivity.class);
            intent.putExtra("sigla", sigla);
            intent.putExtra("nomeCompleto", nomeCompleto);
            intent.putExtra("descricao", desc);
            intent.putExtra("capaResId", imgResId);
            startActivity(intent);
        });

        contFacul.addView(fView);
    }

    private void renderizarPrincipaisCursos() {
        if (contCursosPublicados == null || getContext() == null) return;
        contCursosPublicados.removeAllViews();

        AlunoCursosFragment.sincronizarECarregarCursosProfessores(requireContext());

        SharedPreferences spGeralCursos = requireContext().getSharedPreferences("ListaGeralCursosPlataforma", Context.MODE_PRIVATE);
        Set<String> setGlobalCursos = spGeralCursos.getStringSet("lista_cursos_publicos_ids", new HashSet<>());

        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (setGlobalCursos.isEmpty()) {
            adicionarCardCursoItem(inflater, "Desenvolvimento Web & TI", "USP • 100 Pontos", R.drawable.logo_proxima_etapa, "web");
            adicionarCardCursoItem(inflater, "Administração & Gestão", "USP • 80 Pontos", R.drawable.adm_logo, "adm");
            adicionarCardCursoItem(inflater, "Logística & Suprimentos", "UNICAMP • 60 Pontos", R.drawable.logistica_logo, "logistica");
            adicionarCardCursoItem(inflater, "Marketing Digital", "UNESP • 50 Pontos", R.drawable.marketing_logo, "marketing");
            return;
        }

        for (String idCurso : setGlobalCursos) {
            String nome = spGeralCursos.getString("nomeCurso_" + idCurso, "");
            String faculdade = spGeralCursos.getString("faculdadeCurso_" + idCurso, "USP");
            String carga = spGeralCursos.getString("cargaHoraria_" + idCurso, "100 Pontos");
            int imgResId = spGeralCursos.getInt("cursoImagem_" + idCurso, R.drawable.proximaetapa1);

            if (nome.isEmpty()) continue;

            adicionarCardCursoItem(inflater, nome, faculdade + " • " + carga, imgResId, idCurso);
        }
    }

    private void adicionarCardCursoItem(LayoutInflater inflater, String nome, String detalhe, int imgResId, String idCurso) {
        View cardItem = inflater.inflate(R.layout.item_cursos, contCursosPublicados, false);

        TextView txtNome = cardItem.findViewById(R.id.txtTituloCurso);
        TextView txtDetalhes = cardItem.findViewById(R.id.txtDescricaoCurso1);
        ImageView imgCapa = cardItem.findViewById(R.id.imgCursos);

        if (txtNome != null) txtNome.setText(nome);
        if (txtDetalhes != null) txtDetalhes.setText(detalhe);
        if (imgCapa != null) imgCapa.setImageResource(imgResId);

        cardItem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TelaAulaCurso.class);
            intent.putExtra("cursoId", idCurso);
            startActivity(intent);
        });

        contCursosPublicados.addView(cardItem);
    }

    private void abrirBackdropFilterDialog() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filtro_cursos, null);

        View btnFechar = dialogView.findViewById(R.id.btnFecharFiltro);
        Button btnAplicar = dialogView.findViewById(R.id.btnAplicarBackdropFilter);

        CheckBox cbTodos = dialogView.findViewById(R.id.cbTodosBackdrop);
        CheckBox cbTech = dialogView.findViewById(R.id.cbTechBackdrop);
        CheckBox cbGestao = dialogView.findViewById(R.id.cbGestaoBackdrop);
        CheckBox cbLogistica = dialogView.findViewById(R.id.cbLogisticaBackdrop);
        CheckBox cbMarketing = dialogView.findViewById(R.id.cbMarketingBackdrop);

        boolean nenhumSelecionado = categoriasMultiSelecionadas.isEmpty() || categoriasMultiSelecionadas.contains("todos");

        if (cbTodos != null) cbTodos.setChecked(nenhumSelecionado);
        if (cbTech != null) cbTech.setChecked(categoriasMultiSelecionadas.contains("tech"));
        if (cbGestao != null) cbGestao.setChecked(categoriasMultiSelecionadas.contains("gestao"));
        if (cbLogistica != null) cbLogistica.setChecked(categoriasMultiSelecionadas.contains("logistica"));
        if (cbMarketing != null) cbMarketing.setChecked(categoriasMultiSelecionadas.contains("marketing"));

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnFechar != null) btnFechar.setOnClickListener(v -> dialog.dismiss());

        if (btnAplicar != null) {
            btnAplicar.setOnClickListener(v -> {
                categoriasMultiSelecionadas.clear();

                if (cbTech != null && cbTech.isChecked()) categoriasMultiSelecionadas.add("tech");
                if (cbGestao != null && cbGestao.isChecked()) categoriasMultiSelecionadas.add("gestao");
                if (cbLogistica != null && cbLogistica.isChecked()) categoriasMultiSelecionadas.add("logistica");
                if (cbMarketing != null && cbMarketing.isChecked()) categoriasMultiSelecionadas.add("marketing");

                if (categoriasMultiSelecionadas.isEmpty() || (cbTodos != null && cbTodos.isChecked())) {
                    categoriasMultiSelecionadas.clear();
                    categoriasMultiSelecionadas.add("todos");
                    if (txtFiltroSelecionadoBackdrop != null) txtFiltroSelecionadoBackdrop.setText("🔍 Filtrar Cursos: [ Todas as Áreas ] ▾");
                } else {
                    StringBuilder sb = new StringBuilder("🔍 Cursos: [ ");
                    int count = 0;
                    for (String cat : categoriasMultiSelecionadas) {
                        count++;
                        sb.append(cat.toUpperCase());
                        if (count < categoriasMultiSelecionadas.size()) sb.append(", ");
                    }
                    sb.append(" ] ▾");
                    if (txtFiltroSelecionadoBackdrop != null) txtFiltroSelecionadoBackdrop.setText(sb.toString());
                }

                dialog.dismiss();
            });
        }

        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (carouselHandler != null && carouselRunnable != null) {
            carouselHandler.removeCallbacks(carouselRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        renderizarPrincipaisCursos();
        if (getView() != null) {
            atualizarSininho(getView());
        }
        if (carouselHandler != null && carouselRunnable != null) {
            carouselHandler.removeCallbacks(carouselRunnable);
            carouselHandler.postDelayed(carouselRunnable, 6000);
        }
    }
}
