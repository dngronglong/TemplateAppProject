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

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class SPCookieStore implements CookieStore {

    private static final String COOKIE_PREFS = "sp_cookie";           //cookie使用prefs保存
    private static final String COOKIE_NAME_PREFIX = "cookie_";         //cookie持久化的统一前缀

    private final Map<String, ConcurrentHashMap<String, Cookie>> cookies;
    private final SharedPreferences cookiePrefs;

    public SPCookieStore(Context context) {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE);
        cookies = new HashMap<>();

        //将持久化的cookies缓存到内存中,数据结构为 Map<Url.host, Map<CookieToken, Cookie>>
        Map<String, ?> prefsMap = cookiePrefs.getAll();
        for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
            if ((entry.getValue()) != null && !entry.getKey().startsWith(COOKIE_NAME_PREFIX)) {
                //获取url对应的所有cookie的key,用","分割
                String[] cookieNames = TextUtils.split((String) entry.getValue(), ",");
                for (String name : cookieNames) {
                    //根据对应cookie的Key,从xml中获取cookie的真实值
                    String encodedCookie = cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
                    if (encodedCookie != null) {
                        Cookie decodedCookie = SerializableCookie.decodeCookie(encodedCookie);
                        if (decodedCookie != null) {
                            if (!cookies.containsKey(entry.getKey())) {
                                cookies.put(entry.getKey(), new ConcurrentHashMap<String, Cookie>());
                            }
                            cookies.get(entry.getKey()).put(name, decodedCookie);
                        }
                    }
                }
            }
        }
    }

    private String getCookieToken(Cookie cookie) {
        return cookie.name() + "@" + cookie.domain();
    }

    /** 当前cookie是否过期 */
    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    @Override
    public void saveCookie(String cookieName, List<Cookie> urlCookies) {
        for (Cookie cookie : urlCookies) {
            saveCookie(cookieName, cookie);
        }
    }


    @Override
    public synchronized void saveCookie(HttpUrl url, List<Cookie> urlCookies) {
        for (Cookie cookie : urlCookies) {
            saveCookie(url, cookie);
        }
    }

    @Override
    public void saveCookie(HttpUrl url, Cookie cookie) {
        if (!cookies.containsKey(url.host())) {
            cookies.put(url.host(), new ConcurrentHashMap<String, Cookie>());
        }
        //当前cookie是否过期
        if (isCookieExpired(cookie)) {
            removeCookie(url, cookie);
        } else {
            saveCookie(url, cookie, getCookieToken(cookie));
        }
    }

    @Override
    public void saveCookie(String cookieName, Cookie cookie) {
        if (!cookies.containsKey(cookieName)) {
            cookies.put(cookieName, new ConcurrentHashMap<String, Cookie>());
        }
        //当前cookie是否过期
        if (isCookieExpired(cookie)) {
            removeAllCookie();
        } else {
            saveCookie(cookieName, cookie, getCookieToken(cookie));
        }
    }

    /** 保存cookie，并将cookies持久化到本地 */
    private void saveCookie(HttpUrl url, Cookie cookie, String cookieToken) {
        //内存缓存
        cookies.get(url.host()).put(cookieToken, cookie);
        //文件缓存
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putString(url.host(), TextUtils.join(",", cookies.get(url.host()).keySet()));
        prefsWriter.putString(COOKIE_NAME_PREFIX + cookieToken, SerializableCookie.encodeCookie(url.host(), cookie));
        prefsWriter.apply();
    }

    /** 保存cookie，并将cookies持久化到本地 */
    private void saveCookie(String key, Cookie cookie, String cookieToken) {
        //内存缓存
        cookies.get(key).put(cookieToken, cookie);
        //文件缓存
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putString(key, TextUtils.join(",", cookies.get(key).keySet()));
        prefsWriter.putString(COOKIE_NAME_PREFIX + cookieToken, SerializableCookie.encodeCookie(key, cookie));
        prefsWriter.apply();
    }

    @Override
    public List<Cookie> getCookieByKey(String key) {
        List<Cookie> ret = new ArrayList<>();
        if (!cookies.containsKey(key)) return ret;

        Collection<Cookie> urlCookies = cookies.get(key).values();
        for (Cookie cookie : urlCookies) {
            if (isCookieExpired(cookie)) {
                removeAllCookie();
            } else {
                ret.add(cookie);
            }
        }
        return ret;
    }

    @Override
    public List<Cookie> loadCookie(HttpUrl url) {
        List<Cookie> ret = new ArrayList<>();
        if (!cookies.containsKey(url.host())) return ret;

        Collection<Cookie> urlCookies = cookies.get(url.host()).values();
        for (Cookie cookie : urlCookies) {
            if (isCookieExpired(cookie)) {
                removeCookie(url, cookie);
            } else {
                ret.add(cookie);
            }
        }
        return ret;
    }

    @Override
    public List<Cookie> getAllCookie() {
        List<Cookie> ret = new ArrayList<>();
        for (String key : cookies.keySet()) {
            ret.addAll(cookies.get(key).values());
        }
        return ret;
    }

    @Override
    public List<Cookie> getCookie(HttpUrl url) {
        List<Cookie> ret = new ArrayList<>();
        Map<String, Cookie> mapCookie = cookies.get(url.host());
        if (mapCookie != null) ret.addAll(mapCookie.values());
        return ret;
    }

    @Override
    public boolean removeCookie(HttpUrl url, Cookie cookie) {
        if (!cookies.containsKey(url.host())) return false;
        String cookieToken = getCookieToken(cookie);
        if (!cookies.get(url.host()).containsKey(cookieToken)) return false;

        //内存移除
        cookies.get(url.host()).remove(cookieToken);
        //文件移除
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        if (cookiePrefs.contains(COOKIE_NAME_PREFIX + cookieToken)) {
            prefsWriter.remove(COOKIE_NAME_PREFIX + cookieToken);
        }
        prefsWriter.putString(url.host(), TextUtils.join(",", cookies.get(url.host()).keySet()));
        prefsWriter.apply();
        return true;
    }

    @Override
    public boolean removeCookie(HttpUrl url) {
        if (!cookies.containsKey(url.host())) return false;

        //内存移除
        ConcurrentHashMap<String, Cookie> urlCookie = cookies.remove(url.host());
        //文件移除
        Set<String> cookieTokens = urlCookie.keySet();
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        for (String cookieToken : cookieTokens) {
            if (cookiePrefs.contains(COOKIE_NAME_PREFIX + cookieToken)) {
                prefsWriter.remove(COOKIE_NAME_PREFIX + cookieToken);
            }
        }
        prefsWriter.remove(url.host());
        prefsWriter.apply();

        return true;
    }

    @Override
    public boolean removeAllCookie() {
        //内存移除
        cookies.clear();
        //文件移除
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
        return true;
    }
}
