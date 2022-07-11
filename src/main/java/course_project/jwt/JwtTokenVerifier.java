package course_project.jwt;

import course_project.entity.user.User;
import course_project.entity.user.UserStatus;
import course_project.exception.InvalidTokenException;
import course_project.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static course_project.entity.user.UserStatus.ACTIVE;

@Component
@RequiredArgsConstructor
public class JwtTokenVerifier extends OncePerRequestFilter {

    @Value("${jwtSecretKey}")
    private String jwtSecretKey;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authToken = request.getHeader("Authorization");

        if (authToken == null || !authToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authToken.replace("Bearer ", "");

        try {

            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(jwtSecretKey)
                    .parseClaimsJws(token);

            Claims body = claimsJws.getBody();

            String email = body.getSubject();

            String role = (String) body.get("role");

            Optional<User> optionalUser = userRepository.findByEmail(email);

            if(optionalUser.isEmpty())
                throw new IllegalStateException("user not found");

            User user = optionalUser.get();
            boolean isInvalidToken = (user.getStatus() != ACTIVE) ||
                    (!user.getRole().toString().equals(role));

            if(isInvalidToken)
                throw new InvalidTokenException("token "+ authToken +" is not valid");

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }catch(Exception e){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }

        filterChain.doFilter(request, response);
    }
}
