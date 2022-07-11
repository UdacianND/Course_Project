package course_project.payload.request;

import lombok.Data;

@Data
public class UserRoleDto {
    Long userId;
    String role;
}
