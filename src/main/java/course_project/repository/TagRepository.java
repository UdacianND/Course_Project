package course_project.repository;

import course_project.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    @Query("select t from Tag t where lower(t.name) like lower(concat('%', ?1,'%'))")
    List<Tag> getTagsByName(String name);

    @Query("select t.name from Tag t order by size(t.items)")
    List<String> getTopTags(Pageable pageable);

}
