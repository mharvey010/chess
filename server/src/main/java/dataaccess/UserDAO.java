package dataaccess;

import model.*;

public interface UserDAO {
    AuthData createUser(UserData user);
    UserData getUser(String username);
}
