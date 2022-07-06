package course_project.controller;


import course_project.base_service.UserBaseService;
import course_project.jwt.JwtProvider;
import course_project.payload.request.UserLoginDto;
import course_project.payload.request.UserSignUpDto;
import course_project.payload.response.UserDto;
import course_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserRepository userRepository;
    private final UserBaseService userBaseService;
    private final JwtProvider jwtProvider;

    @GetMapping("/main")
    public String mainPage(){
        List<UserDto> userList = userBaseService.getUserList();
        return "Hello";
    }

    @PostMapping("register")
    public ResponseEntity<?> signUp(
            @RequestBody UserSignUpDto userSignUpDto
            ){
        int STATUS_CODE = userBaseService.registerUser(userSignUpDto);
        return ResponseEntity.status(STATUS_CODE).body(null);
    }

    @PostMapping("login")
//    @RequestMapping(value = "login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(
            @RequestBody UserLoginDto userDto
            ){
        return jwtProvider.authenticateUser(userDto);
    }
}
