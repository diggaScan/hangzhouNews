package com.sunland.hangzhounews.bean;

public class NewsListRequestBean extends BaseRequestBean {
    private int pageNo;
    private int pageIndex;
    private String dqid;
    private int lbid;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getDqid() {
        return dqid;
    }

    public void setDqid(String dqid) {
        this.dqid = dqid;
    }

    public int getLbid() {
        return lbid;
    }

    public void setLbid(int lbid) {
        this.lbid = lbid;
    }
}
