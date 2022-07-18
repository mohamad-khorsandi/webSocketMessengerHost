package root;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import root.loadStoreOperation.LoadStoreOperations;
import root.utils.connections.ConnectionPack;
import root.utils.connections.MultiReceiveConnectionPack;
import root.utils.connections.NormalConnectionPack;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class Host implements Serializable{
    public void setFields(String ip, String firstPort, String lastPort) throws IOException {
        this.ip = ip;
        this.firstPort = Integer.parseInt(firstPort);
        this.lastPort = Integer.parseInt(lastPort);

        serCon = ConnectionPack.newMulRecConnectionPack(ip, 8000);
    }

    public static void main(String[] args) throws Exception {
        host = new Host();
        host.setFields(args[0], args[1], args[2]);
        host.registerToServer();

        executor.submit(() -> host.listenToCreateWorkspace());

        host.listenSysAdminCmd();
        LoadStoreOperations.saveObject(host);
        System.exit(0);
    }

    static public Host host;
    static public ObjectMapper objectMapper = new ObjectMapper();
    static public ExecutorService executor = Executors.newCachedThreadPool();
    static private Scanner sc = new Scanner(System.in);

    ArrayList<Workspace> workspacesList = new ArrayList<>();

    transient String ip;
    transient int firstPort;
    transient int lastPort;
    transient MultiReceiveConnectionPack serCon;

    void registerToServer() throws Exception {
        //1-------------------------------
        serCon.format("create-host %s %d %d", ip, firstPort, lastPort);
        //2-------------------------------
        serCon.throwIfResIsNotOK();
        //3-------------------------------
        int randPort = serCon.nextInt();
        //4,5-------------------------------
        AtomicReference<String> testCode = new AtomicReference<>();
        Thread testListenerThread = new Thread(() -> {
            try {
                testCode.set(this.receiveCodeOnPort(randPort));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        testListenerThread.start();
        Thread.sleep(1);
        serCon.format("check");
        testListenerThread.join();
        //6-------------------------------
        serCon.format(testCode.get());
        //7,8-------------------------------
        serCon.throwIfResIsNotOK();
    }

    Void listenToCreateWorkspace() throws Exception {
        while (true) {
            //3 -------------------------------
            serCon.waitForNext("create-workspace");
            int port = serCon.nextInt();
            User creator = new User();
            creator.id = serCon.nextInt();
            //4 -------------------------------
            Workspace newWorkspace = new Workspace(port);
            workspacesList.add(newWorkspace);
            executor.submit(newWorkspace);
            serCon.format("OK");
        }
    }

    String receiveCodeOnPort(int port) throws Exception {
        //5-------------------------------
        String code;

        try( NormalConnectionPack tmpCon = ConnectionPack.newNormConnectionPack(port))
        {
            tmpCon.throwIfResIsNotOK();
            code = tmpCon.next();
        }
        return code;
    }

    private void listenSysAdminCmd() {
        while (true){
            if(sc.next().equals("shutdown"))
                break;
        }
    }
}