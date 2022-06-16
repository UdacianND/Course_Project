package course_project.payload.response;

import course_project.entity.Role;
import course_project.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String password;
    private UserStatus status;
    private String role;
}
