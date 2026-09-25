package com.example.errornull_proximaetapa;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    /*@GET("/cursos")
    Call<List<Curso>> getCursos();*/

    @POST("/alunos/login")
    Call<Void> fazerLogin(@Body LoginRequest request);

    @POST("/alunos/cadastrar")
    Call<Void> fazerCadastro(@Body CadastroRequest request);

}