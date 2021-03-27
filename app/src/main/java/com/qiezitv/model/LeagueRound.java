package com.qiezitv.model;

import java.io.Serializable;
import java.util.List;

public class LeagueRound implements Serializable {
    private List<String> rounds;

    public List<String> getRounds() {
        return rounds;
    }

    public void setRounds(List<String> rounds) {
        this.rounds = rounds;
    }
}

