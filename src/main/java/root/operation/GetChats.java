package root.operation;

public class GetChats extends Operation{
    @Override
    Boolean operate() throws Exception {
        con.format("OK");
        con.format(this.user.chatList.getChatsJson());
        return true;
    }
}
