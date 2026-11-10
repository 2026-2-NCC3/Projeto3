package com.example.errornull_proximaetapa.professor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.errornull_proximaetapa.CarouselAdapter;
import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.UserSession;
import com.example.errornull_proximaetapa.aluno.ListaEventosActivity;
import com.example.errornull_proximaetapa.auth.NotificationHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfAgendaFragment extends Fragment {

    private static final String KEY_CUSTOM_EVENTOS = "lista_eventos_custom_string";

    private ViewPager2 viewPagerAvisosCarousel;
    private final Handler carouselHandler = new Handler(Looper.getMainLooper());
    private Runnable carouselRunnable;
    private int currentCarouselPosition = 0;

    private View dot1, dot2, dot3;
    private Button btnVerEventos;
    private LinearLayout contCursosPublicados;
    private TextView txtEventoVazio;

    private View btnSininhoNotificacoes;
    private CalendarView calendarViewAvisos;
    private TextView txtDataSelecionadaTitulo, txtDetalheDataCalendario;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prof_agenda, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dot1 = view.findViewById(R.id.pontoImg1);
        dot2 = view.findViewById(R.id.pontoImg2);
        dot3 = view.findViewById(R.id.pontoImg3);

        btnVerEventos = view.findViewById(R.id.btnVerEventos);
        contCursosPublicados = view.findViewById(R.id.contCursosPublicados);
        txtEventoVazio = view.findViewById(R.id.txtEventoVazio);

        calendarViewAvisos = view.findViewById(R.id.calendarViewAvisos);
        txtDataSelecionadaTitulo = view.findViewById(R.id.txtDataSelecionadaTitulo);
        txtDetalheDataCalendario = view.findViewById(R.id.txtDetalheDataCalendario);

        btnSininhoNotificacoes = view.findViewById(R.id.btnSininhoNotificacoes);
        if (btnSininhoNotificacoes != null) {
            btnSininhoNotificacoes.setOnClickListener(v -> NotificationHelper.exibirCentralNotificacoes(requireContext(), () -> atualizarSininho(view)));
        }

        inicializarCarrossel(view);
        renderizarEventosProfessor();
        configurarCalendario();
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

        viewPagerAvisosCarousel = view.findViewById(R.id.viewPagerCursosCarousel);
        if (viewPagerAvisosCarousel != null) {
            viewPagerAvisosCarousel.setAdapter(new CarouselAdapter(carouselImages));

            viewPagerAvisosCarousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
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
                    if (viewPagerAvisosCarousel != null) {
                        currentCarouselPosition = (currentCarouselPosition + 1) % carouselImages.length;
                        viewPagerAvisosCarousel.setCurrentItem(currentCarouselPosition, true);
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

    private void renderizarEventosProfessor() {
        if (contCursosPublicados == null || getContext() == null) return;
        contCursosPublicados.removeAllViews();

        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
        List<String> listaEventos = carregarEventosSp(sp);

        if (listaEventos.isEmpty()) {
            if (txtEventoVazio != null) txtEventoVazio.setVisibility(View.VISIBLE);
        } else {
            if (txtEventoVazio != null) txtEventoVazio.setVisibility(View.GONE);
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        int limite = Math.min(4, listaEventos.size());

        for (int i = 0; i < limite; i++) {
            String eventoStr = listaEventos.get(i);
            String[] partes = eventoStr.split(";;;");
            String titulo = partes.length > 0 ? partes[0] : "Evento";
            String data = partes.length > 1 ? partes[1] : "Data A Definir";
            String desc = partes.length > 2 ? partes[2] : "";

            View eventView = inflater.inflate(R.layout.item_eventos, contCursosPublicados, false);
            TextView txtTitulo = eventView.findViewById(R.id.txtTituloEvento);
            TextView txtData = eventView.findViewById(R.id.txtData);
            TextView txtDesc = eventView.findViewById(R.id.txtDescricaoEncontro);

            if (txtTitulo != null) txtTitulo.setText(titulo);
            if (txtData != null) txtData.setText(data);
            if (txtDesc != null) txtDesc.setText(desc);

            contCursosPublicados.addView(eventView);
        }

        if (btnVerEventos != null) {
            btnVerEventos.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ListaEventosActivity.class);
                startActivity(intent);
            });
        }
    }

    private List<String> carregarEventosSp(SharedPreferences sp) {
        String savedString = sp.getString(KEY_CUSTOM_EVENTOS, "");
        List<String> lista = new ArrayList<>();

        if (!savedString.isEmpty()) {
            String[] split = savedString.split("###");
            lista.addAll(Arrays.asList(split));
        } else {
            lista.add("Feira de Profissões Próxima Etapa 2026;;;15/04/2026;;;Conecte-se com universidades públicas e privadas e conheça opções de carreira.");
            lista.add("Workshop de Carreira & Mentoria USP;;;28/05/2026;;;Palestra prática sobre os primeiros passos no mercado de trabalho e universidade.");
            lista.add("Semana de Tecnologia & Inovação UNICAMP;;;10/06/2026;;;Demonstração de projetos de computação, robótica e inteligência artificial.");
        }
        return lista;
    }

    private void configurarCalendario() {
        if (calendarViewAvisos != null) {
            calendarViewAvisos.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                String dataSel = String.format("%02d/%02d/%d", dayOfMonth, (month + 1), year);
                if (txtDataSelecionadaTitulo != null) txtDataSelecionadaTitulo.setText("Programação Acadêmica de " + dataSel);
                if (txtDetalheDataCalendario != null) {
                    txtDetalheDataCalendario.setText("Aulas e bancas de avaliação agendadas. Clique em 'Ver Todos os Eventos' para gerenciar os encontros do dia " + dataSel + ".");
                }
            });
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
        renderizarEventosProfessor();
        if (getView() != null) {
            atualizarSininho(getView());
        }
        if (carouselHandler != null && carouselRunnable != null) {
            carouselHandler.removeCallbacks(carouselRunnable);
            carouselHandler.postDelayed(carouselRunnable, 6000);
        }
    }
}
