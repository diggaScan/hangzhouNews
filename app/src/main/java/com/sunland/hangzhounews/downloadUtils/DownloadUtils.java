package com.sunland.hangzhounews.downloadUtils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by PeitaoYe
 * On 2018/9/6
 **/
public class DownloadUtils {

    long file_size = 0;
    long target_file_size = 0;
    private Context mContet;
    private boolean isPaused;
    private int progress;
    private String save_path;
    private long downloaded_size;
    private byte[] buf;
    private File apk_file;
    private String url;
    private OnDownLoadListener onDownLoadListener;

    private DownloadUtils(Context context) {
        this.mContet = context;
    }

    public static DownloadUtils getDownloadManager(Context context) {
        return new DownloadUtils(context);
    }

    public File getApk_file() {
        return apk_file;
    }

    public void initDownloadParams(String url, String title, String saveDir, @Nullable OnDownLoadListener onDownLoadListener) {
        try {
            String file_dir = getExistPath(saveDir);
            String file_name = getNameByUrl(url);
            String file_name2 = title + "." + "png";
            apk_file = new File(file_dir, file_name2);
            save_path = apk_file.getAbsolutePath();
            downloaded_size = apk_file.length();
            Log.d(TAG, "initDownloadParams: " + downloaded_size + apk_file.exists());

            buf = new byte[1024];
            this.url = url;
            this.onDownLoadListener = onDownLoadListener;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startDownload() {
        if (target_file_size == 0) {
            target_file_size = getTargetFileLength(url);
        }
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("RANGE", "bytes=" + downloaded_size + "-" + target_file_size)
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(mContet, "下载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                isPaused = false;
                InputStream is = null;
                FileOutputStream fos = null;
                int buf_len = 0;
                try {
                    is = response.body().byteStream();

                    downloaded_size = apk_file.length();
                    Log.d(TAG, " 已下载大小" + downloaded_size);

                    file_size = response.body().contentLength();
                    Log.d(TAG, " apk的大小" + file_size);
                    if (downloaded_size == target_file_size) {
                        onDownLoadListener.onDownloadFinish(OnDownLoadListener.ALREADY_DOWNLOADED);
                        return;
                    }
                    int code = response.code();
                    RandomAccessFile savedFile = new RandomAccessFile(apk_file, "rwd");

                    while ((buf_len = is.read(buf)) != -1 && !isPaused) {
                        savedFile.seek(downloaded_size);
                        savedFile.write(buf, 0, buf_len);
                        downloaded_size += buf_len;
                        progress = (int) (downloaded_size * 1.0f / file_size * 100);
                        onDownLoadListener.onDownloadProgress(progress);
                    }
                    if (isPaused && progress < 100) {
                        Log.d(TAG, "downloaded size: " + downloaded_size);
                        onDownLoadListener.onPause();
                    } else {
                        onDownLoadListener.onDownloadFinish(OnDownLoadListener.JUST_DOWNLOADED);
                    }
                } catch (Exception e) {
                    onDownLoadListener.onDownloadFail();
                } finally {
                    if (is != null) {
                        is.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                }
            }
        });
    }

    private String getExistPath(String saveDir) throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory(), saveDir);
            if (!file.mkdirs()) {
                file.createNewFile();
            }
            String savePath = file.getAbsolutePath();
            Log.d("savePath:", "getExistPath: " + savePath);
            return savePath;
        }
        return null;
    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void removeDownLoadedFile() {
        if (apk_file == null)
            return;

        if (apk_file.exists()) {
            apk_file.delete();
        }
    }

    private String getNameByUrl(String url) {
        return url.substring(url.lastIndexOf("/"));
    }

    public long getTargetFileLength(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "identity")
                .url(url)
                .build();
        long file_length = 0;
        try {
            file_length = client.newCall(request).execute().body().contentLength();
        } catch (Exception e) {

        }
        return file_length;
    }

    public interface OnDownLoadListener {
        int ALREADY_DOWNLOADED = 1002;
        int JUST_DOWNLOADED = 1001;

        void onDownloadProgress(int progress);

        void onDownloadFail();

        void onDownloadFinish(int status);

        void onPause();
    }

}
