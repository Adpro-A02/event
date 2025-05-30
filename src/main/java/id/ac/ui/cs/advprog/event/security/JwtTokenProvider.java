package id.ac.ui.cs.advprog.event.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.Key;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    @Value("${JWT_SECRET}")
    private String jwtSecret;

   private Key getSigningKey() {
    logger.debug("Raw jwtSecret: '{}'", jwtSecret);

       byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
       return Keys.hmacShaKeyFor(keyBytes);





   }

    public String getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String getRoleFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();

        // Handle case where role might be in different case
        String role = claims.get("role", String.class);
        if (role == null) {
            role = claims.get("ROLE", String.class);
        }
        return role;
    }

    public boolean validateToken(String token) {
    

        try {
            Claims claims = getClaims(token);

           
            return true;
        } catch (ExpiredJwtException ex) {
            logger.warn("Token expired at: {}", ex.getClaims().getExpiration());
        } catch (Exception e) {
            logger.warn("Invalid token. Reason: {}", e.getMessage());
            logger.warn("Header.Payload.Signature: {}", token);
        }
        return false;
    }


    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}


