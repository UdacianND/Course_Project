package course_project.repository;

import course_project.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicRepository  extends JpaRepository<Topic, Long> {
    Optional<Topic> findByName(String name);
}
