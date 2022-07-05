package course_project.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class JwtAuthFilter {
    @Value("${jwtSecretKey}")
    private String jwtSecretKey;
    @Value("${jwtExpirationMs}")
    private String jwtExpirationMs;
    @Value("${jwtTokenPrefix}")
    private String jwtTokenPrefix;

    private final AuthenticationManager authenticationManager;


    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        Date expiredDate = new Date(System.currentTimeMillis() + jwtExpirationMs);
        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                        .signWith(SignatureAlgorithm.HS512, jwtSecretKey).compact();


        response.addHeader(HttpHeaders.AUTHORIZATION, jwtTokenPrefix + token);
    }
}


