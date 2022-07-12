package root.operation;
import root.User;
import root.Workspace;
import root.utils.AutoFormatter;


import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Callable;

abstract public class Operation implements Callable<Boolean> {

    public static Operation newOperation (Workspace workspace, User user, String strCmd, AutoFormatter send, Scanner receive){
        Command cmd = Objects.requireNonNull(Command.type(strCmd));
        Operation operation;
        switch (cmd){
            case DISCONNECT_WS:
                operation = new DisconnectWorkspace();
                break;

            case SEND_MSG:
                operation = new SendMessage();
                break;

            default:
                throw new RuntimeException();
        }
        operation.workspace = workspace;
        operation.user = user;
        operation.cmd = strCmd;
        operation.send = send;
        operation.receive = receive;

        return operation;
    }
    Workspace workspace;
    User user;
    String cmd;
    Scanner receive;
    AutoFormatter send;

    @Override
    public Boolean call() throws Exception {
        boolean keepCon = operate();
        System.out.println(cmd + " was successful");
        return keepCon;
    }

    abstract Boolean operate() throws Exception;
}
