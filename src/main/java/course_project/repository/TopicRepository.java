package course_project.repository;

import course_project.entity.Tag;
import course_project.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TopicRepository  extends JpaRepository<Topic, Long> {
    Optional<Topic> findByName(String name);

    @Query("select t from Topic t where lower(t.name) like lower(concat('%', ?1,'%'))")
    List<Topic> getTopicsByName(String name);
}
