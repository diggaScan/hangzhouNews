package com.sunland.hangzhounews.bean.i_newsDetail_bean;

import com.sunlandgroup.def.bean.result.ResultBase;

public class NewsDetailResponseBean extends ResultBase {
    private NewsDetail newsDetail;

    public NewsDetail getNewsDetail() {
        return newsDetail;
    }

    public void setNewsDetail(NewsDetail newsDetail) {
        this.newsDetail = newsDetail;
    }
}
