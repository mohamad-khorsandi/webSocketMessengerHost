package root.operation;

public class GetChats extends Operation{
    @Override
    void operate() throws Exception {
        con.format("OK");
        con.format(this.user.chatList.getChatsJson());
    }
}
