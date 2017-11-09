package org.rvlander.schotten.server;

public class ClientMove {
    private ClientMoveType type;

    public static final int NOT_USED = -1;

    private int milestoneIndex;
    private int cardInHandIndex;

    private ClientMove(ClientMoveType type, int milestoneIndex, int cardInHandIndex) {
        this.type = type;
        this.milestoneIndex = milestoneIndex;
        this.cardInHandIndex = cardInHandIndex;
    }

    public ClientMoveType getType() {
        return type;
    }

    public int getMilestoneIndex() {
        return milestoneIndex;
    }

    public int getCardInHandIndex() {
        return cardInHandIndex;
    }

    public static ClientMove createReclaimMilestoneMove(int milestoneIndex) {
        return new ClientMove(ClientMoveType.RECLAIM_MILESTONE, milestoneIndex, ClientMove.NOT_USED);
    }

    public static ClientMove createPlayCardMove(int cardInHandIndex, int milestoneIndex) {
        return new ClientMove(ClientMoveType.PLAY_CARD, milestoneIndex, cardInHandIndex);
    }
}
