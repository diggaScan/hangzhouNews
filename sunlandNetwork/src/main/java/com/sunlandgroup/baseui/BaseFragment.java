/**
 * 版权：浙江信电版权所有 (c) 2014
 * 文件：BaseFragment.java
 * 包名：com.sunland.ydjw.framework
 * 作者：lsj
 * 邮箱：lsj@sunlandgroup.cn
 * 日期：2014年10月8日 下午2:03:09
 */
package com.sunlandgroup.baseui;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    private Receiver rec;
    private List<String> mListAction;
    private OnBroadCastListener bdListener;

    /**
     * 注册广播消息，基类会自动执行unregisterReceiver，子类无需实现
     *
     * @param listMsg
     */

    protected void registerBroadcast(List<String> listMsg, OnBroadCastListener listener) {
        this.bdListener = listener;

        mListAction = new ArrayList<String>();
        rec = new Receiver();
        IntentFilter filter = new IntentFilter();// 创建IntentFilter对象
        for (String msg : listMsg) {
            filter.addAction(msg);
            mListAction.add(msg);
        }
        getActivity().registerReceiver(rec, filter);
    }

    /**
     * 注册广播消息，基类会自动执行unregisterReceiver，子类无需实现
     *
     * @param filter
     * @param listener
     */
    protected void registerBroadcast(IntentFilter filter, OnBroadCastListener listener) {
        if (filter == null)
            return;

        this.bdListener = listener;

        mListAction = new ArrayList<String>();
        rec = new Receiver();

        for (int i = 0; i < filter.countActions(); i++)
            mListAction.add(filter.getAction(i));

        getActivity().registerReceiver(rec, filter);
    }

    @Override
    public void onDestroyView() {
        if (rec != null) {
            getActivity().unregisterReceiver(rec);
            rec = null;
        }
        super.onDestroyView();
    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (mListAction.indexOf(action) >= 0) {
                if (bdListener != null)
                    bdListener.onBroadCastReceived(action, intent);
            }
        }
    }

    /**
     * 广播监听接口
     *
     * @author Jacky
     */
    public interface OnBroadCastListener {
        /**
         * 广播接收回调接口
         *
         * @param action
         * @param intent
         */
        public void onBroadCastReceived(String action, Intent intent);
    }
}
