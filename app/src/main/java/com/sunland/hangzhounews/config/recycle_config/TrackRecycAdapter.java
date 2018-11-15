package com.sunland.hangzhounews.config.recycle_config;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunland.hangzhounews.R;
import com.sunland.hangzhounews.dbHelper.news_collection.News;
import com.sunland.hangzhounews.utils.TimeConvertor;

import java.util.List;

public class TrackRecycAdapter extends RecyclerView.Adapter<TrackRecycAdapter.MyViewHolder> {

    private Context mContext;
    private List<News> dataSet;
    private OnItemSelectedListener onItemSelectedListener;


    public TrackRecycAdapter(Context context, List<News> dataSet) {
        this.mContext = context;
        this.dataSet = dataSet;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.track_recycle_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final News news = dataSet.get(position);
        holder.title.setText(news.title);
        holder.tv_date.setText(TimeConvertor.Stamp2date(news.timeStamp));
        holder.item_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onClick(position, news);
                }
            }
        });

    }

    public void setOnItemSelectedItem(OnItemSelectedListener l) {
        this.onItemSelectedListener = l;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private RelativeLayout item_container;
        private TextView tv_date;


        public MyViewHolder(View view) {
            super(view);
            item_container = view.findViewById(R.id.item_container);
            title = view.findViewById(R.id.title);
            tv_date = view.findViewById(R.id.scsj);
        }
    }


}
