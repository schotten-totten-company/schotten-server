package org.rvlander.schotten.encoding;

import org.rvlander.schotten.persistence.Game;

public interface Encoder<T> {
    public T encode (Game game);
    public Game decode (T encodedGame);
}
