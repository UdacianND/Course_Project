package course_project.controller;


import course_project.base_service.UserBaseService;
import course_project.jwt.JwtProvider;
import course_project.payload.request.UserLoginDto;
import course_project.payload.request.UserSignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class UserAuthController {

    private final UserBaseService userBaseService;
    private final JwtProvider jwtProvider;

    @PostMapping("register")
    public ResponseEntity<?> signUp(
            @RequestBody UserSignUpDto userSignUpDto
            ){
        int STATUS_CODE = userBaseService.registerUser(userSignUpDto);
        return ResponseEntity.status(STATUS_CODE).body(null);
    }

    @PostMapping("login")
    public ResponseEntity<?> login(
            @RequestBody UserLoginDto userDto
            ){
        return jwtProvider.authenticateUser(userDto);
    }
}
