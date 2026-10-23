package com.example.errornull_proximaetapa;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("usuarios/login")
    Call<TokenResponse> fazerLogin(@Body LoginRequest request);

    @POST("usuarios/cadastro")
    Call<Object> fazerCadastro(@Body CadastroRequest request);
}