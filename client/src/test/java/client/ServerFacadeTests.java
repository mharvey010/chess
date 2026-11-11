package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverFacade = new ServerFacade(String.format("http://localhost:%d", port));
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void setup() throws Exception {
        serverFacade.clearApplication();
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
        UserData user = new UserData("new_user", "secret", "email@zmail.com");
        AuthData returnedAuth = serverFacade.register(user);
        assertNotNull(returnedAuth);
        assertNotNull(returnedAuth.authToken());
        assertEquals(user.username(), returnedAuth.username());
    }

    @Test
    void registerDuplicateFail() throws Exception {
        UserData existingUser = new UserData("existingUser", "p1", "e1");
        serverFacade.register(existingUser);
        assertThrows(Exception.class, () -> serverFacade.register(existingUser));
    }

    @Test
    void loginSuccess() throws Exception {
        UserData existingUser = new UserData("new_user", "secret", "email@zmail.com");
        serverFacade.register(existingUser);
        AuthData auth = serverFacade.login(existingUser);
        assertNotNull(auth);
        assertNotNull(auth.authToken());
        assertEquals(existingUser.username(), auth.username());
    }
}
