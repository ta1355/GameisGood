package game.gamegoodgood.post.like;

import game.gamegoodgood.post.Post;
import game.gamegoodgood.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Long> {
    Optional<Like> findByUserAndPost(Users user, Post post);
}
