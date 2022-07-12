package root.operation;

public enum Command {
    DISCONNECT_WS("disconnect"), SEND_MSG("send-message"),
    GET_CHATS("get-chats"), GET_MESSAGES("get-messages");

    public String str;

    Command(String str) {
        this.str = str;
    }

    public static Command type(String strCmd){
        for (Command cmd : Command.values())
            if (cmd.str.equals(strCmd))
                return cmd;
        return null;
    }
}
