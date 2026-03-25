package client;

import com.google.gson.Gson;
import model.GameData;
import model.GamesList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class ServerFacade {
    private String serverURL;
    public String authToken;
    String serverDomain;

    public ServerFacade() throws Exception {
        this("localhost:8080");
        serverURL = "http://localhost:8080";
    }

    public ServerFacade(String serverDomain) throws Exception {
        this.serverDomain = serverDomain;
        serverURL = serverDomain;
    }

    public boolean register(String username, String password, String email) {
        var body = Map.of("username", username, "password", password, "email", email);
        var jsonBody = new Gson().toJson(body);
        Map resp = request("POST", "/user", jsonBody);
        if (resp.containsKey("Error")) {
            return false;
        }
        authToken = (String) resp.get("authToken");
        return true;
    }

    public boolean login(String username, String password) {
        var body = Map.of("username", username, "password", password);
        var jsonBody = new Gson().toJson(body);
        Map resp = request("POST", "/session", jsonBody);
        if (resp.containsKey("Error")) {
            return false;
        }
        authToken = (String) resp.get("authToken");
        return true;
    }
    public boolean logout() {
        Map resp = request("DELETE", "/session", null);
        if (resp.containsKey("Error")) {
            return false;
        }
        authToken = null;
        return true;
    }

    public int createGame(String gameName) {
        var body = Map.of("gameName", gameName);
        var jsonBody = new Gson().toJson(body);
        Map resp = request("POST", "/game", jsonBody);
        if (resp.containsKey("Error")) {
            return -1;
        }
        double gameID = (double) resp.get("gameID");
        return (int) gameID;
    }

    public HashSet<GameData> listGames() {
        String resp = requestString("GET", "/game", null);
        if (resp.contains("Error")) {
            return HashSet.newHashSet(8);
        }
        GamesList games = new Gson().fromJson(resp, GamesList.class);

        return games.games();
    }

    public boolean joinGame(int gameId, String playerColor) {
        Map body;
        if (playerColor != null) {
            body = Map.of("gameID", gameId, "playerColor", playerColor);
        } else {
            body = Map.of("gameID", gameId);
        }
        var jsonBody = new Gson().toJson(body);
        Map resp = request("PUT", "/game", jsonBody);
        return !resp.containsKey("Error");
    }

    private Map request(String method, String endpoint, String body) {
        Map respMap;
        try {
            HttpURLConnection http = makeConnection(method, endpoint, body);

            try {
                if (http.getResponseCode() == 401) {
                    return Map.of("Error", 401);
                }
            } catch (IOException e) {
                return Map.of("Error", 401);
            }


            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                respMap = new Gson().fromJson(inputStreamReader, Map.class);
            }

        } catch (URISyntaxException | IOException exception) {
            return Map.of("Error", exception.getMessage());
        }

        return respMap;
    }

    private String requestString(String method, String endpoint, String body) {
        String resp;
        try {
            HttpURLConnection http = makeConnection(method, endpoint, body);

            try {
                if (http.getResponseCode() == 401) {
                    return "Error: 401";
                }
            } catch (IOException e) {
                return "Error: 401";
            }


            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                resp = readerToString(inputStreamReader);
            }

        } catch (URISyntaxException | IOException exception) {
            return String.format("Error: %s", exception.getMessage());
        }

        return resp;
    }

    private HttpURLConnection makeConnection(String method, String endpoint, String body) throws URISyntaxException, IOException {
        URI uri = new URI(serverURL + endpoint);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);

        if (authToken != null) {
            http.addRequestProperty("authorization", authToken);
        }

        if (!Objects.equals(body, null)) {
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }

        http.connect();
        return http;
    }

    private String readerToString(InputStreamReader reader) {
        StringBuilder sb = new StringBuilder();
        try {
            for (int ch; (ch = reader.read()) != -1; ) {
                sb.append((char) ch);
            }
            return sb.toString();
        } catch (IOException e) {
            return "";
        }

    }
}
