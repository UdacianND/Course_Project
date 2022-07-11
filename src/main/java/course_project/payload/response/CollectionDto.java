package course_project.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CollectionDto {
    private Long id;
    private Long ownerId;
    private String name;
    private String topic;
    private String description;
    private String imageUrl;
}
