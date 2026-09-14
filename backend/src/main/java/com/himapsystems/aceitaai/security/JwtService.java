package com.himapsystems.aceitaai.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtService {

    private final SecretKey chaveSecreta;
    private final long expiracaoMs;

    public JwtService(
            @Value("${jwt.secret}") String secretBase64,
            @Value("${jwt.expiration-ms}") long expiracaoMs
    ) {
        this.chaveSecreta = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.expiracaoMs = expiracaoMs;
    }

    public String gerarToken(Long autonomoId, String email) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("autonomoId", autonomoId)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plusMillis(expiracaoMs)))
                .signWith(chaveSecreta)
                .compact();
    }

    public Long extrairAutonomoId(String token) {
        return extrairTodasClaims(token).get("autonomoId", Long.class);
    }

    public boolean tokenValido(String token) {
        try {
            extrairTodasClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extrairTodasClaims(String token) {
        return Jwts.parser()
                .verifyWith(chaveSecreta)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
