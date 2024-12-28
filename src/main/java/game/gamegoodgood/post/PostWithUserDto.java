package game.gamegoodgood.post;

import java.time.LocalDateTime;

public record PostWithUserDto(
        Long id,
        String title,
        String detail,
        String game,
        String image,
        String username,
        LocalDateTime createDateTime,
        LocalDateTime deletedDateTime,
        Integer likeCount,
        Integer viewCount
) {
}