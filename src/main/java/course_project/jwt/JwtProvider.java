package course_project.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import course_project.entity.user.User;
import course_project.payload.request.UserLoginDto;
import course_project.payload.response.UserAuthDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwtSecretKey}")
    private String jwtSecretKey;
    @Value("${jwtExpirationMs}")
    private int jwtExpirationMs;

    private final AuthenticationManager authManager;
    private final ObjectMapper mapper;


    public ResponseEntity<?> authenticateUser(UserLoginDto userDto){
        try{
            Authentication authenticate = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDto.getEmail(),
                            userDto.getPassword()
                    ));
            User principal = (User) authenticate.getPrincipal();
            String token = Jwts.builder()
                    .setSubject(principal.getEmail())
                    .claim("role", principal.getRole().toString())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                    .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
                    .compact();
            UserAuthDto userAuthDto = new UserAuthDto(
                    principal.getId(),
                    token,
                    principal.getRole().toString());


            return ResponseEntity.ok().body(mapper.writeValueAsString(userAuthDto));

        } catch (JsonProcessingException | AuthenticationException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(e);
        }
    }


}
