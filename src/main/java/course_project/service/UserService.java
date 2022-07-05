package course_project.service;

import course_project.base_service.UserBaseService;
import course_project.entity.user.Role;
import course_project.entity.user.User;
import course_project.entity.user.UserStatus;
import course_project.payload.request.UserRoleDto;
import course_project.payload.request.UserSignUpDto;
import course_project.payload.response.UserDto;
import course_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static course_project.entity.user.Role.USER;
import static course_project.entity.user.UserStatus.ACTIVE;
import static course_project.entity.user.UserStatus.BLOCKED;
import static course_project.utils.SessionManager.getSessionByUsername;
import static course_project.utils.SessionManager.removeSession;
import static course_project.utils.StatusCode.EXISTS_BY_EMAIL;
import static course_project.utils.StatusCode.EXISTS_BY_USERNAME;


@RequiredArgsConstructor
@Service
public class UserService implements UserBaseService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getUserList() {
        List<User> userList = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user: userList){
            userDtoList.add(
                    new UserDto(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getPassword(),
                            user.getStatus(),
                            user.getRole().name()
                    )
            );
        }
        return userDtoList;
    }

    @Override
    public void deleteAllById(Long[] userIds) {
        for (Long id: userIds) {
            invalidateUserSession(id);
            userRepository.deleteById(id);
        }
    }

    @Override
    public void blockAllById(Long[] userIds) {
        for (Long id : userIds){
            setUserStatus(id, BLOCKED);
            invalidateUserSession(id);
        }
    }

    @Override
    public void unblockAllById(Long[] userIds) {
        for (Long id : userIds)
            setUserStatus(id, ACTIVE );
    }

    @Override
    public void setUserRole(UserRoleDto userRoleDto) {
        Long userId = userRoleDto.getId();
        Role role = Role.valueOf(userRoleDto.getRole());
        User user = getUserById(userId);
        user.setRole(role);
        invalidateUserSession(userId);
        userRepository.save(user);
    }

    @Override
    public int registerUser(UserSignUpDto user) {
        boolean existsByUsername = userRepository.existsByUsername(user.getUsername());
        if(existsByUsername)
            return EXISTS_BY_USERNAME;

        boolean existsByEmail = userRepository.existsByEmail(user.getEmail());
        if (existsByEmail)
            return EXISTS_BY_EMAIL;

        saveUser(user);
        return HttpStatus.OK.value();
    }
    private void saveUser(UserSignUpDto userDto){
        User user = new User(
                userDto.getUsername(),
                userDto.getEmail(),
                passwordEncoder.encode(userDto.getPassword()),
                USER
        );
        userRepository.save(user);
    }

    public void setUserStatus(Long id, UserStatus status) {
            User user = getUserById(id);
            user.setStatus(status);
            userRepository.save(user);
    }

    private User getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty())
            throw new RuntimeException("User not found with id - "+id);
        return optionalUser.get();
    }

    private void invalidateUserSession(Long userId){
        String username = getUserById(userId).getUsername();
        HttpSession session = getSessionByUsername(username) ;
        if(session != null){
            session.invalidate();
            removeSession(username);
        }
    }
}
