package game.gamegoodgood.post;

public class PostExceptions {

    public static class PostNotFoundException extends RuntimeException {
        public PostNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

}
