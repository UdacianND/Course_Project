package course_project.repository;

import course_project.entity.Collection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findAllByUserId(Long userId);

    @Query("select c from Collection c order by size(c.items) desc")
    List<Collection> gelTopCollections(Pageable pageable);
}
