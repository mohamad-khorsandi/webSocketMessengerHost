package root.operation;

public enum Command {
    CREATE_WS("create-workspace"), DISCONNECT_WS("disconnect");

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
