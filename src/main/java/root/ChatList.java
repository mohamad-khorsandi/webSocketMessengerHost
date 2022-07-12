package root;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatList {

    public ChatList(User user) {
        this.owner = user;
    }

    User owner;
    HashMap<User, Chat> userChatMap = new HashMap<>();

    public int addMsg(String receiverName, String msgJSON) throws JsonProcessingException {
        User receiver = owner.workspace.getUser(receiverName);
        Chat chat = userChatMap.get(receiver);
        Message newMsg = new Message(msgJSON);

        if (chat != null){
            int seq = chat.addMsg(new Message(msgJSON));
            return seq;
        }

        chat = new Chat(receiverName);
        chat.addMsg(newMsg);

        userChatMap.put(receiver, chat);
        receiver.chatList.userChatMap.put(owner, chat);
        return 1;
    }

    public String toJson() throws JsonProcessingException {
        String json = Host.objectMapper.writeValueAsString(userChatMap.values());
        System.out.println(json);
        return json;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class Chat{
        public Chat(String username) {
            this.name = username;
        }

        @JsonIgnore
        private ArrayList<Message> messages = new ArrayList<>();

        String name;
        int unreadCount = 0;

        int addMsg(Message msg){
            messages.add(msg);
            unreadCount++;
            return messages.size();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class Message{
        public Message(String JSON) throws JsonProcessingException {
            ObjectMapper objectMapper = new ObjectMapper();
            Message tmpMsg = objectMapper.readValue(JSON, Message.class);
            this.type = tmpMsg.type;
            this.body = tmpMsg.body;
        }

        String type;
        String body;
    }
}
