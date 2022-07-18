package root.operation;
import root.User;
import root.Workspace;
import root.utils.connections.NormalConnectionPack;

import java.util.Objects;
import java.util.concurrent.Callable;

abstract public class Operation implements Callable<Void> {

    public static Operation newOperation (Workspace workspace, User user, String strCmd){
        Command cmd = Objects.requireNonNull(Command.type(strCmd));
        Operation operation;
        switch (cmd){
            case DISCONNECT_WS:
                operation = new Disconnect();
                break;

            case SEND_MSG:
                operation = new SendMessage();
                break;

            case GET_CHATS:
                operation = new GetChats();
                break;

            case GET_MESSAGES:
                operation = new GetMessages();
                break;

            default:
                throw new RuntimeException();
        }
        operation.workspace = workspace;
        operation.user = user;
        operation.cmd = strCmd;
        operation.con = user.con;
        return operation;
    }

    Workspace workspace;
    User user;
    String cmd;
    NormalConnectionPack con;

    @Override
    public Void call() throws Exception {
        operate();
        System.out.println(cmd + " was successful");
        return null;
    }

    abstract void operate() throws Exception;

 }
