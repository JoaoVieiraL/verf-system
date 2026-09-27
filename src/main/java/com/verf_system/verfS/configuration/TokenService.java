package com.verf_system.verfS.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.verf_system.verfS.database.entity.FuncionarioEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;
    public String generateToken (FuncionarioEntity funcionario){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create().withIssuer("verf-system").withSubject(funcionario.getEmail()).withExpiresAt(generateExpirationDate()).sign(algorithm);
            return token;
        }catch(JWTCreationException exception){
            throw new RuntimeException("Erro enquanto gerava token", exception);

        }
    }

    public String validateToken (String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm).withIssuer("verf-system").build().verify(token).getSubject();
        }catch(JWTVerificationException exception){
            return "";
        }
    }

    private Instant generateExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
