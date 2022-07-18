package root.utils.connections;

import java.util.concurrent.ExecutionException;

public class MultiReceiveConnectionPack extends ConnectionPack {
    protected MultiReceiveConnectionPack() {
    }

    MultiReceiveScanner receive;

    public String next() throws ExecutionException, InterruptedException {
        return receive.next();
    }

    public String nextLine() throws ExecutionException, InterruptedException {
        return receive.nextLine();
    }

    public int nextInt() throws ExecutionException, InterruptedException {
        return receive.nextInt();
    }

    public String waitForNext(String msg) throws ExecutionException, InterruptedException {
        return receive.waitForNext(msg);
    }

    public void throwIfResIsNotOK() throws Exception {
        String response = receive.next();
        if (!response.equals("OK"))
            throw new Exception(response + " " + receive.nextLine());
    }

    @Override
    public void close() {
        closeAll(socket, send, receive);
    }
}
