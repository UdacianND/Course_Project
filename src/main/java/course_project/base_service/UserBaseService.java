package course_project.base_service;


import course_project.payload.request.UserRoleDto;
import course_project.payload.request.UserSignUpDto;
import course_project.payload.response.UserDto;

import java.util.List;

public interface UserBaseService {
    List<UserDto> getUserList();
    void deleteAllById(Long[] id);
    void blockAllById(Long[] id);
    void unblockAllById(Long[] userIds);
    void setUserRole(UserRoleDto user);
    int registerUser(UserSignUpDto user);
}
