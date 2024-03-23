package com.miniprojet.miniprojet.security.util;

import com.miniprojet.miniprojet.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public static String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 864_000_000); // 10 jours en millisecondes

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("password", user.getPassword()) // Include the password as a custom claim
                .claim("roles", user.getRole())
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String getEmailFromToken(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
    }

    public static boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
