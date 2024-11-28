package game.gamegoodgood.bot;

public class MessageRequest {

    private String channel_id;
    private String message;

    protected MessageRequest() {
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
