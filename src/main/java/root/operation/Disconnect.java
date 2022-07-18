package root.operation;

public class Disconnect extends Operation{
    @Override
    void operate() {
        user.isConnected = false;
        con.close();
    }
}
