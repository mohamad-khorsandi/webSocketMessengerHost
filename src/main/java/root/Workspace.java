package root;

import root.operation.Operation;
import root.utils.connections.NormalConnectionPack;
import root.utils.connections.ConnectionPack;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import static root.Host.host;

public class Workspace implements Callable<Object>, Serializable {
    public Workspace(int port) {
        this.port = port;
    }

    private int port;
    private HashMap<Integer, User> userMap = new HashMap<>();

    @Override
    public Object call() throws Exception {

        NormalConnectionPack clientCon = ConnectionPack.newNormConnectionPack(port);

        User user = connectClient(clientCon);
        addUser(user);
        listenClientCmd(user, clientCon);

        return null;
    }

    private User connectClient(NormalConnectionPack con) throws Exception {
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
        User user = new User();
        user.id = id;
        user.con = con;
        con.format("username?");
        user.username = con.next();
        con.format("OK");
        return user;
    }

    private void listenClientCmd(User user, NormalConnectionPack con) throws Exception {
        boolean isConnected = true;
        while (isConnected){
            String cmd = con.next();
            isConnected = Operation.newOperation(this, user, cmd).call();
        }
        con.close();
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