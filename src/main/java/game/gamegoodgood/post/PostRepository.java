package game.gamegoodgood.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndDeletedFalse(Long id);

    @Query("SELECT new game.gamegoodgood.post.PostTodayPopularityDTO(p.id, p.title, p.users.username, p.createDateTime, p.viewCount) " +
            "FROM Post p " +
            "WHERE p.createDateTime >= :startOfDay " +
            "AND p.deleted = false " +
            "ORDER BY p.viewCount DESC")
    List<PostTodayPopularityDTO> findTodayTopViewedPosts(@Param("startOfDay") LocalDateTime startOfDay, Pageable pageable);

    List<Post> findAllByDeletedFalse();

    Page<Post> findAllByDeletedFalse(Pageable pageable);

}




