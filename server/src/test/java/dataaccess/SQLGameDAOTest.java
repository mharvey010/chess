package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {
    private final GameDAO dataAccess = new SQLGameDAO();

    @BeforeEach
    public void clearDatabase() throws DataAccessException {
        dataAccess.clear();
    }

    @Test
    void createGame() throws DataAccessException {
        int gameID = dataAccess.createGame("game1");
        GameData returnedGame = dataAccess.getGame(gameID);
        assertNotNull(returnedGame);
    }

    @Test
    void getGame() throws DataAccessException {
        GameData game = dataAccess.getGame(1);
        assertNull(game);
    }

    @Test
    void updateGame() throws DataAccessException {
        int gameID = dataAccess.createGame("game1");
        GameData game = dataAccess.getGame(gameID);
        GameData newGame = new GameData(1, "iamwhite", "iamblack", "updated game", new ChessGame());
        dataAccess.updateGame(gameID, newGame);
        GameData updatedGame = dataAccess.getGame(gameID);
        assertNotEquals(game, updatedGame);
    }

    @Test
    void listGames() throws DataAccessException {
        dataAccess.createGame("game1");
        dataAccess.createGame("game2");
        Collection<GameData> games = dataAccess.listGames();
        assertNotNull(games);
        assertEquals(2, games.size());
    }
}