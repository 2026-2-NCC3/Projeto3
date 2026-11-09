package com.example.errornull_proximaetapa.auth;

import com.example.errornull_proximaetapa.auth.InscricaoRequest;
import com.example.errornull_proximaetapa.auth.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @POST("usuarios/login")
    Call<TokenResponse> fazerLogin(@Body LoginRequest request);

    @POST("usuarios/cadastro")
    Call<Object> fazerCadastro(@Body CadastroRequest request);

    @GET("cursos")
    Call<List<CursoResponse>> listarCursos();

    @POST("inscricoes")
    Call<Void> inscreverAluno(@Body InscricaoRequest request);


}