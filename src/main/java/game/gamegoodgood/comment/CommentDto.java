package game.gamegoodgood.comment;

import game.gamegoodgood.post.Post;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String detail,
        String username,
        LocalDateTime createDateTime,
        Long postId
) {
}
