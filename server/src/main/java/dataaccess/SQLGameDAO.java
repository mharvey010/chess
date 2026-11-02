package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() {
        try {
            SQLTableSetup.configureDatabase(CREATE_STATEMENT);
        } catch (DataAccessException _) {
        }
    }

    public int createGame(String gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO games (game_name, game) VALUES (?, ?);";
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, gameName);
                ps.setString(2, new Gson().toJson(new chess.ChessGame()));
                ps.executeUpdate();
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
                return 0;
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM games WHERE id = ?;";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, String.valueOf(gameID));
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        String whiteUser = rs.getString(2);
                        String blackUser = rs.getString(3);
                        String gameName = rs.getString(4);
                        String gameString = rs.getString(5);
                        ChessGame game = new Gson().fromJson(gameString, ChessGame.class);
                        return new GameData(id, whiteUser, blackUser, gameName, game);
                    }
                    return null;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void updateGame(int gameID, GameData newGame) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement =
            "UPDATE games SET id = ?, white_username = ?, black_username = ?, game_name = ?, game = ? WHERE id = ?;";

            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, newGame.gameID());
                ps.setString(2, newGame.whiteUsername());
                ps.setString(3, newGame.blackUsername());
                ps.setString(4, newGame.gameName());
                ps.setString(5, new Gson().toJson(newGame.game()));
                ps.setInt(6, gameID);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> gameList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM games;";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String whiteUser = rs.getString(2);
                        String blackUser = rs.getString(3);
                        String gameName = rs.getString(4);
                        String gameString = rs.getString(5);
                        ChessGame game = new Gson().fromJson(gameString, ChessGame.class);
                        gameList.add(new GameData(id, whiteUser, blackUser, gameName, game));
                    }
                    return gameList;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE TABLE games;";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private static final String CREATE_STATEMENT =
        """
        CREATE TABLE IF NOT EXISTS games (
            id INT NOT NULL AUTO_INCREMENT,
            white_username VARCHAR(32),
            black_username VARCHAR(32),
            game_name VARCHAR(30) NOT NULL,
            game TEXT NOT NULL,
            PRIMARY KEY (id)
        );
        """
        ;
}
