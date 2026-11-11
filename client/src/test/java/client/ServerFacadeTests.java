package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static UserData existingUser;
    private static String existingAuth;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverFacade = new ServerFacade(String.format("http://localhost:%d", port));
        existingUser = new UserData("existing_user", "secret", "email@zmail.com");
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void setup() throws Exception {
        serverFacade.clearApplication();

        //one user already logged in
        AuthData auth = serverFacade.register(existingUser);
        existingAuth = auth.authToken();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    void registerSuccess() throws Exception {
        UserData newUser = new UserData("new_user", "p1", "e1");
        AuthData returnedAuth = serverFacade.register(newUser);
        assertNotNull(returnedAuth);
        assertNotNull(returnedAuth.authToken());
        assertEquals(newUser.username(), returnedAuth.username());
    }

    @Test
    void registerDuplicateFail() {
        assertThrows(Exception.class, () -> serverFacade.register(existingUser));
    }

    @Test
    void loginSuccess() throws Exception {
        AuthData auth = serverFacade.login(existingUser);
        assertNotNull(auth);
        assertNotNull(auth.authToken());
        assertEquals(existingUser.username(), auth.username());
    }

    @Test
    void logoutSuccess() throws Exception {
        AuthData auth = serverFacade.login(existingUser);
        serverFacade.logout(auth.authToken());
        assertDoesNotThrow(() -> serverFacade.login(existingUser));
    }

    @Test
    public void logoutTwice() throws Exception {
        //log out user twice
        //second logout should fail
        serverFacade.logout(existingAuth);
        assertThrows(Exception.class, () -> serverFacade.logout(existingAuth));
    }
}
