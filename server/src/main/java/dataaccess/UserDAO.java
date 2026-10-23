package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDAO {
    AuthData createUser(UserData user);
    UserData getUser(String username);
}
