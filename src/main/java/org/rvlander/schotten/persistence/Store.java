package org.rvlander.schotten.persistence;

import org.rvlander.schotten.encoding.Encoder;

public abstract class Store<E extends Encoder> {

    protected abstract void storeNewMatch(KeyPair keyPair, Match m);

    public KeyPair newGame(Match m) {
        KeyPair keyPair = new KeyPair();
        this.storeNewMatch(keyPair, m);
        return keyPair;
    }

    public abstract void updateGame(Match m);

    public abstract Match getGame(Key key);
}