package com.qiezitv.model;

import java.io.Serializable;

public class MatchStatistics implements Serializable {
    //传球
    private Integer pass;
    //控球
    private Integer possession;
    //射门
    private Integer shoot;
    //射正
    private Integer shoot_ontarget;
    //射在门框
    private Integer shoot_bar;
    //射门被拦截
    private Integer shoot_blocked;
    //射偏
    private Integer shoot_outside;
    //越位
    private Integer offside;
    //抢断
    private Integer tackle;
    //抢断成功
    private Integer tackle_success;
    //任意球
    private Integer free_kick;
    //犯规
    private Integer foul;
    //扑救
    private Integer save;
    //角球
    private Integer corner;
    //传中
    private Integer cross;
    //传中成功
    private Integer cross_success;
    //长传
    private Integer long_pass;
    //解围
    private Integer clearance;
    //黄牌
    private Integer yellow;
    //红牌
    private Integer red;
    //进球
    private Integer goal;
    //点球
    private Integer penalty_kick;

    public Integer getPass() {
        return pass;
    }

    public void setPass(Integer pass) {
        this.pass = pass;
    }

    public Integer getPossession() {
        return possession;
    }

    public void setPossession(Integer possession) {
        this.possession = possession;
    }

    public Integer getShoot() {
        return shoot;
    }

    public void setShoot(Integer shoot) {
        this.shoot = shoot;
    }

    public Integer getShoot_ontarget() {
        return shoot_ontarget;
    }

    public void setShoot_ontarget(Integer shoot_ontarget) {
        this.shoot_ontarget = shoot_ontarget;
    }

    public Integer getShoot_bar() {
        return shoot_bar;
    }

    public void setShoot_bar(Integer shoot_bar) {
        this.shoot_bar = shoot_bar;
    }

    public Integer getShoot_blocked() {
        return shoot_blocked;
    }

    public void setShoot_blocked(Integer shoot_blocked) {
        this.shoot_blocked = shoot_blocked;
    }

    public Integer getShoot_outside() {
        return shoot_outside;
    }

    public void setShoot_outside(Integer shoot_outside) {
        this.shoot_outside = shoot_outside;
    }

    public Integer getOffside() {
        return offside;
    }

    public void setOffside(Integer offside) {
        this.offside = offside;
    }

    public Integer getTackle() {
        return tackle;
    }

    public void setTackle(Integer tackle) {
        this.tackle = tackle;
    }

    public Integer getTackle_success() {
        return tackle_success;
    }

    public void setTackle_success(Integer tackle_success) {
        this.tackle_success = tackle_success;
    }

    public Integer getFree_kick() {
        return free_kick;
    }

    public void setFree_kick(Integer free_kick) {
        this.free_kick = free_kick;
    }

    public Integer getFoul() {
        return foul;
    }

    public void setFoul(Integer foul) {
        this.foul = foul;
    }

    public Integer getSave() {
        return save;
    }

    public void setSave(Integer save) {
        this.save = save;
    }

    public Integer getCorner() {
        return corner;
    }

    public void setCorner(Integer corner) {
        this.corner = corner;
    }

    public Integer getCross() {
        return cross;
    }

    public void setCross(Integer cross) {
        this.cross = cross;
    }

    public Integer getCross_success() {
        return cross_success;
    }

    public void setCross_success(Integer cross_success) {
        this.cross_success = cross_success;
    }

    public Integer getLong_pass() {
        return long_pass;
    }

    public void setLong_pass(Integer long_pass) {
        this.long_pass = long_pass;
    }

    public Integer getClearance() {
        return clearance;
    }

    public void setClearance(Integer clearance) {
        this.clearance = clearance;
    }

    public Integer getYellow() {
        return yellow;
    }

    public void setYellow(Integer yellow) {
        this.yellow = yellow;
    }

    public Integer getRed() {
        return red;
    }

    public void setRed(Integer red) {
        this.red = red;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    public Integer getPenalty_kick() {
        return penalty_kick;
    }

    public void setPenalty_kick(Integer penalty_kick) {
        this.penalty_kick = penalty_kick;
    }
}
