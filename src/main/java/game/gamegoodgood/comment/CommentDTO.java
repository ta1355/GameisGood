package game.gamegoodgood.comment;

import java.time.LocalDateTime;

public record CommentDTO(
        Long indexId,
        String detail,
        String username,
        LocalDateTime createDateTime,
        Long postId
) {
}
