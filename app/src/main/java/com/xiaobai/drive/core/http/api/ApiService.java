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

package com.xiaobai.drive.core.http.api;

import com.xuexiang.templateproject.core.http.entity.TipInfo;
import com.xuexiang.xhttp2.model.ApiResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * @author xuexiang
 * @since 2021/1/9 7:01 PM
 */
public class ApiService {

    /**
     * 使用的是retrofit的接口定义
     */
    public interface IGetService {

        /**
         * 获取验证码
         * @return
         */
        @GET("/api/v3/site/captcha")
        Observable<Map> getCaptcha();

        /**
         * 用户注册
         */
        @POST("/api/v3/user")
        Observable<Map> register_user(String email,String password,String captcha);
    }

}
