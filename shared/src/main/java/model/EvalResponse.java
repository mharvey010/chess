package model;

public record EvalResponse(ResultType type, Object data) {
    public enum ResultType {
        SUCCESS,
        ERROR,
        GAMES_LIST,
        BOARD,
        QUIT
    }
}
