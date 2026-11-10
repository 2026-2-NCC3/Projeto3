package com.example.errornull_proximaetapa.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.errornull_proximaetapa.CarrosselAdapter;
import com.example.errornull_proximaetapa.MainActivity;
import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.UserSession;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginInfo extends AppCompatActivity {

    private EditText inputEmail, inputSenha;
    private Button btnLogar, btnIrParaCadastro;
    private TextView tabLoginAluno, tabLoginProfessor;
    private View ponto1, ponto2, ponto3;
    private ViewPager2 viewPagerCarrossel;
    private CarrosselAdapter adapter;
    private ApiService api;
    private String selectedRole = "aluno";

    private final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPagerCarrossel != null && adapter != null) {
                int nextItem = (viewPagerCarrossel.getCurrentItem() + 1) % adapter.getItemCount();
                viewPagerCarrossel.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 6000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_login);

        inputEmail = findViewById(R.id.input_email);
        inputSenha = findViewById(R.id.input_senha);
        btnLogar = findViewById(R.id.btn_logar);
        btnIrParaCadastro = findViewById(R.id.btn_ir_cadastro);

        tabLoginAluno = findViewById(R.id.tabLoginAluno);
        tabLoginProfessor = findViewById(R.id.tabLoginProfessor);

        ponto1 = findViewById(R.id.ponto1);
        ponto2 = findViewById(R.id.ponto2);
        ponto3 = findViewById(R.id.ponto3);

        // Configuração do Carrossel com transição suave (Fade in / Fade out)
        viewPagerCarrossel = findViewById(R.id.viewPagerCarrossel);
        if (viewPagerCarrossel != null) {
            int[] imagens = {
                    R.drawable.proximaetapa1,
                    R.drawable.proximaetapa2,
                    R.drawable.proximaetapa3,
                    R.drawable.proximaetapa4,
                    R.drawable.proximaetapa5,
                    R.drawable.proximaetapa6
            };
            adapter = new CarrosselAdapter(imagens);
            viewPagerCarrossel.setAdapter(adapter);

            // Transição Fade suave
            viewPagerCarrossel.setPageTransformer((page, position) -> {
                page.setTranslationX(-position * page.getWidth());
                if (position < -1 || position > 1) {
                    page.setAlpha(0f);
                } else if (position <= 0) {
                    page.setAlpha(1 + position);
                } else if (position <= 1) {
                    page.setAlpha(1 - position);
                }
            });

            viewPagerCarrossel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    int index = position % 3;
                    updateDots(index);
                }
            });
        }

        // Configuração das Abas de Tipo de Login
        View.OnClickListener roleClickListener = v -> {
            int id = v.getId();
            if (id == R.id.tabLoginAluno) {
                selectedRole = "aluno";
                if (tabLoginAluno != null) tabLoginAluno.setBackgroundResource(R.drawable.bg_tab_selected);
                if (tabLoginProfessor != null) tabLoginProfessor.setBackgroundResource(R.drawable.bg_tab_unselected);
            } else if (id == R.id.tabLoginProfessor) {
                selectedRole = "professor";
                if (tabLoginAluno != null) tabLoginAluno.setBackgroundResource(R.drawable.bg_tab_unselected);
                if (tabLoginProfessor != null) tabLoginProfessor.setBackgroundResource(R.drawable.bg_tab_selected);
            }
        };

        if (tabLoginAluno != null) tabLoginAluno.setOnClickListener(roleClickListener);
        if (tabLoginProfessor != null) tabLoginProfessor.setOnClickListener(roleClickListener);

        // Configuração do OkHttpClient com timeout estendido para o Render acordar
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pi3-kc5f.onrender.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ApiService.class);

        btnLogar.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String senha = inputSenha.getText().toString().trim();

            if (email.length() < 5 || !email.contains("@")) {
                Toast.makeText(LoginInfo.this, "Informe um e-mail válido com '@' e no mínimo 2 caracteres antes do '@'!", Toast.LENGTH_SHORT).show();
                inputEmail.setError("E-mail inválido!");
                return;
            }

            String[] partesEmail = email.split("@");
            if (partesEmail[0].length() < 2) {
                Toast.makeText(LoginInfo.this, "O e-mail deve ter no mínimo 2 caracteres antes do '@'!", Toast.LENGTH_SHORT).show();
                inputEmail.setError("Mínimo de 2 caracteres antes do '@'!");
                return;
            }

            String dominio = partesEmail.length > 1 ? partesEmail[1].toLowerCase() : "";
            if (!dominio.contains(".com") && !dominio.contains(".br")) {
                Toast.makeText(LoginInfo.this, "O e-mail deve conter um domínio com .com ou .br (ex: usuario@email.com, usuario@gmail.com)!", Toast.LENGTH_SHORT).show();
                inputEmail.setError("Domínio deve conter .com ou .br!");
                return;
            }

            if (senha.length() < 2) {
                Toast.makeText(LoginInfo.this, "A senha deve ter no mínimo 2 caracteres!", Toast.LENGTH_SHORT).show();
                inputSenha.setError("Mínimo de 2 caracteres!");
                return;
            }

            LoginRequest request = new LoginRequest(email, senha);

            api.fazerLogin(request).enqueue(new Callback<TokenResponse>() {
                @Override
                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().getToken();
                        Toast.makeText(LoginInfo.this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();

                        // Salva sessão e papel do usuário validados pelo back-end
                        SessionManager.setRole(LoginInfo.this, selectedRole);
                        UserSession.setUsuarioAtual(LoginInfo.this, email);
                        SharedPreferences sp = UserSession.getScopedPreferences(LoginInfo.this);
                        sp.edit().putString("tipoUsuario", selectedRole).putString("email", email).apply();

                        // Redireciona para MainActivity e fecha a tela de login
                        Intent intent = new Intent(LoginInfo.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LoginInfo.this, "E-mail ou senha inválidos.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TokenResponse> call, Throwable t) {
                    Toast.makeText(LoginInfo.this, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show();
                    Log.e("API_ERRO", "Falha: " + t.getMessage());
                }
            });
        });

        if (btnIrParaCadastro != null) {
            btnIrParaCadastro.setOnClickListener(v -> {
                startActivity(new Intent(LoginInfo.this, Cadastro.class));
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 6000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    private void updateDots(int activeIndex) {
        float density = getResources().getDisplayMetrics().density;
        int activeWidth = (int) (20 * density);
        int inactiveWidth = (int) (8 * density);

        if (ponto1 != null) {
            ponto1.setBackgroundResource(activeIndex == 0 ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
            ponto1.getLayoutParams().width = activeIndex == 0 ? activeWidth : inactiveWidth;
            ponto1.requestLayout();
        }
        if (ponto2 != null) {
            ponto2.setBackgroundResource(activeIndex == 1 ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
            ponto2.getLayoutParams().width = activeIndex == 1 ? activeWidth : inactiveWidth;
            ponto2.requestLayout();
        }
        if (ponto3 != null) {
            ponto3.setBackgroundResource(activeIndex == 2 ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
            ponto3.getLayoutParams().width = activeIndex == 2 ? activeWidth : inactiveWidth;
            ponto3.requestLayout();
        }
    }
}
