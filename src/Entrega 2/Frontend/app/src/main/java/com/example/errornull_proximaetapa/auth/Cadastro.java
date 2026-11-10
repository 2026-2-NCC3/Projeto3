package com.example.errornull_proximaetapa.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.UserSession;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Cadastro extends AppCompatActivity {

    private TextView txtIndicadorEtapa;
    private RadioGroup rgTipoUsuario;
    private Button btnIrLogin;

    private CadastroViewModel viewModel;
    private ApiService api;
    private boolean abasBloqueadas = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_cadastro);

        viewModel = new ViewModelProvider(this).get(CadastroViewModel.class);

        inicializarViews();
        configurarNavegacaoAbas();
        configurarRetrofit();

        // Inicia na Etapa 1
        navegarParaEtapa(1);
    }

    private void inicializarViews() {
        rgTipoUsuario = findViewById(R.id.rg_tipo_usuario);
        txtIndicadorEtapa = findViewById(R.id.txtIndicadorEtapa);
        btnIrLogin = findViewById(R.id.btn_ir_login);

        if (btnIrLogin != null) {
            btnIrLogin.setOnClickListener(v -> {
                startActivity(new Intent(Cadastro.this, LoginInfo.class));
                finish();
            });
        }
    }

    private void configurarNavegacaoAbas() {
        if (rgTipoUsuario != null) {
            rgTipoUsuario.setOnCheckedChangeListener((group, checkedId) -> {
                if (abasBloqueadas) {
                    Toast.makeText(this, "O tipo de conta não pode ser alterado após iniciar o preenchimento.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (checkedId == R.id.rb_aluno) {
                    selecionarTipoUsuario("aluno");
                } else if (checkedId == R.id.rb_professor) {
                    selecionarTipoUsuario("professor");
                }
            });
        }
    }

    public void bloquearTabs(boolean bloquear) {
        this.abasBloqueadas = bloquear;
        if (rgTipoUsuario != null) {
            for (int i = 0; i < rgTipoUsuario.getChildCount(); i++) {
                rgTipoUsuario.getChildAt(i).setEnabled(!bloquear);
                rgTipoUsuario.getChildAt(i).setAlpha(bloquear ? 0.6f : 1.0f);
            }
        }
    }

    public void selecionarTipoUsuario(String tipo) {
        viewModel.setTipoUsuario(tipo);

        Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_cadastro);
        if (activeFragment instanceof CadastroPt2Fragment) {
            ((CadastroPt2Fragment) activeFragment).atualizarVisibilidadeTipo(tipo);
        } else if (activeFragment instanceof CadastroPt3Fragment) {
            ((CadastroPt3Fragment) activeFragment).atualizarVisibilidadeTipo(tipo);
        }
    }

    public void navegarParaEtapa(int etapa) {
        Fragment fragment;
        if (etapa == 1) {
            fragment = new CadastroPt1Fragment();
            if (txtIndicadorEtapa != null) txtIndicadorEtapa.setText("Parte 1 de 3: Informações Pessoais");
        } else if (etapa == 2) {
            bloquearTabs(true); // Bloqueia abas ao passar para a Parte 2
            fragment = new CadastroPt2Fragment();
            if (txtIndicadorEtapa != null) txtIndicadorEtapa.setText("Parte 2 de 3: Informações Escolares");
        } else {
            bloquearTabs(true); // Bloqueia abas ao passar para a Parte 3
            fragment = new CadastroPt3Fragment();
            if (txtIndicadorEtapa != null) txtIndicadorEtapa.setText("Parte 3 de 3: Personalização de Perfil");
        }

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container_cadastro, fragment)
                .commit();
    }

    private void configurarRetrofit() {
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
    }

    public void executarCadastroFinal() {
        String cpf = viewModel.getCpf();
        String nome = viewModel.getNome();
        String email = viewModel.getEmail();
        String senha = viewModel.getSenha();
        String tipoUsuario = viewModel.getTipoUsuario();

        if (cpf.isEmpty() || nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(Cadastro.this, "Preencha os campos obrigatórios na Parte 1!", Toast.LENGTH_SHORT).show();
            navegarParaEtapa(1);
            return;
        }

        String telAluno = null, escola = null, cidade = null, grade = null;
        String telProf = null, especialidade = null, cargo = null;

        if ("aluno".equals(tipoUsuario)) {
            telAluno = viewModel.getTelefoneAluno();
            escola = viewModel.getEscola();
            cidade = viewModel.getCidade();
            grade = viewModel.getGrade();
        } else if ("professor".equals(tipoUsuario)) {
            telProf = viewModel.getTelefoneProfessor();
            especialidade = viewModel.getEspecialidade();
        } else if ("admin".equals(tipoUsuario)) {
            cargo = viewModel.getCargo();
        }

        // Define a sessão e papel do usuário recém-cadastrado
        SessionManager.setRole(Cadastro.this, tipoUsuario);
        UserSession.setUsuarioAtual(Cadastro.this, email);

        // Salvar dados do perfil no SharedPreferences
        SharedPreferences sp = UserSession.getScopedPreferences(Cadastro.this);
        sp.edit()
                .putString("nome", nome)
                .putString("email", email)
                .putString("cpf", cpf)
                .putString("tipoUsuario", tipoUsuario)
                .putString("idade", viewModel.getIdade())
                .putString("estado", viewModel.getCidade())
                .putString("escola", escola)
                .putString("grade", grade)
                .putString("cidade", cidade)
                .putString("pretensaoFaculdade", viewModel.getPretensaoFaculdade())
                .putString("bio", viewModel.getBio())
                .putString("preferencias", viewModel.getPreferencias())
                .putString("foto_perfil_uri", viewModel.getFotoUri())
                .apply();

        CadastroRequest request = new CadastroRequest(
                cpf, nome, email, senha, tipoUsuario,
                telAluno, escola, cidade, grade,
                telProf, especialidade, cargo
        );

        api.fazerCadastro(request).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Cadastro.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Cadastro.this, LoginInfo.class));
                    finish();
                } else {
                    try {
                        String erroMsg = response.errorBody() != null ? response.errorBody().string() : "";
                        Toast.makeText(Cadastro.this, "Erro ao cadastrar. Verifique os dados.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(Cadastro.this, "Erro: E-mail ou CPF já cadastrados no sistema!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(Cadastro.this, "Erro: " + t.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERRO", "Detalhe completo da falha: ", t);
            }
        });
    }

    private boolean validarEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validarSenha(String senha) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{6,}$";
        return Pattern.matches(regex, senha);
    }
}
