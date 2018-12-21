package com.sunland.hangzhounews.bean.i_newsDetail_bean;

import com.sunland.hangzhounews.bean.BaseRequestBean;

public class NewsDetailRequestBean extends BaseRequestBean {
    private String newsId;
    private int lbid;
    private String dqid;

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
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
