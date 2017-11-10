package org.rvlander.schotten.persistence;

import org.rvlander.schotten.encoding.Encoder;

import java.util.HashMap;

public class MemoryStore<T> extends Store<T> {


    private HashMap<GameKey, StoreRow<T>> mapGame;
    private HashMap<PlayerKey, StoreRow<T>> mapPlayerOne;
    private HashMap<PlayerKey, StoreRow<T>> mapPlayerTwo;

    public MemoryStore(Encoder<T> encoder) {
        super(encoder);
        this.mapGame = new HashMap<>();
        this.mapPlayerOne = new HashMap<>();
        this.mapPlayerTwo = new HashMap<>();
    }

    protected StoreRow<T> storeNewMatch(StoreRow<T> storeRow) throws KeyAlreadyPresentException {
        StoreRow game = this.mapGame.get(storeRow.getGameKey());
        StoreRow gamePlayerOne = this.mapPlayerOne.get(storeRow.getPlayerOneKey());
        StoreRow gamePlayerTwo = this.mapPlayerTwo.get(storeRow.getPlayerTwoKey());
        if(game != null || gamePlayerOne!=null || storeRow.getPlayerTwoKey() != null && gamePlayerTwo != null) {
            throw new KeyAlreadyPresentException();
        }
        mapGame.put(storeRow.getGameKey(), storeRow);
        mapPlayerOne.put(storeRow.getPlayerOneKey(), storeRow);

        if(storeRow.getPlayerTwoKey() != null) {
            mapPlayerTwo.put(storeRow.getPlayerTwoKey(), storeRow);
        }

        return storeRow;
    }

    public StoreRow<T> updateStoredGame(PlayerKey key, T game) throws PlayerNotFoundException {
        StoreRow<T> row = this.mapPlayerOne.get(key);
        if(row == null) {
            row = this.mapPlayerTwo.get(key);
        }
        if (row == null) {
            throw new PlayerNotFoundException();
        }
        row.setGame(game);
        return row;
    }

    protected StoreRow<T> getStoreRow(PlayerKey key) throws PlayerNotFoundException {
        StoreRow<T> row = this.mapPlayerOne.get(key);
        if(row == null) {
            row = this.mapPlayerTwo.get(key);
        }
        if (row == null) {
            throw new PlayerNotFoundException();
        }
        return row;
    }

    protected StoreRow<T> getStoreRow(GameKey key) throws GameNotFoundException {
        StoreRow<T> row = this.mapGame.get(key);
        if (row == null) {
            throw new GameNotFoundException();
        }
        return row;
    }

    @Override
    protected StoreRow<T> getEmptySlotEncoded() {
        for(StoreRow<T> row: this.mapGame.values()) {
            if(row.getPlayerTwoKey() == null) {
                return row;
            }
        }
        return null;
    }

    @Override
    protected StoreRow<T> addPlayerTwoKeyToGame(GameKey gameKey, PlayerKey playerTwoKey) throws KeyAlreadyPresentException {
        StoreRow<T> row = this.mapGame.get(gameKey);
        assert(row != null);
        assert(row.getPlayerTwoKey() == null);
        row.setPlayerTwoKey(playerTwoKey);
        if(this.mapPlayerTwo.get(playerTwoKey) != null) {
            throw new KeyAlreadyPresentException();
        }
        this.mapPlayerTwo.put(playerTwoKey, row);
        return row;
    }
}
