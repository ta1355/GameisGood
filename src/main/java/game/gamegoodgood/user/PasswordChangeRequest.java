package game.gamegoodgood.user;

public record PasswordChangeRequest(
        String username,
        String userEmail,
        String newPassword
) {
}
