package com.qiezitv.model;

public class FootballStatusEventData {
    private int backgroundResourceId;
    private String text;
    private String conditionText;
    private Integer minute;

    public FootballStatusEventData(int backgroundResourceId, String text, String conditionText, Integer minute) {
        this.backgroundResourceId = backgroundResourceId;
        this.text = text;
        this.conditionText = conditionText;
        this.minute = minute;
    }

    public int getBackgroundResourceId() {
        return backgroundResourceId;
    }

    public void setBackgroundResourceId(int backgroundResourceId) {
        this.backgroundResourceId = backgroundResourceId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getConditionText() {
        return conditionText;
    }

    public void setConditionText(String conditionText) {
        this.conditionText = conditionText;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }
}
