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

package com.xiaobai.drive.utils.cookie;

import com.xiaobai.drive.cache.UserInfoCache;
import com.xiaobai.drive.utils.request.CookieUtils;
import com.xuexiang.xui.XUI;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * cookie持久化
 */
public class CookieJarImpl implements CookieJar {

    private CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalArgumentException("cookieStore can not be null!");
        }
        this.cookieStore = cookieStore;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (url.toString().contains("session")){
            CookieUtils.setCookie(cookies.get(0).toString());
            cookieStore.saveCookie("cookie",cookies);
        }
        cookieStore.saveCookie(url, cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        System.out.println(cookieStore.getCookieByKey("cookie"));
        return cookieStore.getCookieByKey("cookie");
    }
    public CookieStore getCookieStore() {
        return cookieStore;
    }
}
