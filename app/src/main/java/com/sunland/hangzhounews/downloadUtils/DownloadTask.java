package com.sunland.hangzhounews.downloadUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sunland.hangzhounews.DataModel;

/**
 * Created by PeitaoYe
 * On 2018/9/10
 **/
public class DownloadTask extends AsyncTask<Integer, Integer, Integer> {
    public String URLStr;

    private DownloadUtils downloadUtils;
    private Context mContext;
    private int notif_id = 1;

    String title;

    private OnProgressUpdateListener onProgressUpdateListener;


    public DownloadTask(Context context, String url, DownloadUtils downloadManager, String title) {
        super();
        mContext = context;
        this.downloadUtils = downloadManager;
        this.URLStr = url;
        this.title = title;
    }


    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }

    public void setOnProgressUpdateListener(OnProgressUpdateListener onProgressUpdateListener) {
        this.onProgressUpdateListener = onProgressUpdateListener;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (onProgressUpdateListener != null) {
            onProgressUpdateListener.onProgressUpdate(values);
        }


    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        Log.d("async", "doInBackground: ");
        // TODO: 2018/11/14/014 把title删除 
        downloadUtils.initDownloadParams(URLStr, title, DataModel.ATTACH_FILE_DIR, new DownloadUtils.OnDownLoadListener() {
            @Override
            public void onDownloadProgress(int progress) {
                Log.d("async", "onDownloadProgress: " + progress);
                publishProgress(progress);
            }

            @Override
            public void onDownloadFail() {

            }

            @Override
            public void onDownloadFinish(int status) {
                switch (status) {
                    case ALREADY_DOWNLOADED:
                        publishProgress(ALREADY_DOWNLOADED);
                        break;
                    case JUST_DOWNLOADED:
                        publishProgress(JUST_DOWNLOADED);
                        break;
                }

            }

            @Override
            public void onPause() {

            }
        });
        downloadUtils.startDownload();
        return 0;
    }

    public void pauseDownload() {
        downloadUtils.pauseDownload();
    }

    public void startDownload() {
        downloadUtils.startDownload();
    }

    public interface OnProgressUpdateListener {
        void onProgressUpdate(Integer... values);
    }
}
