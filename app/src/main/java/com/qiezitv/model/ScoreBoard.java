package com.qiezitv.model;

import java.io.Serializable;

public class ScoreBoard implements Serializable {
    private Integer id;
    private ScoreboardDetail detail;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ScoreboardDetail getDetail() {
        return detail;
    }

    public void setDetail(ScoreboardDetail detail) {
        this.detail = detail;
    }
}
