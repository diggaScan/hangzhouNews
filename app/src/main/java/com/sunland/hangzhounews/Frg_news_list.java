package com.sunland.hangzhounews;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sunland.hangzhounews.activities.Ac_main;
import com.sunland.hangzhounews.activities.Ac_news_detail;
import com.sunland.hangzhounews.bean.i_newsList_bean.GeneralNewsInfo;
import com.sunland.hangzhounews.bean.i_newsList_bean.NewsListRequestBean;
import com.sunland.hangzhounews.bean.i_newsList_bean.NewsListResponseBean;
import com.sunland.hangzhounews.config.Rv_Item_decoration;
import com.sunland.hangzhounews.config.Rv_NewsList_Adapter;
import com.sunland.hangzhounews.customView.DragToRefreshView.DragToRefreshView;
import com.sunland.hangzhounews.dbHelper.MyDatabase;
import com.sunland.hangzhounews.dbHelper.OpenDbHelper;
import com.sunlandgroup.Global;
import com.sunlandgroup.def.bean.result.ResultBase;
import com.sunlandgroup.network.OnRequestCallback;
import com.sunlandgroup.network.RequestManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Frg_news_list extends Fragment implements OnRequestCallback {

    public static final String TAG = "Frg_news_list";
    private final int items_per_page = 10;
    @BindView(R.id.recycle)
    public RecyclerView rv_news_list;
    @BindView(R.id.empty_news_container)
    public RelativeLayout rl_container;
    @BindView(R.id.drag2Refresh)
    public DragToRefreshView d2r_refresh;
    @BindView(R.id.loading_icon)
    public RelativeLayout rl_loading;

    private RequestManager mRequestManager;
    private String dqid;
    private int lbid;
    private String category_name;
    private int cur_page = 1;
    private boolean isHopBack = false;
    private Context context;
    private Rv_NewsList_Adapter adapter;
    private List<GeneralNewsInfo> dataSet;

    //判断fragment的状态
    private boolean isVisible;
    private boolean onResumed;
    private boolean hasLoaded;//fragment中是否已有数据
    private boolean hasCached;//fragment中的数据是否被缓存

    private Thread dbThread;

    public Frg_news_list() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_news_list, container, false);
        ButterKnife.bind(this, view);
        mRequestManager = new RequestManager(context, this);
        dataSet = new ArrayList<>();
        initView();
        return view;
    }

    public void initView() {
        adapter = new Rv_NewsList_Adapter(context, dataSet);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        rv_news_list.setAdapter(adapter);
        rv_news_list.setLayoutManager(manager);
        rv_news_list.addItemDecoration(new Rv_Item_decoration(context));
        adapter.setOnItemClickedListener(new Rv_NewsList_Adapter.OnItemClickedListener() {
            @Override
            public void onClicked(int position, String newsId, String title) {
                Bundle bundle = new Bundle();
                bundle.putString("dqid", dqid);
                bundle.putInt("lbid", lbid);
                bundle.putString("newsId", newsId);
                bundle.putString("title", title);
                ((Ac_main) context).hop2Activity(Ac_news_detail.class, bundle);
            }
        });
        d2r_refresh.setUpdateListener(new DragToRefreshView.OnUpdateListener() {
            @Override
            public void onRefreshing(DragToRefreshView view) {
                if (view.isHeaderRefreshing()) {
                    queryYdjwData();
                } else if (view.isFooterRefreshing()) {
                    mRequestManager.addRequest(Global.ip, Global.port, Global.postfix, V_config.NEWS_LIST_REQNAME, requestNextPageBean(), 15000);
                    mRequestManager.postRequestWithoutDialog();
//                    recyclerView.smoothScrollToPosition(news_title.size() - 1);//不建议使用
                }
            }

            @Override
            public void onFinished(DragToRefreshView view) {
                if (view.getState() == DragToRefreshView.State.footer_release_to_load) {
                    int scroll_position = dataSet.size() - items_per_page;
                    if (scroll_position > 0) {
                        rv_news_list.scrollToPosition(dataSet.size());
                    }
                }
            }
        });
        d2r_refresh.addMainContent(rv_news_list);
    }


    public String getCategory_name() {
        return category_name;
    }

    @Override
    public void onResume() {
        super.onResume();
        onResumed = true;
        if (isHopBack) {
            isHopBack = false;
        } else if (isVisible && !hasCached) {
            queryYdjwData();
            rl_loading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isHopBack = true;//跳转至另一页面时，调用至onStop()截止
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (!isVisibleToUser && mRequestManager != null) {
            mRequestManager.cancelAll();
        }
        if (hasLoaded) {
            return;
        }
        if (isVisibleToUser && onResumed) {
            onFragmentVisible(isVisibleToUser);
        }
        Log.d(TAG, "setUserVisibleHint: " + category_name + isVisibleToUser);
    }

    private void onFragmentVisible(boolean isVisibleToUser) {
        if (hasCached) {
            dbThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    MyDatabase db = OpenDbHelper.getDb(context);
                    final List<GeneralNewsInfo> list = db.getListDao().loadNewsBylm(lbid);

                    ((Ac_main) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (list != null && !list.isEmpty()) {
                                dataSet.clear();
                                dataSet.addAll(list);
                                adapter.notifyDataSetChanged();
                            } else {
                                queryYdjwData();
                                rl_loading.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });
            dbThread.start();
        } else {
            queryYdjwData();
            rl_loading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hasLoaded = false;
        cacheCurrentData();
        isHopBack = false; //viewPager滑动时,遗弃的fragment会调用此方法
    }

    private void cacheCurrentData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                MyDatabase db = OpenDbHelper.getDb(context);
                for (GeneralNewsInfo info : dataSet) {
                    info.setLbid(lbid);
                    db.getListDao().insert(info);
                }
                hasCached = true;
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((Ac_main) context).mContentAdapter.notifyDataSetChanged();
    }

    public void setQueryParams(String dqid, int lbid, String category_name) {
        this.dqid = dqid;
        this.lbid = lbid;
        this.category_name = category_name;
    }

    private void queryYdjwData() {
        mRequestManager.addRequest(Global.ip, Global.port, Global.postfix, V_config.NEWS_LIST_REQNAME
                , assembleRequestObj(), 15000);
        mRequestManager.postRequestWithoutDialog();
    }

    private void initNewsList(List<GeneralNewsInfo> list) {

        //上拉和下拉刷新的数据逻辑
        rl_loading.setVisibility(View.GONE);
        //判断下拉刷新是否已无更多内容
//        if (d2r_refresh.isFooterRefreshing() && (list.isEmpty() || list == null)) {
//            Toast.makeText(context, "无更多内容", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (dataSet.isEmpty()) {
            dataSet.clear();
            dataSet.addAll(list);
            adapter.notifyDataSetChanged();
        } else {
            // 根据现有后台以分页的形式返回数据，很难兼顾多种下拉和上拉的数据返回情况，顾可能存在漏洞
            if (list.get(0).getNewsid().equals(dataSet.get(0).getNewsid())) {
                Toast.makeText(context, "已是最新内容", Toast.LENGTH_SHORT).show();
                return;
            } else if (list.get(0).getNewsid().compareTo(dataSet.get(0).getNewsid()) < 0) {
                dataSet.addAll(list);
                adapter.notifyItemRangeInserted(dataSet.size(), list.size());
            } else if (list.get(0).getNewsid().compareTo(dataSet.get(0).getNewsid()) > 0) {
//                for (int i = 0; dataSet.get(0).getNewsid().compareTo(list.get(i).getNewsid()) <= 0; i++) {
                dataSet.addAll(list);

//                Collections.sort(dataSet);
                adapter.notifyDataSetChanged();
            }
        }
        hasLoaded = true;//数据已加载
    }

    private NewsListRequestBean assembleRequestObj() {
        NewsListRequestBean requestBean = new NewsListRequestBean();
        ((Ac_main) context).assembleBasicRequest(requestBean);
        requestBean.setPageIndex(1);
        requestBean.setPageNo(items_per_page);
        requestBean.setDqid(this.dqid);
        requestBean.setLbid(this.lbid);
        return requestBean;
    }

    private NewsListRequestBean requestNextPageBean() {
        cur_page++;
        NewsListRequestBean requestBean = new NewsListRequestBean();
        ((Ac_main) context).assembleBasicRequest(requestBean);
        requestBean.setPageIndex(cur_page);
        requestBean.setPageNo(items_per_page);
        requestBean.setDqid(dqid);
        requestBean.setLbid(lbid);
        return requestBean;
    }

    @Override
    public <T> void onRequestFinish(String reqId, String reqName, T bean) {
        NewsListResponseBean responseBean = (NewsListResponseBean) bean;
        d2r_refresh.dismiss();
        if (responseBean != null) {
            if (responseBean.getCode().equals("0")) {
                List<GeneralNewsInfo> list = responseBean.getGeneralNewsInfo();
                if (list == null || list.isEmpty()) {
                    Toast.makeText(context, "新闻列表返回为空", Toast.LENGTH_SHORT).show();
//                } else if (list.isEmpty()) {
//                    initNewsList(list);
                } else {
                    initNewsList(list);
                }
            } else {
                Toast.makeText(context, "服务异常，无法获取数据", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "数据接入错误", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public <T extends ResultBase> Class<?> getBeanClass(String reqId, String reqName) {
        return NewsListResponseBean.class;
    }

}
