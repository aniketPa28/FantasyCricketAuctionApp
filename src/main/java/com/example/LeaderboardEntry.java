package com.example;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class LeaderboardEntry {
    private final StringProperty name;
    private final IntegerProperty score;

    public LeaderboardEntry(String name, int score) {
        this.name = new SimpleStringProperty(name);
        this.score = new SimpleIntegerProperty(score);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty scoreProperty() {
        return score;
    }
}