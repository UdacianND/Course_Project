package course_project.payload.response;

import course_project.payload.request.ValueDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemInfoDto {
    private Long ownerId;
    private String name;
    private String tags;
    private String imageUrl;
    private List<ValueDto> values;
}
