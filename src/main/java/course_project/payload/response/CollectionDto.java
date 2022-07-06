package course_project.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CollectionDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
}
