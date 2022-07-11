package root.operation;
import root.utils.AutoFormatter;


import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Callable;
import root.utils.AutoFormatter;
abstract public class Operation implements Callable<Boolean> {

    public static Operation newOperation (String strCmd, AutoFormatter send, Scanner receive){
        Command cmd = Objects.requireNonNull(Command.type(strCmd));
        Operation operation;
        switch (cmd){
            case DISCONNECT_WS:
                operation = new DisconnectWorkspace();
                break;

            default:
                throw new RuntimeException();
        }
        operation.cmd = strCmd;
        operation.send = send;
        operation.receive = receive;

        return operation;
    }
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
