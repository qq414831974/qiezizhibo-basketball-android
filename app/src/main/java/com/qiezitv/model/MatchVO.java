package com.qiezitv.model;

import com.qiezitv.common.Constants;

public class MatchVO extends Match {
    private LeagueVO league;
    private TeamVO hostTeam;
    private TeamVO guestTeam;
    private Long hostNooice;
    private Long guestNooice;
    private Long online;
    private Long onlineReal;
    private Long onlineCount;

    public Long getHostNooice() {
        return hostNooice;
    }

    public void setHostNooice(Long hostNooice) {
        this.hostNooice = hostNooice;
    }

    public Long getGuestNooice() {
        return guestNooice;
    }

    public void setGuestNooice(Long guestNooice) {
        this.guestNooice = guestNooice;
    }

    public LeagueVO getLeague() {
        return league;
    }

    public void setLeague(LeagueVO league) {
        this.league = league;
    }

    public TeamVO getHostTeam() {
        return hostTeam;
    }

    public void setHostTeam(TeamVO hostTeam) {
        this.hostTeam = hostTeam;
    }

    public TeamVO getGuestTeam() {
        return guestTeam;
    }

    public void setGuestTeam(TeamVO guestTeam) {
        this.guestTeam = guestTeam;
    }

    public Long getOnline() {
        return online;
    }

    public void setOnline(Long online) {
        this.online = online;
    }

    public Long getOnlineReal() {
        return onlineReal;
    }

    public void setOnlineReal(Long onlineReal) {
        this.onlineReal = onlineReal;
    }

    public Long getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(Long onlineCount) {
        this.onlineCount = onlineCount;
    }

    public boolean isAllowStreaming() {
        return this.getActivityId() != null;
    }

    public boolean isAllowTimeLine() {
        return this.getType() != null && this.getType().contains(Constants.MatchType.TIME_LINE);
    }

    public int getHalfDuration() {
        return this.getDuration() != null ? this.getDuration() / 2 : 45;
    }
}
