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

package com.xiaobai.drive.utils.request;

import com.xiaobai.drive.entity.BaseEntity;

import java.util.Hashtable;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HttpEngine {

    private static Api api = RetrofitServiceManager.getInstance().create(Api.class);

    /**
     * 获取验证码
     * @param observer
     */
    public static void getCaptcha(String t,Observer<BaseEntity> observer){
        setSubscribe(api.getCaptcha(t),observer);
    }

    /**
     * 用户注册
     * @param params
     * @param observer
     */
    public static void register(Map<String,String> params,Observer<BaseEntity> observer){
        setSubscribe(api.register_user(params),observer);
    }

    /**
     * 用户登录
     * @param params
     * @param observer
     */
    public static void login(Map<String,String> params,Observer<BaseEntity> observer){
        setSubscribe(api.login(params),observer);
    }

    /**
     * 获取配置文件
     * @param observer
     */
    public static void config(Observer<BaseEntity> observer){
        setSubscribe(api.config(),observer);
    }

    /**
     * 获取网盘所有视频
     * @param observer
     */
    public static void videoList(Observer<BaseEntity> observer){
        setSubscribe(api.videoList(),observer);
    }

    /**
     * 获取网盘所有图片
     * @param observer
     */
    public static void imageList(Observer<BaseEntity> observer){
        setSubscribe(api.imageList(),observer);
    }

    /**
     * 获取网盘所有音频
     * @param observer
     */
    public static void audioList(Observer<BaseEntity> observer){
        setSubscribe(api.audioList(),observer);
    }

    /**
     * 获取网盘所有文档
     * @param observer
     */
    public static void docList(Observer<BaseEntity> observer){
        setSubscribe(api.docList(),observer);
    }

    /**
     * 预览图片/视频
     * @param observer
     */
    public static void preview(String id,Observer<BaseEntity> observer){
        setSubscribe(api.preview(id),observer);
    }

    public static void directory(String path,Observer<BaseEntity> observer){
        setSubscribe(api.directory(path),observer);
    }

    /**
     * 获取下载链接
     * @param id id
     * @param observer
     */
    public static void download(String id,Observer<BaseEntity> observer){
        setSubscribe(api.download(id),observer);
    }





    private static <T> void setSubscribe(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())//子线程访问网络
                .observeOn(AndroidSchedulers.mainThread())//回调到主线程
                .subscribe(observer);
    }
}
