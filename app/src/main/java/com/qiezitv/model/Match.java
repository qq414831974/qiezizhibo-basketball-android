package com.qiezitv.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Match implements Serializable {

    private Long id;
    private String name;
    private Long leagueId;
    private String activityId;
    private Boolean available;
    private Long hostTeamId;
    private Long guestTeamId;
    private Date startTime;
    private Integer duration;
    private Integer status;
    private String place;
    private String score;
    private String penaltyScore;
    private String playPath;
    private String poster;
    private List<Integer> type;
    private String round;
    private String subgroup;
    private String remark;
    private PeopleExpand expand;
    private Integer areaType;
    private Integer wechatType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Long leagueId) {
        this.leagueId = leagueId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Long getHostTeamId() {
        return hostTeamId;
    }

    public void setHostTeamId(Long hostTeamId) {
        this.hostTeamId = hostTeamId;
    }

    public Long getGuestTeamId() {
        return guestTeamId;
    }

    public void setGuestTeamId(Long guestTeamId) {
        this.guestTeamId = guestTeamId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
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

    public String getPlayPath() {
        return playPath;
    }

    public void setPlayPath(String playPath) {
        this.playPath = playPath;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public List<Integer> getType() {
        return type;
    }

    public void setType(List<Integer> type) {
        this.type = type;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(String subgroup) {
        this.subgroup = subgroup;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public PeopleExpand getExpand() {
        return expand;
    }

    public void setExpand(PeopleExpand expand) {
        this.expand = expand;
    }

    public Integer getAreaType() {
        return areaType;
    }

    public void setAreaType(Integer areaType) {
        this.areaType = areaType;
    }

    public Integer getWechatType() {
        return wechatType;
    }

    public void setWechatType(Integer wechatType) {
        this.wechatType = wechatType;
    }
}
