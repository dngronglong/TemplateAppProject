/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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

package com.xiaobai.drive.fragment.home;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.google.gson.internal.LinkedTreeMap;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xiaobai.drive.MyApp;
import com.xiaobai.drive.R;
import com.xiaobai.drive.activity.FilesActivity;
import com.xiaobai.drive.activity.VideoActivity;
import com.xiaobai.drive.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.xiaobai.drive.adapter.base.delegate.SimpleDelegateAdapter;
import com.xiaobai.drive.adapter.base.delegate.SingleDelegateAdapter;
import com.xiaobai.drive.entity.BaseEntity;
import com.xiaobai.drive.entity.FileItemEntity;
import com.xiaobai.drive.entity.ImageViewInfo;
import com.xiaobai.drive.fragment.file.FilesFragment;
import com.xiaobai.drive.utils.DemoDataProvider;
import com.xiaobai.drive.utils.Util;
import com.xiaobai.drive.utils.XToastUtils;
import com.xiaobai.drive.utils.download.DownloadManagerUtil;
import com.xiaobai.drive.utils.request.HttpEngine;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.imageview.ImageLoader;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.preview.PreviewBuilder;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.samlss.broccoli.Broccoli;

import static android.view.View.VISIBLE;

/**
 * 首页动态
 *
 * @author xuexiang
 * @since 2019-10-30 00:15
 */
@Page(anim = CoreAnim.none, name = "主页/文件")
public class HomeFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private SimpleDelegateAdapter<FileItemEntity> mNewsAdapter;

    private static boolean isShow; // 是否显示CheckBox标识

//    private static String IMG_PREVIEW=getString(R.string.img_preview);

    private static List<FileItemEntity> files = new ArrayList<>();

    private static String title = "内容";

    private MyApp myApp;

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news;
    }

    @Override
    public void onAttach(Context context) {
        myApp = (MyApp) context.getApplicationContext();
        super.onAttach(context);
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        //获取网盘文件列表
//        getFiles();

        //轮播条
//        SingleDelegateAdapter bannerAdapter = new SingleDelegateAdapter(R.layout.include_head_view_banner) {
//            @Override
//            public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
//                SimpleImageBanner banner = holder.findViewById(R.id.sib_simple_usage);
//                banner.setSource(DemoDataProvider.getBannerList())
//                        .setOnItemClickListener((view, item, position1) -> XToastUtils.toast("headBanner position--->" + position1)).startScroll();
//            }
//        };

        //九宫格菜单
        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(4);
        gridLayoutHelper.setPadding(0, 16, 0, 0);
        gridLayoutHelper.setVGap(10);
        gridLayoutHelper.setHGap(0);
        SimpleDelegateAdapter<AdapterItem> commonAdapter = new SimpleDelegateAdapter<AdapterItem>(R.layout.adapter_common_grid_item, gridLayoutHelper, DemoDataProvider.getGridItems(getContext())) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void bindData(@NonNull RecyclerViewHolder holder, int position, AdapterItem item) {
                if (item != null) {
                    RadiusImageView imageView = holder.findViewById(R.id.riv_item);
                    imageView.setCircle(true);
                    ImageLoader.get().loadImage(imageView, item.getIcon());
//                    holder.viewClick()

                    holder.text(R.id.tv_title, item.getTitle().toString().substring(0, 1));
                    holder.text(R.id.tv_sub_title, item.getTitle());
                    holder.click(R.id.ll_container, v -> {
                        String data = "";
                        String path = "";
                        switch (item.getTitle().toString()) {
                            case "视频":
                                data = "{\"id\":\"" + 1 + "\",\"path\":\"video/internal" + "\"}";
                                //videoList();
                                path = "video/internal";
                                break;
                            case "图片":
                                data = "{\"id\":\"" + 1 + "\",\"path\":\"image/internal" + "\"}";
                                path = "image/internal";
                                //imageList();
                                break;
                            case "音频":
                                data = "{\"id\":\"" + 1 + "\",\"path\":\"audio/internal" + "\"}";
                                path = "audio/internal";
                                //audioList();
                                break;
                            case "文档":
                                data = "{\"id\":\"" + 1 + "\",\"path\":\"doc/internal" + "\"}";
                                path = "doc/internal";
                                //docList();
                                break;
                        }
                        myApp.path.push(path);
                        ActivityUtils.startActivity(FilesActivity.class, "data", data);
                        title = item.getTitle().toString();
                    });
                }
            }
        };

        //资讯的标题
        SingleDelegateAdapter titleAdapter = new SingleDelegateAdapter(R.layout.adapter_title_item) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
                holder.text(R.id.tv_title, title);
                holder.text(R.id.tv_action, "更多");
                holder.click(R.id.tv_action, v -> XToastUtils.toast("更多"));
            }
        };

        //文件
        mNewsAdapter = new BroccoliSimpleDelegateAdapter<FileItemEntity>(R.layout.adpater_litem, new LinearLayoutHelper(), files) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onBindData(RecyclerViewHolder holder, FileItemEntity model, int position) {
                if (model != null) {
                    String[] prefix = model.getFileName().split("\\.");
                    String str = prefix[prefix.length - 1].toLowerCase();

                    //设置item
                    Util.setItem(holder, model, str);
                    holder.findView(R.id.item_content).setOnLongClickListener(v -> {
                        files.forEach(i -> {
                            holder.findView(R.id.checks).setVisibility(VISIBLE);
                        });
                        System.out.println("长按了。。。。。");
                        return true;
                    });
                    holder.checkedListener(R.id.checks, (buttonView, isChecked) -> {
                        System.out.println("选中了");
                    });
                    holder.click(R.id.item_action, v -> {
//                        BottomSheet.BottomListSheetBuilder builder = new BottomSheet.BottomListSheetBuilder(getActivity());
//                        if ("dir".equals(model.getType())) {
//                            builder.setTitle("操作")
//                                    .addItem("创建分享链接")
//                                    .addItem("详细信息")
//                                    .addItem("重命名")
//                                    .addItem("复制")
//                                    .addItem("移动")
//                                    .addItem("删除")
//                                    .setIsCenter(true)
//                                    .build()
//                                    .show();
//                        } else {
//                            builder.setTitle("操作")
//                                    .addItem("下载")
//                                    .addItem("创建分享链接")
//                                    .addItem("详细信息")
//                                    .addItem("重命名")
//                                    .addItem("复制")
//                                    .addItem("移动")
//                                    .addItem("删除")
//                                    .setIsCenter(true)
//                                    .build()
//                                    .show();
//                        }
//                        builder.setOnSheetItemClickListener((dialog, itemView, positions, tag) -> {
//                            System.out.println(tag);
//                            switch (tag){
//                                case "下载":
//                                    getDownloadUrl(model.getId(),model.getFileName(),"下载中",model.getFileName(),getContext());
//                                    break;
//
////                                    downloadFile("","","",model.getFileName(),getContext());
//                            }
//                            dialog.dismiss();
//                        });


                        BottomSheet.BottomGridSheetBuilder builder = new BottomSheet.BottomGridSheetBuilder(getActivity());
                        if ("dir".equals(model.getType())) {
                            builder
                                    .addItem(R.drawable.share_1, "创建分享链接", "创建分享链接", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.detail, "详细信息", "详细信息", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.edit, "重命名", "重命名", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.copy, "复制", "复制", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.move, "移动", "移动", BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                                    .addItem(R.drawable.delete, "删除", "删除", BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                                    .setOnSheetItemClickListener((dialog, itemView) -> {
                                        dialog.dismiss();
                                        String tag =  itemView.getTag().toString();
                                        XToastUtils.toast("tag:" + tag + ", content:" + itemView.toString());

                                    }).build().show();
                        }else{
                            builder
                                    .addItem(R.drawable.download, "下载", "下载", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.share_1, "创建分享链接", "创建分享链接", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.detail, "详细信息", "详细信息", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.edit, "重命名", "重命名", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.copy, "复制", "复制", BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                                    .addItem(R.drawable.move, "移动", "移动", BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                                    .addItem(R.drawable.delete, "删除", "删除", BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                                    .setOnSheetItemClickListener((dialog, itemView) -> {
                                        dialog.dismiss();
                                        String tag =  itemView.getTag().toString();
                                        switch (tag){
                                            case "下载":
                                                getDownloadUrl(model.getId(),model.getFileName(),"下载中",model.getFileName(),getContext());
                                                break;
                                        }

                                    }).build().show();
                        }

                        System.out.println("点击");
                    });
                    holder.click(R.id.item_content, v -> {
                        if (model.getType().equals("dir")) {
                            //如果是文件夹
                            myApp.path.push(model.getFileName());
                            openNewPage(FilesFragment.class);
                        } else {
                            preview(str, model, position);
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
        delegateAdapter.addAdapter(commonAdapter);
        delegateAdapter.addAdapter(titleAdapter);
        delegateAdapter.addAdapter(mNewsAdapter);

        recyclerView.setAdapter(delegateAdapter);
    }

    @Override
    protected void initListeners() {
        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            this.getFiles();
//            refreshLayout.getLayout().postDelayed(() -> {
//                mNewsAdapter.refresh(DemoDataProvider.getEmptyFileItem());
//                refreshLayout.finishRefresh();
//            }, 1000);
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.finishLoadMore();
//            refreshLayout.getLayout().postDelayed(() -> {
//                mNewsAdapter.loadMore(DemoDataProvider.getEmptyFileItem());
//                refreshLayout.finishLoadMore();
//            }, 1000);
            this.getFiles();
        });
        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }

    protected void getDownloadUrl(String id,String title,String desc,String fileName,Context context){
        HttpEngine.download(id, new Observer<BaseEntity>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(@io.reactivex.annotations.NonNull BaseEntity baseEntity) {
                if (baseEntity.getCode()==0){
                    downloadFile(baseEntity.getData().toString(),title,desc,fileName,context);
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
     * 下载文件
     * @param url
     * @param title
     * @param desc
     * @param fileName
     * @param context
     */
    protected void downloadFile(String url,String title,String desc,String fileName,Context context){
        DownloadManagerUtil downloadManagerUtil=new DownloadManagerUtil(context);
        long id=downloadManagerUtil.download(url, title, desc, fileName);
    }

    /**
     * 预览获取真实链接
     */
    protected void preview(String str, FileItemEntity model, int position) {
        HttpEngine.preview(model.getId(), new Observer<BaseEntity>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(@io.reactivex.annotations.NonNull BaseEntity baseEntity) {

                if (str.equals("mp4")) {
                    String data = "{\"title\":\"" + model.getFileName() + "\",\"url\":\"" + baseEntity.getData() + "\"}";
                    ActivityUtils.startActivity(VideoActivity.class, "data", data);
                } else if (str.equals("jpg")) {
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
     * 获取网盘文件
     */
    protected void getFiles() {
        HttpEngine.directory("", new Observer<BaseEntity>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(@io.reactivex.annotations.NonNull BaseEntity baseEntity) {
                if (baseEntity.getCode() == 0) {
                    files.clear();
                    LinkedTreeMap<String, Object> result = (LinkedTreeMap<String, Object>) baseEntity.getData();
                    List<LinkedTreeMap<String, Object>> list = (List<LinkedTreeMap<String, Object>>) result.get("objects");
                    files = Util.toList(list);
                    System.out.println("列表长度：" + files.size());
                    mNewsAdapter.refresh(files);
                    refreshLayout.finishRefresh();
                }
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
