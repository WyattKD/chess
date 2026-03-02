package model.requests;

public record CreateGameRequest(String authToken, String gameName) { }