package client;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.*;
import model.*;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData userInfo) throws Exception {
        var request = buildRequest("POST", "/user", userInfo);
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new Gson().fromJson(response.body(), AuthData.class);
    }

    public void login(UserData userInfo) throws Exception {
    }

    private void logout() throws Exception {
    }

    private void listGames() throws Exception {
    }

    private void createGame() throws Exception {
    }

    private void joinGame() throws Exception {
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, HttpRequest.BodyPublishers.ofString(new Gson().toJson(body)));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }
}
