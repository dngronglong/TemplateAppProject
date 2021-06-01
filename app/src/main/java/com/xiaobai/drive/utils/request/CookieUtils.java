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

import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.xiaobai.drive.activity.LoginActivity;
import com.xiaobai.drive.utils.MMKVUtils;
import com.xiaobai.drive.utils.XToastUtils;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.common.StringUtils;


public class CookieUtils {

    private static String cookie;

    private static final String KEY_COOKIE = "LoginCookie";

    private static final String KEY_PROFILE_CHANNEL = "xiaobai";

    /**
     * 初始化Cookie信息
     */
    public static void init(Context context) {
        MMKVUtils.init(context);
        cookie = MMKVUtils.getString(KEY_COOKIE, "");
    }

    public static void setCookie(String scookie) {
        cookie = scookie;
        MMKVUtils.put(KEY_COOKIE, cookie);
    }

    public static void clearCookie() {
        cookie = null;
        MMKVUtils.remove(KEY_COOKIE);
    }

    public static String getCookie() {
        return cookie;
    }

    public static boolean hasCookie() {
        return MMKVUtils.containsKey(KEY_COOKIE);
    }

    /**
     * 处理登录成功的事件
     *
     * @param cookie 账户信息
     */
    public static boolean handleLoginSuccess(String cookie) {
        if (!StringUtils.isEmpty(cookie)) {
            XToastUtils.success("登录成功！");
            MobclickAgent.onProfileSignIn(KEY_PROFILE_CHANNEL, cookie);
            setCookie(cookie);
            return true;
        } else {
            XToastUtils.error("登录失败！");
            return false;
        }
    }

    /**
     * 处理登出的事件
     */
    public static void handleLogoutSuccess() {
        MobclickAgent.onProfileSignOff();
        //登出时，清除账号信息
        clearCookie();
        XToastUtils.success("登出成功！");
        //跳转到登录页
        ActivityUtils.startActivity(LoginActivity.class);
    }
}
