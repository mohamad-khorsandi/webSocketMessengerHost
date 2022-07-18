package root;

import root.operation.Operation;
import root.utils.connections.NormalConnectionPack;
import root.utils.connections.ConnectionPack;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static root.Host.executor;
import static root.Host.host;

public class Workspace implements Callable<Object>, Serializable {
    public Workspace(int port) {
        this.port = port;
    }

    private int port;
    private HashMap<Integer, User> userMap = new HashMap<>();

    @Override
    public Object call() throws Exception {
        while (true){
            NormalConnectionPack clientCon = ConnectionPack.newNormConnectionPack(port);

            Future<User> futureUser = executor.submit(() -> listenForConnectClient(clientCon));

            executor.submit(() -> listenClientCmd(futureUser.get(), clientCon));
        }
    }

    private User listenForConnectClient(NormalConnectionPack con) throws Exception {
        //4 ---------------------------
        con.next();//connect
        String token = con.next();
        //5 ---------------------------
        host.serCon.format("whois %s", token);
        //6 ---------------------------
        host.serCon.throwIfResIsNotOK();
        int id = host.serCon.nextInt();
        //7,8,9 ---------------------------
        if (userMap.containsKey(id)) {
            con.format("OK");
            return userMap.get(id);
        }

        con.format("username?");
        String username = con.next();

        User user = new User(username, id, true, con);
        addUser(user);

        con.format("OK");
        return user;
    }

    private Void listenClientCmd(User user, NormalConnectionPack con) throws Exception {
        while (user.isConnected){
            String cmd = con.next();
            Operation.newOperation(this, user, cmd).call();
        }
        return null;
    }

    public void addUser(User user) {
        if(user.id == null)
            throw new RuntimeException();
        userMap.put(user.id, user);
    }

    public User getUser(String username){
        AtomicReference<User> user = new AtomicReference<>();
        userMap.forEach((key, val) ->{
            if (val.username.equals(username))
                user.set(val);
        });
        return user.get();
    }
}