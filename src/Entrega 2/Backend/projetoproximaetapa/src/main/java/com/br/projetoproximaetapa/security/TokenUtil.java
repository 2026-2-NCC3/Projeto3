package com.br.projetoproximaetapa.security;

import java.security.Key;
import java.util.Date;

import org.springframework.security.core.Authentication;

import com.br.projetoproximaetapa.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Collections;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class TokenUtil {
    
    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";
    private static final String EXPIRATION = "12*60*60*1000";
    private static final String SECRET_KEY = "7x#9P$2mK!4vR8@wL%1jH*6tY#3bZ&9f";
    private static final String EMISSOR = "DevNice";

    public static String createToken(Usuario usuario){
        Key secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        String token = io.jsonwebtoken.Jwts.builder()
                .setSubject(usuario.getEmail())
                .setIssuer(EMISSOR)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();


        return PREFIX + token;
    }

    private static Boolean isExpirationValid(Date expiration) {
        return expiration.after(new Date(System.currentTimeMillis()));
    }

    private static Boolean isEmissorValid(String emissor) {
        return emissor.equals(EMISSOR);
    }

    private static Boolean isSubjectValid(String email) {
        return email != null && !email.isEmpty() && email.contains("@") && email.contains(".") && email.length() >= 5;
    }

    public static Authentication validate(HttpServletRequest request) {
        String token = request.getHeader(HEADER);
        token = token.replace(PREFIX, "");
        
        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token);

        String email = jwsClaims.getBody().getSubject();
        String issuer = jwsClaims.getBody().getIssuer();
        Date expiration = jwsClaims.getBody().getExpiration();

        if (isSubjectValid(email) && isEmissorValid(issuer) && isExpirationValid(expiration)) {
            return new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
        }

        return null;
    }
}