package course_project.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValueDto {
    private Long id;
    private String name;
    private String type;
    private String value;
}
