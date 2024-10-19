package wbe.lastHunters.rarities;

public class Reward {

    private String id;

    private String message;

    private String command;

    public Reward(String id, String message, String command) {
        this.id = id;
        this.message = message;
        this.command = command;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
