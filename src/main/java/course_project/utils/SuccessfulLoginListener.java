package course_project.utils;

import course_project.entity.user.User;
import course_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;


import static course_project.utils.SessionManager.newSession;


@Component
@RequiredArgsConstructor
public class SuccessfulLoginListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UserRepository userRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
            User user = (User) event.getAuthentication().getPrincipal();
            HttpSession userSession = getUserSession();
            newSession(user.getUsername(), userSession);
            userRepository.save(user);
    }

    public  HttpSession getUserSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }
}
