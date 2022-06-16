package course_project.controller;


import course_project.base_service.UserBaseService;
import course_project.entity.Role;
import course_project.entity.User;
import course_project.payload.response.UserDto;
import course_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

import static course_project.entity.Role.USER;

@RequiredArgsConstructor
@Controller
public class UserAuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserBaseService userBaseService;

    @GetMapping("/")
    public String mainPage(Model model){
        List<UserDto> userList = userBaseService.getUserList();
        model.addAttribute("userList", userList);
        return "user-list";
    }

    @GetMapping("register")
    public String getSignUpView(){
        return "sign-up";
    }



    @GetMapping("login")
    public String getLoginView(){
        return "login";
    }

    @PostMapping("register")
    public String signUp(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            Model model

    ){
        return registerUser(username, email, password, model);
    }

    void saveUser(String username, String email, String password){
        User user = new User(
                username,
                email,
                passwordEncoder.encode(password),
                USER
        );
        userRepository.save(user);
    }

    String registerUser(String username, String email, String password, Model model){
        Optional<User> byUsername = userRepository.findByUsername(username);

        if(byUsername.isPresent()){
            model.addAttribute("registerMsg","User already exists with username - "+username);
            return "sign-up";
        }

        Optional<User> byEmail = userRepository.findByEmail(email);
        if(byEmail.isPresent()){
            model.addAttribute("registerMsg","User already exists with email - "+email);
            return "sign-up";
        }
        saveUser(username, email, password);
        return "redirect:/login";
    }

    @GetMapping("access-forbidden")
    public String getLoginPage() {
        return "access-forbidden";
    }

}
