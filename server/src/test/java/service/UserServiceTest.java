package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService service;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        service = new UserService(userDAO, authDAO);
    }

    @Test
    void register() throws Exception {
        UserData request = new UserData("player1", "password", "p1@email.com");
        service.register(request);
        UserData user = userDAO.getUser("player1");
        assertEquals("player1", user.username());
    }

    @Test
    void login() throws Exception {
        UserData request = new UserData("player1", "password", "p1@email.com");
        service.register(request);
        AuthData auth = service.login(request);
        assertEquals(auth, authDAO.getAuth(auth.authToken()));
    }

    @Test
    void logout() {
    }
}