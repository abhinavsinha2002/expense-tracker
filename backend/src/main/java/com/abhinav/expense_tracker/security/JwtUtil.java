package com.abhinav.expense_tracker.security;
import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    @Value("${jwt.secret}") private String jwtSecret;
    @Value("${jwt.expirationMs}") private long jwtExpirationMs;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String username){
        Date now=new Date();
        return Jwts.builder().setSubject(username).setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+jwtExpirationMs))
                .signWith(getSigningKey(),SignatureAlgorithm.HS256).compact();
    }

    public String extractUsername(String token){
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        }
        catch(JwtException ex){
            return false;
        }
    }
}
