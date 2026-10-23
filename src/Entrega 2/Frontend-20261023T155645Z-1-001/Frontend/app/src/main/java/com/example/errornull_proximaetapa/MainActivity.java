package com.example.errornull_proximaetapa;

import android.os.Bundle;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.errornull_proximaetapa.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    //TESTEEE

    ActivityMainBinding binding;
    FloatingActionButton menu_perfil;
    private boolean perfilSelecionado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        menu_perfil = findViewById(R.id.menu_perfil);

// Estado inicial do botão central
        atualizarBotaoPerfil(false);

// Clique no botão central
        menu_perfil.setOnClickListener(v -> {

            // 1. Abre o PerfilFragment diretamente
            mudarFragment(new PerfilFragment());

            // 2. Marca o item invisível como selecionado
            binding.bottomnavview.getMenu()
                    .findItem(R.id.menu_nada)
                    .setChecked(true);

            // 3. Muda a cor do ícone central para branco
            atualizarBotaoPerfil(true);

            // 4. Animação de clique
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

// Cliques nos itens normais da barra
        binding.bottomnavview.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.menu_nada) {

                mudarFragment(new PerfilFragment());
                atualizarBotaoPerfil(true);
                return true;

            } else if (id == R.id.menu_home) {

                mudarFragment(new HomeFragment());

            } else if (id == R.id.menu_cursos) {

                mudarFragment(new CursosFragment());

            } else if (id == R.id.menu_avisos) {

                mudarFragment(new AvisosFragment());

            } else if (id == R.id.menu_config) {

                mudarFragment(new ConfigFragment());

            } else {
                return false;
            }

            // Outro item foi selecionado:
            // o botão central volta ao estado normal.
            atualizarBotaoPerfil(false);

            return true;
        });




    }

   /* private void mudarFragment (androidx.fragment.app.Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.frame_layout, fragment)
                .commit();

    }*/

    private void mudarFragment(
            androidx.fragment.app.Fragment fragment) {

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

        int corIcone = selecionado
                ? Color.WHITE
                : Color.BLACK;

        menu_perfil.setImageTintList(
                ColorStateList.valueOf(corIcone)
        );

        menu_perfil.animate()
                .scaleX(selecionado ? 1.08f : 1f)
                .scaleY(selecionado ? 1.08f : 1f)
                .setDuration(180)
                .start();
    }

    private void limparSelecaoBottomNavigation() {

        // Desativa temporariamente a seleção automática
        binding.bottomnavview.getMenu()
                .setGroupCheckable(0, false, true);

        // Desmarca todos os itens da barra
        for (int i = 0;
             i < binding.bottomnavview.getMenu().size();
             i++) {

            MenuItem item = binding.bottomnavview
                    .getMenu().getItem(i);

            item.setChecked(false);
        }

        // Restaura o comportamento normal da barra
        binding.bottomnavview.getMenu()
                .setGroupCheckable(0, true, true);
    }


}
