package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO users (username, password) VALUES (?, ?);";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                ps.setString(2, user.password());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM users WHERE username = ?;";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    String name = rs.getString(1);
                    String pass = rs.getString(2);
                    String email = rs.getString(3);
                    return new UserData(name, pass, email);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clear() throws DataAccessException {

    }

    private static final String createStatement =
        """
        CREATE TABLE IF NOT EXISTS users (
            username VARCHAR(32) NOT NULL,
            password CHAR(60) NOT NULL,
            email VARCHAR(255) NOT NULL,
            PRIMARY KEY (username)
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
