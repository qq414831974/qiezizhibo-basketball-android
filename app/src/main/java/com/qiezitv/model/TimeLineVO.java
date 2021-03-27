package com.qiezitv.model;

public class TimeLineVO extends TimeLine {
    private PlayerVO player;
    private PlayerVO secondPlayer;
    private Integer time;

    public PlayerVO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerVO player) {
        this.player = player;
    }

    public PlayerVO getSecondPlayer() {
        return secondPlayer;
    }

    public void setSecondPlayer(PlayerVO secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
}
