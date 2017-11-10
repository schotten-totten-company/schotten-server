package org.rvlander.schotten.server;

import com.boardgames.bastien.schotten_totten.controllers.SimpleGameManager;
import com.boardgames.bastien.schotten_totten.exceptions.*;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.PlayerType;
import org.rvlander.schotten.persistence.*;

import java.util.HashMap;

public class ClientManager<T> implements ClientListener{

    HashMap<PlayerKey, Client> clients = new HashMap<>();
    Store<T> store;

    public ClientManager(Store<T> store) {
        this.store = store;
    }

    public boolean register(Client client) {

        PlayerKey playerKey = client.getPlayerKey();
        GameKey gameKey = client.getGameKey();

        if(playerKey != null) {
            try {
                StoreRow row = store.getGame(playerKey);
                clients.put(playerKey, client);
                this.playNextTurn(ClientErrorCode.OK, row, null);
                return true;
            } catch(PlayerNotFoundException e) {
                return false;
            }
        } else if(gameKey != null){
            try{
                StoreRow row = store.resgiterPlayerTwo(gameKey);
                client.setPlayerKey(row.getPlayerTwoKey());
                clients.put(row.getPlayerTwoKey(), client);
                this.playNextTurn(ClientErrorCode.OK, row, null);
                return true;
            } catch(GameNotFoundException e){
                return false;
            }
            catch( GameAlreadyHasPlayerTwoException e) {
            }
        } else { // both keys are null or provided keys where wrong => get the first empty game or create one
            try {
                PlayerKey rowPlayerKey = null;
                StoreRow row = store.getEmptySlot();
                if(row == null) {
                    row = store.newGame(new Game());
                    rowPlayerKey = row.getPlayerOneKey();
                } else {
                    rowPlayerKey = row.getPlayerTwoKey();
                }

                client.setPlayerKey(rowPlayerKey);
                client.setGameKey(row.getGameKey());
                clients.put(rowPlayerKey, client);
                this.playNextTurn(ClientErrorCode.OK, row, null);
            } catch(GameCreationException e) {
                System.exit(-1);
            }
        }
        return true;
    }

    public void nextMove(Client client, ClientMove move) {
        PlayerKey key = client.getPlayerKey();
        if(clients.get(key) == client) {
            try {
                StoreRow<Game> row = store.getGame(key);
                //appliquer le coup
                SimpleGameManager gameManager = new SimpleGameManager(row.getGame());

                PlayerType playerType = PlayerType.ONE;
                if(key == row.getPlayerTwoKey()) {
                    playerType = PlayerType.TWO;
                } else if(key != row.getPlayerOneKey()) {
                    //we have a huge problem
                    System.exit(-1);
                }

                try {
                    switch (move.getType()) {
                        case RECLAIM_MILESTONE:
                            gameManager.reclaimMilestone(playerType, move.getMilestoneIndex());
                            break;
                        case PLAY_CARD:
                            assert (move.getCardInHandIndex() != ClientMove.NOT_USED);
                            gameManager.playerPlays(playerType, move.getCardInHandIndex(), move.getMilestoneIndex());
                            gameManager.swapPlayingPlayer();
                            break;
                    }
                } catch (NotYourTurnException e) {
                    // do nothing what would you want to do?
                } catch(MilestoneSideMaxReachedException e) {
                    playNextTurn(ClientErrorCode.INVALID_PLAY, row, null); // same player will be asked to play
                } catch (EmptyDeckException e) {
                    // I guess it can't happen ...
                }

                Game newGame = gameManager.getGame();
                // Sauver le coup
                row = store.updateGame(key, newGame);
                //appeler client process turn de la clef correspondante a tour suivant

                PlayerType winningPlayer = null;
                try {
                    winningPlayer = newGame.getWinner().getPlayerType();
                } catch(NoPlayerException e) {
                }
                if( winningPlayer == null) {
                    // nobody
                    playNextTurn(ClientErrorCode.OK, row, null);
                } else {
                    // somebody
                    playNextTurn(ClientErrorCode.WON, row, playerType);
                    playNextTurn(ClientErrorCode.LOST, row, playerType == PlayerType.TWO ? PlayerType.ONE : PlayerType.TWO);
                }
            } catch (PlayerNotFoundException e) {
                // should not happen
                System.exit(-1);
            }
        }
        //else we do nothing, client is not register: might be some attacker!
    }

    private void playNextTurn(ClientErrorCode errorCode, StoreRow<Game> row, PlayerType playerType) {
        //chose whose key it is to play looking in state
        try {
            PlayerKey nextPlayer = null;
            PlayerType truePlayerType = playerType;
            if(playerType == null) {
                truePlayerType = row.getGame().getPlayingPlayer().getPlayerType();
            }
            switch (truePlayerType) {
                case ONE:
                    nextPlayer = row.getPlayerOneKey();
                    break;
                case TWO:
                    nextPlayer = row.getPlayerTwoKey();
                    break;
                case NONE:
                    throw new NoPlayerException("");
            }

            if(nextPlayer != null) {
                Client cpt = clients.get(nextPlayer);

                if (cpt != null) {
                    cpt.processTurn(errorCode, nextPlayer, row.getGameKey(),
                                row.getGame().getBoardFromPlayingPlayerView());
                }
            }
        } catch(NoPlayerException e) {
            System.exit(-2);
        }
    }

}
