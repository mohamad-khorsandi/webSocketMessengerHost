package root.loadStoreOperation;

import root.Host;
import root.Workspace;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoadStoreOperations {
    
    public static Host recoverLastState(String ip, String startPort, String endPort) throws IOException, ClassNotFoundException {
        String fileName = makeFileName(ip, startPort, endPort);
        Path file = Paths.get(fileName);
        if (!Files.exists(file)){
            return null;
        }
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
        Host host = (Host) in.readObject();

        host.setFields(ip, startPort, endPort);
        host.getSerCon().format("nop");

        for (Workspace workspace : host.getWorkspacesList())
            Host.executor.submit(workspace);
        return host;
    }
    
    public static void saveObject(Host host) throws IOException {
        String fileName = makeFileName(host);
        Path file = Paths.get(fileName);
        if (!Files.exists(file))
            Files.createFile(file);

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeObject(host);
    }

    static String makeFileName(String ip, String startPort, String endPort){
        StringBuilder fileName = new StringBuilder();
        fileName.append(ip);
        fileName.append(":");
        fileName.append(startPort);
        fileName.append("-");
        fileName.append(endPort);
        return fileName.toString();
    }
    static String makeFileName(Host host){
        StringBuilder fileName = new StringBuilder();
        fileName.append(host.getIp());
        fileName.append(":");
        fileName.append(host.getFirstPort());
        fileName.append("-");
        fileName.append(host.getLastPort());
        return fileName.toString();
    }
}
