package com.sunland.hangzhounews.config;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunland.hangzhounews.R;
import com.sunland.hangzhounews.bean.GeneralNewsInfo;

import java.util.List;

public class Rv_NewsList_Adapter extends RecyclerView.Adapter<Rv_NewsList_Adapter.MyViewHolder> {

    private Context context;
    private List<GeneralNewsInfo> dataSet;
    private LayoutInflater inflater;
    private OnItemClickedListener onItemClickedListener;

    public Rv_NewsList_Adapter(Context context, List<GeneralNewsInfo> dataSet) {
        super();
        this.context = context;
        this.dataSet = dataSet;
        inflater = LayoutInflater.from(context);
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    @NonNull
    @Override
    public Rv_NewsList_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.rv_news_list_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Rv_NewsList_Adapter.MyViewHolder myViewHolder, final int i) {
        final GeneralNewsInfo info = dataSet.get(i);
        myViewHolder.tv_title.setText(info.getTitle());
        myViewHolder.tv_add_time.setText(info.getAddTime().substring(0,10));
        myViewHolder.tv_read_count.setText(String.valueOf(info.getReadCount()));
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
        void onClicked(int position, int newsId);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_add_time;
        TextView tv_read_count;
        RelativeLayout rl_container;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.news_title);
            tv_add_time = itemView.findViewById(R.id.addTime);
            tv_read_count = itemView.findViewById(R.id.read_count);
            rl_container = itemView.findViewById(R.id.rv_item_container);
        }
    }
}
