package com.example;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class AwardedPlayer {
    private final StringProperty playerName;
    private final StringProperty winnerName;
    private final StringProperty winningBid;

    public AwardedPlayer(String playerName, String winnerName, String winningBid) {
        this.playerName = new SimpleStringProperty(playerName);
        this.winnerName = new SimpleStringProperty(winnerName);
        this.winningBid = new SimpleStringProperty(winningBid);
    }

    public StringProperty playerNameProperty() {
        return playerName;
    }

    public StringProperty winnerNameProperty() {
        return winnerName;
    }

    public StringProperty winningBidProperty() {
        return winningBid;
    }

    public String getPlayerName() {
        return playerName.get();
    }

    public String getWinnerName() {
        return winnerName.get();
    }

    public String getWinningBid() {
        return winningBid.get();
    }
}
