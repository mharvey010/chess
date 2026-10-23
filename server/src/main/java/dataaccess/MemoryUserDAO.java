package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    private final HashMap<String, UserData> users = new HashMap<>();

    public AuthData createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Username already taken");
        }
        users.put(user.username(), user);
        return createAuth(user.username());
    }

    public UserData getUser(String username) throws DataAccessException {
        if (!users.containsKey(username)) {
            throw new DataAccessException("No account by that username");
        }
        return users.get(username);
    }

    public void clear() {
        users.clear();
    }
}
