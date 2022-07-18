package root;

import lombok.NoArgsConstructor;
import root.utils.connections.NormalConnectionPack;

import java.io.Serializable;

@NoArgsConstructor
public class User implements Serializable {
    public User(String username, Integer id, boolean isConnected, NormalConnectionPack con) {
        this.username = username;
        this.id = id;
        this.isConnected = isConnected;
        this.con = con;
    }

    public String username;
    public Integer id;
    public boolean isConnected = false;
    transient public NormalConnectionPack con;

    public ChatList chatList = new ChatList(this);

    @Override
    public boolean equals(Object obj) {
        User u2 = (User) obj;
        return u2.id.equals(this.id);
    }
}
