package game.gamegoodgood.post;

import game.gamegoodgood.user.Users;

import java.time.LocalDateTime;

public record PostTodayPopularityDTO(
        Long id,
        String title,
        String username,
        LocalDateTime createDateTime,
        Integer viewCount
) {
}
