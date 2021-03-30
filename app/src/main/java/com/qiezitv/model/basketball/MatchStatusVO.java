package com.qiezitv.model.basketball;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchStatusVO extends MatchStatus {
    private List<TimeLine> timeLines;
}
