package course_project.payload.request;

import lombok.Data;

@Data
public class UserSignUpDto {
    private String username;
    private String email;
    private String password;
}

