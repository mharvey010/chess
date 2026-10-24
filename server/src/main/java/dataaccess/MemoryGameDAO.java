package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();

    public void createGame(GameData game) {
        games.put(game.gameID(), game);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("That game does not exist");
        }
        return games.get(gameID);
    }

    public void updateGame(int gameID, GameData newGame) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("That game does not exist");
        }
        games.put(gameID, newGame);
    }

    public Collection<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    public void clear() {
        games.clear();
    }
}
