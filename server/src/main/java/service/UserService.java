package service;

import dataaccess.*;
import model.*;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData request) throws Exception {
        if (userDAO.getUser(request.username()) != null) {
            throw new AlreadyTakenException();
        }
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new BadRequestException();
        }
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(newUser);
        AuthData auth = new AuthData(UUID.randomUUID().toString(), request.username());
        authDAO.createAuth(auth);
        return auth;
    }

    public AuthData login(UserData request) throws Exception {
        if (request.username() == null || request.password() == null) {
            throw new BadRequestException();
        }
        UserData user = userDAO.getUser(request.username());
        if (user == null || !Objects.equals(user.password(), request.password())) {
            throw new UnauthorizedException();
        }
        AuthData auth = new AuthData(UUID.randomUUID().toString(), request.username());
        authDAO.createAuth(auth);
        return auth;
    }

    public void logout(String token) throws Exception {
        if (authDAO.getAuth(token) == null) {
            throw new UnauthorizedException();
        }
        authDAO.deleteAuth(token);
    }
}
