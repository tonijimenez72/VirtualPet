package cat.itacademy.s05.t02.virtualpet.auth.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private final Key key;
    private final JwtParser jwtParser;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public JwtTokenUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parser().setSigningKey(key).build(); // 🔹 CORREGIDO
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256) // 🔹 CORREGIDO
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getSubject(); // 🔹 CORREGIDO
    }

    public boolean validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token); // 🔹 CORREGIDO
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
