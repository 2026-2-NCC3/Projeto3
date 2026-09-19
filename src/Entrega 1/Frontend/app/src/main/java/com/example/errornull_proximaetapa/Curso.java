package com.example.errornull_proximaetapa;

import android.widget.ImageView;

public class Curso {
    public String curso;
    public String data;
    public String desc;

    public int imagemId;



    public Curso(String curso, String data, String desc, int imagemId){
        this.curso = curso;
        this.data = data;
        this.desc = desc;
        this.imagemId = imagemId;
    }

    public String getInfo(){
        return "Nome do Curso: " + curso + "\nData do curso: " + data + "\nDescrição:\n" + desc;
    }

    public int getImageId(){
        return imagemId;
    }

}
