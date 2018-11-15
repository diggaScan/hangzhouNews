package com.sunland.hangzhounews.bean;

import com.sunlandgroup.def.bean.result.ResultBase;

import java.util.List;

public class NewsKindResponseBean extends ResultBase {
    private List<NewsCategory> newsCategory;

    public List<NewsCategory> getNewsCategory() {
        return newsCategory;
    }

    public void setNewsCategory(List<NewsCategory> newsCategory) {
        this.newsCategory = newsCategory;
    }
}
