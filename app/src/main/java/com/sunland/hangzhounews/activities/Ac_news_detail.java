package com.sunland.hangzhounews.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sunland.hangzhounews.GlideApp;
import com.sunland.hangzhounews.R;
import com.sunland.hangzhounews.V_config;
import com.sunland.hangzhounews.bean.i_newsDetail_bean.NewsDetail;
import com.sunland.hangzhounews.bean.i_newsDetail_bean.NewsDetailRequestBean;
import com.sunland.hangzhounews.bean.i_newsDetail_bean.NewsDetailResponseBean;
import com.sunland.hangzhounews.customView.ZoomImageView;
import com.sunland.hangzhounews.customView.shimmer.ShimmerRecyclerView;
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

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class Ac_news_detail extends Ac_base implements OnRequestCallback {


    @BindView(R.id.collect_button)
    public ImageView btn_collect;
    @BindView(R.id.index_title)
    public TextView tv_toolbar_title;
    @BindView(R.id.news_detail)
    public WebView wv_news;
    @BindView(R.id.loading_layout)
    public ShimmerRecyclerView ll_loading_layout;


    public TextView tv_alert_content;
    private ProgressBar progressBar;
    private TextView progress_num;
    private Button btn_pos;
    private Button btn_neutral;
    private String dqid;
    private int lbid;
    private String newsId;
    public boolean isCollected = false;
    private RequestManager mRequestManager;

    private NewsDetail newsDetail;

    private DownloadTask downloadTask;
    private DownloadUtils downloadUtils;
    private AlertDialog dialog;
    private String base_url = "http://" + Global.ip + ":" + Global.port + "/";
    private String html_body;

    private String url;
    private String file_name;//附件名称
    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarLayout(R.layout.toolbar_layout_detail);
        setContentLayout(R.layout.ac_news_detail);
        mRequestManager = new RequestManager(this, this);
        handleIntent();
        tv_toolbar_title.setText(title);
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
                newsId = bundle.getString("newsId");
                title = bundle.getString("title");
            }
        }
    }

    private void queryYdjwData() {
        mRequestManager.addRequest(Global.ip, Global.port, Global.postfix, V_config.NEWS_DETAIL_REQNAME
                , assembleRequestObj(), 15000);
        mRequestManager.postRequestWithoutDialog();
    }

    private NewsDetailRequestBean assembleRequestObj() {
        NewsDetailRequestBean newsDetailRequestBean = new NewsDetailRequestBean();
        assembleBasicRequest(newsDetailRequestBean);
        newsDetailRequestBean.setNewsId(newsId);
        newsDetailRequestBean.setDqid(dqid);
        newsDetailRequestBean.setLbid(lbid);
        return newsDetailRequestBean;
    }

    private void initNewsDetail() {
        tv_title.setText(newsDetail.getTitle());
        html_body = StringEscapeUtils.unescapeHtml4(newsDetail.getContent());
        wv_news.loadDataWithBaseURL(base_url, html_body, "text/html; charset=UTF-8", null, null);
    }

    private void initWebView() {
        WebSettings webSettings = wv_news.getSettings();
        webSettings.setDefaultTextEncodingName("UTF-8");
        wv_news.setLayerType(View.LAYER_TYPE_HARDWARE, null);//打开WebView硬件加速
        wv_news.addJavascriptInterface(new MJavaScriptInterface(this), "imageClickedListener");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(false);//设置webview推荐使用的窗口
        webSettings.setLoadWithOverviewMode(true);//设置webview加载的页面的模式
        webSettings.setDisplayZoomControls(false);//隐藏webview缩放按钮
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        wv_news.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                for (int i = 0; i < 3; i++) {
                    url = url.substring(url.indexOf("/") + 1);
                }
                StringBuilder sb = new StringBuilder();
                sb.append(base_url).append(url);//拼接省厅链路
                onInterceptUrl(sb.toString());
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                imgAdjust();
                paraReset();
                addImgClickedListener();
                //js执行需要一定时间，所以使用延迟300毫秒
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading_layout.setVisibility(View.GONE);
                    }
                }, 100);
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
                    btn_collect.setBackgroundResource(R.drawable.ic_unstar_24dp);
                    Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
                    deleteNews();
                } else {
                    btn_collect.setBackgroundResource(R.drawable.ic_star);
                    Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
                    collectNews();
                }
                isCollected = !isCollected;
                break;
        }
    }

    public void imgAdjust() {
        wv_news.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "var img = objs[i];   " +
                "    img.style.maxWidth = '100%'; img.style.height = 'auto';  " +
                "}" +
                "})()");
    }

    public void paraReset() {
        wv_news.loadUrl("javascript:(function(){\n" +
                "\tvar objs=document.getElementsByTagName(\"p\");\n" +
                "\tfor(var i=0;i<objs.length;i++){\n" +
                "\t\tvar p=objs[i];\n" +
                "\t\tp.style.textIndent=\"0pt\";\n" +
                "\t}\n" +
                "})()");
    }

    public void addImgClickedListener() {
        wv_news.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                " var array=new Array(); " +
                " for(var j=0;j<objs.length;j++){ array[j]=objs[j].src; }" +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imageClickedListener.openImage(this.src,array);  " +
                "    }  " +
                "}" +
                "})()");
    }

    private void deleteNews() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                MyDatabase db = OpenDbHelper.getDb(Ac_news_detail.this);
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
                MyDatabase db = OpenDbHelper.getDb(Ac_news_detail.this);
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
                    btn_collect.setBackground(getResources().getDrawable(R.drawable.ic_star));
                } else {
                    btn_collect.setBackground(getResources().getDrawable(R.drawable.ic_unstar_24dp));
                }
                isCollected = result;
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyDatabase db = OpenDbHelper.getDb(Ac_news_detail.this);
                List<String> newsIds = db.newsDAO().loadAllNewsId();
                boolean isCollected = newsIds.contains(newsId);
                Message msg = handler.obtainMessage();
                msg.obj = isCollected;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void onInterceptUrl(final String url) {
        this.url = url;
        View view = LayoutInflater.from(this).inflate(R.layout.download_progress_layout, null);
        progressBar = view.findViewById(R.id.download_progress);
        progressBar.setMax(100);
        progress_num = view.findViewById(R.id.num);
        tv_alert_content = view.findViewById(R.id.alert_content);

        //获取附件名称
        Document document = Jsoup.parse(html_body);
        Elements elements = document.select("a[href]");
        for (Element element : elements) {
            if (url.contains(element.attr("href"))) {
                String text = element.text();
                this.file_name = text.substring(0, text.lastIndexOf("."));
                break;
            }
        }
        if (file_name.isEmpty()) {
            file_name = newsDetail.getTitle();
        }
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
                viewFile();
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

    private void viewFile() {
        String file_name_2 = url.substring(url.lastIndexOf("/"));
        String ext = file_name_2.substring(file_name_2.lastIndexOf("."));
        String fileName = file_name + ext;
        String mimeType = FileUtils.getMIMEType(fileName);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 23) {
            uri = FileProvider.getUriForFile(Ac_news_detail.this, "com.sunland.hangzhounews.fileprovider",
                    new File(Environment.getExternalStorageDirectory(), V_config.ATTACH_FILE_DIR + "/" + fileName));
        } else {
            uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), V_config.ATTACH_FILE_DIR + "/" + fileName));
        }
        if (uri != null) {
            // Grant temporary read permission to the content URI
            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, mimeType);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(Ac_news_detail.this, "无相关应用可以打开本文件", Toast.LENGTH_SHORT).show();
        }
    }

    private void startDownLoad(String url) {
        downloadUtils = DownloadUtils.getDownloadManager(this);
        downloadTask = new DownloadTask(this, url, downloadUtils, file_name);
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

    class MJavaScriptInterface {
        private Context context;
        private String[] imageUrls;

        public MJavaScriptInterface(Context context) {
            this.context = context;

        }

        @android.webkit.JavascriptInterface
        public void openImage(final String src, final String[] imgs) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showImage(src, imgs);
                }
            });
        }
    }


    private void showImage(final String src, final String[] imgs) {
        final Dialog dialog = new Dialog(this);
        final Window window = dialog.getWindow();
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.image_exhibition, null);
        ViewPager viewPager = view.findViewById(R.id.image);
        final TextView tv_index = view.findViewById(R.id.index);

        MpageAdapter mpageAdapter = new MpageAdapter(Arrays.asList(imgs));
        viewPager.setAdapter(mpageAdapter);
        int index = Arrays.asList(imgs).indexOf(src);
        viewPager.setCurrentItem(index);
        viewPager.setPageMargin(20);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            boolean start = true;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //使用一次该函数来显示index,然后都使用onPageSelected()
                if (start) {
                    tv_index.setText(position + 1 + "/" + imgs.length);
                    start = false;
                }
            }

            @Override
            public void onPageSelected(int position) {
                tv_index.setText(position + 1 + "/" + imgs.length);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setContentView(view);
        window.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dialog.show();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    private class MpageAdapter extends PagerAdapter {
        List<String> urls;

        public MpageAdapter(List<String> urls) {
            this.urls = urls;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ZoomImageView imageView = new ZoomImageView(Ac_news_detail.this);
            GlideApp.with(Ac_news_detail.this).asBitmap()
                    .load(urls.get(position))
                    .into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public int getCount() {
            return urls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
