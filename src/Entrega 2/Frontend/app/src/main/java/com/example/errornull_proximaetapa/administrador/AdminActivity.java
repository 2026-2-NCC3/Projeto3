package com.example.errornull_proximaetapa.administrador;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.UserSession;
import com.example.errornull_proximaetapa.databinding.ActivityMainBinding;

public class AdminActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = UserSession.getScopedPreferences(this);
        sp.edit().putString("tipoUsuario", "admin").apply();

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

        configurarNavegacaoAdmin();
        mudarFragment(new AdmModeradorFragment());
    }

    private void configurarNavegacaoAdmin() {
        MenuItem itemHome = binding.bottomnavview.getMenu().findItem(R.id.menu_home);
        MenuItem itemCursos = binding.bottomnavview.getMenu().findItem(R.id.menu_cursos);
        MenuItem itemAvisos = binding.bottomnavview.getMenu().findItem(R.id.menu_avisos);
        MenuItem itemConfig = binding.bottomnavview.getMenu().findItem(R.id.menu_config);

        if (itemHome != null) {
            itemHome.setTitle("Painel");
            itemHome.setIcon(android.R.drawable.ic_menu_manage);
        }
        if (itemCursos != null) {
            itemCursos.setTitle("Gerenciar");
            itemCursos.setIcon(android.R.drawable.ic_menu_edit);
        }
        if (itemAvisos != null) {
            itemAvisos.setTitle("Requerimentos");
            itemAvisos.setIcon(android.R.drawable.ic_menu_send);
        }
        if (itemConfig != null) {
            itemConfig.setTitle("Ajustes");
            itemConfig.setIcon(android.R.drawable.ic_menu_preferences);
        }

        binding.bottomnavview.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_nada) {
                mudarFragment(new AdmModeradorFragment());
                return true;
            } else if (id == R.id.menu_home) {
                mudarFragment(new AdmModeradorFragment());
            } else if (id == R.id.menu_cursos) {
                mudarFragment(new AdmInfoCursosFragment());
            } else if (id == R.id.menu_avisos) {
                mudarFragment(new AdmInfoUserFragment());
            } else if (id == R.id.menu_config) {
                mudarFragment(new AdmConfigFragment());
            } else {
                return false;
            }
            return true;
        });

        if (binding.menuPerfil != null) {
            binding.menuPerfil.setOnClickListener(v -> mudarFragment(new AdmModeradorFragment()));
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
