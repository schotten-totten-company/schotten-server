package org.rvlander.schotten.server;

import org.rvlander.schotten.persistence.*;

import java.util.HashMap;

public class ClientManager<T> {

    HashMap<PlayerKey, ClientProcessTurn> clients = new HashMap<>();
    Store<T> store;

    public ClientManager(Store<T> store) {
        this.store = store;
    }

    public void register(PlayerKey playerKey, GameKey gameKey, ClientProcessTurn clientProcessTurn) {
        if(playerKey != null) {
            try {
                StoreRow row = store.getGame(playerKey);
                clients.put(playerKey, clientProcessTurn);
                return this.playNextTurn(row);
            } catch(PlayerNotFoundException e) {
            }
        } else if(gameKey != null){
            try{
                StoreRow row = store.resgiterPlayerTwo(gameKey);
                clients.put(row.getPlayerTwoKey(), clientProcessTurn);
                return this.playNextTurn(row);
            } catch(GameNotFoundException e){}
            catch( GameAlreadyHasPlayerTwoException e) {
            }
        } else { // both keys are null or provided keys where wrong => create new Game
            StoreRow row = store.newGame();
            clients.put(row.getPlayerOneKey(), clientProcessTurn);
            this.playNextTurn(row.getPlayerOneKey());
        }
    }

    public void next(PlayerKey key, ) {
        StoreRow row = store.getGame(key);
        //appliquer le coup
        Game newGame =
        // Sauver le coup
        row = store.updateGame(key, newGame);
        //appeler client process turn de la clef correspondante a tour suivant
        playNextTurn(row);
    }

    private void playNextTurn(StoreRow<Game> row) {
        //chose whose key it is to play looking in state
        PlayerKey nextPlayer = ...

        ClientProcessTurn cpt = clients.get(nextPlayer);

        if(cpt != null) {
            cpt.processTurn();
        }
    }
}
