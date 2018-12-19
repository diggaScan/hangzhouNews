package com.sunland.hangzhounews.config;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunland.hangzhounews.GlideApp;
import com.sunland.hangzhounews.R;
import com.sunland.hangzhounews.bean.i_newsList_bean.GeneralNewsInfo;
import com.sunland.hangzhounews.utils.WindowInfoUtils;
import com.sunlandgroup.Global;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class Rv_NewsList_Adapter extends RecyclerView.Adapter<Rv_NewsList_Adapter.MyViewHolder> {

    private Context context;
    private List<GeneralNewsInfo> dataSet;
    private LayoutInflater inflater;
    private OnItemClickedListener onItemClickedListener;
    private List<String> paths;
    private HashMap<Integer, List<String>> path_map;

    public Rv_NewsList_Adapter(Context context, List<GeneralNewsInfo> dataSet) {
        super();
        this.context = context;
        this.dataSet = dataSet;
        inflater = LayoutInflater.from(context);
        path_map = new HashMap<>();
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    @Override
    public int getItemViewType(int position) {
        List<String> urls = path_map.get(position);
        if (urls != null) {
            paths = urls;
        } else {
            paths = new ArrayList<>();
            GeneralNewsInfo info = dataSet.get(position);
            Document docs = Jsoup.parse(info.getContent());
            Elements elements = docs.getElementsByTag("img");
            // TODO: 2018/12/17/017 内存缓存
            for (int k = 0; k < elements.size(); k++) {
                Element element = elements.get(k);
                String path = element.attr("src");
                if (!path.contains("icon16")) {
                    paths.add(path);
                }
                if (paths.size() >= 3) {
                    break;
                }
            }
            path_map.put(position, paths);
        }
        int img_num = paths.size();
        if (img_num >= 3) {
            return 2;
        } else if (img_num < 1) {
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public Rv_NewsList_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        switch (i) {
            case 0:
                view = inflater.inflate(R.layout.rv_news_list_no_img, viewGroup, false);
                return new MyViewHolder(view, i);
            case 1:
                view = inflater.inflate(R.layout.rv_news_list_one_img, viewGroup, false);
                return new MyViewHolder(view, i);
            case 2:
                view = inflater.inflate(R.layout.rv_news_list_three_img, viewGroup, false);
                return new MyViewHolder(view, i);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull Rv_NewsList_Adapter.MyViewHolder myViewHolder, final int i) {
        final GeneralNewsInfo info = dataSet.get(i);
        int img_size = WindowInfoUtils.dp2px(context, 80);
        myViewHolder.tv_title.setText(info.getTitle());
        myViewHolder.tv_add_time.setText(info.getAddTime().substring(0, 10));
        String src = String.valueOf(info.getEditname());
        if (src == null || src.isEmpty()) {
            myViewHolder.tv_src.setVisibility(View.GONE);
        } else {
            myViewHolder.tv_src.setText(src);
        }
        switch (myViewHolder.viewType) {
            case 2:
                GlideApp.with(context).asBitmap()
                        .load("http://" + Global.ip + ":" + Global.port + paths.get(0))
                        .override(img_size, img_size)
                        .thumbnail(0.10f)
                        .into(myViewHolder.iv_img_one);

                GlideApp.with(context).asBitmap()
                        .load("http://" + Global.ip + ":" + Global.port + paths.get(1))
                        .override(img_size, img_size)
                        .thumbnail(0.10f)
                        .into(myViewHolder.iv_img_two);

                GlideApp.with(context).asBitmap()
                        .load("http://" + Global.ip + ":" + Global.port + paths.get(1))
                        .override(img_size, img_size)
                        .thumbnail(0.10f)
                        .into(myViewHolder.iv_img_three);

                break;
            case 1:
                GlideApp.with(context)
                        .load("http://" + Global.ip + ":" + Global.port + paths.get(0))
                        .transition(withCrossFade())
                        .override(img_size, img_size)
                        .thumbnail(0.10f)
                        .into(myViewHolder.iv_img);
                break;
            case 0:

                break;
        }

        if (onItemClickedListener != null) {
            myViewHolder.rl_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickedListener.onClicked(i, info.getNewsid());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickedListener {
        void onClicked(int position, String newsId);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_add_time;
        TextView tv_src;
        RelativeLayout rl_container;
        ImageView iv_img;
        ImageView iv_img_one;
        ImageView iv_img_two;
        ImageView iv_img_three;
        int viewType;

        MyViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            switch (viewType) {
                case 0:
                    break;
                case 1:
                    iv_img = itemView.findViewById(R.id.img);
                    break;
                case 2:
                    iv_img_one = itemView.findViewById(R.id.img_one);
                    iv_img_two = itemView.findViewById(R.id.img_two);
                    iv_img_three = itemView.findViewById(R.id.img_three);
                    break;
            }
            tv_title = itemView.findViewById(R.id.news_title);
            tv_add_time = itemView.findViewById(R.id.time);
            tv_src = itemView.findViewById(R.id.src);
            rl_container = itemView.findViewById(R.id.rv_item_container);
        }
    }
}
