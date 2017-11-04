package org.rvlander.schotten.persistence;

public class StoreRow<T> {
    private GameKey gameKey;
    private PlayerKey playerOneKey;
    private PlayerKey playerTwoKey;
    private T game;

    public StoreRow(GameKey gameKey, PlayerKey playerOneKey, PlayerKey playerTwoKey, T game) {
        this.gameKey = gameKey;
        this.playerOneKey = playerOneKey;
        this.playerTwoKey = playerTwoKey;
        this.game = game;
    }

    public GameKey getGameKey() {
        return gameKey;
    }

    public void setGameKey(GameKey gameKey) {
        this.gameKey = gameKey;
    }

    public PlayerKey getPlayerOneKey() {
        return playerOneKey;
    }

    public void setPlayerOneKey(PlayerKey playerOneKey) {
        this.playerOneKey = playerOneKey;
    }

    public PlayerKey getPlayerTwoKey() {
        return playerTwoKey;
    }

    public void setPlayerTwoKey(PlayerKey playerTwoKey) {
        this.playerTwoKey = playerTwoKey;
    }

    public T getGame() {
        return game;
    }

    public void setGame(T game) {
        this.game = game;
    }
}
