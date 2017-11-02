package org.rvlander.schotten.persistence;

import org.rvlander.schotten.encoding.Encoder;

public abstract class Store<T> {

    protected Encoder<T> encoder;

    public Store(Encoder<T> encoder) {
        this.encoder = encoder;
    }

    protected abstract void storeNewEncodedMatch(KeyPair keyPair, T encodedGame) throws KeyAlreadyPresentException;

    public KeyPair newGame(Game game) {
        KeyPair keyPair = new KeyPair();
        try {
            this.storeNewEncodedMatch(keyPair, this.encoder.encode(game));
        } catch (KeyAlreadyPresentException e) {
            /* TODO: log issue */
            keyPair = this.newGame(game);
        }
        return keyPair;
    }

    public void updateGame(Key key, Game game) throws GameNotFoundException {
        this.updateEncodedGame(key, this.encoder.encode(game));
    }

    public Game getGame(Key key) throws GameNotFoundException {
       return this.encoder.decode(this.getEncodedGame(key)) ;
    }

    protected abstract void updateEncodedGame(Key key, T encodedGame) throws GameNotFoundException;

    protected abstract T getEncodedGame(Key key) throws GameNotFoundException;
}