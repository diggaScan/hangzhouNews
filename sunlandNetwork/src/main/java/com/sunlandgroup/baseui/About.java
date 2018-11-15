package com.sunlandgroup.baseui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunlandgroup.baseui.basetab.OnTabItemData;


public class About extends BaseFragment implements OnTabItemData<String> {

    private View contextView;
    private TextView tv_info;

//	public static About newInstance(String info) {
//		Bundle args = new Bundle();
//		args.putString("info", info);
//		About about = new About();
//		about.setArguments(args);
//		return about;
//	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contextView = inflater.inflate(R.layout.activity_about, container, false);

        tv_info = (TextView) contextView.findViewById(R.id.tv_info);
        String info = "";
        try {
            info = getArguments().getString("info", "");
            if (TextUtils.isEmpty(info))
                info = "详情";
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_info.setText(info);
        return contextView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public String getTabItemData() {
        return tv_info.getText().toString();
    }
}

