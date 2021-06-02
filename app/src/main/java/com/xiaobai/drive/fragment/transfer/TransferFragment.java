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

package com.xiaobai.drive.fragment.transfer;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.ProgressBar;

import com.xiaobai.drive.R;
import com.xiaobai.drive.service.DownloadServiceImpl;
import com.xiaobai.drive.service.DownloadService;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import static android.content.Context.BIND_AUTO_CREATE;

public class TransferFragment extends BaseFragment implements DownloadServiceImpl {

    private String TAG = this.getClass().getSimpleName();

    private ProgressBar progressBar;

    private boolean isBindService;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
            DownloadService downloadService = binder.getService();

            //接口回调，下载进度
            downloadService.setOnProgressListener(new DownloadService.OnProgressListener() {
                @Override
                public void onProgress(float fraction) {
                    Log.i(TAG, "下载进度：" + fraction);
                    progressBar.setProgress((int) (fraction * 100));

                    //判断是否真的下载完成进行安装了，以及是否注册绑定过服务
//                    if (fraction == DownloadService.UNBIND_SERVICE && isBindService) {
//                        unbindService(conn);
//                        isBindService = false;
//                        MToast.shortToast("下载完成！");
//                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };





    @Override
    protected int getLayoutId() {
        return R.layout.fragment_transfer;
    }

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
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
