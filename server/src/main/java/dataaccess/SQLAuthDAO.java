package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO{
    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createAuth(AuthData auth) throws DataAccessException {

    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {

    }

    public void clear() throws DataAccessException {

    }

    private static final String createStatement =
        """
        CREATE TABLE IF NOT EXISTS auth_tokens (
            auth_token CHAR(36) NOT NULL,
            username VARCHAR(32) NOT NULL,
            PRIMARY KEY (auth_token)
        );
        """
        ;

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
