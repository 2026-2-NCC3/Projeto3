package com.example.errornull_proximaetapa.aluno;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.errornull_proximaetapa.AttendanceManager;
import com.example.errornull_proximaetapa.SuspensaoManager;
import com.example.errornull_proximaetapa.CarouselAdapter;
import com.example.errornull_proximaetapa.curso.CursoInfo;
import com.example.errornull_proximaetapa.auth.NotificationHelper;
import com.example.errornull_proximaetapa.curso.PerfilFaculdadeActivity;
import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.curso.TelaAulaCurso;
import com.example.errornull_proximaetapa.UserSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlunoCursosFragment extends Fragment {

    private ViewPager2 viewPagerCursosCarousel;
    private final Handler carouselHandler = new Handler(Looper.getMainLooper());
    private Runnable carouselRunnable;
    private int currentCarouselPosition = 0;

    private View dot1, dot2, dot3;
    private View facul1, facul2, facul3;
    private View cardCurso1, cardCurso2, cardCurso3, cardCurso4;

    private View btnToggleBackdropFilter;
    private TextView txtFiltroSelecionadoBackdrop;
    private final Set<String> categoriasMultiSelecionadas = new HashSet<>();

    // Elementos do Perfil da Faculdade do Professor
    private LinearLayout containerCursosProfessorPage;
    private LinearLayout containerCursosAlunoPage;
    private Button btnPostarNovaDisciplinaProfPage;
    private LinearLayout containerDisciplinasProfPage;
    private LinearLayout containerCursosDinamicosProfPublicados;
    private TextView txtNomeEscolaProfessor, txtDeptoProfessor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aluno_cursos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dot1 = view.findViewById(R.id.dotCursos1);
        dot2 = view.findViewById(R.id.dotCursos2);
        dot3 = view.findViewById(R.id.dotCursos3);

        facul1 = view.findViewById(R.id.facul1);
        facul2 = view.findViewById(R.id.facul2);
        facul3 = view.findViewById(R.id.facul3);

        cardCurso1 = view.findViewById(R.id.cardCurso1);
        cardCurso2 = view.findViewById(R.id.cardCurso2);
        cardCurso3 = view.findViewById(R.id.cardCurso3);
        cardCurso4 = view.findViewById(R.id.cardCurso4);

        btnToggleBackdropFilter = view.findViewById(R.id.btnToggleBackdropFilter);
        txtFiltroSelecionadoBackdrop = view.findViewById(R.id.txtFiltroSelecionadoBackdrop);

        if (btnToggleBackdropFilter != null) {
            btnToggleBackdropFilter.setOnClickListener(v -> abrirBackdropFilterDialog());
        }

        containerCursosProfessorPage = view.findViewById(R.id.containerCursosProfessorPage);
        containerCursosAlunoPage = view.findViewById(R.id.containerCursosAlunoPage);
        btnPostarNovaDisciplinaProfPage = view.findViewById(R.id.btnPostarNovaDisciplinaProfPage);
        containerDisciplinasProfPage = view.findViewById(R.id.containerDisciplinasProfPage);
        containerCursosDinamicosProfPublicados = view.findViewById(R.id.containerCursosDinamicosProfPublicados);
        txtNomeEscolaProfessor = view.findViewById(R.id.txtNomeEscolaProfessor);
        txtDeptoProfessor = view.findViewById(R.id.txtDeptoProfessor);

        View btnSininho = view.findViewById(R.id.btnSininhoNotificacoes);
        if (btnSininho != null) {
            btnSininho.setOnClickListener(v -> NotificationHelper.exibirCentralNotificacoes(requireContext(), () -> atualizarSininho(view)));
        }

        if (btnPostarNovaDisciplinaProfPage != null) {
            btnPostarNovaDisciplinaProfPage.setOnClickListener(v -> exibirDialogPostarCursoProf());
        }

        inicializarCarrossel(view);
        configurarCliquesFaculdades();
        configurarCliquesCursos();
        configurarFerramentasAdmin();
        atualizarCursosDisponiveis();
        renderizarPrincipaisCursos();
        renderizarCursosDinamicosPublicados();
        atualizarSininho(view);
    }

    public static int getQtdInscritosNoCurso(Context context, String courseId) {
        if (context == null || courseId == null) return 0;
        SharedPreferences spGeneral = context.getSharedPreferences("ListaGeralAlunosAdmin", Context.MODE_PRIVATE);
        String savedString = spGeneral.getString("lista_alunos_cadastrados_string", "");
        if (savedString.isEmpty()) return 0;

        String[] listaAlunos = savedString.split("###");
        int count = 0;
        for (String aStr : listaAlunos) {
            String[] p = aStr.split(";;;");
            String emailA = p.length > 1 ? p[1] : "";
            if (emailA.isEmpty()) continue;

            SharedPreferences spAluno = UserSession.getScopedPreferencesForUser(context, emailA);
            Set<String> cursosInscritos = spAluno.getStringSet("lista_cursos_ids", new HashSet<>());
            if (cursosInscritos.contains(courseId)) {
                count++;
            }
        }
        return count;
    }

    public static void sincronizarECarregarCursosProfessores(Context context) {
        if (context == null) return;

        SharedPreferences spGeralCursos = context.getSharedPreferences("ListaGeralCursosPlataforma", Context.MODE_PRIVATE);
        Set<String> setGlobalCursos = new HashSet<>(spGeralCursos.getStringSet("lista_cursos_publicos_ids", new HashSet<>()));

        SharedPreferences.Editor edGeral = spGeralCursos.edit();

        setGlobalCursos.add("adm");
        setGlobalCursos.add("web");
        setGlobalCursos.add("logistica");
        setGlobalCursos.add("marketing");

        edGeral.putString("tagCurso_adm", "gestao");
        edGeral.putString("tagCurso_web", "tech");
        edGeral.putString("tagCurso_logistica", "logistica");
        edGeral.putString("tagCurso_marketing", "marketing");

        if (!spGeralCursos.contains("nomeCurso_adm")) {
            edGeral.putString("nomeCurso_adm", "Administração & Gestão");
            edGeral.putString("faculdadeCurso_adm", "USP");
            edGeral.putString("cargaHoraria_adm", "80 horas");
            edGeral.putString("diasSemana_adm", "Seg, Qua e Sex (14h - 16h)");
            edGeral.putInt("cursoImagem_adm", R.drawable.adm_logo);
        }

        if (!spGeralCursos.contains("nomeCurso_web")) {
            edGeral.putString("nomeCurso_web", "Desenvolvimento Web & TI");
            edGeral.putString("faculdadeCurso_web", "USP");
            edGeral.putString("cargaHoraria_web", "100 horas");
            edGeral.putString("diasSemana_web", "Ter, Qui e Sáb (14h - 17h)");
            edGeral.putInt("cursoImagem_web", R.drawable.logo_proxima_etapa);
        }

        if (!spGeralCursos.contains("nomeCurso_logistica")) {
            edGeral.putString("nomeCurso_logistica", "Logística & Suprimentos");
            edGeral.putString("faculdadeCurso_logistica", "UNICAMP");
            edGeral.putString("cargaHoraria_logistica", "60 horas");
            edGeral.putString("diasSemana_logistica", "Ter, Qui e Sáb (09h - 11h)");
            edGeral.putInt("cursoImagem_logistica", R.drawable.logistica_logo);
        }

        if (!spGeralCursos.contains("nomeCurso_marketing")) {
            edGeral.putString("nomeCurso_marketing", "Marketing Digital");
            edGeral.putString("faculdadeCurso_marketing", "UNESP");
            edGeral.putString("cargaHoraria_marketing", "50 horas");
            edGeral.putString("diasSemana_marketing", "Seg, Qua e Sex (16h - 18h)");
            edGeral.putInt("cursoImagem_marketing", R.drawable.marketing_logo);
        }

        List<SharedPreferences> perfisParaVarrer = new ArrayList<>();
        perfisParaVarrer.add(UserSession.getScopedPreferences(context));
        perfisParaVarrer.add(context.getSharedPreferences("PerfilUsuario_usuario_convidado", Context.MODE_PRIVATE));
        perfisParaVarrer.add(context.getSharedPreferences("PerfilUsuario_usuario_convidado_professor", Context.MODE_PRIVATE));
        perfisParaVarrer.add(context.getSharedPreferences("PerfilUsuario_usuario_convidado_admin", Context.MODE_PRIVATE));
        perfisParaVarrer.add(context.getSharedPreferences("PerfilUsuario_usuario_convidado_aluno", Context.MODE_PRIVATE));

        for (SharedPreferences spSource : perfisParaVarrer) {
            if (spSource == null) continue;
            Set<String> setCursosSource = new HashSet<>(spSource.getStringSet("lista_cursos_ids", new HashSet<>()));

            for (String key : spSource.getAll().keySet()) {
                if (key.startsWith("nomeCurso_")) {
                    String idCurso = key.replace("nomeCurso_", "");
                    setCursosSource.add(idCurso);
                }
            }

            for (String idCurso : setCursosSource) {
                String nome = spSource.getString("nomeCurso_" + idCurso, "");
                if (!nome.isEmpty()) {
                    setGlobalCursos.add(idCurso);
                    edGeral.putString("nomeCurso_" + idCurso, nome);
                    edGeral.putString("faculdadeCurso_" + idCurso, spSource.getString("faculdadeCurso_" + idCurso, "USP"));
                    edGeral.putString("cargaHoraria_" + idCurso, spSource.getString("cargaHoraria_" + idCurso, "100 pontos"));
                    edGeral.putString("diasSemana_" + idCurso, spSource.getString("diasSemana_" + idCurso, "Seg, Qua, Sex (14h - 16h)"));
                    edGeral.putString("prazoReserva_" + idCurso, spSource.getString("prazoReserva_" + idCurso, "29/10/2026"));
                    edGeral.putString("prazoTermino_" + idCurso, spSource.getString("prazoTermino_" + idCurso, "30/11/2026"));
                    edGeral.putString("qtdAulas_" + idCurso, spSource.getString("qtdAulas_" + idCurso, "10 Aulas"));
                    edGeral.putString("modulos_" + idCurso, spSource.getString("modulos_" + idCurso, ""));
                }
            }
        }

        edGeral.putStringSet("lista_cursos_publicos_ids", setGlobalCursos).apply();
    }

    private void renderizarPrincipaisCursos() {
        View contCursosView = getView() != null ? getView().findViewById(R.id.contCursosPublicados) : null;
        if (contCursosView == null || !(contCursosView instanceof ViewGroup) || getContext() == null) return;
        ViewGroup container = (ViewGroup) contCursosView;
        container.removeAllViews();

        sincronizarECarregarCursosProfessores(requireContext());

        SharedPreferences spGeralCursos = requireContext().getSharedPreferences("ListaGeralCursosPlataforma", Context.MODE_PRIVATE);
        Set<String> setGlobalCursos = spGeralCursos.getStringSet("lista_cursos_publicos_ids", new HashSet<>());

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (String idCurso : setGlobalCursos) {
            String nome = spGeralCursos.getString("nomeCurso_" + idCurso, "");
            String faculdade = spGeralCursos.getString("faculdadeCurso_" + idCurso, "USP");
            String carga = spGeralCursos.getString("cargaHoraria_" + idCurso, "100 Pontos");
            String dias = spGeralCursos.getString("diasSemana_" + idCurso, "Seg, Qua e Sex (14h - 16h)");
            String tag = spGeralCursos.getString("tagCurso_" + idCurso, "tech");
            int imgResId = spGeralCursos.getInt("cursoImagem_" + idCurso, R.drawable.proximaetapa1);

            if (nome.isEmpty()) continue;

            if (!categoriasMultiSelecionadas.isEmpty() && !categoriasMultiSelecionadas.contains("todos")) {
                if (!categoriasMultiSelecionadas.contains(tag.toLowerCase())) {
                    continue;
                }
            }

            View cardItem = inflater.inflate(R.layout.item_cursos, container, false);

            TextView txtNome = cardItem.findViewById(R.id.txtTituloCurso);
            TextView txtDetalhes = cardItem.findViewById(R.id.txtDescricaoCurso1);
            ImageView imgCapa = cardItem.findViewById(R.id.imgCursos);

            if (txtNome != null) txtNome.setText(nome);
            if (txtDetalhes != null) txtDetalhes.setText(faculdade + " • " + carga);
            if (imgCapa != null && imgResId != 0) imgCapa.setImageResource(imgResId);

            final String finalIdCurso = idCurso;
            final String finalNome = nome;
            final String finalFacul = faculdade;
            final String finalCarga = carga;
            final String finalDias = dias;
            final int finalImg = imgResId;

            cardItem.setOnClickListener(v -> abrirDetalhesCurso(
                    finalNome,
                    "Curso oferecido pela faculdade " + finalFacul + ". Inscreva-se para garantir sua vaga e certificado.",
                    finalImg, finalFacul, finalIdCurso, finalCarga, finalDias
            ));

            container.addView(cardItem);
        }
    }

    private void renderizarCursosDinamicosPublicados() {
        if (containerCursosDinamicosProfPublicados == null || getContext() == null) return;
        containerCursosDinamicosProfPublicados.removeAllViews();

        sincronizarECarregarCursosProfessores(requireContext());

        SharedPreferences spGeralCursos = requireContext().getSharedPreferences("ListaGeralCursosPlataforma", Context.MODE_PRIVATE);
        Set<String> setGlobalCursos = spGeralCursos.getStringSet("lista_cursos_publicos_ids", new HashSet<>());

        SharedPreferences spUser = UserSession.getScopedPreferences(requireContext());
        Set<String> cursosInscritos = spUser.getStringSet("lista_cursos_ids", new HashSet<>());

        if (setGlobalCursos.isEmpty()) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (String idCurso : setGlobalCursos) {
            String nome = spGeralCursos.getString("nomeCurso_" + idCurso, "");
            String faculdade = spGeralCursos.getString("faculdadeCurso_" + idCurso, "USP");
            String carga = spGeralCursos.getString("cargaHoraria_" + idCurso, "100 pontos");
            String dias = spGeralCursos.getString("diasSemana_" + idCurso, "Seg, Qua, Sex (14h - 16h)");
            String prazoReserva = spGeralCursos.getString("prazoReserva_" + idCurso, "29/10/2026");
            String tag = spGeralCursos.getString("tagCurso_" + idCurso, "tech");
            int imgResId = spGeralCursos.getInt("cursoImagem_" + idCurso, R.drawable.proximaetapa1);

            if (nome.isEmpty() || cursosInscritos.contains(idCurso)) continue;

            if (!categoriasMultiSelecionadas.isEmpty() && !categoriasMultiSelecionadas.contains("todos")) {
                if (!categoriasMultiSelecionadas.contains(tag.toLowerCase())) {
                    continue;
                }
            }

            View cardItem = inflater.inflate(R.layout.item_cursos, containerCursosDinamicosProfPublicados, false);

            TextView txtNome = cardItem.findViewById(R.id.txtTituloCurso);
            TextView txtDetalhes = cardItem.findViewById(R.id.txtDescricaoCurso1);
            ImageView imgCapa = cardItem.findViewById(R.id.imgCursos);

            if (txtNome != null) txtNome.setText(nome);
            if (txtDetalhes != null) txtDetalhes.setText(faculdade + " • " + carga);
            if (imgCapa != null && imgResId != 0) {
                imgCapa.setImageResource(imgResId);
            }

            final String finalIdCurso = idCurso;
            cardItem.setOnClickListener(v -> abrirDetalhesCurso(
                    nome,
                    "Disciplina publicada por professor parceiro com reserva garantida até " + prazoReserva + ".",
                    imgResId, faculdade, finalIdCurso, carga, dias
            ));

            containerCursosDinamicosProfPublicados.addView(cardItem);
        }
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

    private void configurarFerramentasAdmin() {
        if (getContext() == null) return;
        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
        String tipo = sp.getString("tipoUsuario", "aluno");

        boolean isProf = "professor".equalsIgnoreCase(tipo);
        boolean isAdminOuProf = "admin".equalsIgnoreCase(tipo) || isProf;

        View btnAddFacul = getView() != null ? getView().findViewById(R.id.btnAdicionarFaculdadeAdmin) : null;
        if (btnAddFacul != null) {
            btnAddFacul.setVisibility(isAdminOuProf ? View.VISIBLE : View.GONE);
            btnAddFacul.setOnClickListener(v -> exibirDialogPostarFaculdadeAdmin());
        }

        if (isProf) {
            if (containerCursosProfessorPage != null) containerCursosProfessorPage.setVisibility(View.VISIBLE);
            if (containerCursosAlunoPage != null) containerCursosAlunoPage.setVisibility(View.GONE);

            String escolaProf = sp.getString("escola", sp.getString("faculdade", ""));
            if (escolaProf.isEmpty()) {
                if (txtNomeEscolaProfessor != null) txtNomeEscolaProfessor.setText("Sem instituição lecionada vinculada");
                if (txtDeptoProfessor != null) txtDeptoProfessor.setText("Faça login ou cadastro para sincronizar sua universidade");
            } else {
                if (txtNomeEscolaProfessor != null) txtNomeEscolaProfessor.setText(escolaProf);
                if (txtDeptoProfessor != null) txtDeptoProfessor.setText("Departamento de Ensino & Pesquisa Acadêmica");
            }

            renderizarDisciplinasProfessor(sp);
        } else {
            if (containerCursosProfessorPage != null) containerCursosProfessorPage.setVisibility(View.GONE);
            if (containerCursosAlunoPage != null) containerCursosAlunoPage.setVisibility(View.VISIBLE);
        }
    }

    private void renderizarDisciplinasProfessor(SharedPreferences sp) {
        if (containerDisciplinasProfPage == null || getContext() == null) return;
        containerDisciplinasProfPage.removeAllViews();

        Set<String> setCursos = sp.getStringSet("lista_cursos_ids", new HashSet<>());

        if (setCursos.isEmpty()) {
            TextView txtVazio = new TextView(getContext());
            txtVazio.setText("Nenhuma disciplina cadastrada por você ainda. Clique em '➕ Postar Disciplina' para publicar uma nova matéria!");
            txtVazio.setTextColor(requireContext().getColor(R.color.dark_gray));
            txtVazio.setTextSize(13);
            txtVazio.setPadding(12, 12, 12, 12);
            containerDisciplinasProfPage.addView(txtVazio);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (String idCurso : setCursos) {
            String nome = sp.getString("nomeCurso_" + idCurso, "");
            String faculdade = sp.getString("faculdadeCurso_" + idCurso, "USP");
            String dias = sp.getString("diasSemana_" + idCurso, "Seg, Qua, Sex (14h - 16h)");
            String prazoReserva = sp.getString("prazoReserva_" + idCurso, "Até 15/05/2026");

            if (nome.isEmpty()) continue;

            View profView = inflater.inflate(R.layout.item_curso_professor, containerDisciplinasProfPage, false);

            TextView txtNome = profView.findViewById(R.id.txtNomeCursoProf);
            TextView txtFacul = profView.findViewById(R.id.txtCursoFaculdadeProf);
            TextView txtStatusInscricao = profView.findViewById(R.id.txtStatusInscricaoProf);
            Button btnAlunos = profView.findViewById(R.id.btnVerAlunosTurmaProf);
            Button btnChamada = profView.findViewById(R.id.btnCursoQrProf);
            ImageView btnExcluir = profView.findViewById(R.id.btnExcluirCursoProf);

            if (txtNome != null) txtNome.setText(nome);
            if (txtFacul != null) txtFacul.setText(faculdade + " • " + dias);
            if (txtStatusInscricao != null) txtStatusInscricao.setText("Reserva: " + prazoReserva);

            int qtdInscritos = getQtdInscritosNoCurso(requireContext(), idCurso);

            if (btnAlunos != null) {
                btnAlunos.setText("👥 ALUNOS (" + qtdInscritos + ")");
                btnAlunos.setOnClickListener(v -> exibirAlunosTurmaProfDialog(idCurso, nome, prazoReserva));
            }

            if (btnChamada != null) {
                btnChamada.setOnClickListener(v -> AttendanceManager.exibirDialogGestaoPresencaProf(requireContext(), idCurso, nome, () -> renderizarDisciplinasProfessor(sp)));
            }

            if (btnExcluir != null) {
                btnExcluir.setOnClickListener(v -> {
                    setCursos.remove(idCurso);
                    sp.edit().putStringSet("lista_cursos_ids", setCursos).remove("nomeCurso_" + idCurso).apply();
                    renderizarDisciplinasProfessor(sp);
                    NotificationHelper.exibirToastCustomizado(getContext(), "Disciplina " + nome + " excluída com sucesso!");
                });
            }

            // Ao clicar no cartão da disciplina, abre a sala de aula digital para gerenciar módulos e QR Code!
            final String finalIdCurso = idCurso;
            profView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), TelaAulaCurso.class);
                intent.putExtra("cursoId", finalIdCurso);
                startActivity(intent);
            });

            containerDisciplinasProfPage.addView(profView);
        }
    }

    private void exibirOpcoesFotoCursoProf(String idCurso, String nomeCurso) {
        if (getContext() == null) return;

        CharSequence[] opcoes = {
                "📷 Capa Robótica / Tecnologia",
                "📊 Capa Administração / Negócios",
                "🚚 Capa Logística / Frotas",
                "📢 Capa Marketing / Vendas",
                "🎓 Capa Padrão Próxima Etapa"
        };

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Alterar Foto da Disciplina: " + nomeCurso)
                .setItems(opcoes, (d, which) -> {
                    int resId = R.drawable.proximaetapa1;
                    if (which == 1) resId = R.drawable.adm_logo;
                    if (which == 2) resId = R.drawable.logistica_logo;
                    if (which == 3) resId = R.drawable.marketing_logo;
                    if (which == 4) resId = R.drawable.logo_proxima_etapa;

                    SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
                    sp.edit().putInt("cursoImagem_" + idCurso, resId).apply();

                    SharedPreferences spGeral = requireContext().getSharedPreferences("ListaGeralCursosPlataforma", Context.MODE_PRIVATE);
                    spGeral.edit().putInt("cursoImagem_" + idCurso, resId).apply();

                    NotificationHelper.exibirToastCustomizado(getContext(), "Foto da disciplina " + nomeCurso + " atualizada!");
                })
                .setNegativeButton("Cancelar", null)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    private void exibirAlunosTurmaProfDialog(String idCurso, String nomeCurso, String prazoInscricao) {
        if (getContext() == null) return;

        LinearLayout dialogView = new LinearLayout(requireContext());
        dialogView.setOrientation(LinearLayout.VERTICAL);
        dialogView.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
        dialogView.setPadding(40, 40, 40, 40);

        TextView txtTitulo = new TextView(requireContext());
        txtTitulo.setText("Disciplina: " + nomeCurso);
        txtTitulo.setTextColor(requireContext().getColor(R.color.dark_indigo));
        txtTitulo.setTextSize(16);
        txtTitulo.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView txtPrazo = new TextView(requireContext());
        txtPrazo.setText("Período de Reserva: " + prazoInscricao);
        txtPrazo.setTextColor(requireContext().getColor(R.color.dark_gray));
        txtPrazo.setTextSize(12);
        txtPrazo.setPadding(0, 4, 0, 16);

        LinearLayout containerAlunos = new LinearLayout(requireContext());
        containerAlunos.setOrientation(LinearLayout.VERTICAL);

        dialogView.addView(txtTitulo);
        dialogView.addView(txtPrazo);
        dialogView.addView(containerAlunos);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Fechar", null)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (containerAlunos != null) {
            containerAlunos.removeAllViews();

            SharedPreferences spGeneral = requireContext().getSharedPreferences("ListaGeralAlunosAdmin", Context.MODE_PRIVATE);
            String savedString = spGeneral.getString("lista_alunos_cadastrados_string", "");
            List<String> listaAlunos = new ArrayList<>();

            if (!savedString.isEmpty()) {
                listaAlunos.addAll(Arrays.asList(savedString.split("###")));
            }

            int contInscritos = 0;

            for (String aStr : listaAlunos) {
                String[] p = aStr.split(";;;");
                String nomeA = p.length > 0 ? p[0] : "Aluno";
                String emailA = p.length > 1 ? p[1] : "";

                if (emailA.isEmpty()) continue;

                SharedPreferences spAluno = UserSession.getScopedPreferencesForUser(requireContext(), emailA);
                Set<String> cursosInscritosAluno = spAluno.getStringSet("lista_cursos_ids", new HashSet<>());

                if (cursosInscritosAluno.contains(idCurso)) {
                    contInscritos++;
                    int progresso = spAluno.getInt("progressoCurso_" + idCurso, 0);

                    TextView txtCardA = new TextView(requireContext());
                    txtCardA.setText("👤 " + nomeA + " (" + emailA + ")\n• Status: MATRICULADO NO CURSO\n• Progresso de Aulas: " + progresso + "% concluído");
                    txtCardA.setTextColor(requireContext().getColor(R.color.dark_indigo));
                    txtCardA.setTextSize(13);
                    txtCardA.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
                    txtCardA.setPadding(16, 12, 16, 12);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 4, 0, 6);
                    txtCardA.setLayoutParams(lp);

                    containerAlunos.addView(txtCardA);
                }
            }

            if (contInscritos == 0) {
                TextView txtVazio = new TextView(requireContext());
                txtVazio.setText("Nenhum aluno inscrito nesta disciplina até o momento.");
                txtVazio.setTextColor(requireContext().getColor(R.color.dark_gray));
                txtVazio.setTextSize(13);
                txtVazio.setPadding(12, 12, 12, 12);
                containerAlunos.addView(txtVazio);
            }
        }

        dialog.show();
    }

    private void exibirDialogPostarCursoProf() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_postar_curso, null);

        ImageView imgPreviewCapa = dialogView.findViewById(R.id.imgPostarCurso);
        Button btnSelecionarCapa = dialogView.findViewById(R.id.btnSelecionarCapaCursoProf);

        EditText inputNome = dialogView.findViewById(R.id.inputNomeCurso);
        EditText inputFaculdade = dialogView.findViewById(R.id.inputFaculdadeCurso);
        EditText inputPontos = dialogView.findViewById(R.id.inputPontosTotaisCurso);
        EditText inputReserva = dialogView.findViewById(R.id.inputPrazoReservaCurso);
        EditText inputTermino = dialogView.findViewById(R.id.inputPrazoTerminoCurso);
        Spinner spinnerQtdAulas = dialogView.findViewById(R.id.spinnerQtdAulasCurso);
        EditText inputDias = dialogView.findViewById(R.id.inputDiasSemanaCurso);

        Button btnAddModulo = dialogView.findViewById(R.id.btnAdicionarModuloProf);
        LinearLayout containerModulos = dialogView.findViewById(R.id.containerModulosProf);
        Button btnPublicar = dialogView.findViewById(R.id.btnPublicarCursoProfFinal);

        final int[] capaResIdHolder = {R.drawable.proximaetapa1};

        if (btnSelecionarCapa != null) {
            btnSelecionarCapa.setOnClickListener(v -> {
                CharSequence[] opcoesCapas = {
                        "📷 Capa 1: Robótica / Tecnologia",
                        "📊 Capa 2: Administração / Negócios",
                        "🚚 Capa 3: Logística / Suprimentos",
                        "📢 Capa 4: Marketing Digital",
                        "🎓 Capa 5: Padrão Próxima Etapa"
                };

                new AlertDialog.Builder(requireContext())
                        .setTitle("Escolher Capa do Curso")
                        .setItems(opcoesCapas, (d, which) -> {
                            if (which == 0) capaResIdHolder[0] = R.drawable.proximaetapa1;
                            else if (which == 1) capaResIdHolder[0] = R.drawable.adm_logo;
                            else if (which == 2) capaResIdHolder[0] = R.drawable.logistica_logo;
                            else if (which == 3) capaResIdHolder[0] = R.drawable.marketing_logo;
                            else capaResIdHolder[0] = R.drawable.logo_proxima_etapa;

                            if (imgPreviewCapa != null) {
                                imgPreviewCapa.setImageResource(capaResIdHolder[0]);
                            }
                            NotificationHelper.exibirToastCustomizado(getContext(), "Capa selecionada!");
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });
        }

        Spinner spinnerTag = dialogView.findViewById(R.id.spinnerTagCurso);
        if (spinnerTag != null) {
            String[] tagsArr = {"💻 Tecnologia (tech)", "📊 Negócios & Gestão (gestao)", "🚚 Logística (logistica)", "📢 Marketing (marketing)"};
            ArrayAdapter<String> adapterTag = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, tagsArr);
            spinnerTag.setAdapter(adapterTag);
        }

        if (spinnerQtdAulas != null) {
            String[] opçõesAulas = {"5 Aulas (Completo)", "10 Aulas (Incompleto/Em Aulas)", "15 Aulas (Incompleto)", "20 Aulas (Finalizado)"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, opçõesAulas);
            spinnerQtdAulas.setAdapter(adapter);
        }

        if (containerModulos != null) {
            adicionarBlocoModulo(containerModulos, "Módulo 1: Introdução Prática", "35", "Aulas teóricas + Teste 1");
        }

        if (btnAddModulo != null && containerModulos != null) {
            btnAddModulo.setOnClickListener(v -> adicionarBlocoModulo(containerModulos, "", "", ""));
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnPublicar != null) {
            btnPublicar.setOnClickListener(v -> {
                String nome = inputNome != null ? inputNome.getText().toString().trim() : "";
                String faculdade = inputFaculdade != null ? inputFaculdade.getText().toString().trim() : "USP";
                String pontos = inputPontos != null ? inputPontos.getText().toString().trim() : "100";
                String prazoReserva = inputReserva != null ? inputReserva.getText().toString().trim() : "Até 15/05/2026";
                String prazoTermino = inputTermino != null ? inputTermino.getText().toString().trim() : "Até 30/11/2026";
                String qtdAulas = spinnerQtdAulas != null && spinnerQtdAulas.getSelectedItem() != null ? spinnerQtdAulas.getSelectedItem().toString() : "10 Aulas";
                String dias = inputDias != null ? inputDias.getText().toString().trim() : "Seg, Qua e Sex (14h - 16h)";

                if (nome.isEmpty()) {
                    NotificationHelper.exibirToastCustomizado(getContext(), "Digite o nome da disciplina!");
                    return;
                }

                String idCurso = "curso_prof_" + System.currentTimeMillis();
                SharedPreferences sp = UserSession.getScopedPreferences(requireContext());

                Set<String> setCursos = new HashSet<>(sp.getStringSet("lista_cursos_ids", new HashSet<>()));
                setCursos.add(idCurso);

                List<String> modulosCriados = new ArrayList<>();
                if (containerModulos != null) {
                    for (int i = 0; i < containerModulos.getChildCount(); i++) {
                        View modView = containerModulos.getChildAt(i);
                        EditText inN = modView.findViewById(R.id.inputNomeModuloProf);
                        EditText inP = modView.findViewById(R.id.inputPontosModuloProf);
                        EditText inM = modView.findViewById(R.id.inputModosModuloProf);

                        String nM = inN != null ? inN.getText().toString().trim() : ("Módulo " + (i + 1));
                        String pM = inP != null ? inP.getText().toString().trim() : "35";
                        String mM = inM != null ? inM.getText().toString().trim() : "Aulas teóricas e práticas";

                        if (!nM.isEmpty()) {
                            modulosCriados.add(nM + ";;;" + pM + ";;;" + mM);
                        }
                    }
                }

                if (modulosCriados.isEmpty()) {
                    modulosCriados.add("Módulo 1: Introdução Prática;;;35;;;Aulas teóricas + Exercícios");
                }

                StringBuilder sbModulos = new StringBuilder();
                for (int i = 0; i < modulosCriados.size(); i++) {
                    sbModulos.append(modulosCriados.get(i));
                    if (i < modulosCriados.size() - 1) sbModulos.append("###");
                }

                String tagSelecionada = "tech";
                if (spinnerTag != null && spinnerTag.getSelectedItem() != null) {
                    String sel = spinnerTag.getSelectedItem().toString().toLowerCase();
                    if (sel.contains("gestao") || sel.contains("negócios")) tagSelecionada = "gestao";
                    else if (sel.contains("logistica") || sel.contains("logística")) tagSelecionada = "logistica";
                    else if (sel.contains("marketing")) tagSelecionada = "marketing";
                    else tagSelecionada = "tech";
                }

                sp.edit()
                        .putBoolean("inscrito", true)
                        .putStringSet("lista_cursos_ids", setCursos)
                        .putString("nomeCurso_" + idCurso, nome)
                        .putString("faculdadeCurso_" + idCurso, faculdade)
                        .putString("cargaHoraria_" + idCurso, pontos + " pontos")
                        .putString("diasSemana_" + idCurso, dias)
                        .putString("prazoReserva_" + idCurso, prazoReserva)
                        .putString("prazoTermino_" + idCurso, prazoTermino)
                        .putString("qtdAulas_" + idCurso, qtdAulas)
                        .putString("modulos_" + idCurso, sbModulos.toString())
                        .putString("tagCurso_" + idCurso, tagSelecionada)
                        .putInt("cursoImagem_" + idCurso, capaResIdHolder[0])
                        .putInt("progressoCurso_" + idCurso, 0)
                        .apply();

                // Salva na Base Geral de Cursos da Plataforma
                SharedPreferences spGeralCursos = requireContext().getSharedPreferences("ListaGeralCursosPlataforma", Context.MODE_PRIVATE);
                Set<String> setGlobalCursos = new HashSet<>(spGeralCursos.getStringSet("lista_cursos_publicos_ids", new HashSet<>()));
                setGlobalCursos.add(idCurso);

                spGeralCursos.edit()
                        .putStringSet("lista_cursos_publicos_ids", setGlobalCursos)
                        .putString("nomeCurso_" + idCurso, nome)
                        .putString("faculdadeCurso_" + idCurso, faculdade)
                        .putString("cargaHoraria_" + idCurso, pontos + " pontos")
                        .putString("diasSemana_" + idCurso, dias)
                        .putString("prazoReserva_" + idCurso, prazoReserva)
                        .putString("prazoTermino_" + idCurso, prazoTermino)
                        .putString("qtdAulas_" + idCurso, qtdAulas)
                        .putString("modulos_" + idCurso, sbModulos.toString())
                        .putString("tagCurso_" + idCurso, tagSelecionada)
                        .putInt("cursoImagem_" + idCurso, capaResIdHolder[0])
                        .apply();

                NotificationHelper.adicionarNotificacao(
                        requireContext(),
                        "Nova Disciplina Publicada",
                        "Você publicou a matéria " + nome + " (" + faculdade + ") com período de reserva " + prazoReserva + "!"
                );

                NotificationHelper.exibirToastCustomizado(getContext(), "📢 Disciplina " + nome + " publicada com sucesso!");
                renderizarDisciplinasProfessor(sp);
                renderizarCursosDinamicosPublicados();
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void adicionarBlocoModulo(LinearLayout container, String nomeDefault, String pontosDefault, String modosDefault) {
        if (getContext() == null || container == null) return;

        View moduloView = LayoutInflater.from(requireContext()).inflate(R.layout.item_modulo_curso_edit, container, false);

        EditText inputNomeM = moduloView.findViewById(R.id.inputNomeModuloProf);
        EditText inputPontosM = moduloView.findViewById(R.id.inputPontosModuloProf);
        EditText inputModosM = moduloView.findViewById(R.id.inputModosModuloProf);
        ImageView btnRemoverM = moduloView.findViewById(R.id.btnRemoverModulo);

        if (inputNomeM != null && !nomeDefault.isEmpty()) inputNomeM.setText(nomeDefault);
        if (inputPontosM != null && !pontosDefault.isEmpty()) inputPontosM.setText(pontosDefault);
        if (inputModosM != null && !modosDefault.isEmpty()) inputModosM.setText(modosDefault);

        if (btnRemoverM != null) {
            btnRemoverM.setOnClickListener(v -> container.removeView(moduloView));
        }

        container.addView(moduloView);
    }

    private void exibirDialogPostarFaculdadeAdmin() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_postar_faculdade, null);

        ImageView imgPreview = dialogView.findViewById(R.id.imgPostarFaculdade);
        Button btnEscolherCapa = dialogView.findViewById(R.id.btnSelecionarCapaFaculdade);

        EditText inputSigla = dialogView.findViewById(R.id.inputSiglaFaculdade);
        EditText inputNomeCompleto = dialogView.findViewById(R.id.inputNomeCompletoFaculdade);
        EditText inputDesc = dialogView.findViewById(R.id.inputDescricaoFaculdade);
        Button btnPublicar = dialogView.findViewById(R.id.btnPublicarFaculdadeFinal);

        final int[] capaResIdHolder = {R.drawable.proximaetapa1};

        if (btnEscolherCapa != null) {
            btnEscolherCapa.setOnClickListener(v -> {
                CharSequence[] opcoesCapas = {
                        "🏛️ Capa Campus USP",
                        "🔬 Capa Unicamp Inovação",
                        "🎓 Capa Unesp Formatura",
                        "⭐ Capa Padrão Próxima Etapa"
                };

                new AlertDialog.Builder(requireContext())
                        .setTitle("Escolher Capa da Faculdade")
                        .setItems(opcoesCapas, (d, which) -> {
                            if (which == 0) capaResIdHolder[0] = R.drawable.proximaetapa1;
                            else if (which == 1) capaResIdHolder[0] = R.drawable.proximaetapa2;
                            else if (which == 2) capaResIdHolder[0] = R.drawable.proximaetapa3;
                            else capaResIdHolder[0] = R.drawable.logo_proxima_etapa;

                            if (imgPreview != null) {
                                imgPreview.setImageResource(capaResIdHolder[0]);
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnPublicar != null) {
            btnPublicar.setOnClickListener(v -> {
                String sigla = inputSigla != null ? inputSigla.getText().toString().trim() : "";
                String nomeComp = inputNomeCompleto != null ? inputNomeCompleto.getText().toString().trim() : "";
                String desc = inputDesc != null ? inputDesc.getText().toString().trim() : "";

                if (sigla.isEmpty()) {
                    NotificationHelper.exibirToastCustomizado(getContext(), "Digite a sigla da faculdade!");
                    return;
                }

                if (nomeComp.isEmpty()) nomeComp = sigla + " - Faculdade Parceira";
                if (desc.isEmpty()) desc = "Instituição de ensino parceira com foco em graduação e extensão acadêmica.";

                SharedPreferences spGeralFacul = requireContext().getSharedPreferences("ListaGeralFaculdadesPlataforma", Context.MODE_PRIVATE);
                String savedFaculs = spGeralFacul.getString("lista_faculdades_cadastradas_string", "");
                List<String> listaFaculs = new ArrayList<>();
                if (!savedFaculs.isEmpty()) {
                    listaFaculs.addAll(Arrays.asList(savedFaculs.split("###")));
                }

                listaFaculs.add(sigla + ";;;" + nomeComp + ";;;" + desc + ";;;" + capaResIdHolder[0]);

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < listaFaculs.size(); i++) {
                    sb.append(listaFaculs.get(i));
                    if (i < listaFaculs.size() - 1) sb.append("###");
                }
                spGeralFacul.edit().putString("lista_faculdades_cadastradas_string", sb.toString()).apply();

                NotificationHelper.adicionarNotificacao(
                        requireContext(),
                        "Nova Faculdade Cadastrada",
                        "A instituição " + sigla + " foi cadastrada na plataforma!"
                );

                NotificationHelper.exibirToastCustomizado(getContext(), "🏛️ Faculdade " + sigla + " cadastrada com sucesso!");
                configurarCliquesFaculdades();
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void configurarCliquesFaculdades() {
        View contFacul = getView() != null ? getView().findViewById(R.id.contFacul) : null;
        if (contFacul instanceof ViewGroup) {
            ViewGroup container = (ViewGroup) contFacul;
            container.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(requireContext());

            adicionarItemFaculdadeHorizontal(container, inflater, "USP", "USP - Universidade de São Paulo", "Líder em ensino superior, inovação acadêmica e pesquisa tecnológica no Brasil.", R.drawable.proximaetapa1);
            adicionarItemFaculdadeHorizontal(container, inflater, "UNICAMP", "UNICAMP - Universidade Estadual de Campinas", "Referência internacional em pesquisa, engenharia e inovação.", R.drawable.proximaetapa2);
            adicionarItemFaculdadeHorizontal(container, inflater, "UNESP", "UNESP - Universidade Estadual Paulista", "Excelente infraestrutura acadêmica com forte impacto social.", R.drawable.proximaetapa3);

            SharedPreferences spGeralFacul = requireContext().getSharedPreferences("ListaGeralFaculdadesPlataforma", Context.MODE_PRIVATE);
            String savedFaculs = spGeralFacul.getString("lista_faculdades_cadastradas_string", "");
            if (!savedFaculs.isEmpty()) {
                String[] split = savedFaculs.split("###");
                for (String fStr : split) {
                    String[] p = fStr.split(";;;");
                    String sigla = p.length > 0 ? p[0] : "";
                    String nomeComp = p.length > 1 ? p[1] : sigla;
                    String desc = p.length > 2 ? p[2] : "";
                    int imgResId = R.drawable.proximaetapa1;
                    if (p.length > 3) {
                        try { imgResId = Integer.parseInt(p[3]); } catch (Exception ignored) {}
                    }
                    if (!sigla.isEmpty()) {
                        adicionarItemFaculdadeHorizontal(container, inflater, sigla, nomeComp, desc, imgResId);
                    }
                }
            }
        }
    }

    private void adicionarItemFaculdadeHorizontal(ViewGroup container, LayoutInflater inflater, String sigla, String nomeCompleto, String desc, int imgResId) {
        View fView = inflater.inflate(R.layout.item_faculdade, container, false);
        TextView txtNome = fView.findViewById(R.id.tvNomeFacul);
        ImageView imgLogo = fView.findViewById(R.id.cursoFacul);

        if (txtNome != null) txtNome.setText(sigla);
        if (imgLogo != null) imgLogo.setImageResource(imgResId);

        fView.setOnClickListener(v -> abrirPerfilFaculdadePage(sigla, nomeCompleto, desc, imgResId));
        container.addView(fView);
    }

    private void abrirPerfilFaculdadePage(String sigla, String nomeCompleto, String desc, int caparesId) {
        Intent intent = new Intent(getActivity(), PerfilFaculdadeActivity.class);
        intent.putExtra("sigla", sigla);
        intent.putExtra("nomeCompleto", nomeCompleto);
        intent.putExtra("descricao", desc);
        intent.putExtra("capaResId", caparesId);
        startActivity(intent);
    }

    private void configurarCliquesCursos() {
        if (cardCurso1 != null) {
            cardCurso1.setOnClickListener(v -> abrirDetalhesCurso(
                    "Administração & Gestão",
                    "Curso focado em liderança, processos organizacionais e gestão estratégica. Aulas práticas com mentoria de profissionais renomados.",
                    R.drawable.adm_logo, "USP", "adm", "80 horas", "Segunda, Quarta e Sexta (14h - 16h)"));
        }

        if (cardCurso2 != null) {
            cardCurso2.setOnClickListener(v -> abrirDetalhesCurso(
                    "Logística & Suprimentos",
                    "Aprenda gerenciamento de cadeias de suprimentos, frotas, armazenagem e distribuição eficiente para o mercado moderno.",
                    R.drawable.logistica_logo, "UNICAMP", "logistica", "60 horas", "Terça, Quinta e Sábado (09h - 11h)"));
        }

        if (cardCurso3 != null) {
            cardCurso3.setOnClickListener(v -> abrirDetalhesCurso(
                    "Marketing Digital",
                    "Desenvolva estratégias em mídias sociais, comunicação de marca, tráfego e vendas para grandes empresas e startups.",
                    R.drawable.marketing_logo, "UNESP", "marketing", "50 horas", "Segunda, Quarta e Sexta (16h - 18h)"));
        }

        if (cardCurso4 != null) {
            cardCurso4.setOnClickListener(v -> abrirDetalhesCurso(
                    "Desenvolvimento Web & TI",
                    "Sua jornada no mundo da programação! Aprenda desenvolvimento de software, aplicações web e tecnologia moderna.",
                    R.drawable.logo_proxima_etapa, "USP", "web", "100 horas", "Terça, Quinta e Sábado (14h - 17h)"));
        }
    }

    private void abrirDetalhesCurso(String nome, String descricao, int imagemResId, String faculdade, String idCurso, String carga, String dias) {
        if (getContext() != null && SuspensaoManager.isSuspensoOuBanido(requireContext())) {
            SuspensaoManager.exibirTelaBloqueioCurso(requireContext());
            return;
        }

        Intent intent = new Intent(getActivity(), CursoInfo.class);
        intent.putExtra("nomeCurso", nome);
        intent.putExtra("cursoInfo", descricao);
        intent.putExtra("cursoImagem", imagemResId);
        intent.putExtra("faculdadePertencente", faculdade);
        intent.putExtra("cursoId", idCurso);
        intent.putExtra("cargaHoraria", carga);
        intent.putExtra("diasSemana", dias);
        startActivity(intent);
    }

    private void atualizarCursosDisponiveis() {
        if (getContext() == null) return;
        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
        Set<String> cursosInscritos = sp.getStringSet("lista_cursos_ids", new HashSet<>());

        if (cardCurso1 != null) {
            boolean deletado = sp.getBoolean("curso_deletado_adm", false);
            cardCurso1.setVisibility(deletado || cursosInscritos.contains("adm") ? View.GONE : View.VISIBLE);
        }
        if (cardCurso2 != null) {
            boolean deletado = sp.getBoolean("curso_deletado_logistica", false);
            cardCurso2.setVisibility(deletado || cursosInscritos.contains("logistica") ? View.GONE : View.VISIBLE);
        }
        if (cardCurso3 != null) {
            boolean deletado = sp.getBoolean("curso_deletado_marketing", false);
            cardCurso3.setVisibility(deletado || cursosInscritos.contains("marketing") ? View.GONE : View.VISIBLE);
        }
        if (cardCurso4 != null) {
            boolean deletado = sp.getBoolean("curso_deletado_web", false);
            cardCurso4.setVisibility(deletado || cursosInscritos.contains("web") ? View.GONE : View.VISIBLE);
        }
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
        configurarFerramentasAdmin();
        atualizarCursosDisponiveis();
        renderizarPrincipaisCursos();
        renderizarCursosDinamicosPublicados();
        if (getView() != null) {
            atualizarSininho(getView());
        }
        if (carouselHandler != null && carouselRunnable != null) {
            carouselHandler.removeCallbacks(carouselRunnable);
            carouselHandler.postDelayed(carouselRunnable, 6000);
        }
    }
}