package course_project.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserAuthDto {
    private Long id;
    private String accessToken;
    private String role;
}
