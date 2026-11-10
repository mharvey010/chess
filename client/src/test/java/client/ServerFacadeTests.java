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

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerUserSuccess() throws Exception {
        UserData user = new UserData("new_user", "supersecret", "chumpy@notanemail.com");
        AuthData returnedAuth = serverFacade.register(user);
        assertNotNull(returnedAuth);
        assertNotNull(returnedAuth.authToken());
        assertEquals(user.username(), returnedAuth.username());
    }

}
