package root.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Utils {

    public static void throwIfResIsNotOK(Scanner receive) throws Exception{
        String response = receive.next();
        if (!response.equals("OK"))
            throw new Exception(response + " " + receive.nextLine());
    }

    public static void closeAll(Closeable... closeables) {
        Arrays.stream(closeables).forEach(closeable -> {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
