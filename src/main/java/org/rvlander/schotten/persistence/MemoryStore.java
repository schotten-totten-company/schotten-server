package org.rvlander.schotten.persistence;

import org.rvlander.schotten.encoding.Encoder;

import java.util.HashMap;

public class MemoryStore<T> extends Store<T> {
    
    public class BoxedGame {
        public T game;

        public BoxedGame(T game) {
            this.game = game;
        }
    }

    private HashMap<Key, BoxedGame> mapPlayerOne;
    private HashMap<Key, BoxedGame> mapPlayerTwo;

    public MemoryStore(Encoder<T> encoder) {
        super(encoder);
        this.mapPlayerOne = new HashMap<>();
        this.mapPlayerTwo = new HashMap<>();
    }

    protected void storeNewEncodedMatch(KeyPair keyPair, T game)  throws KeyAlreadyPresentException {
        BoxedGame gamePlayerOne = this.mapPlayerOne.get(keyPair.getKeyPlayerOne());
        BoxedGame gamePlayerTwo = this.mapPlayerTwo.get(keyPair.getKeyPlayerTwo());
        if(gamePlayerOne!=null || gamePlayerTwo!=null) {
            throw new KeyAlreadyPresentException();
        }
        BoxedGame newBoxedGame = new BoxedGame(game);
        mapPlayerOne.put(keyPair.getKeyPlayerOne(), newBoxedGame);
        mapPlayerTwo.put(keyPair.getKeyPlayerTwo(), newBoxedGame);
    }

    private BoxedGame getBoxedGame(Key key)  throws GameNotFoundException{
        BoxedGame game = this.mapPlayerOne.get(key);
        if(game == null) {
            game = this.mapPlayerTwo.get(key);
        }
        if (game == null) {
            throw new GameNotFoundException();
        }
    }

    protected void updateEncodedGame(Key key, T game) throws GameNotFoundException{
        getBoxedGame(key).game = game;
    }

    protected T getEncodedGame(Key key) throws GameNotFoundException{
        return getBoxedGame(key).game;
    }
}
