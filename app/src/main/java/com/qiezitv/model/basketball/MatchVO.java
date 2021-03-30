package com.qiezitv.model.basketball;

import com.qiezitv.common.Constants;
import com.qiezitv.model.basketball.League;
import com.qiezitv.model.basketball.LeagueVO;
import com.qiezitv.model.basketball.Match;
import com.qiezitv.model.basketball.MatchStatus;
import com.qiezitv.pojo.MatchAgainstNooice;
import com.qiezitv.pojo.MatchAgainstVO;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchVO extends Match {
    private League league;
    private Map<Integer, MatchAgainstVO> againstTeams;
    private Map<Integer, MatchAgainstNooice> againstTeamsNooice;
    private MatchStatus status;
    private Long online;
    private Long onlineReal;
    private Long onlineCount;

    public boolean isAllowStreaming() {
        return this.getActivityId() != null;
    }

    public boolean isAllowTimeLine() {
        return this.getType() != null && this.getType().contains(Constants.MatchType.STATISTICS);
    }
}
