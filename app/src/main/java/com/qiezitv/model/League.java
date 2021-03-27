package com.qiezitv.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class League implements Serializable {
    private Long id;
    private Long parentId;
    private Boolean isParent;
    private String name;
    private String shortName;
    private String englishName;
    private String majorSponsor;
    private String sponsor;
    private List<String> place;
    private String city;
    private String country;
    private String province;
    private Integer areaType;
    private Integer type;
    private LeagueRound round;
    private LeagueGroup subgroup;
    private LeagueRegulations regulations;
    private String poster;
    private Date dateBegin;
    private Date dateEnd;
    private Long phoneNumber;
    private String wechat;
    private String headImg;
    private String remark;
    private String description;

    public Integer getWechatType() {
        return wechatType;
    }

    public void setWechatType(Integer wechatType) {
        this.wechatType = wechatType;
    }

    private Integer wechatType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(Boolean isParent) {
        this.isParent = isParent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getMajorSponsor() {
        return majorSponsor;
    }

    public void setMajorSponsor(String majorSponsor) {
        this.majorSponsor = majorSponsor;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public List<String> getPlace() {
        return place;
    }

    public void setPlace(List<String> place) {
        this.place = place;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Integer getAreaType() {
        return areaType;
    }

    public void setAreaType(Integer areaType) {
        this.areaType = areaType;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public LeagueRound getRound() {
        return round;
    }

    public void setRound(LeagueRound round) {
        this.round = round;
    }

    public LeagueGroup getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(LeagueGroup subgroup) {
        this.subgroup = subgroup;
    }

    public LeagueRegulations getRegulations() {
        return regulations;
    }

    public void setRegulations(LeagueRegulations regulations) {
        this.regulations = regulations;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Date getDateBegin() {
        return dateBegin;
    }

    public void setDateBegin(Date dateBegin) {
        this.dateBegin = dateBegin;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
