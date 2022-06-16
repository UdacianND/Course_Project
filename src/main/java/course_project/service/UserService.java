package course_project.service;

import course_project.base_service.UserBaseService;
import course_project.entity.Role;
import course_project.entity.User;
import course_project.entity.UserStatus;
import course_project.payload.request.UserRoleDto;
import course_project.payload.response.UserDto;
import course_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static course_project.entity.UserStatus.ACTIVE;
import static course_project.entity.UserStatus.BLOCKED;
import static course_project.utils.SessionManager.getSessionByUsername;
import static course_project.utils.SessionManager.removeSession;


@RequiredArgsConstructor
@Service
public class UserService implements UserBaseService {

    private final UserRepository userRepository;

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
