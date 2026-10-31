package dataaccess;

import model.AuthData;
import model.UserData;

public class SQLAuthDAO implements AuthDAO{
    public SQLAuthDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException _) {
        }
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO auth_tokens (auth_token, username) VALUES (?, ?);";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth.authToken());
                ps.setString(2, auth.username());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM auth_tokens WHERE auth_token = ?;";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String auth = rs.getString(1);
                        String username = rs.getString(2);
                        return new AuthData(auth, username);
                    }
                    return null;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {

    }

    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE TABLE auth_tokens";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
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
