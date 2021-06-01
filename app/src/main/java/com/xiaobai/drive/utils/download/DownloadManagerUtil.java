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

package com.xiaobai.drive.utils.download;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.xiaobai.drive.activity.MainActivity;

//下载工具
public class DownloadManagerUtil {

    private Context mContext;

    private DownloadManager downloadManager;

    private DownloadReceiver downloadReceiver;

    public DownloadManagerUtil(Context context) {
        mContext = context;
    }

    /**
     * @param url   下载地址
     * @param title 通知栏标题
     * @param desc  通知栏描述信息
     * @return
     */
    public long download(String url, String title, String desc, String fileName) {
        Uri uri = Uri.parse(url);
        Log.i("下载地址",url);
        DownloadManager.Request req = new DownloadManager.Request(uri);
        //设置WIFI下进行更新
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);

        //使用系统默认的下载路径 此处为应用内 /android/data/packages ,所以兼容7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            req.setRequiresDeviceIdle(false);
            req.setRequiresCharging(false);
        }
        req.setVisibleInDownloadsUi(true);//是否显示下载 从Android Q开始会被忽略
        req.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, fileName);
        req.setVisibleInDownloadsUi(true);
        //通知栏标题
        req.setTitle(title);

        //req.setMimeType("application/cn.trinea.download.file");
        //通知栏描述信息
        req.setDescription(desc);

        /**设置漫游状态下是否可以下载*/
        req.setAllowedOverRoaming(false);
        //设置类型为.apk
        //req.setMimeType("application/vnd.android.package-archive");
        //下载中和下载完后都显示通知栏
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //获取下载任务ID
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        long loadId = downloadManager.enqueue(req);
        Log.i("min77", "loadId = " + loadId);
        //使用DownloadManager.Query可以查询下载状态
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(loadId);
        try {
//            boolean flag = true;
//            while (flag) {
            Cursor cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)); //获取下载状态
                System.out.println(status);
                //String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)); //获取保存路径
                switch (status) {
                    //判断下载状态
                    case DownloadManager.STATUS_SUCCESSFUL:
//                            flag = false;
                        Log.d("Jason", "download success");
                        break;
                    case DownloadManager.STATUS_FAILED:
//                            flag = false;
                        Log.d("Jason", "download fail");
                        break;
                    case DownloadManager.STATUS_RUNNING:
                        Log.d("下载", "下载中");
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        Log.d("下载状态", "网络错误正在重试");
                        break;
                }
//                }

                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loadId;
    }

    /**
     * 下载前先移除前一个任务，防止重复下载
     *
     * @param downloadId
     */
    public void clearCurrentTask(long downloadId) {
        DownloadManager dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        try {
            dm.remove(downloadId);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    public void queryFileName(long id) {
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            String file = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
            Log.e("调试_临时_log", "this_" + file);
        }
    }

}
