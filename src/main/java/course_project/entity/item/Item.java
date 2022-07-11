package course_project.entity.item;

import course_project.entity.Collection;
import course_project.entity.Comment;
import course_project.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import javax.persistence.*;
import java.util.List;

@Entity
@Indexed
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @FullTextField(analyzer = "customAnalyzer")
    private String name;
    private String imageUrl;


    @ManyToOne
    @IndexedEmbedded
    private Collection collection;
    @ManyToMany
    @IndexedEmbedded
    private List<Tag> tags;

    public Item(String name, String imageUrl, Collection collection, List<Tag> tags) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.collection = collection;
        this.tags = tags;
    }

    @IndexedEmbedded
    @OneToMany(mappedBy = "item")
    private List<Comment> comments;
    @IndexedEmbedded
    @OneToMany(mappedBy = "item")
    private List<Value> values;

}
