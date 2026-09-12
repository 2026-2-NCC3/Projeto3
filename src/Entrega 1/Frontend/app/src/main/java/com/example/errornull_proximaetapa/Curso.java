package com.example.errornull_proximaetapa;

import android.widget.ImageView;

public class Curso {
    public String curso;
    public String data;
    public String desc;

    /*public ImageView imgCurso;*/


    public Curso(String curso, String data, String desc /*ImageView imgCurso*/){
        this.curso = curso;
        this.data = data;
        this.desc = desc;
        //this.imgCurso = imgCurso;
    }

    public String getInfo(){
        return /*imgCurso*/ "Nome do Curso: " + curso + "\nData do curso: " + data + "\nDescrição:\n" + desc;
    }

}
