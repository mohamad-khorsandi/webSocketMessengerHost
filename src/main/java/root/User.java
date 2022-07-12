package root;


import root.utils.AutoFormatter;

import java.util.Scanner;

public class User {
    public String username;
    public Integer id;
    Workspace workspace;
    public Scanner receive;
    public AutoFormatter send;

    public ChatList chatList = new ChatList(this);

    @Override
    public boolean equals(Object obj) {
        User u2 = (User) obj;
        return u2.id.equals(this.id);
    }
}
