package service;

import dataaccess.*;
import model.*;

import java.util.Collection;
import java.util.Objects;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public Collection<GameData> listGames(String token) throws Exception {
        if (authDAO.getAuth(token) == null) {
            throw new UnauthorizedException();
        }
        return gameDAO.listGames();
    }

    public int createGame(String token, String gameName) throws Exception {
        if (authDAO.getAuth(token) == null) {
            throw new UnauthorizedException();
        }
        if (gameName == null) {
            throw new BadRequestException();
        }
        return gameDAO.createGame(gameName);
    }

    public void joinGame(String token, JoinGameRequest request) throws Exception {
        if (authDAO.getAuth(token) == null) {
            throw new UnauthorizedException();
        }
        if (isBadJoinRequest(request)) {
            throw new BadRequestException();
        }
        GameData game = gameDAO.getGame(request.gameID());
        String user = authDAO.getAuth(token).username();
        if (request.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException();
            }
            GameData newGame = new GameData(request.gameID(), user, game.blackUsername(), game.gameName(), game.game());
            gameDAO.updateGame(request.gameID(), newGame);
        } else if (request.playerColor().equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException();
            }
            GameData newGame = new GameData(request.gameID(), game.whiteUsername(), user, game.gameName(), game.game());
            gameDAO.updateGame(request.gameID(), newGame);
        }
    }

    private boolean isBadJoinRequest(JoinGameRequest request) {
        return request.playerColor() == null ||
                request.gameID() == null || !(
                Objects.equals(request.playerColor(), "WHITE") ||
                Objects.equals(request.playerColor(), "BLACK"));
    }
}
