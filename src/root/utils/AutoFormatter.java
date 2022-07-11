package root.utils;

import java.io.Closeable;
import java.io.OutputStream;
import java.util.Formatter;

public class AutoFormatter implements Closeable {
    public AutoFormatter(OutputStream stream) {
        sender = new Formatter(stream);
    }

    Formatter sender;
    public void format(String format, Object... args){
        sender.format(format+"\n", args);
        sender.flush();
    }

    @Override
    public void close(){
        sender.close();
    }
}