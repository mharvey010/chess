package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static UserData existingUser;
    private static String existingAuth;
    private static CreateGameRequest createRequest;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverFacade = new ServerFacade(String.format("http://localhost:%d", port));
        existingUser = new UserData("existing_user", "secret", "email@zmail.com");
        createRequest = new CreateGameRequest("game1");
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

    @Test
    public void createGameSuccess() throws Exception {
        int result = serverFacade.createGame(existingAuth, new CreateGameRequest("game1"));

        assertTrue(result > 0, "Result returned invalid game ID");
    }

    @Test
    public void createGameUnauthorized() throws Exception {
        //log out user so auth is invalid
        serverFacade.logout(existingAuth);

        assertThrows(Exception.class, () -> serverFacade.createGame(existingAuth, createRequest));
    }

    @Test
    public void createGameBadRequest() {
        CreateGameRequest badRequest = new CreateGameRequest(null);

        assertThrows(Exception.class, () -> serverFacade.createGame(existingAuth, badRequest));
    }

    @Test
    public void listGamesEmpty() throws Exception {
        Collection<GameData> result = serverFacade.listGames(existingAuth);

        assertNotNull(result, "List result did not contain an empty game list");
        assertEquals(0, result.size(), "Found games when none should be there");
    }

    @Test
    public void joinGameSuccess() throws Exception {
        //create game
        int createResult = serverFacade.createGame(existingAuth, createRequest);

        //join as white
        JoinGameRequest joinRequest = new JoinGameRequest("WHITE", createResult);

        //try join
        serverFacade.joinGame(existingAuth, joinRequest);

        Collection<GameData> games = serverFacade.listGames(existingAuth);
        for (GameData game : games) {
            Assertions.assertNotEquals(0, games.size(), "List result did not contain games");
            Assertions.assertEquals(1, games.size(), "List result is incorrect size");
            Assertions.assertEquals(existingUser.username(), game.whiteUsername(),
                    "Username of joined player not present in list result");
            Assertions.assertNull(game.blackUsername(), "Username present on non-joined color");
            break;
        }
    }

    @Test
    public void joinGameUnauthorized() throws Exception {
        //create game
        int createResult = serverFacade.createGame(existingAuth, createRequest);

        //try join as white
        JoinGameRequest joinRequest = new JoinGameRequest("WHITE", createResult);

        assertThrows(Exception.class, () -> serverFacade.joinGame(existingAuth + "bad stuff", joinRequest));
    }
}
