package com.sunland.hangzhounews.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sunland.hangzhounews.R;


public class DialogUtils {

    private Context mContext;
    private PopupWindow pop_window;
    private TextView tv_title;
    private TextView tv_description;
    private Button btn_cancel;
    private Button btn_confirm;

    private OnCancelListener onCancelListener;
    private onConfirmListener onConfirmListener;

    private Window mWindow;

    public DialogUtils(Context context) {
        this.mContext = context;
        mWindow = ((AppCompatActivity) mContext).getWindow();
    }

    public void initDialog() {
        pop_window = new PopupWindow(mContext);
        View view = getContentView();

        DisplayMetrics metrics = WindowInfoUtils.getWindowMetrics(mContext);
        int width = (int) (metrics.widthPixels * 0.6f);
        int height = (int) (width * 0.638f);

        pop_window.setBackgroundDrawable(new ColorDrawable());
        pop_window.setOutsideTouchable(false);
        pop_window.setHeight(height);
        pop_window.setWidth(width);
        pop_window.setContentView(view);
    }

    private View getContentView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.online_test_dialog, null);
        tv_title = view.findViewById(R.id.title);
        tv_description = view.findViewById(R.id.description);
        btn_cancel = view.findViewById(R.id.cancel_button);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancelListener != null) {
                    onCancelListener.onCancel();
                }
                dismiss();
            }
        });
        btn_confirm = view.findViewById(R.id.confirm_button);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirmListener != null) {
                    onConfirmListener.onConfirm();
                }
                dismiss();
            }
        });

        return view;
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setDescription(String description) {
        tv_description.setText(description);
    }

    public void show() {
        pop_window.showAtLocation(mWindow.getDecorView(), Gravity.CENTER, 0, 0);
        dimWindow();
    }

    public void dismiss() {
        pop_window.dismiss();
        restoreDimWindow();
    }

    private void dimWindow() {
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.alpha = 0.25f;
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mWindow.setAttributes(lp);
    }

    private void restoreDimWindow() {
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.alpha = 1f;
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mWindow.setAttributes(lp);
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void setOnConfirmListener(DialogUtils.onConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public interface OnCancelListener {
        void onCancel();
    }

    public interface onConfirmListener {
        void onConfirm();
    }
}
