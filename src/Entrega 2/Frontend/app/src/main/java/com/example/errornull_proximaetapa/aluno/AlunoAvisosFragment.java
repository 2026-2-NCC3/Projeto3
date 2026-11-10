package com.example.errornull_proximaetapa.aluno;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.errornull_proximaetapa.CarouselAdapter;
import com.example.errornull_proximaetapa.auth.NotificationHelper;
import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.auth.RequerimentosManager;
import com.example.errornull_proximaetapa.UserSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlunoAvisosFragment extends Fragment {

    private static final String KEY_CUSTOM_EVENTOS = "lista_eventos_custom_string";

    private ViewPager2 viewPagerAvisosCarousel;
    private final Handler carouselHandler = new Handler(Looper.getMainLooper());
    private Runnable carouselRunnable;
    private int currentCarouselPosition = 0;
    private View ponto1, ponto2, ponto3, btnWhats, btnInsta, btnYt, btnSininhoNotificacoes;
    private Button btnCriarEvento, btnNovoRequerimentoAvisos, btnVerTodosEventos;
    private LinearLayout containerEventos, containerRequerimentosAtivosAvisos;
    private CalendarView calendarViewAvisos;
    private TextView txtDataSelecionadaTitulo, txtDetalheDataCalendario;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aluno_avisos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ponto1 = view.findViewById(R.id.pontoImg1);
        ponto2 = view.findViewById(R.id.pontoImg2);
        ponto3 = view.findViewById(R.id.pontoImg3);

        btnCriarEvento = view.findViewById(R.id.btnCriarEvento);
        btnNovoRequerimentoAvisos = view.findViewById(R.id.btnNovoRequerimentoAvisos);
        btnVerTodosEventos = view.findViewById(R.id.btnVerTodosEventos);

        containerEventos = view.findViewById(R.id.contCursosPublicados);
        containerRequerimentosAtivosAvisos = view.findViewById(R.id.containerRequerimentosAtivosAvisos);

        btnWhats = view.findViewById(R.id.btnWhats);
        btnInsta = view.findViewById(R.id.btnInsta);
        btnYt = view.findViewById(R.id.btnYt);

        calendarViewAvisos = view.findViewById(R.id.calendarViewAvisos);
        txtDataSelecionadaTitulo = view.findViewById(R.id.txtDataSelecionadaTitulo);
        txtDetalheDataCalendario = view.findViewById(R.id.txtDetalheDataCalendario);

        btnSininhoNotificacoes = view.findViewById(R.id.btnSininhoNotificacoes);
        if (btnSininhoNotificacoes != null) {
            btnSininhoNotificacoes.setOnClickListener(v -> NotificationHelper.exibirCentralNotificacoes(requireContext(), () -> atualizarSininho(view)));
        }

        inicializarCarrossel(view);
        configurarEventos();
        configurarCalendario();
        configurarContatos();
        renderizarRequerimentosAtivosAluno();
        atualizarSininho(view);
    }

    private void renderizarRequerimentosAtivosAluno() {
        if (containerRequerimentosAtivosAvisos == null || getContext() == null) return;
        containerRequerimentosAtivosAvisos.removeAllViews();

        SharedPreferences spUser = UserSession.getScopedPreferences(requireContext());
        String tipoUsuario = spUser.getString("tipoUsuario", "aluno");

        if ("admin".equalsIgnoreCase(tipoUsuario)) {
            Button btnPainelAdm = new Button(requireContext());
            btnPainelAdm.setText("📬 Abrir Painel de Gestão de Requerimentos dos Alunos");
            btnPainelAdm.setBackgroundTintList(android.content.res.ColorStateList.valueOf(requireContext().getColor(R.color.yellow)));
            btnPainelAdm.setTextColor(requireContext().getColor(R.color.dark_indigo));
            btnPainelAdm.setTypeface(null, Typeface.BOLD);
            btnPainelAdm.setTextSize(12);

            btnPainelAdm.setOnClickListener(v -> RequerimentosManager.abrirPainelRequerimentosAdmin(requireContext()));
            containerRequerimentosAtivosAvisos.addView(btnPainelAdm);
            return;
        }

        String myEmail = UserSession.getCurrentUserEmail(requireContext());
        List<String> todosReqs = RequerimentosManager.getListaRequerimentosGeral(requireContext());
        int meusReqsCount = 0;

        for (String reqStr : todosReqs) {
            String[] p = reqStr.split("\\|\\|\\|");
            String emailAluno = p.length > 1 ? p[1] : "";
            String tipoProb = p.length > 3 ? p[3] : "Solicitação";
            String relato = p.length > 4 ? p[4] : "";
            String dataHora = p.length > 5 ? p[5] : "";
            String status = p.length > 6 ? p[6] : "Pendente";
            String resposta = p.length > 7 ? p[7] : "Sem resposta da Direção ainda";

            if (emailAluno.equalsIgnoreCase(myEmail) || emailAluno.isEmpty() || myEmail.contains("convidado")) {
                meusReqsCount++;

                LinearLayout cardReq = new LinearLayout(requireContext());
                cardReq.setOrientation(LinearLayout.VERTICAL);
                cardReq.setBackgroundResource(R.drawable.bg_card_sobreposicao2);
                cardReq.setPadding(16, 14, 16, 14);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 4, 0, 10);
                cardReq.setLayoutParams(lp);

                TextView txtTipo = new TextView(requireContext());
                txtTipo.setText("📬 " + tipoProb + " (" + dataHora + ")");
                txtTipo.setTextColor(requireContext().getColor(R.color.purple));
                txtTipo.setTextSize(13);
                txtTipo.setTypeface(null, Typeface.BOLD);

                TextView txtRel = new TextView(requireContext());
                txtRel.setText("Relato: " + relato);
                txtRel.setTextColor(requireContext().getColor(R.color.dark_indigo));
                txtRel.setTextSize(12);
                txtRel.setPadding(0, 4, 0, 4);

                TextView txtSt = new TextView(requireContext());
                boolean respondido = "Respondido".equalsIgnoreCase(status);
                txtSt.setText("Status: " + (respondido ? "✅ RESPONDIDO PELA DIREÇÃO" : "⏳ AGUARDANDO RESPOSTA DA DIREÇÃO"));
                txtSt.setTextColor(requireContext().getColor(respondido ? R.color.mint : R.color.dark_gray));
                txtSt.setTextSize(11);
                txtSt.setTypeface(null, Typeface.BOLD);

                cardReq.addView(txtTipo);
                cardReq.addView(txtRel);
                cardReq.addView(txtSt);

                if (respondido) {
                    TextView txtResp = new TextView(requireContext());
                    txtResp.setText("💬 Resposta ADM: " + resposta);
                    txtResp.setTextColor(requireContext().getColor(R.color.dark_indigo));
                    txtResp.setTextSize(12);
                    txtResp.setTypeface(null, Typeface.BOLD);
                    txtResp.setBackgroundResource(R.drawable.bg_pill_yellow);
                    txtResp.setPadding(12, 8, 12, 8);
                    cardReq.addView(txtResp);
                }

                containerRequerimentosAtivosAvisos.addView(cardReq);
            }
        }

        if (meusReqsCount == 0) {
            TextView txtVazio = new TextView(requireContext());
            txtVazio.setText("Nenhum requerimento enviado por você ainda. Clique em '➕ Novo Requerimento' para solicitar atestados, declarações ou suporte!");
            txtVazio.setTextColor(requireContext().getColor(R.color.dark_gray));
            txtVazio.setTextSize(12);
            txtVazio.setPadding(8, 8, 8, 8);
            containerRequerimentosAtivosAvisos.addView(txtVazio);
        }
    }

    private void exibirDialogCriarRequerimentoAluno() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_form_requerimento_aluno, null);

        Spinner spinnerTipo = dialogView.findViewById(R.id.spinnerTipoProblemaRequerimento);
        EditText inputRelato = dialogView.findViewById(R.id.inputRelatoProblemaRequerimento);
        Button btnCancelar = dialogView.findViewById(R.id.btnFecharRequerimento);
        Button btnEnviar = dialogView.findViewById(R.id.btnEnviarRequerimento);

        if (spinnerTipo != null) {
            String[] tiposArr = {
                    "Solicitação de Atestado / Declaração",
                    "Dúvidas sobre Matrícula / Cursos",
                    "Problema Técnico / Acesso à Plataforma",
                    "Denúncia ou Outro Assunto"
            };
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, tiposArr);
            spinnerTipo.setAdapter(adapter);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnCancelar != null) btnCancelar.setOnClickListener(v -> dialog.dismiss());

        if (btnEnviar != null && inputRelato != null && spinnerTipo != null) {
            btnEnviar.setOnClickListener(v -> {
                String email = UserSession.getUsuarioAtual(requireContext());
                if (email.isEmpty() || "aluno@exemplo.com".equalsIgnoreCase(email) || "usuario_convidado".equalsIgnoreCase(email)) {
                    NotificationHelper.exibirToastCustomizado(getContext(), "🔒 Faça login ou cadastre-se para enviar requerimentos!");
                    Intent loginIntent = new Intent(getActivity(), com.example.errornull_proximaetapa.auth.LoginInfo.class);
                    startActivity(loginIntent);
                    dialog.dismiss();
                    return;
                }

                String relato = inputRelato.getText().toString().trim();
                String tipoProb = spinnerTipo.getSelectedItem() != null ? spinnerTipo.getSelectedItem().toString() : "Geral";

                if (relato.isEmpty()) {
                    NotificationHelper.exibirToastCustomizado(getContext(), "Descreva o seu relato/solicitação!");
                    return;
                }

                SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
                String nome = sp.getString("nome", "Estudante");

                RequerimentosManager.enviarRequerimento(requireContext(), email, nome, tipoProb, relato);
                renderizarRequerimentosAtivosAluno();
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
        if (ponto1 == null || ponto2 == null || ponto3 == null) return;

        ponto1.setBackgroundResource(position == 0 ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
        ponto2.setBackgroundResource(position == 1 ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
        ponto3.setBackgroundResource(position == 2 ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
    }

    private void configurarEventos() {
        if (getContext() == null) return;
        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
        String tipoUsuario = sp.getString("tipoUsuario", "aluno");

        boolean isAdmin = "admin".equalsIgnoreCase(tipoUsuario) || "professor".equalsIgnoreCase(tipoUsuario);

        if (btnCriarEvento != null) {
            btnCriarEvento.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            btnCriarEvento.setOnClickListener(v -> exibirDialogCriarEvento());
        }

        if (btnNovoRequerimentoAvisos != null) {
            btnNovoRequerimentoAvisos.setVisibility("aluno".equalsIgnoreCase(tipoUsuario) ? View.VISIBLE : View.GONE);
            btnNovoRequerimentoAvisos.setOnClickListener(v -> exibirDialogCriarRequerimentoAluno());
        }

        renderizarListaEventos();
    }

    private void renderizarListaEventos() {
        if (containerEventos == null || getContext() == null) return;
        containerEventos.removeAllViews();

        SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
        List<String> listaEventos = carregarEventosSp(sp);

        if (btnVerTodosEventos != null) {
            btnVerTodosEventos.setText("🔍 Ver Todos os Eventos (" + listaEventos.size() + ")");
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        int limite = Math.min(3, listaEventos.size());

        for (int i = 0; i < limite; i++) {
            String eventoStr = listaEventos.get(i);
            String[] partes = eventoStr.split(";;;");
            String titulo = partes.length > 0 ? partes[0] : "Evento";
            String data = partes.length > 1 ? partes[1] : "Data A Definir";
            String desc = partes.length > 2 ? partes[2] : "";

            View eventView = inflater.inflate(R.layout.item_eventos, containerEventos, false);
            TextView txtTitulo = eventView.findViewById(R.id.txtTituloEvento);
            TextView txtData = eventView.findViewById(R.id.txtData);
            TextView txtDesc = eventView.findViewById(R.id.txtDescricaoEncontro);

            if (txtTitulo != null) txtTitulo.setText(titulo);
            if (txtData != null) txtData.setText(data);
            if (txtDesc != null) txtDesc.setText(desc);

            containerEventos.addView(eventView);
        }

        if (btnVerTodosEventos != null) {
            btnVerTodosEventos.setOnClickListener(v -> abrirTelaTodosEventosActivity());
        }
    }

    private void abrirTelaTodosEventosActivity() {
        if (getContext() == null) return;
        Intent intent = new Intent(getActivity(), ListaEventosActivity.class);
        startActivity(intent);
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
            salvarEventosSp(sp, lista);
        }
        return lista;
    }

    private void salvarEventosSp(SharedPreferences sp, List<String> lista) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            sb.append(lista.get(i));
            if (i < lista.size() - 1) sb.append("###");
        }
        sp.edit().putString(KEY_CUSTOM_EVENTOS, sb.toString()).apply();
    }

    private void exibirDialogCriarEvento() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_criar_evento, null);

        EditText inputTitulo = dialogView.findViewById(R.id.inputTituloEvento);
        EditText inputData = dialogView.findViewById(R.id.inputDataEvento);
        EditText inputDesc = dialogView.findViewById(R.id.inputDescricaoEvento);
        Button btnSalvar = dialogView.findViewById(R.id.btnSalvarNovoEvento);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnSalvar != null) {
            btnSalvar.setOnClickListener(v -> {
                String titulo = inputTitulo != null ? inputTitulo.getText().toString().trim() : "";
                String data = inputData != null ? inputData.getText().toString().trim() : "";
                String desc = inputDesc != null ? inputDesc.getText().toString().trim() : "";

                if (titulo.isEmpty() || data.isEmpty()) {
                    NotificationHelper.exibirToastCustomizado(getContext(), "Preencha o título e a data do evento!");
                    return;
                }

                SharedPreferences sp = UserSession.getScopedPreferences(requireContext());
                List<String> lista = carregarEventosSp(sp);
                lista.add(0, titulo + ";;;" + data + ";;;" + desc);
                salvarEventosSp(sp, lista);

                NotificationHelper.adicionarNotificacao(requireContext(), "Novo Evento Cadastrado", "O evento '" + titulo + "' foi agendado para " + data + "!");
                NotificationHelper.exibirToastCustomizado(getContext(), "Novo evento cadastrado com sucesso!");
                renderizarListaEventos();
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void configurarCalendario() {
        if (calendarViewAvisos != null) {
            calendarViewAvisos.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                String dataSel = String.format("%02d/%02d/%d", dayOfMonth, (month + 1), year);
                if (txtDataSelecionadaTitulo != null) txtDataSelecionadaTitulo.setText("Programação de " + dataSel);
                if (txtDetalheDataCalendario != null) {
                    txtDetalheDataCalendario.setText("Aulas regulares da sua grade agendadas. Clique em 'Ver Todos os Eventos' para conferir palestras no dia " + dataSel + "!");
                }
            });
        }
    }

    private void configurarContatos() {
        if (btnWhats != null) {
            btnWhats.setOnClickListener(v -> {
                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=5511999998888&text=Olá! Preciso de suporte no Próxima Etapa.");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    NotificationHelper.exibirToastCustomizado(getContext(), "WhatsApp Suporte: (11) 99999-8888");
                }
            });
        }

        if (btnInsta != null) {
            btnInsta.setOnClickListener(v -> {
                Uri uri = Uri.parse("https://www.instagram.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    NotificationHelper.exibirToastCustomizado(getContext(), "Instagram: @proximaetapa");
                }
            });
        }

        if (btnYt != null) {
            btnYt.setOnClickListener(v -> {
                Uri uri = Uri.parse("https://www.youtube.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    NotificationHelper.exibirToastCustomizado(getContext(), "YouTube: Canal Próxima Etapa");
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
        configurarEventos();
        renderizarRequerimentosAtivosAluno();
        if (getView() != null) {
            atualizarSininho(getView());
        }
        if (carouselHandler != null && carouselRunnable != null) {
            carouselHandler.removeCallbacks(carouselRunnable);
            carouselHandler.postDelayed(carouselRunnable, 6000);
        }
    }
}