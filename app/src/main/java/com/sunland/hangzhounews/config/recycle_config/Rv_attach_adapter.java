package com.sunland.hangzhounews.config.recycle_config;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunland.hangzhounews.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class Rv_attach_adapter extends RecyclerView.Adapter<Rv_attach_adapter.MyViewHolder> {

    private Context context;
    private List<File> dataSet;
    private LayoutInflater inflater;
    private SimpleDateFormat formatter;
    private OnFileClickedListener onFileClickedListener;

    public Rv_attach_adapter(Context context, List<File> dataSet) {
        super();
        this.context = context;
        this.dataSet = dataSet;
        this.inflater = LayoutInflater.from(context);
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void setOnFileClickedListener(OnFileClickedListener onFileClickedListener) {
        this.onFileClickedListener = onFileClickedListener;
    }

    @NonNull
    @Override
    public Rv_attach_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.rv_ac_attach_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Rv_attach_adapter.MyViewHolder myViewHolder, final int i) {
        final File file = dataSet.get(i);
        final String file_name = file.getName();
        myViewHolder.tv_name.setText(file_name);
        myViewHolder.tv_add_time.setText(formatter.format(file.lastModified()));
        myViewHolder.file_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFileClickedListener != null) {
                    onFileClickedListener.onFileClicked(i, file.getName());
                }
            }
        });

        myViewHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFileClickedListener != null) {
                    onFileClickedListener.onFileDeletedClicked(i, file.getName());
                }
            }
        });
        String type = file_name.substring(file_name.lastIndexOf("."));
        myViewHolder.tv_type.setText(type);

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_add_time;
        RelativeLayout file_container;
        TextView tv_type;
        RelativeLayout tv_delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.file_name);
            tv_add_time = itemView.findViewById(R.id.create_time);
            file_container = itemView.findViewById(R.id.file_container);
            tv_type = itemView.findViewById(R.id.type);
            tv_delete = itemView.findViewById(R.id.delete);
        }
    }

    public interface OnFileClickedListener {
        void onFileClicked(int positon, String name);//0左视图，1右视图

        void onFileDeletedClicked(int position, String name);
    }
}
