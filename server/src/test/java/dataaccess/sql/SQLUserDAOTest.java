package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {
    private final UserDAO dataAccess = new SQLUserDAO();

    @BeforeEach
    public void clearDatabase() throws DataAccessException {
        dataAccess.clear();
    }

    @Test
    void createUser() throws DataAccessException {
        UserData user = new UserData("new_user", "supersecret", "chumpy@notanemail.com");
        dataAccess.createUser(user);
        UserData returnedUser = dataAccess.getUser("new_user");
        assertEquals(user, returnedUser);
    }

    @Test
    @DisplayName("User doesn't exist")
    void getUserNull() throws DataAccessException {
        UserData returnedUser = dataAccess.getUser("new_user");
        assertNull(returnedUser);
    }
}