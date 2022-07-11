package course_project.repository;

import course_project.entity.field.Field;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FieldRepository extends JpaRepository<Field, Long> {
    List<Field> findAllByCollection_Id(Long collectionId);
    void deleteAllByCollection_Id(Long collectionId);
}
