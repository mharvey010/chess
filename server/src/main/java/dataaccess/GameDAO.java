package dataaccess;

import model.*;

import java.util.Collection;

public interface GameDAO {
    GameData createGame(String whiteUsername, String blackUsername, String gameName);
    GameData getGame(int gameID);
    GameData updateGame(int gameID, int newGameID);
    Collection<GameData> listGames();
    void clear();
}
