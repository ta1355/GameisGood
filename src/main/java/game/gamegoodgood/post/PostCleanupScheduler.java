package game.gamegoodgood.post;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PostCleanupScheduler {

    private final PostRepository postRepository;

    public PostCleanupScheduler(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void deleteOldSoftDeletedPosts() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Post> oldPosts = postRepository.findSoftDeletedBefore(threeMonthsAgo);

        for (Post post : oldPosts) {
            if (post.getDeletedDateTime().isBefore(threeMonthsAgo)) {
                postRepository.delete(post);
            }
        }
    }
}