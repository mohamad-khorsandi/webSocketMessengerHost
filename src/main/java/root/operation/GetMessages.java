package root.operation;

import root.User;

public class GetMessages extends Operation{
    @Override
    void operate() throws Exception {
        //2 -------------------------------------
        User otherUser = workspace.getUser(con.next());
        user.chatList.markAsReadMessagesOf(otherUser);
        con.format("OK");
        con.format(user.chatList.getMessagesJson(otherUser));
    }
}
