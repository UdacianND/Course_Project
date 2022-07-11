package course_project.entity;

import course_project.entity.item.Item;
import course_project.entity.user.User;
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
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Indexed
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @FullTextField(analyzer = "customAnalyzer")
    private String name;
    @FullTextField(analyzer = "customAnalyzer")
    private String description;
    @ManyToOne
    @IndexedEmbedded
    private Topic topic;
    private String imageUrl;
    @ManyToOne
    private User user;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "collection")
    private List<Item> items;

    public Collection(String name, String description, Topic topic, String imageUrl, User user) {
        this.name = name;
        this.description = description;
        this.topic = topic;
        this.imageUrl = imageUrl;
        this.user = user;
    }
}

