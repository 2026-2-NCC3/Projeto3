package com.example.errornull_proximaetapa;

import android.os.Bundle;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.errornull_proximaetapa.Fragments.AvisosFragment;
import com.example.errornull_proximaetapa.Fragments.ConfigFragment;
import com.example.errornull_proximaetapa.Fragments.CursosFragment;
import com.example.errornull_proximaetapa.Fragments.HomeFragment;
import com.example.errornull_proximaetapa.Fragments.PerfilFragment;
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
        atualizarBotaoPerfil(false);

        menu_perfil.setOnClickListener(v -> {

            mudarFragment(new PerfilFragment());
            binding.bottomnavview.getMenu().findItem(R.id.menu_nada).setChecked(true);

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


        binding.bottomnavview.getMenu()
                .setGroupCheckable(0, false, true);


        for (int i = 0;
             i < binding.bottomnavview.getMenu().size();
             i++) {

            MenuItem item = binding.bottomnavview.getMenu().getItem(i);

            item.setChecked(false);
        }


        binding.bottomnavview.getMenu()
                .setGroupCheckable(0, true, true);
    }


}
