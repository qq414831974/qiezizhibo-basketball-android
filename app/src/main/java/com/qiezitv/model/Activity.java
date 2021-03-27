package com.qiezitv.model;

import java.io.Serializable;
import java.util.Date;

public class Activity implements Serializable {
    private String id;

    private String name;

    private Date createTime;

    private Date startTime;

    private Date endTime;

    private Integer wechatType;

    //拉流id
    private String ingestStreamId;

    //拉流url
    private String ingestStreamUrl;

    //推流地址
    private String pushStreamUrl;

    //播放地址
    private ActivityPullUrls pullStreamUrls;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getWechatType() {
        return wechatType;
    }

    public void setWechatType(Integer wechatType) {
        this.wechatType = wechatType;
    }

    public String getIngestStreamId() {
        return ingestStreamId;
    }

    public void setIngestStreamId(String ingestStreamId) {
        this.ingestStreamId = ingestStreamId;
    }

    public String getIngestStreamUrl() {
        return ingestStreamUrl;
    }

    public void setIngestStreamUrl(String ingestStreamUrl) {
        this.ingestStreamUrl = ingestStreamUrl;
    }

    public String getPushStreamUrl() {
        return pushStreamUrl;
    }

    public void setPushStreamUrl(String pushStreamUrl) {
        this.pushStreamUrl = pushStreamUrl;
    }

    public ActivityPullUrls getPullStreamUrls() {
        return pullStreamUrls;
    }

    public void setPullStreamUrls(ActivityPullUrls pullStreamUrls) {
        this.pullStreamUrls = pullStreamUrls;
    }

    public static final class ActivityPullUrls {
        private String rtmp;
        private String flv;
        private String hls;
        private String udp;

        public String getRtmp() {
            return rtmp;
        }

        public void setRtmp(String rtmp) {
            this.rtmp = rtmp;
        }

        public String getFlv() {
            return flv;
        }

        public void setFlv(String flv) {
            this.flv = flv;
        }

        public String getHls() {
            return hls;
        }

        public void setHls(String hls) {
            this.hls = hls;
        }

        public String getUdp() {
            return udp;
        }

        public void setUdp(String udp) {
            this.udp = udp;
        }
    }
}
