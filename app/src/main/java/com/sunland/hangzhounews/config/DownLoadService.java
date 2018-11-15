package com.sunland.hangzhounews.config;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.sunland.hangzhounews.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class DownLoadService extends Service {

    public static final String FLAG="DownLoadService";
    private String my_url;
    private final int notif_id=1;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        my_url=intent.getBundleExtra("data").getString("url");

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("下载中...");
        builder.setContentText("正在下载");
         builder .setWhen(System.currentTimeMillis());
        final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notif_id, builder.build());
        builder.setProgress(100,0,false);
        new Thread(new Runnable() {
            @Override
            public void run() {
               File file= Environment.getExternalStorageDirectory();
               String target_path=file.getAbsolutePath()+"/议程摘要.pdf";
               File target_file=new File(target_path);
               try{
                   OutputStream fos=new FileOutputStream(target_file);
                   for(int i=0;i<100;i++){
                       String content="议程摘要";
                       fos.write(content.getBytes());
                       fos.flush();
                       builder.setProgress(100,i,false);
                       manager.notify(notif_id,builder.build());
                       builder.setContentText("下载"+i+"%");

                       try {
                           Thread.sleep(50);//演示休眠50毫秒
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                   }

               }catch (Exception e){
                   e.printStackTrace();
               }
                builder.setContentTitle("下载完成");
                builder.setContentText("");
                builder.setProgress(0,0,false);
                builder.setWhen(System.currentTimeMillis());

                Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath());
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(Uri.fromFile(file),"*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                PendingIntent pi = PendingIntent.getActivity(DownLoadService.this, 0, intent, 0);
                builder.setContentIntent(pi);
                builder.setAutoCancel(true);
                manager.notify(notif_id,builder.build());
            }
        }).start();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("info","onDestroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d("info","onRebind");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
