package com.security.SpringSecurityAuth.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Component 
public class JwtUtils {

    // Clé secrète récupérée depuis le application properties
    @Value("${app.secret-key}")
    private String secretKey;

    // Durée de validité du token
    @Value("${app.expiration-time}")
    private long expirationTime;

    // Génère un token simple contenant le username en subject
    public String generateToken(String username, Set<String> roles){
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        return createToken(claims, username);
    }

    // Construction du Jwt avec les claims, subjects, issuedAt, expiration et signature
    private String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .setClaims(claims) // payload custom
                .setSubject(subject) // Identifiant principal (username)
                .setIssuedAt(new Date(System.currentTimeMillis())) // date d'émission
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // expiration
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // signature HMAC-SHA256
                .compact(); // construit la chaîne Jwt
    }

    // Crée la Key à partir de la chaîne secrète
    private Key getSignKey(){
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // Valide que le token appartient bien au userDetails et qu'il n'est pas expiré
    public boolean validateToken(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Vérifie si la date d'expiration du token est passée
    private boolean isTokenExpired(String token){
        return extractExpirationDate(token).before(new Date());
    }

    // Extrait le username (subject) du token
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // Extrait la date d'expiration du token
    private Date extractExpirationDate(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    // Extrait une information des claims
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parse toutes les claims du token signé et renvoie le Claims body
    private Claims extractAllClaims(String token){
        return Jwts.parser() // clé utilisée pour vérifier la signature
                .setSigningKey(getSignKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
