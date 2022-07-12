package root.operation;

public class GetChats extends Operation{
    @Override
    Boolean operate() throws Exception {
        send.format("OK");
        send.format(this.user.chatList.toJson());
        return null;
    }
}
