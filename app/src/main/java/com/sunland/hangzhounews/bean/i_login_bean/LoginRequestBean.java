package com.sunland.hangzhounews.bean.i_login_bean;

import com.sunland.hangzhounews.bean.BaseRequestBean;

public class LoginRequestBean extends BaseRequestBean {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
