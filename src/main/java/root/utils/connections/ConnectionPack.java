package root.utils.connections;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Scanner;

abstract public class ConnectionPack implements Closeable {
    public static NormalConnectionPack newNormConnectionPack(String ip, int port) throws IOException {
        NormalConnectionPack con = new NormalConnectionPack();
        con.socket = new Socket(ip, port);
        con.send = new Formatter(con.socket.getOutputStream());
        con.receive = new Scanner(con.socket.getInputStream());
        return con;
    }

    public static NormalConnectionPack newNormConnectionPack(int port) throws IOException {
        NormalConnectionPack con = new NormalConnectionPack();
        ServerSocket serverSocket = new ServerSocket(port);
        con.socket = serverSocket.accept();
        con.send = new Formatter(con.socket.getOutputStream());
        con.receive = new Scanner(con.socket.getInputStream());
        serverSocket.close();
        return con;
    }

    //----------------------------------------------------------

    public static MultiReceiveConnectionPack newMulRecConnectionPack(String ip, int port) throws IOException {
        MultiReceiveConnectionPack con = new MultiReceiveConnectionPack();
        con.socket = new Socket(ip, port);
        con.send = new Formatter(con.socket.getOutputStream());
        con.receive = new MultiReceiveScanner(con.socket.getInputStream());
        return con;
    }

    public static MultiReceiveConnectionPack newMulRecConnectionPack(int port) throws IOException {
        MultiReceiveConnectionPack con = new MultiReceiveConnectionPack();
        ServerSocket serverSocket = new ServerSocket(port);
        con.socket = serverSocket.accept();
        con.send = new Formatter(con.socket.getOutputStream());
        con.receive = new MultiReceiveScanner(con.socket.getInputStream());
        serverSocket.close();
        return con;
    }

    protected Socket socket;
    protected Formatter send;

    public void format(String format, Object... args){
        send.format(format+"\n", args);
        send.flush();
    }

    protected static void closeAll(Closeable... closeables) {
        Arrays.stream(closeables).forEach(closeable -> {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    abstract public void close();
    abstract public void throwIfResIsNotOK() throws Exception;
}


