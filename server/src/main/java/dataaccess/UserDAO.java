package dataaccess;

import model.*;

public interface UserDAO {
    AuthData createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clear();
}
