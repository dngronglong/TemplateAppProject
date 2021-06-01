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


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.xiaobai.drive.activity.LoginActivity;
import com.xiaobai.drive.activity.MainActivity;
import com.xuexiang.xutil.app.ActivityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.xuexiang.xui.widget.layout.linkage.LinkageScrollLayout.TAG;

public class ResponseLogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        Request request = chain.request();
        System.out.println("请求头："+request.headers());
        Log.i("请求地址", "code     =  : " + request.url());
        Log.i("重定向地址：",response.request().url().toString());
        if (request.url().toString().contains("preview")){
            String str="{\"data\":\""+response.request().url().toString()+"\",\"code\":0,\"msg\":\"获取成功\"}";
            ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), str);
            return response.newBuilder().body(responseBody).build();
        }
        if (response.body() != null && response.body().contentType() != null) {
            MediaType mediaType = response.body().contentType();
            String string = response.body().string();
            Gson gson=new Gson();
            Map<String,Object> result=new LinkedTreeMap<>();
            result=gson.fromJson(string, LinkedTreeMap.class);
            Log.d(TAG, "mediaType =  :  " + mediaType.toString());
            Log.d(TAG, "string    =  : " + result.get("msg"));
            Log.d(TAG, "状态码    =  : " + result.get("code"));
            if (Double.parseDouble(result.get("code").toString())==401.0){
                //未登录
                ActivityUtils.startActivity(LoginActivity.class);
                return response;
            }

            ResponseBody responseBody = ResponseBody.create(mediaType, string);
            return response.newBuilder().body(responseBody).build();
        } else {
            return response;
        }
    }
}
