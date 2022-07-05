package course_project.payload.request;

import lombok.Data;

@Data
public class UserLoginDto {
    private String email;
    private String password;
}
