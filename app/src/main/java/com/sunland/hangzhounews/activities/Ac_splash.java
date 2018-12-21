package com.sunland.hangzhounews.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.sunland.hangzhounews.V_config;
import com.sunland.hangzhounews.bean.BaseRequestBean;
import com.sunland.hangzhounews.bean.i_login_bean.LoginResBean;
import com.sunland.hangzhounews.bean.i_mm_login_bean.LoginMMRequestBean;
import com.sunlandgroup.Global;
import com.sunlandgroup.def.bean.result.ResultBase;
import com.sunlandgroup.network.OnRequestCallback;
import com.sunlandgroup.network.RequestManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.cybertech.models.User;
import cn.com.cybertech.pdk.OperationLog;


public class Ac_splash extends Ac_base implements OnRequestCallback {

    private final String TAG = this.getClass().getSimpleName();
    private RequestManager mRequestManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setVisibility(View.GONE);

        //独立App版
        if (mApplication.isIsoApp()) {
            hop2Activity(Ac_login.class);
            return;
        }
        //广达App版，免密登录
        if (mApplication.isAppCyber()) {
            User user = cn.com.cybertech.pdk.UserInfo.getUser(this);
            try {
                V_config.YHDM = user.getAccount();
            } catch (NullPointerException e) {
                Toast.makeText(this, "无法获取警号", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            mRequestManager = new RequestManager(this, this);
            queryYdjwData(V_config.MM_USER_LOGIN);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void queryYdjwData(String reqName) {
        mRequestManager.addRequest(Global.ip, Global.port, Global.postfix, reqName, assembleRequestObj(), 15000);
        mRequestManager.postRequestWithoutDialog();
    }

    public BaseRequestBean assembleRequestObj() {
        LoginMMRequestBean loginBean = new LoginMMRequestBean();
        loginBean.setYhdm(V_config.YHDM);
        loginBean.setImei(V_config.imei);
        loginBean.setImsi(V_config.imsi1);
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String pda_time = simpleDateFormat.format(date);
        loginBean.setPdaTime(pda_time);
        loginBean.setGpsX("gpsx");
        loginBean.setGpsY("gpsy");
        loginBean.setDlmk("1");
        loginBean.setSjpp("1");
        loginBean.setSjxx("1");
        loginBean.setZzxt("1");
        return loginBean;
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public <T> void onRequestFinish(String reqId, String reqName, T bean) {

        LoginResBean loginResBean = (LoginResBean) bean;
        if (loginResBean == null) {
            Toast.makeText(this, "服务异常", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!loginResBean.getCode().equals("0")) {
            saveLog(0, OperationLog.OperationResult.CODE_SUCCESS, appendString(V_config.YHDM, V_config.BRAND, V_config.MODEL));
            Bundle bundle = new Bundle();
            bundle.putString("bmcode", loginResBean.getDljyxx().getBmcode());
            hop2Activity(Ac_main.class, bundle);
        } else {
            saveLog(0, OperationLog.OperationResult.CODE_FAILURE,
                    appendString(V_config.YHDM, V_config.BRAND, V_config.MODEL));
            Toast.makeText(this, loginResBean.getMessage(), Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public <T extends ResultBase> Class<?> getBeanClass(String reqId, String reqName) {
        return LoginResBean.class;
    }
}
