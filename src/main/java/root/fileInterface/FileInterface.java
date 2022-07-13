package root.fileInterface;

import root.Host;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileInterface {
    public static void saveObject(Host host) throws IOException {
        String fileName = makeFileName(host);
        Path file = Paths.get(fileName);
        if (!Files.exists(file))
            Files.createFile(file);

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeObject(host);
    }

    public static Host loadObject(String ip, String startPort, String endPort) throws IOException, ClassNotFoundException {
        String fileName = makeFileName(ip, startPort, endPort);
        Path file = Paths.get(fileName);
        if (!Files.exists(file))
            return null;
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
        return (Host) in.readObject();
    }

    static String makeFileName(String ip, String startPort, String endPort){
        StringBuilder fileName = new StringBuilder();
        fileName.append(ip);
        fileName.append(":");
        fileName.append(startPort);
        fileName.append("-");
        fileName.append(endPort);
        fileName.append(".txt");
        return fileName.toString();
    }
    static String makeFileName(Host host){
        StringBuilder fileName = new StringBuilder();
        fileName.append(host.getIp());
        fileName.append(":");
        fileName.append(host.getFirstPort());
        fileName.append("-");
        fileName.append(host.getLastPort());
        fileName.append(".txt");
        return fileName.toString();
    }
}
