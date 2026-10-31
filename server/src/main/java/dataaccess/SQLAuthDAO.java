package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO{
    public void createAuth(AuthData auth) throws DataAccessException {

    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {

    }

    public void clear() throws DataAccessException {

    }

    private final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS auth_tokens (
                auth_token CHAR(36) NOT NULL,
                username VARCHAR(32) NOT NULL,
                PRIMARY KEY (auth_token),
            );
            """
            ;
}
