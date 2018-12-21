package com.sunland.hangzhounews.bean.i_login_bean;

import com.sunland.hangzhounews.bean.BaseRequestBean;

public class LoginRequestBean extends BaseRequestBean {
    private String password;   //	密码
    private String dlmk;    //	登录模块(区分不同的APP登录)
    private String sjpp;    //	手机品牌
    private String sjxx;    //	手机型号
    private String zzxt;    //	手机操作系统

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDlmk() {
        return dlmk;
    }

    public void setDlmk(String dlmk) {
        this.dlmk = dlmk;
    }

    public String getSjpp() {
        return sjpp;
    }

    public void setSjpp(String sjpp) {
        this.sjpp = sjpp;
    }

    public String getSjxx() {
        return sjxx;
    }

    public void setSjxx(String sjxx) {
        this.sjxx = sjxx;
    }

    public String getZzxt() {
        return zzxt;
    }

    public void setZzxt(String zzxt) {
        this.zzxt = zzxt;
    }
}
