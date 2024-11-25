package com.example;
import java.util.ArrayList;
import java.util.List;

public class Participant {
    private final String name;
    private int budget;
    private int currentBudget; // Stores budget before "Process Bid" is hit.

    public Participant(String name, int budget) {
        this.name = name;
        this.budget = budget;
        this.currentBudget = budget; // Initialize to the same value as budget.
    }

    public String getName() {
        return name;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getCurrentBudget() {
        return currentBudget;
    }

    public void setCurrentBudget(int currentBudget) {
        this.currentBudget = currentBudget;
    }

    // Override toString to return the participant's name
    @Override
    public String toString() {
        return name;
    }
}