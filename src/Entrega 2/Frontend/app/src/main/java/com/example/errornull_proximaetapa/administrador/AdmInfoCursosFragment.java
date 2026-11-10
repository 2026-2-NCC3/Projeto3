package com.example.errornull_proximaetapa.administrador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.errornull_proximaetapa.R;
import com.example.errornull_proximaetapa.aluno.AlunoCursosFragment;

public class AdmInfoCursosFragment extends AlunoCursosFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adm_info_cursos, container, false);
    }
}
