package com.sunland.hangzhounews.bean;

public class NewsDetailRequestBean extends BaseRequestBean {
    private int newsId;
    private int lbid;
    private String dqid;

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public int getLbid() {
        return lbid;
    }

    public void setLbid(int lbid) {
        this.lbid = lbid;
    }

    public String getDqid() {
        return dqid;
    }

    public void setDqid(String dqid) {
        this.dqid = dqid;
    }
}
