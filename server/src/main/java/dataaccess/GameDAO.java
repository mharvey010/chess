package dataaccess;

import model.*;

import java.util.Collection;

public interface GameDAO {
    void createGame(GameData game);
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(int gameID, GameData newGame) throws DataAccessException;
    Collection<GameData> listGames();
    void clear();
}
