package root.operation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import root.User;

import java.util.HashMap;
import java.util.Map;

public class SendMessage extends Operation{
    @Override
    void operate() throws JsonProcessingException {
        //2 ---------------------------------
        User otherUser = workspace.getUser(con.next());
        String receiveJSON = con.nextLine();
        //3 ---------------------------------
        int seq = user.chatList.addMsg(otherUser, receiveJSON);
        con.format("OK %d", seq);
        //4 ---------------------------------
        if (otherUser == null)
            throw new RuntimeException();
        String sendJSON = makeSendJSON(seq, otherUser.username, receiveJSON);
        otherUser.con.format("receive-message %s %s", user.username, sendJSON);
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
        //todo use message class
        return objectMapper.writeValueAsString(tarMap);
    }
}
