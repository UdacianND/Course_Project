package course_project.entity.field;

import course_project.entity.Collection;
import course_project.payload.request.CollectionFieldDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private FieldType type;
    @ManyToOne
    private Collection collection;

    public Field(CollectionFieldDto fieldDto, Collection collection){
        name = fieldDto.getName();
        type = FieldType.valueOf(fieldDto.getType());
        this.collection = collection;
    }
}
