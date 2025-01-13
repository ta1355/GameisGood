package game.gamegoodgood.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class PostCleanupSchedulerTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostCleanupScheduler postCleanupScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteOldSoftDeletedPosts_ShouldDeletePostsOlderThanThreeMonths() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeMonthsAgo = now.minusMonths(3);

        Post oldPost1 = new Post();
        oldPost1.setId(1L);
        oldPost1.setDeletedDateTime(threeMonthsAgo.minusDays(1));

        Post oldPost2 = new Post();
        oldPost2.setId(2L);
        oldPost2.setDeletedDateTime(threeMonthsAgo.minusMonths(1));

        List<Post> oldPosts = Arrays.asList(oldPost1, oldPost2);

        when(postRepository.findSoftDeletedBefore(any(LocalDateTime.class)))
                .thenReturn(oldPosts);

        // When
        postCleanupScheduler.deleteOldSoftDeletedPosts();

        // Then
        verify(postRepository, times(1)).findSoftDeletedBefore(any(LocalDateTime.class));
        verify(postRepository, times(1)).delete(oldPost1);
        verify(postRepository, times(1)).delete(oldPost2);
    }

    @Test
    void deleteOldSoftDeletedPosts_ShouldNotDeleteRecentPosts() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoMonthsAgo = now.minusMonths(2);

        Post recentPost = new Post();
        recentPost.setId(3L);
        recentPost.setDeletedDateTime(twoMonthsAgo);

        List<Post> recentPosts = Arrays.asList(recentPost);

        when(postRepository.findSoftDeletedBefore(any(LocalDateTime.class)))
                .thenReturn(recentPosts);

        // When
        postCleanupScheduler.deleteOldSoftDeletedPosts();

        // Then
        verify(postRepository, times(1)).findSoftDeletedBefore(any(LocalDateTime.class));
        verify(postRepository, never()).delete(any(Post.class));
    }
}