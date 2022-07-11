package root;

import root.operation.Operation;
import root.utils.AutoFormatter;
import root.utils.Utils;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Callable;

import static root.Host.serReceive;
import static root.Host.serSend;


public class Workspace implements Callable<Object>{
    public Workspace(int port) {
        this.port = port;
    }

    private int port;
    private HashMap<Integer, User> userMap = new HashMap<>();

    @Override
    public Object call() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket clientSocket = serverSocket.accept();
        Scanner clientReceive = new Scanner(clientSocket.getInputStream());
        AutoFormatter clientSend = new AutoFormatter(clientSocket.getOutputStream());

        User user = connectClient(clientSend, clientReceive);
        addUser(user);
        listenClientCmd(user, clientSocket, clientSend, clientReceive);

        serverSocket.close();
        return null;
    }

    private User connectClient(AutoFormatter send, Scanner receive) throws Exception {
        //4 ---------------------------
        receive.next();//connect
        String token = receive.next();
        //5 ---------------------------
        serSend.format("whois %s", token);
        //6 ---------------------------
        Utils.throwIfResIsNotOK(serReceive);
        int id = serReceive.nextInt();
        //7,8,9 ---------------------------
        if (userMap.containsKey(id)) {
            send.format("OK");
            return userMap.get(id);
        }
        User user = new User();
        user.id = id;
        send.format("username?");
        user.username = receive.next();
        send.format("OK");
        return user;
    }

    private void listenClientCmd(User user, Socket socket, AutoFormatter send, Scanner receive) throws Exception {
        boolean isConnected = true;
        while (isConnected){
            String cmd = receive.next();
            isConnected = Operation.newOperation(cmd, send, receive).call();
        }
        Utils.closeAll(socket, send, receive);
    }

    private void addUser(User user) {
        if(user.id == null)
            throw new RuntimeException();
        userMap.put(user.id, user);
    }
}

