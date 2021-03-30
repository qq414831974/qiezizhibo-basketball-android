package com.qiezitv.model.basketball;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeagueVO extends League {
    private String currentRound;
}
