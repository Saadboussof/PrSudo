package com.xoudouqi.model;

public enum AnimalType {
    ELEPHANT(8, "Elephant", "🐘"),
    LION(7, "Lion", "🦁"),
    TIGER(6, "Tiger", "🐅"),
    PANTHER(5, "Panther", "🐆"),
    CHIEN(4, "Dog", "🐕"),
    LOUP(3, "Wolf", "🐺"),
    CHAT(2, "Cat", "🐱"),
    RAT(1, "Rat", "🐭");

    private final int strength;
    private final String name;
    private final String symbol;

    AnimalType(int strength, String name, String symbol) {
        this.strength = strength;
        this.name = name;
        this.symbol = symbol;
    }

    public int getStrength() {
        return strength;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean canCapture(AnimalType target) {
        if (this == RAT && target == ELEPHANT) {
            return true;
        }
        if (this == ELEPHANT && target == RAT) {
            return false;
        }
        return this.strength >= target.strength;
    }


    public boolean canCrossWater() {
        return this == LION || this == TIGER;
    }

    public boolean canSwimInWater() {
        return this == RAT;
    }
}
