package com.example;
public class Player {
    private String name;
    private String role;
    private int basePrice;

    public Player(String name, String role, int basePrice) {
        this.name = name;
        this.role = role;
        this.basePrice = basePrice;
    }

    public String getName() { return name; }
    public String getRole() { return role; }
    public int getBasePrice() { return basePrice; }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}