package com.xiaobai.drive.service;

public interface DownloadServiceImpl {
    /**
     *绑定版本更新下载服务
     *
     */
    void bindService(String url, String title, String desc, String fileName);
}
