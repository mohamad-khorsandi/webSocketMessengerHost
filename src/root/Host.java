package root;

import root.utils.Utils;
import root.utils.AutoFormatter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Host {
    public static void main(String[] args) throws Exception{
        ip = args[0];
        firstPort = Integer.parseInt(args[1]);
        lastPort = Integer.parseInt(args[2]);
        socketToSer = new Socket("localhost", 8000);
        serSend = new AutoFormatter(socketToSer.getOutputStream());
        serReceive = new Scanner(socketToSer.getInputStream());

        registerToServer();
        Workspace workspace = createWorkspace();
        workspace.call();
        //todo : do this in while(cause multi req to serScanner problem)
    }

    static String ip;
    static int firstPort;
    static int lastPort;
    static Socket socketToSer;
    public static AutoFormatter serSend;
    public static Scanner serReceive;
    public static ExecutorService executor = Executors.newCachedThreadPool();

    static void registerToServer() throws Exception {
        //1-------------------------------
        serSend.format("create-host %s %d %d", ip, firstPort, lastPort);
        //2-------------------------------
        Utils.throwIfResIsNotOK(serReceive);
        //3-------------------------------
        int randPort = serReceive.nextInt();
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
        serSend.format("check");
        testListenerThread.join();
        //6-------------------------------
        serSend.format(testCode.get());
        //7,8-------------------------------
        Utils.throwIfResIsNotOK(serReceive);
    }

    static Workspace createWorkspace(){
        //3 -------------------------------
        serReceive.next();//create-workspace
        int port = serReceive.nextInt();
        User creator = new User();
        creator.id = serReceive.nextInt();
        //4 -------------------------------
        Workspace newWorkspace = new Workspace(port);
        serSend.format("OK");
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