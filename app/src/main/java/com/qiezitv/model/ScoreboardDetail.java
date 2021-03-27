package com.qiezitv.model;

import java.io.Serializable;

public class ScoreboardDetail implements Serializable {
    private ScoreboardPosition title;
    private ScoreboardPosition hostname;
    private ScoreboardPosition guestname;
    private ScoreboardPosition hostscore;
    private ScoreboardPosition guestscore;
    private ScoreboardPosition hostshirt;
    private ScoreboardPosition guestshirt;
    private ScoreboardPosition time;
    private String scoreboardpic;
    private String hostshirtpic;
    private String guestshirtpic;

    public ScoreboardPosition getTitle() {
        return title;
    }

    public void setTitle(ScoreboardPosition title) {
        this.title = title;
    }

    public ScoreboardPosition getHostname() {
        return hostname;
    }

    public void setHostname(ScoreboardPosition hostname) {
        this.hostname = hostname;
    }

    public ScoreboardPosition getGuestname() {
        return guestname;
    }

    public void setGuestname(ScoreboardPosition guestname) {
        this.guestname = guestname;
    }

    public ScoreboardPosition getHostscore() {
        return hostscore;
    }

    public void setHostscore(ScoreboardPosition hostscore) {
        this.hostscore = hostscore;
    }

    public ScoreboardPosition getGuestscore() {
        return guestscore;
    }

    public void setGuestscore(ScoreboardPosition guestscore) {
        this.guestscore = guestscore;
    }

    public ScoreboardPosition getHostshirt() {
        return hostshirt;
    }

    public void setHostshirt(ScoreboardPosition hostshirt) {
        this.hostshirt = hostshirt;
    }

    public ScoreboardPosition getGuestshirt() {
        return guestshirt;
    }

    public void setGuestshirt(ScoreboardPosition guestshirt) {
        this.guestshirt = guestshirt;
    }

    public ScoreboardPosition getTime() {
        return time;
    }

    public void setTime(ScoreboardPosition time) {
        this.time = time;
    }

    public String getScoreboardpic() {
        return scoreboardpic;
    }

    public void setScoreboardpic(String scoreboardpic) {
        this.scoreboardpic = scoreboardpic;
    }

    public String getHostshirtpic() {
        return hostshirtpic;
    }

    public void setHostshirtpic(String hostshirtpic) {
        this.hostshirtpic = hostshirtpic;
    }

    public String getGuestshirtpic() {
        return guestshirtpic;
    }

    public void setGuestshirtpic(String guestshirtpic) {
        this.guestshirtpic = guestshirtpic;
    }
}
