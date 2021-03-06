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
import android.content.Intent;
import android.os.Build;
import android.util.Log;

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
import com.xiaobai.drive.service.DownloadService;
import com.xiaobai.drive.service.DownloadServiceImpl;
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
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.samlss.broccoli.Broccoli;

import static android.view.View.VISIBLE;

/**
 * ????????????
 *
 * @author xuexiang
 * @since 2019-10-30 00:15
 */
@Page(anim = CoreAnim.none, name = "??????/??????")
public class HomeFragment extends BaseFragment implements DownloadServiceImpl{

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private SimpleDelegateAdapter<FileItemEntity> mNewsAdapter;

    private static boolean isShow; // ????????????CheckBox??????

//    private static String IMG_PREVIEW=getString(R.string.img_preview);

    private static List<FileItemEntity> files = new ArrayList<>();

    private static String title = "??????";

    private MyApp myApp;

    /**
     * @return ????????? null????????????????????????
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * ???????????????id
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
     * ???????????????
     */
    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        //????????????????????????
//        getFiles();

        //?????????
//        SingleDelegateAdapter bannerAdapter = new SingleDelegateAdapter(R.layout.include_head_view_banner) {
//            @Override
//            public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
//                SimpleImageBanner banner = holder.findViewById(R.id.sib_simple_usage);
//                banner.setSource(DemoDataProvider.getBannerList())
//                        .setOnItemClickListener((view, item, position1) -> XToastUtils.toast("headBanner position--->" + position1)).startScroll();
//            }
//        };

        //???????????????
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
                            case "??????":
                                data = "{\"id\":\"" + 1 + "\",\"path\":\"video/internal" + "\"}";
                                //videoList();
                                path = "video/internal";
                                break;
                            case "??????":
                                data = "{\"id\":\"" + 1 + "\",\"path\":\"image/internal" + "\"}";
                                path = "image/internal";
                                //imageList();
                                break;
                            case "??????":
                                data = "{\"id\":\"" + 1 + "\",\"path\":\"audio/internal" + "\"}";
                                path = "audio/internal";
                                //audioList();
                                break;
                            case "??????":
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

        //???????????????
        SingleDelegateAdapter titleAdapter = new SingleDelegateAdapter(R.layout.adapter_title_item) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
                holder.text(R.id.tv_title, title);
                holder.text(R.id.tv_action, "??????");
                holder.click(R.id.tv_action, v -> XToastUtils.toast("??????"));
            }
        };

        //??????
        mNewsAdapter = new BroccoliSimpleDelegateAdapter<FileItemEntity>(R.layout.adpater_litem, new LinearLayoutHelper(), files) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onBindData(RecyclerViewHolder holder, FileItemEntity model, int position) {
                if (model != null) {
                    String[] prefix = model.getFileName().split("\\.");
                    String str = prefix[prefix.length - 1].toLowerCase();

                    //??????item
                    Util.setItem(holder, model, str);
                    holder.findView(R.id.item_content).setOnLongClickListener(v -> {
                        files.forEach(i -> {
                            holder.findView(R.id.checks).setVisibility(VISIBLE);
                        });
                        System.out.println("????????????????????????");
                        return true;
                    });
                    holder.checkedListener(R.id.checks, (buttonView, isChecked) -> {
                        System.out.println("?????????");
                    });
                    holder.click(R.id.item_action, v -> {
//                        BottomSheet.BottomListSheetBuilder builder = new BottomSheet.BottomListSheetBuilder(getActivity());
//                        if ("dir".equals(model.getType())) {
//                            builder.setTitle("??????")
//                                    .addItem("??????????????????")
//                                    .addItem("????????????")
//                                    .addItem("?????????")
//                                    .addItem("??????")
//                                    .addItem("??????")
//                                    .addItem("??????")
//                                    .setIsCenter(true)
//                                    .build()
//                                    .show();
//                        } else {
//                            builder.setTitle("??????")
//                                    .addItem("??????")
//                                    .addItem("??????????????????")
//                                    .addItem("????????????")
//                                    .addItem("?????????")
//                                    .addItem("??????")
//                                    .addItem("??????")
//                                    .addItem("??????")
//                                    .setIsCenter(true)
//                                    .build()
//                                    .show();
//                        }
//                        builder.setOnSheetItemClickListener((dialog, itemView, positions, tag) -> {
//                            System.out.println(tag);
//                            switch (tag){
//                                case "??????":
//                                    getDownloadUrl(model.getId(),model.getFileName(),"?????????",model.getFileName(),getContext());
//                                    break;
//
////                                    downloadFile("","","",model.getFileName(),getContext());
//                            }
//                            dialog.dismiss();
//                        });


                        BottomSheet.BottomGridSheetBuilder builder = new BottomSheet.BottomGridSheetBuilder(getActivity());
                        if ("dir".equals(model.getType())) {
                            builder
                                    .addItem(R.drawable.share_1, "??????????????????", "??????????????????", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.detail, "????????????", "????????????", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.edit, "?????????", "?????????", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.copy, "??????", "??????", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.move, "??????", "??????", BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                                    .addItem(R.drawable.delete, "??????", "??????", BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                                    .setOnSheetItemClickListener((dialog, itemView) -> {
                                        dialog.dismiss();
                                        String tag =  itemView.getTag().toString();
                                        XToastUtils.toast("tag:" + tag + ", content:" + itemView.toString());

                                    }).build().show();
                        }else{
                            builder
                                    .addItem(R.drawable.download, "??????", "??????", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.share_1, "??????????????????", "??????????????????", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.detail, "????????????", "????????????", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.edit, "?????????", "?????????", BottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                                    .addItem(R.drawable.copy, "??????", "??????", BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                                    .addItem(R.drawable.move, "??????", "??????", BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                                    .addItem(R.drawable.delete, "??????", "??????", BottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                                    .setOnSheetItemClickListener((dialog, itemView) -> {
                                        dialog.dismiss();
                                        String tag =  itemView.getTag().toString();
                                        switch (tag){
                                            case "??????":
                                                getDownloadUrl(model.getId(),model.getFileName(),"?????????",model.getFileName(),getContext());
                                                break;
                                        }

                                    }).build().show();
                        }

                        System.out.println("??????");
                    });
                    holder.click(R.id.item_content, v -> {
                        if (model.getType().equals("dir")) {
                            //??????????????????
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
        //????????????
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 ?????????????????????????????????
            this.getFiles();
//            refreshLayout.getLayout().postDelayed(() -> {
//                mNewsAdapter.refresh(DemoDataProvider.getEmptyFileItem());
//                refreshLayout.finishRefresh();
//            }, 1000);
        });
        //????????????
//        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 ?????????????????????????????????
            //refreshLayout.finishLoadMore();
//            refreshLayout.getLayout().postDelayed(() -> {
//                mNewsAdapter.loadMore(DemoDataProvider.getEmptyFileItem());
//                refreshLayout.finishLoadMore();
//            }, 1000);
            //this.getFiles();
//        });
        refreshLayout.autoRefresh();//????????????????????????????????????????????????
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
//                    bindService(model.getId(),model.getFileName(),"?????????",model.getFileName());
                    downloadFile(baseEntity.getData().toString(),title,desc,fileName,context);
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                System.out.println("????????????");
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("????????????");
            }
        });
    }


    /**
     * ????????????
     * @param url
     * @param title
     * @param desc
     * @param fileName
     * @param context
     */
    protected void downloadFile(String url,String title,String desc,String fileName,Context context){
        DownloadManagerUtil downloadManagerUtil=new DownloadManagerUtil(context);
        long id=downloadManagerUtil.download(url, title, desc, fileName);
        System.out.println("????????????ID???"+id);
        downloadManagerUtil.queryFileName(id);
        Timer timer=new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                System.out.println("1");
                int i=downloadManagerUtil.getDownloadPercent(id);
                Log.i("????????????",i+"");
                int j[]=downloadManagerUtil.getBytesAndStatus(id);
                for (int k=0;k<j.length;k++){
                    System.out.println(j[k]);
                }
            }
        };
        timer.schedule(timerTask,2000);

    }

    /**
     * ????????????????????????
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
                            .setImg(new ImageViewInfo(baseEntity.getData().toString())) //??????????????????
                            .setCurrentIndex(position) //?????????????????????
                            .setSingleFling(true)
                            .setType(PreviewBuilder.IndicatorType.Number) //??????????????????
                            .start();
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                System.out.println("????????????");
                System.out.println(e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("????????????");
            }
        });
    }

    /**
     * ??????????????????
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
                    System.out.println("???????????????" + files.size());
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

    @Override
    public void bindService(String url, String title, String desc, String fileName) {
        Intent intent = new Intent(this.getActivity(), DownloadService.class);
        intent.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_URL, url);
        intent.putExtra("title", title);
        intent.putExtra("desc", desc);
        intent.putExtra("fileName", fileName);
    }
}
