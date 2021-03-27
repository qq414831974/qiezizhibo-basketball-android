package com.qiezitv.model;

import java.io.Serializable;
import java.util.List;

public class LeagueGroup implements Serializable {
    private List<String> groups;

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }
}
