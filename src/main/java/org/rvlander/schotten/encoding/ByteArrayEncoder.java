package org.rvlander.schotten.encoding;

import com.boardgames.bastien.schotten_totten.model.*;
import org.rvlander.schotten.server.ClientErrorCode;
import org.rvlander.schotten.server.ClientMove;

import java.util.List;

public class ByteArrayEncoder implements Encoder<byte []>{

    public static final int HAND_SIZE = 6;
    public static final int MILESTONE_SIZE = 7;
    public static final int NB_MILESTONES = 9;
    public static final int BOARD_SIZE = MILESTONE_SIZE * NB_MILESTONES;


    @Override
    public byte[] encode(Game game) {
        return new byte[0];
    }

    @Override
    public Game decodeGame(byte[] encodedGame) {
        return null;
    }

    public byte[] encode(ClientErrorCode code) {
        byte[] res = new byte[1];
        switch(code) {
            case OK:
                res[0] = 0x00;
                break;
            case WON:
                res[0] = 0x01;
                break;
            case LOST:
                res[0] = 0x02;
                break;
            case INVALID_PLAY:
                res[0] = 0x03;
                break;
        }
        return res;
    }

    public byte[] encode(BoardFromPlayerView board) {
        byte [] encodedMilestone = new byte[BOARD_SIZE + HAND_SIZE];
        for (Milestone milestone: board.getMilestones()) {
            this.encode(milestone, board.getPlayingPlayer(), encodedMilestone, 0);
        }
        this.encode(board.getHand(), encodedMilestone, BOARD_SIZE);
        return encodedMilestone;
    }

    private void encode(Milestone milestone, PlayerType playingPlayer, byte[] target, int boardOffset) {
        int column = milestone.getId();
        List<Card> p1Side = milestone.getPlayer1Side();
        List<Card> p2Side = milestone.getPlayer2Side();

        int columnOffset = column * MILESTONE_SIZE;

        PlayerType milestoneOwner = milestone.getCaptured();

        target[columnOffset + 3] = (byte)(playingPlayer == milestoneOwner?  1 : (milestoneOwner == PlayerType.NONE ? 0 : 2));

        int p1RowOffset = playingPlayer == PlayerType.ONE ? 4 : 0;
        for (int i = 0; i < p1Side.size(); i++) {
            this.encode(p1Side.get(i), target, columnOffset + i + p1RowOffset);
        }

        int p2RowOffset = playingPlayer == PlayerType.ONE ? 0 : 4;
        for (int i = 0; i < p2Side.size(); i++) {
            this.encode(p2Side.get(i), target, columnOffset + i + p2RowOffset);
        }
    }

    private void encode(Hand hand, byte[] target, int offset) {
        List<Card> handCards = hand.getCards();
        for(int i = 0; i < handCards.size(); i++) {
            this.encode(handCards.get(i), target, offset + i);
        }
    }

    private void encode(PlayerType playerType, byte[] target, int offset) {
        switch (playerType) {
            case NONE:
                target[offset] = 0;
                break;
            case ONE:
                target[offset] = 1;
                break;
            case TWO:
                target[offset] = 2;
        }
    }

    private void encode(Card card, byte[] target, int offset) {
        byte color = 0;
        switch (card.getColor()) {
            case BLUE:
                color = 01;
                break;
            case CYAN:
                color = 02;
                break;
            case GREEN:
                color = 03;
                break;
            case GREY:
                color = 04;
                break;
            case RED:
                color = 05;
                break;
            case YELLOW:
                color = 07;
                break;
        }

        byte number = 0;
        switch (card.getNumber()) {
            case ONE:
                number = 10;
                break;
            case TWO:
                number = 20;
                break;
            case THREE:
                number = 30;
                break;
            case FOUR:
                number = 40;
                break;
            case FIVE:
                number = 50;
                break;
            case SIX:
                number = 60;
                break;
            case SEVEN:
                number = 70;
                break;
            case EIGHT:
                number = 80;
                break;
            case NINE:
                number = 90;
                break;
        }

        target[offset] =  (byte)(color + number);
    }


    public ClientMove decodeClientMove(byte [] data) throws DecodingException {
        if(data.length !=3) {
            throw new DecodingException("Data has wrong size.");
        }

        int moveType = data[0];
        int cardInHandIndex = data[1];
        int milestoneIndex = data[2];

        // Should we test bounds here?

        if(moveType == 1) {
            return ClientMove.createReclaimMilestoneMove(milestoneIndex);
        } else if(moveType == 2){
            return ClientMove.createPlayCardMove(cardInHandIndex, milestoneIndex);
        } else {
            throw new DecodingException("Move type is not known.");
        }
    }
}
