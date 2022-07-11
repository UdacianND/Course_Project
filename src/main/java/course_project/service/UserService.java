package course_project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import course_project.base_service.UserBaseService;
import course_project.entity.user.Role;
import course_project.entity.user.User;
import course_project.entity.user.UserStatus;
import course_project.payload.request.UserRoleDto;
import course_project.payload.request.UserSignUpDto;
import course_project.payload.response.UserDto;
import course_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static course_project.entity.user.Role.USER;
import static course_project.entity.user.UserStatus.*;
import static course_project.utils.StatusCode.EXISTS_BY_EMAIL;
import static course_project.utils.StatusCode.EXISTS_BY_USERNAME;


@RequiredArgsConstructor
@Service
public class UserService implements UserBaseService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Override
    public String getUserList() throws JsonProcessingException {
        List<User> userList = userRepository.getUserList();
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user: userList){
            userDtoList.add(new UserDto(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getStatus(),
                            user.getRole().name()));}
        return objectMapper.writeValueAsString(userDtoList);
    }

    @Override
    public void deleteAllById(Long[] userIds) {
        for (Long id: userIds) {
            setUserStatus(id, DELETED);
        }
    }

    @Override
    public void blockAllById(Long[] userIds) {
        for (Long id : userIds){
            setUserStatus(id, BLOCKED);
        }
    }

    @Override
    public void unblockAllById(Long[] userIds) {
        for (Long id : userIds)
            setUserStatus(id, ACTIVE );
    }

    @Override
    public void setUserRole(String userData) throws JsonProcessingException {
        UserRoleDto userRoleDto =  objectMapper.readValue(userData, new TypeReference<>() {});
        Long userId = userRoleDto.getUserId();
        Role role = Role.valueOf(userRoleDto.getRole());
        User user = userRepository.findById(userId).orElseThrow(()-> new ObjectNotFoundException(userId,"User"));
        user.setRole(role);
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
            User user = userRepository.findById(id).orElseThrow(()-> new ObjectNotFoundException(id,"User"));
            user.setStatus(status);
            userRepository.save(user);
    }
}
