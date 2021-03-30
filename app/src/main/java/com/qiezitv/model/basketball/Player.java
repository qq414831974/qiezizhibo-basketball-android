package com.qiezitv.model.basketball;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player implements Serializable {
    private Long id;
    private String name;
    private String englishName;
    private List<String> position;
    private Integer sex;
    private Integer height;
    private Integer weight;
    private String headImg;
    private String remark;
    private Integer shirtNum;
    private Integer status;
    private String province;
    private String city;
    //0：茄子体育，1：青少年，2：茄子fc
    private Integer wechatType;
}
