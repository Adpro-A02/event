package id.ac.ui.cs.advprog.event.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

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
    public JwtPayload parseToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret.getBytes())
                .parseClaimsJws(token)
                .getBody();

        JwtPayload payload = new JwtPayload();
        payload.setSub(UUID.fromString(claims.getSubject()));
        payload.setRole((String) claims.get("role"));
        payload.setExp(claims.getExpiration().getTime() / 1000);

        return payload;
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
        logger.debug("Validating token: {}", token);
        logger.debug("Using secret: '{}'", jwtSecret);

        try {
            Claims claims = getClaims(token);

            logger.debug("Token valid for user: {}", claims.getSubject());
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


