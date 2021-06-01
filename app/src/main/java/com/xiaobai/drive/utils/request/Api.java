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
import com.xuexiang.xhttp2.model.ApiResult;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface Api {
    /**
     * 获取验证码
     *
     * @return
     */
    @GET("/api/v3/site/captcha")
    Observable<BaseEntity> getCaptcha(@Query("t") String t);

    /**
     * 用户注册
     */
    @POST("/api/v3/user")
    Observable<BaseEntity> register_user(@Body Map<String, String> params);

    /**
     * 用户登录
     *
     * @param params
     * @return
     */
    @POST("/api/v3/user/session")
    Observable<BaseEntity> login(@Body Map<String, String> params);

    /**
     * 获取配置文件
     *
     * @return
     */
    @GET("/api/v3/site/config")
    Observable<BaseEntity> config();

    /**
     * 获取网盘所有视频
     *
     * @return
     */
    @GET("/api/v3/file/search/video%2Finternal")
    Observable<BaseEntity> videoList();

    /**
     * 获取网盘所有图片
     *
     * @return
     */
    @GET("/api/v3/file/search/image%2Finternal")
    Observable<BaseEntity> imageList();

    /**
     * 获取网盘所有文档
     *
     * @return
     */
    @GET("/api/v3/file/search/doc%2Finternal")
    Observable<BaseEntity> docList();

    /**
     * 获取网盘所有音频
     *
     * @return
     */
    @GET("/api/v3/file/search/audio%2Finternal")
    Observable<BaseEntity> audioList();

    /**
     * 预览
     *
     * @return
     */
    @GET("/api/v3/file/preview/{id}")
    Observable<BaseEntity> preview(@Path("id") String id);

    /**
     * 获取网盘文件
     *
     * @return
     */
    @GET("/api/v3/directory/{path}")
    Observable<BaseEntity> directory(@Path("path") String path);

    /**
     * 获取下载链接
     * @param id
     * @return
     */
    @PUT("/api/v3/file/download/{id}")
    Observable<BaseEntity> download(@Path("id") String id);
}
