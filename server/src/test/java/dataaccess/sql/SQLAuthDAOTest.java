package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {
    private final AuthDAO dataAccess = new SQLAuthDAO();

    @BeforeEach
    public void clearDatabase() throws DataAccessException {
        dataAccess.clear();
    }

    @Test
    void createAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("new_token", "new_user");
        dataAccess.createAuth(auth);
        AuthData returnedAuth = dataAccess.getAuth("new_token");
        assertEquals(auth, returnedAuth);
    }

    @Test
    @DisplayName("No auth in table")
    void getAuthNull() throws DataAccessException {
        AuthData returnedAuth = dataAccess.getAuth("new_token");
        assertNull(returnedAuth);
    }

    @Test
    void deleteAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("new_token", "new_user");
        dataAccess.createAuth(auth);
        AuthData returnedAuth = dataAccess.getAuth("new_token");
        assertNotNull(returnedAuth);
        dataAccess.deleteAuth("new_token");
        returnedAuth = dataAccess.getAuth("new_token");
        assertNull(returnedAuth);
    }
}