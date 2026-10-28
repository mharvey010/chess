package dataaccess;

import model.*;

import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName);
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(int gameID, GameData newGame) throws DataAccessException;
    Collection<GameData> listGames();
    void clear();
}
