package root;

public class User {
    String username;
    public Integer id;

    @Override
    public boolean equals(Object obj) {
        User u2 = (User) obj;
        return u2.id.equals(this.id);
    }
}
