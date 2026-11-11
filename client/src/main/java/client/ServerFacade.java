package client;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.*;
import java.util.Map;

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
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(UserData userInfo) throws Exception {
        var request = buildRequest("POST", "/session", userInfo);
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response, AuthData.class);
    }

    private void logout() throws Exception {
    }

    private void listGames() throws Exception {
    }

    private void createGame() throws Exception {
    }

    private void joinGame() throws Exception {
    }

    public void clearApplication() throws Exception {
        var request = buildRequest("DELETE", "/db", null);
        client.send(request, HttpResponse.BodyHandlers.ofString());
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

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                var error = new Gson().fromJson(body, Map.class);
                throw new Exception(error.get("message").toString());
            }
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
