package com.example.errornull_proximaetapa;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

//import com.example.errornull_proximaetapa.Fragments.ConfigFragment;
import com.example.errornull_proximaetapa.aluno.AlunoAvisosFragment;
import com.example.errornull_proximaetapa.aluno.AlunoConfigFragment;
import com.example.errornull_proximaetapa.aluno.AlunoCursosFragment;
import com.example.errornull_proximaetapa.aluno.AlunoHomeFragment;
import com.example.errornull_proximaetapa.aluno.AlunoPerfilFragment;
import com.example.errornull_proximaetapa.administrador.AdmConfigFragment;
import com.example.errornull_proximaetapa.administrador.AdmInfoCursosFragment;
import com.example.errornull_proximaetapa.administrador.AdmInfoUserFragment;
import com.example.errornull_proximaetapa.administrador.AdmModeradorFragment;
import com.example.errornull_proximaetapa.auth.SessionManager;
import com.example.errornull_proximaetapa.professor.ProfAgendaFragment;
import com.example.errornull_proximaetapa.professor.ProfConfigFragment;
import com.example.errornull_proximaetapa.professor.ProfEscolaFragment;
import com.example.errornull_proximaetapa.professor.ProfHomeFragment;
import com.example.errornull_proximaetapa.professor.ProfPerfilFragment;
import com.example.errornull_proximaetapa.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FloatingActionButton menu_perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Integração Contínua da Barra Amarela
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(getColor(R.color.yellow));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getWindow().setNavigationBarContrastEnforced(false);
            }
        }

        menu_perfil = findViewById(R.id.menu_perfil);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, windowInsets) -> {
            if (binding.bottomappbar != null && binding.bottomappbar.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.bottomappbar.getLayoutParams();
                params.bottomMargin = 0;
                binding.bottomappbar.setLayoutParams(params);
            }
            return windowInsets;
        });

        atualizarBotaoPerfil(false);
        configurarMenuNavegacaoPorPapel();

        menu_perfil.setOnClickListener(v -> {
            mudarFragment(getFragmentPerfilPorRole());
            binding.bottomnavview.getMenu()
                    .findItem(R.id.menu_nada)
                    .setChecked(true);
            atualizarBotaoPerfil(true);
            menu_perfil.animate()
                    .scaleX(0.88f)
                    .scaleY(0.88f)
                    .setDuration(100)
                    .withEndAction(() ->
                            menu_perfil.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(160)
                                    .start()
                    )
                    .start();
        });

        // Cliques nos itens normais da barra com roteamento exclusivo de Fragment por perfil
        binding.bottomnavview.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_nada) {
                mudarFragment(getFragmentPerfilPorRole());
                atualizarBotaoPerfil(true);
                return true;
            } else if (id == R.id.menu_home) {
                mudarFragment(getFragmentHomePorRole());
            } else if (id == R.id.menu_cursos) {
                mudarFragment(getFragmentCursosPorRole());
            } else if (id == R.id.menu_avisos) {
                mudarFragment(getFragmentAvisosPorRole());
            } else if (id == R.id.menu_config) {
                mudarFragment(getFragmentConfigPorRole());
            } else {
                return false;
            }

            atualizarBotaoPerfil(false);
            return true;
        });

        if (getIntent().getBooleanExtra("abrir_perfil", false)) {
            mudarFragment(getFragmentPerfilPorRole());
            atualizarBotaoPerfil(true);
        } else {
            mudarFragment(getFragmentHomePorRole());
        }
    }

    private Fragment getFragmentHomePorRole() {
        String role = SessionManager.getRole(this);
        if ("professor".equalsIgnoreCase(role)) {
            return new ProfHomeFragment();
        } else if ("admin".equalsIgnoreCase(role)) {
            return new AdmModeradorFragment();
        } else {
            return new AlunoHomeFragment();
        }
    }

    private Fragment getFragmentCursosPorRole() {
        String role = SessionManager.getRole(this);
        if ("professor".equalsIgnoreCase(role)) {
            return new ProfEscolaFragment();
        } else if ("admin".equalsIgnoreCase(role)) {
            return new AdmInfoCursosFragment();
        } else {
            return new AlunoCursosFragment();
        }
    }

    private Fragment getFragmentAvisosPorRole() {
        String role = SessionManager.getRole(this);
        if ("professor".equalsIgnoreCase(role)) {
            return new ProfAgendaFragment();
        } else if ("admin".equalsIgnoreCase(role)) {
            return new AdmInfoUserFragment();
        } else {
            return new AlunoAvisosFragment();
        }
    }

    private Fragment getFragmentPerfilPorRole() {
        String role = SessionManager.getRole(this);
        if ("professor".equalsIgnoreCase(role)) {
            return new ProfPerfilFragment();
        } else if ("admin".equalsIgnoreCase(role)) {
            return new AdmInfoUserFragment();
        } else {
            return new AlunoPerfilFragment();
        }
    }

    private Fragment getFragmentConfigPorRole() {
        String role = SessionManager.getRole(this);
        if ("professor".equalsIgnoreCase(role)) {
            return new ProfConfigFragment();
        } else if ("admin".equalsIgnoreCase(role)) {
            return new AdmConfigFragment();
        } else {
            return new AlunoConfigFragment();
        }
    }

    private void configurarMenuNavegacaoPorPapel() {
        if (binding == null || binding.bottomnavview == null || binding.bottomnavview.getMenu() == null) return;
        String tipo = SessionManager.getRole(this);

        MenuItem itemHome = binding.bottomnavview.getMenu().findItem(R.id.menu_home);
        MenuItem itemCursos = binding.bottomnavview.getMenu().findItem(R.id.menu_cursos);
        MenuItem itemAvisos = binding.bottomnavview.getMenu().findItem(R.id.menu_avisos);
        MenuItem itemConfig = binding.bottomnavview.getMenu().findItem(R.id.menu_config);

        if ("professor".equalsIgnoreCase(tipo)) {
            if (itemHome != null) {
                itemHome.setTitle("Home");
                itemHome.setIcon(R.drawable.icon_round_home);
            }
            if (itemCursos != null) {
                itemCursos.setTitle("Turmas");
                itemCursos.setIcon(R.drawable.icon_round_school);
            }
            if (itemAvisos != null) {
                itemAvisos.setTitle("Agenda");
                itemAvisos.setIcon(R.drawable.icon_round_calendar);
            }
            if (itemConfig != null) {
                itemConfig.setTitle("Ajustes");
                itemConfig.setIcon(R.drawable.icon_round_settings);
            }
        } else if ("admin".equalsIgnoreCase(tipo)) {
            if (itemHome != null) {
                itemHome.setTitle("Moderador");
                itemHome.setIcon(R.drawable.icon_round_add_moderator);
            }
            if (itemCursos != null) {
                itemCursos.setTitle("Gerenciar");
                itemCursos.setIcon(R.drawable.icon_round_assignment);
            }
            if (itemAvisos != null) {
                itemAvisos.setTitle("Info");
                itemAvisos.setIcon(R.drawable.icon_round_info);
            }
            if (itemConfig != null) {
                itemConfig.setTitle("Ajustes");
                itemConfig.setIcon(R.drawable.icon_round_settings);
            }
        } else {
            if (itemHome != null) {
                itemHome.setTitle("Home");
                itemHome.setIcon(R.drawable.icon_round_home);
            }
            if (itemCursos != null) {
                itemCursos.setTitle("Cursos");
                itemCursos.setIcon(R.drawable.icon_round_menu_book);
            }
            if (itemAvisos != null) {
                itemAvisos.setTitle("Avisos");
                itemAvisos.setIcon(R.drawable.icon_round_calendar);
            }
            if (itemConfig != null) {
                itemConfig.setTitle("Config");
                itemConfig.setIcon(R.drawable.icon_round_settings);
            }
        }
    }

    private void mudarFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    private void atualizarBotaoPerfil(boolean selecionado) {
        int corIcone = selecionado ? Color.WHITE : Color.BLACK;

        menu_perfil.setImageTintList(ColorStateList.valueOf(corIcone));

        menu_perfil.animate()
                .scaleX(selecionado ? 1.08f : 1f)
                .scaleY(selecionado ? 1.08f : 1f)
                .setDuration(180)
                .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        configurarMenuNavegacaoPorPapel();
    }
}