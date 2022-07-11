package course_project.repository;

import course_project.entity.ItemLike;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface ItemLikeRepository extends JpaRepository<ItemLike, Long> {
    Optional<ItemLike> findByItem_IdAndUser_Id(Long itemId, Long userId);
    @Transactional
    void deleteAllByItem_Id(Long itemId);
}
