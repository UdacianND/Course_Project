package course_project.payload.response;

import course_project.entity.field.FieldType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldDto {
    private Long id;
    private String name;
    private String type;
    private String value;

    public FieldDto(Long id, String name, FieldType type) {
        this.id = id;
        this.name = name;
        this.type = type.toString().toLowerCase();
        value = "";
    }
}
