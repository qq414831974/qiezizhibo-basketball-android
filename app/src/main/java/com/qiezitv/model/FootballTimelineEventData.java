package com.qiezitv.model;

import com.qiezitv.R;

import java.util.HashMap;
import java.util.Map;

public class FootballTimelineEventData {
    private String text;
    private boolean isNeedPlayerId;
    private String remarkName;
    private Map<String, String> remarkValueMap;

    public FootballTimelineEventData(String text, boolean isNeedPlayerId, String remarkName, Map<String, String> remarkValueMap) {
        this.text = text;
        this.isNeedPlayerId = isNeedPlayerId;
        this.remarkName = remarkName;
        this.remarkValueMap = remarkValueMap;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isNeedPlayerId() {
        return isNeedPlayerId;
    }

    public void setNeedPlayerId(boolean needPlayerId) {
        isNeedPlayerId = needPlayerId;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public Map<String, String> getRemarkValueMap() {
        return remarkValueMap;
    }

    public void setRemarkValueMap(Map<String, String> remarkValueMap) {
        this.remarkValueMap = remarkValueMap;
    }

    public static Map<Integer, FootballTimelineEventData> timelineEventDataMap = new HashMap<Integer, FootballTimelineEventData>() {
        {
            put(FootballEvent.GOAL, new FootballTimelineEventData("进球", true, "助攻", null));
            put(FootballEvent.SHOOT, new FootballTimelineEventData("射门", true, "状态", new HashMap<String, String>() {{
                put("射偏", "3");
                put("射在门框", "2");
                put("射门被拦截", "1");
            }}));
            put(FootballEvent.OFFSIDE, new FootballTimelineEventData("越位", false, null, null));
            put(FootballEvent.TACKLE, new FootballTimelineEventData("抢断", false, "状态", new HashMap<String, String>() {{
                put("成功", "1");
                put("失败", "0");
            }}));
            put(FootballEvent.FREE_KICK, new FootballTimelineEventData("任意球", false, null, null));
            put(FootballEvent.FOUL, new FootballTimelineEventData("犯规", false, null, null));
            put(FootballEvent.YELLOW, new FootballTimelineEventData("黄牌", true, null, null));
            put(FootballEvent.RED, new FootballTimelineEventData("红牌", true, null, null));
            put(FootballEvent.SAVE, new FootballTimelineEventData("扑救", false, null, null));
            put(FootballEvent.SUBSTITUTE, new FootballTimelineEventData("换人", true, "换上", null));
            put(FootballEvent.CORNER, new FootballTimelineEventData("角球", false, null, null));
            put(FootballEvent.CROSS, new FootballTimelineEventData("传中", false, "状态", new HashMap<String, String>() {{
                put("成功", "1");
                put("失败", "0");
            }}));
            put(FootballEvent.LONG_PASS, new FootballTimelineEventData("长传", false, null, null));
            put(FootballEvent.CLEARANCE, new FootballTimelineEventData("解围", false, null, null));
            put(FootballEvent.OWN_GOAL, new FootballTimelineEventData("乌龙球", true, null, null));
            put(FootballEvent.PENALTY_KICK, new FootballTimelineEventData("点球", false, "状态", new HashMap<String, String>() {{
                put("成功", "1");
                put("失败", "0");
            }}));
            put(FootballEvent.PENALTY_GOAL, new FootballTimelineEventData("点球大战进球", false, null, null));
        }
    };
}
