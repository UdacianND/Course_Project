package course_project.entity;

import course_project.entity.item.Item;
import course_project.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @FullTextField(analyzer = "customAnalyzer")
    private String content;
    private Time time;
    @ManyToOne
    private User user;
    @ManyToOne()
    @JoinColumn(name="item_id")
    private Item item;
}
