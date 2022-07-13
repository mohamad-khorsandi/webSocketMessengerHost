package root;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import root.fileInterface.FileInterface;
import root.utils.connections.ConnectionPack;
import root.utils.connections.NormalConnectionPack;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Host implements Serializable {
    void constructor(String ip, String firstPort, String lastPort) throws IOException {
        this.ip = ip;
        this.firstPort = Integer.parseInt(firstPort);
        this.lastPort = Integer.parseInt(lastPort);

        serCon = ConnectionPack.newNormConnectionPack(ip, 8000);
    }

    public static void main(String[] args) throws Exception{
        host = FileInterface.loadObject(args[0], args[1], args[2]);
        if (host == null)
            host = new Host();
        host.constructor(args[0], args[1], args[2]);

        host.registerToServer();
        Workspace workspace = host.createWorkspace();
        workspace.call();

        //todo : do this in while(cause multi req to serScanner problem)
    }
    static public Host host;
    static public ObjectMapper objectMapper = new ObjectMapper();
    static public ExecutorService executor = Executors.newCachedThreadPool();
    ArrayList<Workspace> workspacesList = new ArrayList<>();

    @Getter
    transient String ip;
    @Getter
    transient int firstPort;
    @Getter
    transient int lastPort;
    transient NormalConnectionPack serCon;

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
                testCode.set(receiveCodeOnPort(randPort));
            } catch (IOException e) {
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

    Workspace createWorkspace(){
        //3 -------------------------------
        serCon.next();//create-workspace
        int port = serCon.nextInt();
        User creator = new User();
        creator.id = serCon.nextInt();
        //4 -------------------------------
        Workspace newWorkspace = new Workspace(port);
        workspacesList.add(newWorkspace);
        serCon.format("OK");
        return newWorkspace;
    }

    private static String receiveCodeOnPort(int port) throws IOException {
        //5-------------------------------
        String code;
        try(ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            Scanner ssc = new Scanner(socket.getInputStream());
        ){
            ssc.next();
            code = ssc.next();
        }
        return code;
    }
}