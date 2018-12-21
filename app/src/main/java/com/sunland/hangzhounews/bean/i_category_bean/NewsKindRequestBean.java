package com.sunland.hangzhounews.bean.i_category_bean;

import com.sunland.hangzhounews.bean.BaseRequestBean;

public class NewsKindRequestBean extends BaseRequestBean {
    private String dqid;

    public String getDqid() {
        return dqid;
    }

    public void setDqid(String dqid) {
        this.dqid = dqid;
    }
}
