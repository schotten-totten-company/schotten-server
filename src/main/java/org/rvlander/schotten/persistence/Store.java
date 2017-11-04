package org.rvlander.schotten.persistence;

import org.rvlander.schotten.encoding.Encoder;

public abstract class Store<T> {

    protected Encoder<T> encoder;

    public Store(Encoder<T> encoder) {
        this.encoder = encoder;
    }

    protected abstract StoreRow<T> storeNewMatch(StoreRow<T> storeRow) throws KeyAlreadyPresentException;

    public StoreRow<Game> newGame(Game game) {
        StoreRow<Game> rowGame;
        try {
            rowGame = this.storeNewMatch(new StoreRow(new GameKey(), new PlayerKey(),
                    null, this.encoder.encode(game)));
        } catch (KeyAlreadyPresentException e) {
            /* TODO: log issue */
            rowGame = this.newGame(game);
        }
        return new StoreRow(rowGame.getGameKey(), rowGame.getPlayerOneKey(), null, game);
    }

    public StoreRow<Game> resgiterPlayerTwo(GameKey key) throws GameNotFoundException, GameAlreadyHasPlayerTwoException {
        StoreRow<T> rowGame = this.getStoreRow(key);
        if(rowGame.getPlayerTwoKey() != null) {
            throw new GameAlreadyHasPlayerTwoException();
        }
        rowGame.setPlayerTwoKey(new PlayerKey());
        return new StoreRow(rowGame.getGameKey(), rowGame.getPlayerOneKey(), rowGame.getPlayerTwoKey(),this.encoder.decode(rowGame.getGame()));
    }

    public StoreRow<Game> updateGame(PlayerKey key, Game game) throws PlayerNotFoundException {
        StoreRow<T> rowGame = this.updateStoredGame(key, this.encoder.encode(game));
        return new StoreRow(rowGame.getGameKey(), rowGame.getPlayerOneKey(), rowGame.getPlayerTwoKey(),this.encoder.decode(rowGame.getGame()));
    }

    public StoreRow<Game> getGame(PlayerKey key) throws PlayerNotFoundException {
        StoreRow<T> rowGame = this.getStoreRow(key);
        return new StoreRow(rowGame.getGameKey(), rowGame.getPlayerOneKey(), rowGame.getPlayerTwoKey(),this.encoder.decode(rowGame.getGame()));
    }

    protected abstract StoreRow<T> updateStoredGame(PlayerKey key, T encodedGame) throws PlayerNotFoundException;

    protected abstract StoreRow<T> getStoreRow(PlayerKey key) throws PlayerNotFoundException;

    protected abstract StoreRow<T> getStoreRow(GameKey key) throws GameNotFoundException;
}