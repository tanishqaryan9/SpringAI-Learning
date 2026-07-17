package com.SpringAI.Generator;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;


@Component
public class JwtUtils {

    @Value("${spring.app.jwtSecretKey}")
    private String jwtSecretKey;

    @Value("${spring.app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    public String getJwtFromHeader(HttpServletRequest request)
    {
        String bearerToken=request.getHeader("Authorization");
        if(bearerToken!=null && bearerToken.startsWith("Bearer"))
        {
            String jwt=bearerToken.split("Bearer ")[1];
            return jwt;
        }
        return null;
    }

    public Key key()
    {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public Boolean validateJwt(String authToken)
    {
        try
        {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        }
        catch (MalformedJwtException e)
        {
            System.out.println("Invalid JWT token: "+e.getMessage());
        }
        catch (ExpiredJwtException e)
        {
            System.out.println("Expired JWT token: "+e.getMessage());
        }
        catch (UnsupportedJwtException e)
        {
            System.out.println("Unsupported JWT token: "+e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("JWT claims string is empty: "+e.getMessage());
        }
        return false;
    }

    public String getUsernameFromJwt(String token)
    {
        return Jwts.parser().verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public String generateTokenFromUsername(String username)
    {
        return Jwts.builder().signWith(key())
                .subject(username).issuedAt(new Date())
                .expiration(new Date(new Date()
                        .getTime()+jwtExpirationMs)).compact();
    }
}
