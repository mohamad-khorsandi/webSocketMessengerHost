package root.operation;

import root.User;

public class GetMessages extends Operation{
    @Override
    Boolean operate() throws Exception {
        //2 -------------------------------------
        User otherUser = user.workspace.getUser(receive.next());
        user.chatList.markAsReadMessagesOf(otherUser);
        send.format("OK");
        send.format(user.chatList.getMessagesJson(otherUser));
        return true;
    }
}
