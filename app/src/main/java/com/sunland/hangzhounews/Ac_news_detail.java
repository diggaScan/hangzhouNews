package com.sunland.hangzhounews;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sunland.hangzhounews.bean.NewsDetail;
import com.sunland.hangzhounews.bean.NewsDetailRequestBean;
import com.sunland.hangzhounews.bean.NewsDetailResponseBean;
import com.sunland.hangzhounews.dbHelper.MyDatabase;
import com.sunland.hangzhounews.dbHelper.OpenDbHelper;
import com.sunland.hangzhounews.dbHelper.news_collection.News;
import com.sunland.hangzhounews.downloadUtils.DownloadTask;
import com.sunland.hangzhounews.downloadUtils.DownloadUtils;
import com.sunland.hangzhounews.utils.FileUtils;
import com.sunlandgroup.Global;
import com.sunlandgroup.def.bean.result.ResultBase;
import com.sunlandgroup.network.OnRequestCallback;
import com.sunlandgroup.network.RequestManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class Ac_news_detail extends Ac_base implements OnRequestCallback {

    @BindView(R.id.title)
    public TextView tv_title;
    @BindView(R.id.addTime)
    public TextView tv_addTime;
    @BindView(R.id.edit_name)
    public TextView tv_edit_name;
    @BindView(R.id.content)
    public TextView tv_content;

    @BindView(R.id.collect_button)
    public ImageView btn_collect;
    @BindView(R.id.index_title)
    public TextView tv_toolbar_title;
    @BindView(R.id.news_detail)
    public WebView wv_news;
    public TextView tv_alert_content;
    private ProgressBar progressBar;
    private TextView progress_num;
    private Button btn_pos;
    private Button btn_neutral;
    private String dqid;
    private int lbid;
    private int newsId;
    public boolean isCollected = false;
    private RequestManager mRequestManager;

    private NewsDetail newsDetail;


    private DownloadTask downloadTask;
    private DownloadUtils downloadUtils;
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarLayout(R.layout.toolbar_layout_detail);
        setContentLayout(R.layout.ac_news_detail);
        tv_toolbar_title.setText("资讯详情");
        mRequestManager = new RequestManager(this, this);
        handleIntent();
        isCollected();
        initWebView();
        queryYdjwData();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getBundleExtra("bundle");
            if (bundle != null) {
                dqid = bundle.getString("dqid");
                lbid = bundle.getInt("lbid");
                newsId = bundle.getInt("newsId");
            }
        }
    }

    private void queryYdjwData() {
        mRequestManager.addRequest(Global.ip, Global.port, Global.postfix, DataModel.NEWS_DETAIL_REQNAME
                , assembleRequestObj(), 15000);
        mRequestManager.postRequest();
    }

    private NewsDetailRequestBean assembleRequestObj() {
        NewsDetailRequestBean newsDetailRequestBean = new NewsDetailRequestBean();
        newsDetailRequestBean.setYhdm("test");
        newsDetailRequestBean.setImei(Global.imei);
        newsDetailRequestBean.setImsi(Global.imsi1);
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String pda_time = simpleDateFormat.format(date);
        newsDetailRequestBean.setPdaTime(pda_time);
        newsDetailRequestBean.setNewsId(newsId);
        newsDetailRequestBean.setDqid(dqid);
        newsDetailRequestBean.setLbid(lbid);
        return newsDetailRequestBean;
    }

    private void initNewsDetail() {
        tv_addTime.setText(newsDetail.getAddTime().substring(0, 10));
        tv_content.setText(newsDetail.getContent());
        tv_edit_name.setText(newsDetail.getEditname());
        tv_title.setText(newsDetail.getTitle());
    }

    private void initWebView() {
        String html = "<html>\n" +
                "\n" +
                "<head>\n" +
                "<title>我的第一个 HTML 页面</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<a href=\"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542780642&di=caad6ceec69197af957b5862b5dc4746&imgtype=jpg&er=1&src=http%3A%2F%2Fimg2.template.cache.wps.cn%2Fwps%2Fcdnwps%2Fupload%2Fofficial%2Ftemplate%2F2013-2-28%2F512f03fde2cec.png\">下载相关图片</a>\n" +
                "</br>" +
                "<a href=\"http://engineering.org.cn/CN/article/downloadArticleFile.do?attachType=PDF&id=12276\">相关pdf下载</a>" +
                "</body>\n" +
                "\n" +
                "</html>\n";
        WebSettings webSettings = wv_news.getSettings();
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(false);//设置webview推荐使用的窗口
        webSettings.setLoadWithOverviewMode(true);//设置webview加载的页面的模式
        webSettings.setDisplayZoomControls(false);//隐藏webview缩放按钮

        wv_news.loadData(html, "text/html; charset=UTF-8", null);
        wv_news.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                onInterceptUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

        });
    }

    @OnClick({R.id.home_image, R.id.collect_button})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.home_image:
                finish();
                break;
            case R.id.collect_button:
                if (isCollected) {
                    btn_collect.setBackgroundResource(R.drawable.ic_bookmark);
                    Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
                    deleteNews();
                } else {
                    btn_collect.setBackgroundResource(R.drawable.ic_bookmark_marked);
                    Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
                    collectNews();
                }
                isCollected = !isCollected;
                break;
        }
    }

    private void deleteNews() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OpenDbHelper.createDb(Ac_news_detail.this);
                MyDatabase db = OpenDbHelper.getDb();
                News news = new News();
                news.newsId = newsId;
                news.dqid = dqid;
                news.lbid = lbid;
                db.newsDAO().deleteNews(news);
            }
        }).start();
    }

    private void collectNews() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OpenDbHelper.createDb(Ac_news_detail.this);
                MyDatabase db = OpenDbHelper.getDb();
                News news = new News();
                news.title = newsDetail.getTitle();
                news.dqid = dqid;
                news.lbid = lbid;
                news.newsId = newsId;
                news.timeStamp = System.currentTimeMillis();
                db.newsDAO().insert(news);
            }
        }).start();
    }

    private void isCollected() {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                boolean result = (Boolean) msg.obj;

                if (result) {
                    btn_collect.setBackground(getResources().getDrawable(R.drawable.ic_bookmark_marked));
                } else {
                    btn_collect.setBackground(getResources().getDrawable(R.drawable.ic_bookmark));
                }
                isCollected = result;
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                OpenDbHelper.createDb(Ac_news_detail.this);
                MyDatabase db = OpenDbHelper.getDb();
                List<Integer> newsIds = db.newsDAO().loadAllNewsId();
                boolean isCollected = newsIds.contains(newsId);
                Message msg = handler.obtainMessage();
                msg.obj = isCollected;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void onInterceptUrl(final String url) {
        View view = LayoutInflater.from(this).inflate(R.layout.download_progress_layout, null);

        progressBar = view.findViewById(R.id.download_progress);
        progressBar.setMax(100);
        progress_num = view.findViewById(R.id.num);
        tv_alert_content = view.findViewById(R.id.alert_content);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("附件下载");
        builder.setView(view);
        builder.setIcon(R.mipmap.ic_app);
        builder.setPositiveButton("取消下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadTask.cancel(true);
            }
        });
        builder.setNegativeButton("查看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String file_name = url.substring(url.lastIndexOf("/"));
                String ext = file_name.substring(file_name.lastIndexOf("."));
                String mimeType = FileUtils.getMIMEType(file_name);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), DataModel.ATTACH_FILE_DIR + "/" + newsDetail.getTitle() + ext));
                intent.setDataAndType(uri, mimeType);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(Ac_news_detail.this, "无相关应用可以打开本文件", Toast.LENGTH_SHORT).show();
                }
            }

        });
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        btn_pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn_neutral = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        btn_neutral.setVisibility(View.GONE);
        startDownLoad(url);
    }

    private void startDownLoad(String url) {
        downloadUtils = DownloadUtils.getDownloadManager(this);
        downloadTask = new DownloadTask(this, url, downloadUtils, newsDetail.getTitle());
        downloadTask.setOnProgressUpdateListener(new DownloadTask.OnProgressUpdateListener() {
            @Override
            public void onProgressUpdate(Integer... values) {
                if (values[0] == DownloadUtils.OnDownLoadListener.JUST_DOWNLOADED) {
                    tv_alert_content.setVisibility(View.VISIBLE);
                    dialog.setTitle("下载完成");
                    btn_neutral.setVisibility(View.VISIBLE);
                    btn_neutral.setText("查看");
                    btn_pos.setText("确定");
                    Toast.makeText(Ac_news_detail.this, "下载完成", Toast.LENGTH_SHORT).show();
                } else if (values[0] == DownloadUtils.OnDownLoadListener.ALREADY_DOWNLOADED) {
                    dialog.setTitle("");
                    tv_alert_content.setVisibility(View.VISIBLE);
                    tv_alert_content.setText("附件已保存至本地");
                    progressBar.setVisibility(View.GONE);
                    progress_num.setVisibility(View.GONE);
                    btn_neutral.setVisibility(View.VISIBLE);
                    btn_neutral.setText("查看");
                    btn_pos.setText("确定");
                    Toast.makeText(Ac_news_detail.this, "文件已下载，无需重复下载", Toast.LENGTH_SHORT).show();
                } else {
                    progress_num.setText(values[0].toString() + "%");
                    progressBar.setProgress(values[0]);
                }
            }
        });
        downloadTask.execute();
    }

    @Override
    public <T> void onRequestFinish(String reqId, String reqName, T bean) {
        NewsDetailResponseBean info = (NewsDetailResponseBean) bean;
        if (info != null) {
            if (info.getCode().equals("0")) {
                NewsDetail newsDetail = info.getNewsDetail();
                if (newsDetail == null) {
                    Toast.makeText(this, "新闻详情返回为空", Toast.LENGTH_SHORT).show();
                } else {
                    this.newsDetail = newsDetail;
                    initNewsDetail();
                }
            } else {
                Toast.makeText(this, "服务异常，无法获取数据", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "数据接入错误", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    @Override
    public <T extends ResultBase> Class<?> getBeanClass(String reqId, String reqName) {
        return NewsDetailResponseBean.class;
    }
}
