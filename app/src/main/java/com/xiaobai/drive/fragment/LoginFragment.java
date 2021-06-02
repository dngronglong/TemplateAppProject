/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
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

package com.xiaobai.drive.fragment;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.google.gson.internal.LinkedTreeMap;
import com.xiaobai.drive.R;
import com.xiaobai.drive.activity.MainActivity;
import com.xiaobai.drive.activity.RegisterActivity;
import com.xiaobai.drive.cache.UserInfoCache;
import com.xiaobai.drive.entity.BaseEntity;
import com.xiaobai.drive.utils.SettingUtils;
import com.xiaobai.drive.utils.XToastUtils;
import com.xiaobai.drive.utils.request.CookieUtils;
import com.xiaobai.drive.utils.request.HttpEngine;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.utils.Utils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xutil.app.ActivityUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;


/**
 * 登录页面
 *
 * @author xuexiang
 * @since 2019-11-17 22:15
 */
@Page(anim = CoreAnim.none)
public class LoginFragment extends BaseFragment {

    @BindView(R.id.et_email)
    MaterialEditText email;
    @BindView(R.id.et_login_password)
    MaterialEditText password;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle()
                .setImmersive(true);
        titleBar.setBackgroundColor(Color.TRANSPARENT);
        titleBar.setTitle("");
        //titleBar.setLeftImageDrawable(ResUtils.getVectorDrawable(getContext(), R.drawable.ic_login_close));
        titleBar.setActionTextColor(ThemeUtils.resolveColor(getContext(), R.attr.colorAccent));
//        titleBar.addAction(new TitleBar.TextAction(R.string.title_jump_login) {
//            @Override
//            public void performAction(View view) {
//                onLoginSuccess();
//            }
//        });
        return titleBar;
    }

    @Override
    protected void initViews() {


        //隐私政策弹窗
        if (!SettingUtils.isAgreePrivacy()) {
            Utils.showPrivacyDialog(getContext(), (dialog, which) -> {
                dialog.dismiss();
                SettingUtils.setIsAgreePrivacy(true);
            });
        }
    }

    @SingleClick
    @OnClick({R.id.btn_login, R.id.tv_other_login, R.id.tv_forget_password, R.id.tv_user_protocol, R.id.tv_privacy_protocol, R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (email.validate()) {
                    this.login();
                }
                break;
            case R.id.tv_other_login:
                XToastUtils.info("其他登录方式");
                break;
            case R.id.tv_forget_password:
                XToastUtils.info("忘记密码");
                break;
            case R.id.tv_user_protocol:
                XToastUtils.info("用户协议");
                break;
            case R.id.tv_privacy_protocol:
                XToastUtils.info("隐私政策");
                break;
            case R.id.btn_register:
                goRegister();
                break;
            default:
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getVerifyCode(String phoneNumber) {
        // TODO: 2020/8/29 这里只是界面演示而已
        XToastUtils.warning("只是演示，验证码请随便输");
    }

    /**
     * 根据验证码登录
     *
     * @param phoneNumber 手机号
     * @param verifyCode  验证码
     */
    private void loginByVerifyCode(String phoneNumber, String verifyCode) {
        // TODO: 2020/8/29 这里只是界面演示而已
        onLoginSuccess();
    }

    private void login() {
        Map<String, String> params = new HashMap<>();
        params.put("Password", password.getEditValue());
        params.put("captchaCode", "");
        params.put("userName", email.getEditValue());
        Log.i("参数：", params.toString());
        //调用封装好的retrofit请求方法
        HttpEngine.login(params, new Observer<BaseEntity>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity data) {
                Log.i("login", "登录中！"+data);
                if (data.getCode() == 0) {
                    LinkedTreeMap<String,Object> result= (LinkedTreeMap<String, Object>) data.getData();
                    UserInfoCache.put(getActivity(), "userInfo", result);
                    //登录成功跳转
                    onLoginSuccess();

                }
                XToastUtils.warning(data.getMsg());

            }

            @Override
            public void onError(Throwable e) {
                //失败
                Log.i("retrofit==111=", "请求错误：" + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 登录成功的处理
     */
    private void onLoginSuccess() {
        System.out.println("登陆成功！");
        System.out.println("是否有全局Cookie："+CookieUtils.hasCookie());
        if (CookieUtils.hasCookie()){
            popToBack();
            ActivityUtils.startActivity(MainActivity.class);
        }

    }

    /**
     * 跳转登录
     */
    private void goRegister() {
        System.out.println("点击了");
        ActivityUtils.startActivity(RegisterActivity.class);
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }
}

