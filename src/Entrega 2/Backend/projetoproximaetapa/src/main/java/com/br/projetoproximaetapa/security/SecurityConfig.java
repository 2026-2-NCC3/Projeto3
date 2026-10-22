package com.br.projetoproximaetapa.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSec) throws Exception {
        System.out.println("SecurityConfig: Configurando segurança da aplicação...");
        
        httpSec
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/usuarios/login", "/usuarios/cadastro").permitAll()
                .anyRequest().authenticated()
            )
            
            .addFilterBefore(new SecurityFilter(), UsernamePasswordAuthenticationFilter.class);

        return httpSec.build();
    }
}