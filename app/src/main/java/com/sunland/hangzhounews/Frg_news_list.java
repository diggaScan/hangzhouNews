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

import com.sunland.hangzhounews.DragToRefreshView.DragToRefreshView;
import com.sunland.hangzhounews.bean.GeneralNewsInfo;
import com.sunland.hangzhounews.bean.NewsListRequestBean;
import com.sunland.hangzhounews.bean.NewsListResponseBean;
import com.sunland.hangzhounews.config.Rv_Item_decoration;
import com.sunland.hangzhounews.config.Rv_NewsList_Adapter;
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
    private final int items_per_page = 10;
    private boolean isHopBack = false;
    private Context context;
    private Rv_NewsList_Adapter adapter;
    private List<GeneralNewsInfo> dataSet;

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
        d2r_refresh.setUpdateListener(new DragToRefreshView.OnUpdateListener() {
            @Override
            public void onRefreshing(DragToRefreshView view) {
                if (view.isHeaderRefreshing()) {
                    queryYdjwData();
                } else if (view.isFooterRefreshing()) {
                    mRequestManager.addRequest(Global.ip, Global.port, Global.postfix, DataModel.NEWS_LIST_REQNAME, requestNextPageBean(), 15000);
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
        if (isHopBack) {
            isHopBack = false;
        } else {
            queryYdjwData();
            rl_loading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        isHopBack = true;//跳转至另一页面时，调用至onStop()截止
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
        isHopBack = false; //viewPager滑动时,遗弃的fragment会调用此方法
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
        mRequestManager.addRequest(Global.ip, Global.port, Global.postfix, DataModel.NEWS_LIST_REQNAME
                , assembleRequestObj(), 15000);
        mRequestManager.postRequestWithoutDialog();
    }

    private void initNewsList(List<GeneralNewsInfo> list) {
        //上拉和下拉刷新的数据逻辑
        rl_loading.setVisibility(View.GONE);
        if ((list == null || list.isEmpty()) && !d2r_refresh.isFooterRefreshing()) {
            d2r_refresh.setVisibility(View.GONE);
            rl_container.setVisibility(View.VISIBLE);
        } else if (d2r_refresh.isFooterRefreshing() && list.isEmpty()) {
            Toast.makeText(context, "无更多内容", Toast.LENGTH_SHORT).show();
            cur_page--;
        } else {
            if (dataSet.isEmpty()) {
                dataSet.clear();
                dataSet.addAll(list);
            } else {
                if (list.get(0).getNewsid() == dataSet.get(0).getNewsid()) {
                    Toast.makeText(context, "已是最新内容", Toast.LENGTH_SHORT).show();
                } else if (list.get(0).getNewsid() > dataSet.get(0).getNewsid()) {
                    for (int i = 0; i < list.size() && list.get(i).getNewsid() > dataSet.get(0).getNewsid(); i++) {
                        dataSet.add(i, list.get(i));
                    }
                }
            }
        }

        d2r_refresh.dismiss();
        adapter = new Rv_NewsList_Adapter(context, dataSet);
        adapter.setOnItemClickedListener(new Rv_NewsList_Adapter.OnItemClickedListener() {
            @Override
            public void onClicked(int position, int newsId) {
                Bundle bundle = new Bundle();
                bundle.putString("dqid", dqid);
                bundle.putInt("lbid", lbid);
                bundle.putInt("newsId", newsId);
                ((Ac_main) context).hop2Activity(Ac_news_detail.class, bundle);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(context);
        rv_news_list.setAdapter(adapter);
        rv_news_list.setLayoutManager(manager);
        rv_news_list.addItemDecoration(new Rv_Item_decoration(context));
    }

    private NewsListRequestBean assembleRequestObj() {
        NewsListRequestBean requestBean = new NewsListRequestBean();
        ((Ac_main) context).assembleBaseInfo(requestBean);
        requestBean.setPageIndex(1);
        requestBean.setPageNo(items_per_page);
        requestBean.setDqid(this.dqid);
        requestBean.setLbid(this.lbid);
        return requestBean;
    }

    private NewsListRequestBean requestNextPageBean() {
        cur_page++;
        NewsListRequestBean requestBean = new NewsListRequestBean();
        ((Ac_main) context).assembleBaseInfo(requestBean);
        requestBean.setPageIndex(cur_page);
        requestBean.setPageNo(items_per_page);
        requestBean.setDqid(dqid);
        requestBean.setLbid(lbid);
        return requestBean;
    }

    @Override
    public <T> void onRequestFinish(String reqId, String reqName, T bean) {
        NewsListResponseBean responseBean = (NewsListResponseBean) bean;
        if (responseBean != null) {
            if (responseBean.getCode().equals("0")) {
                List<GeneralNewsInfo> list = responseBean.getGeneralNewsInfo();
                if (list == null) {
                    initNewsList(null);
                    Toast.makeText(context, "新闻列表返回为空", Toast.LENGTH_SHORT).show();
                } else if (list.isEmpty()) {
                    initNewsList(list);
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
