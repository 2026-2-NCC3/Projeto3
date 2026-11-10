package com.example.errornull_proximaetapa.professor;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.UserSession;
import com.example.errornull_proximaetapa.databinding.ActivityMainBinding;

public class ProfessorActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = UserSession.getScopedPreferences(this);
        sp.edit().putString("tipoUsuario", "professor").apply();

        UserSession.aplicarPreferenciasTema(this);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(getColor(R.color.yellow));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getWindow().setNavigationBarContrastEnforced(false);
            }
        }

        configurarNavegacaoProfessor();
        mudarFragment(new ProfHomeFragment());
    }

    private void configurarNavegacaoProfessor() {
        MenuItem itemHome = binding.bottomnavview.getMenu().findItem(R.id.menu_home);
        MenuItem itemCursos = binding.bottomnavview.getMenu().findItem(R.id.menu_cursos);
        MenuItem itemAvisos = binding.bottomnavview.getMenu().findItem(R.id.menu_avisos);
        MenuItem itemConfig = binding.bottomnavview.getMenu().findItem(R.id.menu_config);

        if (itemHome != null) {
            itemHome.setTitle("Turmas");
            itemHome.setIcon(android.R.drawable.ic_menu_agenda);
        }
        if (itemCursos != null) {
            itemCursos.setTitle("Postar");
            itemCursos.setIcon(android.R.drawable.ic_menu_add);
        }
        if (itemAvisos != null) {
            itemAvisos.setTitle("Agenda");
            itemAvisos.setIcon(android.R.drawable.ic_menu_today);
        }
        if (itemConfig != null) {
            itemConfig.setTitle("Ajustes");
            itemConfig.setIcon(android.R.drawable.ic_menu_preferences);
        }

        binding.bottomnavview.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_nada) {
                mudarFragment(new ProfPerfilFragment());
                return true;
            } else if (id == R.id.menu_home) {
                mudarFragment(new ProfHomeFragment());
            } else if (id == R.id.menu_cursos) {
                mudarFragment(new ProfEscolaFragment());
            } else if (id == R.id.menu_avisos) {
                mudarFragment(new ProfAgendaFragment());
            } else if (id == R.id.menu_config) {
                mudarFragment(new ProfConfigFragment());
            } else {
                return false;
            }
            return true;
        });

        if (binding.menuPerfil != null) {
            binding.menuPerfil.setOnClickListener(v -> mudarFragment(new ProfPerfilFragment()));
        }
    }

    private void mudarFragment(androidx.fragment.app.Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.frame_layout, fragment)
                .commit();
    }
}
