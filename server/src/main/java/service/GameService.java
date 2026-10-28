package service;

import dataaccess.*;
import model.*;

import java.util.Collection;

public class GameService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public Collection<GameData> listGames(String token) throws Exception {
        if (authDAO.getAuth(token) == null) {
            throw new UnauthorizedException();
        }
        return gameDAO.listGames();
    }
}
