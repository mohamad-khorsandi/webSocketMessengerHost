package root;


import root.utils.connections.NormalConnectionPack;

import java.io.Serializable;

public class User implements Serializable {
    public String username;
    public Integer id;

    public NormalConnectionPack con;

    public ChatList chatList = new ChatList(this);

    @Override
    public boolean equals(Object obj) {
        User u2 = (User) obj;
        return u2.id.equals(this.id);
    }
}
