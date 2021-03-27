package com.qiezitv.model;

import java.io.Serializable;
import java.util.List;

public class Player implements Serializable {
    private Long id;
    private String name;
    private String englishName;
    private String province;
    private String city;
    private Integer wechatType;
    private List<String> position;
    private Integer sex;
    private Integer height;
    private Integer weight;
    private String headImg;
    private Integer shirtNum;
    private Integer status;

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

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getWechatType() {
        return wechatType;
    }

    public void setWechatType(Integer wechatType) {
        this.wechatType = wechatType;
    }

    public List<String> getPosition() {
        return position;
    }

    public void setPosition(List<String> position) {
        this.position = position;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }


    public Integer getShirtNum() {
        return shirtNum;
    }

    public void setShirtNum(Integer shirtNum) {
        this.shirtNum = shirtNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
