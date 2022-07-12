package root;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class ChatList {

    public ChatList(User user) {
        this.owner = user;
    }

    User owner;
    private HashMap<User, Chat> userChatMap = new HashMap<>();

    public int addMsg(String receiverName, String msgJSON) throws JsonProcessingException {
        User receiver = owner.workspace.getUser(receiverName);
        Chat chat = userChatMap.get(receiver);
        Message newMsg = new Message(msgJSON);

        if (chat != null){
            int seq = chat.addMsg(new Message(msgJSON));
            return seq;
        }

        chat = new Chat(receiver);
        chat.addMsg(newMsg);

        userChatMap.put(receiver, chat);
        receiver.chatList.userChatMap.put(owner, chat);
        return 1;
    }

    public String getChatsJson() throws JsonProcessingException {
        SimpleModule module = new SimpleModule();

        module.addSerializer(Chat.class, new StdSerializer<>(Chat.class) {
            @Override
            public void serialize(Chat chat, JsonGenerator jGen, SerializerProvider serializerProvider) throws IOException {
                jGen.writeStartObject();
                jGen.writeStringField("name", chat.otherUser.username);
                jGen.writeNumberField("unread_count", chat.unreadCount);
                jGen.writeEndObject();
            }

        });

        Host.objectMapper.registerModule(module);

        String json = Host.objectMapper.writeValueAsString(userChatMap.values());
        return json;
    }

    public String getMessagesJson(User otherUser) throws JsonProcessingException {
        SimpleModule module = new SimpleModule();

        module.addSerializer(Message.class, new StdSerializer<>(Message.class) {
            @Override
            public void serialize(Message message, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeNumberField("seq", message.seq);
                jsonGenerator.writeStringField("from", message.chat.otherUser.username);
                jsonGenerator.writeStringField("type", message.type);
                jsonGenerator.writeStringField("body", message.body);
                jsonGenerator.writeEndObject();
            }
        });
        Host.objectMapper.registerModule(module);

        return Host.objectMapper.writeValueAsString(userChatMap.get(otherUser).messages);
    }

    public void markAsReadMessagesOf(User otherUser){
        Chat chatWithU2 = this.userChatMap.get(otherUser);
        chatWithU2.markAsRead();
    }
    @Data
    static class Chat{
        public Chat(User user) {
            this.otherUser = user;
        }

        private ArrayList<Message> messages = new ArrayList<>();

        private User otherUser;
        private int unreadCount = 0;

        void markAsRead(){
            unreadCount = 0;
        }

        int addMsg(Message msg){
            messages.add(msg);
            msg.chat = this;
            unreadCount++;
            msg.seq = messages.size();
            return messages.size();
        }
    }

    @Data
    @NoArgsConstructor
    static class Message{
        public Message(String type, String body) {
            this.type = type;
            this.body = body;
        }

        public Message(String JSON) throws JsonProcessingException {
            ObjectMapper objectMapper = new ObjectMapper();
            Message tmpMsg = objectMapper.readValue(JSON, Message.class);
            this.type = tmpMsg.type;
            this.body = tmpMsg.body;
        }
        private String type;
        private String body;

        @JsonIgnore
        private int seq;
        @JsonIgnore
        private Chat chat;

    }
}
