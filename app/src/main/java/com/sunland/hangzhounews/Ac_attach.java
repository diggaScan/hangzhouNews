package com.sunland.hangzhounews;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sunland.hangzhounews.config.Rv_Item_decoration;
import com.sunland.hangzhounews.config.recycle_config.Rv_attach_adapter;
import com.sunland.hangzhounews.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class Ac_attach extends Ac_base {

    @BindView(R.id.recycler_view)
    public RecyclerView recyclerView;

    private Rv_attach_adapter rv_attach_adapter;
    private List<File> sub_files;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.ac_attach);
        sub_files = new ArrayList<>();
        setToolbarTitle("附件管理");
        setNavVisible(true);
        getAllFiles(DataModel.ATTACH_FILE_DIR);
    }

    private void getAllFiles(String dir) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory(), dir);
            if (!file.exists()) {
                Toast.makeText(this, "文件夹为空", Toast.LENGTH_SHORT).show();
            } else {
                for (File file_one : file.listFiles()) {
                    sub_files.add(file_one);
                }
                initRecyclerView();
            }
        }
    }

    private void initRecyclerView() {
        rv_attach_adapter = new Rv_attach_adapter(this, sub_files);
        rv_attach_adapter.setOnFileClickedListener(new Rv_attach_adapter.OnFileClickedListener() {
            @Override
            public void onFileClicked(int positon, String name) {
                String mimeType = FileUtils.getMIMEType(name);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), DataModel.ATTACH_FILE_DIR + "/" + name));
                intent.setDataAndType(uri, mimeType);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(Ac_attach.this, "无相关应用可以打开本文件", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFileLongClicked(final int position, String name) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Ac_attach.this)
                        .setTitle("提示")
                        .setMessage("是否删除该附件")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File file = sub_files.get(position);
                                file.delete();
                                sub_files.remove(file);
                                rv_attach_adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setAdapter(rv_attach_adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new Rv_Item_decoration(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ac_attach_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.trash:
                initAlertDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否删除所有附件")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAllFiles();
                        rv_attach_adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void removeAllFiles() {
        for (File file : sub_files) {
            file.delete();
        }
        sub_files.clear();
        rv_attach_adapter.notifyDataSetChanged();
    }
}
