/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xiaobai.drive.fragment.file;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xiaobai.drive.MyApp;
import com.xiaobai.drive.R;
import com.xiaobai.drive.activity.VideoActivity;
import com.xiaobai.drive.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.xiaobai.drive.adapter.base.delegate.SimpleDelegateAdapter;
import com.xiaobai.drive.entity.BaseEntity;
import com.xiaobai.drive.entity.FileItemEntity;
import com.xiaobai.drive.entity.ImageViewInfo;
import com.xiaobai.drive.utils.Util;
import com.xiaobai.drive.utils.XToastUtils;
import com.xiaobai.drive.utils.request.HttpEngine;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.imageview.preview.PreviewBuilder;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.samlss.broccoli.Broccoli;

import static android.view.View.VISIBLE;
import static com.xiaobai.drive.utils.Util.linkPath;

@Page(anim = CoreAnim.fade,name = "文件")
public class FilesFragment extends BaseFragment {

//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private static List<FileItemEntity> files=new ArrayList<>();

    private SimpleDelegateAdapter<FileItemEntity> mNewsAdapter;

    private static String path="";

    private MyApp myApp;

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle()
                .setImmersive(true);
        titleBar.setTitle("文件");
//        titleBar.setLeftImageDrawable(ResUtils.getVectorDrawable(getContext(), R.drawable.ic_login_close));
        titleBar.setActionTextColor(ThemeUtils.resolveColor(getContext(), R.attr.colorAccent));
        titleBar.setLeftClickListener(v -> {
            System.out.println("返回");
//            popBackStack();
            popToBack();
            //弹出最后一个路径元素
            myApp.path.pop();
        });
        return titleBar;
    }


    @Override
    public void onAttach(Context context) {
        myApp= (MyApp) context.getApplicationContext();
        System.out.println(myApp.path);
        path=linkPath(myApp.path);
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((view, i, keyEvent) -> {
            if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK){
                myApp.path.pop();
                popToBack();
                System.out.println("按了返回键");
                return true;
            }
            return false;
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_files;
    }

    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);
        //获取网盘文件列表
        switch (path){
            case "doc/internal":
                docList();
                break;
            case "video/internal":
                videoList();
                break;
            case "image/internal":
                imageList();
                break;
            case "audio/internal":
                audioList();
                break;
            default : //可选
                //语句
                getFiles(path);
        }

        //文件
        mNewsAdapter = new BroccoliSimpleDelegateAdapter<FileItemEntity>(R.layout.adpater_litem, new LinearLayoutHelper(), files) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onBindData(RecyclerViewHolder holder, FileItemEntity model, int position) {
                if (model != null) {
                    String[] prefix=model.getFileName().split("\\.");
                    String str=prefix[prefix.length-1].toLowerCase();
                    //设置item
                    Util.setItem(holder,model,str);
                    holder.itemView.setOnLongClickListener(v -> {
                        System.out.println("长按了。。。。。");
                        return true;
                    });
                    holder.checkedListener(R.id.checks, (buttonView, isChecked) -> {

                    });
                    holder.click(R.id.item_action,v ->{

//                        new BottomSheet.BottomListSheetBuilder(getActivity())
//                                .setTitle("标题")
//                                .addItem("Item 1")
//                                .addItem("Item 2")
//                                .addItem("Item 3")
//                                .setIsCenter(true)
//                                .setOnSheetItemClickListener((dialog, itemView, positions, tag) -> {
//                                    dialog.dismiss();
//                                })
//                                .build()
//                                .show();

                        BottomSheet.BottomGridSheetBuilder builder = new BottomSheet.BottomGridSheetBuilder(getActivity());
                        builder
                                .addItem(R.drawable.download, "下载", "分享", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                .addItem(R.drawable.share, "创建分享链接", "分享", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                .addItem(R.drawable.detail, "详细信息", "分享", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                .addItem(R.drawable.edit, "重命名", "分享", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                .addItem(R.drawable.copy, "复制", "分享", BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                                .addItem(R.drawable.move, "移动", "分享", BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                                .addItem(R.drawable.delete, "删除", "分享", BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                                .setOnSheetItemClickListener((dialog, itemView) -> {
                                    dialog.dismiss();
                                    String tag =  itemView.getTag().toString();
                                    XToastUtils.toast("tag:" + tag + ", content:" + itemView.toString());
                                }).build().show();
//                        System.out.println("点击");
                    });
                    holder.click(R.id.item_content, v -> {
                        if (model.getType().equals("dir")){
                            System.out.println("文件夹");
                            System.out.println(myApp.path);
                            //如果是文件夹
                            String data="{\"id\":\""+model.getId()+"\",\"path\":\""+path+"/"+model.getFileName()+"\"}";
                            Bundle bundle=new Bundle();
                            myApp.path.push(model.getFileName());
                            bundle.putString("data",data);
                            openPage(FilesFragment.class,true);
                        }else{
                            preview(str,model,position);
                        }
                    });
                }
            }

            @Override
            protected void onBindBroccoli(RecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.tv_user_name),
                        holder.findView(R.id.tv_tag),
                        holder.findView(R.id.tv_title),
                        holder.findView(R.id.tv_summary),
                        holder.findView(R.id.tv_praise),
                        holder.findView(R.id.tv_comment),
                        holder.findView(R.id.tv_read),
                        holder.findView(R.id.iv_image)
                );
            }
        };

        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
//        delegateAdapter.addAdapter(bannerAdapter);
//        delegateAdapter.addAdapter(commonAdapter);
//        delegateAdapter.addAdapter(titleAdapter);
        delegateAdapter.addAdapter(mNewsAdapter);

        recyclerView.setAdapter(delegateAdapter);

    }

    /**
     * 获取网盘文件
     */
    protected void getFiles(String path){
        HttpEngine.directory(path,new Observer<BaseEntity>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(@io.reactivex.annotations.NonNull BaseEntity baseEntity) {
                files.clear();
                LinkedTreeMap<String, Object> result = (LinkedTreeMap<String, Object>) baseEntity.getData();
                 List<LinkedTreeMap<String, Object>> list = (List<LinkedTreeMap<String, Object>>) result.get("objects");
                files= Util.toList(list);
                System.out.println("列表长度："+files.size());
                refreshLayout.finishRefresh();
                mNewsAdapter.refresh(files);
                refreshLayout.finishRefresh();

            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });

    }

    /**
     * 预览获取真实链接
     */

    protected void preview(String str,FileItemEntity model,int position){
        HttpEngine.preview(model.getId(),new Observer<BaseEntity>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(@io.reactivex.annotations.NonNull BaseEntity baseEntity) {

                if (str.equals("mp4")){
                    String data="{\"title\":\""+model.getFileName()+"\",\"url\":\""+baseEntity.getData()+"\"}";
                    ActivityUtils.startActivity(VideoActivity.class,"data",data);
                }else if (str.equals("jpg")){
                    PreviewBuilder.from(getActivity())
                            .setImg(new ImageViewInfo(baseEntity.getData().toString())) //图片对象集合
                            .setCurrentIndex(position) //当前预览的索引
                            .setSingleFling(true)
                            .setType(PreviewBuilder.IndicatorType.Number) //指示器为数字
                            .start();
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                System.out.println("请求错误");
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("请求完成");
            }
        });
    }

    /**
     * 获取所有视频文件
     */
    protected void videoList() {
        HttpEngine.videoList(new Observer<BaseEntity>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(@io.reactivex.annotations.NonNull BaseEntity baseEntity) {
                files.clear();
                LinkedTreeMap<String, Object> result = (LinkedTreeMap<String, Object>) baseEntity.getData();
                List<LinkedTreeMap<String, Object>> list = (List<LinkedTreeMap<String, Object>>) result.get("objects");
                files= Util.toList(list);
                System.out.println("列表长度："+files.size());
                mNewsAdapter.refresh(files);
                refreshLayout.finishRefresh();

            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 获取所有音频文件
     */
    protected void audioList() {
        HttpEngine.audioList(new Observer<BaseEntity>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(@io.reactivex.annotations.NonNull BaseEntity baseEntity) {
                files.clear();
                LinkedTreeMap<String, Object> result = (LinkedTreeMap<String, Object>) baseEntity.getData();
                List<LinkedTreeMap<String, Object>> list = (List<LinkedTreeMap<String, Object>>) result.get("objects");
                files= Util.toList(list);
                System.out.println("列表长度："+files.size());
                mNewsAdapter.refresh(files);
                refreshLayout.finishRefresh();
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 获取所有图片文件
     */
    protected void imageList() {
        HttpEngine.imageList(new Observer<BaseEntity>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(@io.reactivex.annotations.NonNull BaseEntity baseEntity) {
                files.clear();
                LinkedTreeMap<String, Object> result = (LinkedTreeMap<String, Object>) baseEntity.getData();
                List<LinkedTreeMap<String, Object>> list = (List<LinkedTreeMap<String, Object>>) result.get("objects");
                files= Util.toList(list);
                System.out.println("列表长度："+files.size());
                mNewsAdapter.refresh(files);
                refreshLayout.finishRefresh();
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 获取所有文档文件
     */
    protected void docList() {
        HttpEngine.docList(new Observer<BaseEntity>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(@io.reactivex.annotations.NonNull BaseEntity baseEntity) {
                files.clear();
                LinkedTreeMap<String, Object> result = (LinkedTreeMap<String, Object>) baseEntity.getData();
                List<LinkedTreeMap<String, Object>> list = (List<LinkedTreeMap<String, Object>>) result.get("objects");
                files= Util.toList(list);
                System.out.println("列表长度："+files.size());
                mNewsAdapter.refresh(files);
                refreshLayout.finishRefresh();
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
