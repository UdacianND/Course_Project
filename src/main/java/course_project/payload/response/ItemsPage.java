package course_project.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ItemsPage {
    List<ItemDto> items;
    boolean hasNextPage;
}
