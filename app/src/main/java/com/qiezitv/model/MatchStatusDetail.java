package com.qiezitv.model;

import java.io.Serializable;
import java.util.List;

public class MatchStatusDetail implements Serializable {
    private Integer minute;
    private Integer status;
    private String score;
    private String penaltyScore;
    private List<TimeLineVO> timeLines;

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }


    public String getPenaltyScore() {
        return penaltyScore;
    }

    public void setPenaltyScore(String penaltyScore) {
        this.penaltyScore = penaltyScore;
    }

    public List<TimeLineVO> getTimeLines() {
        return timeLines;
    }

    public void setTimeLines(List<TimeLineVO> timeLines) {
        this.timeLines = timeLines;
    }

}
