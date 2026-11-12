package client;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.*;
import java.util.Collection;
import java.util.Map;

import model.*;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData userInfo) throws Exception {
        var request = buildRequest("POST", "/user", userInfo, null);
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(UserData userInfo) throws Exception {
        var request = buildRequest("POST", "/session", userInfo, null);
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        handleResponse(response, null);
    }

    public Collection<GameData> listGames(String authToken) throws Exception {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ListGamesResult result = handleResponse(response, ListGamesResult.class);
        return result.games();
    }

    public int createGame(String authToken, CreateGameRequest gameName) throws Exception {
        var request = buildRequest("POST", "/game", gameName, authToken);
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        CreateGameResult result = handleResponse(response, CreateGameResult.class);
        return result.gameID();
    }

    public void joinGame(String authToken, JoinGameRequest joinRequest) throws Exception {
        var request = buildRequest("PUT", "/game", joinRequest, authToken);
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        handleResponse(response, null);
    }

    public void clearApplication() throws Exception {
        var request = buildRequest("DELETE", "/db", null, null);
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, HttpRequest.BodyPublishers.ofString(new Gson().toJson(body)));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            var error = new Gson().fromJson(body, Map.class);
            throw new Exception(error.get("message").toString());
        }
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
