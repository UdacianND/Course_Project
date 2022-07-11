package course_project.base_service;


import com.fasterxml.jackson.core.JsonProcessingException;
import course_project.payload.request.UserSignUpDto;

public interface UserBaseService {
    String getUserList() throws JsonProcessingException;
    void deleteAllById(Long[] id);
    void blockAllById(Long[] id);
    void unblockAllById(Long[] userIds);
    void setUserRole(String user) throws JsonProcessingException;
    int registerUser(UserSignUpDto user);
}
