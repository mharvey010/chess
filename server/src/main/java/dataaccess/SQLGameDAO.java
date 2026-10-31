package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException{
        configureDatabase();
    }

    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    public void updateGame(int gameID, GameData newGame) throws DataAccessException {

    }

    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    public void clear() throws DataAccessException {

    }

    private static final String createStatement =
        """
        CREATE TABLE IF NOT EXISTS users (
            id INT NOT NULL AUTO_INCREMENT,
            white_username VARCHAR(32),
            black_username VARCHAR(32),
            game_name VARCHAR(30) NOT NULL,
            game TEXT NOT NULL
            PRIMARY KEY (id)
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
