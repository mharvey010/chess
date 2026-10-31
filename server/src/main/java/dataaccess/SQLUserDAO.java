package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO {

    public void createUser(UserData user) throws DataAccessException {

    }

    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    public void clear() throws DataAccessException {

    }

    private final String createStatement =
        """
        CREATE TABLE IF NOT EXISTS users (
            username VARCHAR(32) NOT NULL,
            password CHAR(60) NOT NULL,
            PRIMARY KEY (username),
        );
        """
    ;
}
