 package id.ac.ui.cs.advprog.event.security;

 import io.jsonwebtoken.JwtException;
 import jakarta.servlet.FilterChain;
 import jakarta.servlet.ServletException;
 import jakarta.servlet.http.HttpServletRequest;
 import jakarta.servlet.http.HttpServletResponse;
 import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
 import org.springframework.security.core.Authentication;
 import org.springframework.security.core.authority.SimpleGrantedAuthority;
 import org.springframework.security.core.context.SecurityContextHolder;
 import org.springframework.stereotype.Component;
 import org.springframework.util.StringUtils;
 import org.springframework.web.filter.OncePerRequestFilter;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import java.io.IOException;
 import java.util.List;

 @Component
 public class JwtAuthenticationFilter extends OncePerRequestFilter {

     private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

     private final JwtTokenProvider jwtTokenProvider;

     public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
         this.jwtTokenProvider = jwtTokenProvider;
     }

     @Override
     protected void doFilterInternal(
             HttpServletRequest request,
             HttpServletResponse response,
             FilterChain filterChain
     ) throws ServletException, IOException {

         String header = request.getHeader("Authorization");
         logger.debug("Authorization Header: {}", header);

         if (header != null && header.startsWith("Bearer ")) {
             String token = header.substring(7);
             logger.debug("Extracted JWT: {}", token);

             try {
                 if (jwtTokenProvider.validateToken(token)) {
                     String userId = jwtTokenProvider.getUserIdFromJWT(token);
                     String role = jwtTokenProvider.getRoleFromJWT(token);



                     List<SimpleGrantedAuthority> authorities =
                             List.of(new SimpleGrantedAuthority(role));



                     UsernamePasswordAuthenticationToken auth =
                             new UsernamePasswordAuthenticationToken(userId, null, authorities);

                     SecurityContextHolder.getContext().setAuthentication(auth);
                     logger.debug("Authentication set in context.");
                 } else {
                     logger.warn("JWT is invalid or expired.");
                 }
             } catch (JwtException ex) {
                 logger.error("Error validating JWT token", ex);
             }
         } else {
             logger.debug("No Bearer token found in request.");
         }

         filterChain.doFilter(request, response);
     }
 }