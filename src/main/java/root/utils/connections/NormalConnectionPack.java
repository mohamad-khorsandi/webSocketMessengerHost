package root.utils.connections;

import java.util.Scanner;

public class NormalConnectionPack extends ConnectionPack {
    public Scanner receive;

    public String next() {
        return receive.next();
    }

    public String nextLine() {
        return receive.nextLine();
    }

    public int nextInt() {
        return receive.nextInt();
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
