package root.operation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import root.User;

import java.util.HashMap;
import java.util.Map;

public class SendMessage extends Operation{
    @Override
    Boolean operate() throws JsonProcessingException {
        //2 ---------------------------------
        String receiverUsername = receive.next();
        String receiveJSON = receive.nextLine();
        //3 ---------------------------------
        int seq = user.chatList.addMsg(receiverUsername, receiveJSON);
        send.format("OK %d", seq);
        //4 ---------------------------------
        User receiver = workspace.getUser(receiverUsername);
        if (receiver == null)
            throw new RuntimeException();
        String sendJSON = makeSendJSON(seq, receiverUsername, receiveJSON);
        receiver.send.format("receive-message %s %s", user.username, sendJSON);
        return true;
    }

    public static String makeSendJSON(int seq, String from, String receivedJSON) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> sourceMap = objectMapper.readValue(receivedJSON, Map.class);
        String type = (String) sourceMap.get("type");
        String body = (String) sourceMap.get("body");

        Map<String, Object> tarMap = new HashMap<>();
        tarMap.put("seq", seq);
        tarMap.put("type", type);
        tarMap.put("from", from);
        tarMap.put("body", body);

        //todo wrong order
        return objectMapper.writeValueAsString(tarMap);
    }
}
