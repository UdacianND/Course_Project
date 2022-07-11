package course_project.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldDto {
    private Long id;
    private String name;
    private String type;
    private String value;
}
