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

package com.xiaobai.drive.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.xiaobai.drive.R;
import com.xiaobai.drive.entity.BaseEntity;
import com.xiaobai.drive.utils.Util;
import com.xiaobai.drive.utils.XToastUtils;
import com.xiaobai.drive.utils.request.HttpEngine;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 注册页面
 */
@Page(anim = CoreAnim.none)
public class RegisterFragment extends BaseFragment {

    @BindView(R.id.et_email)
    MaterialEditText email;

    @BindView(R.id.et_register_password)
    MaterialEditText password;

    @BindView(R.id.et_register_password_again)
    MaterialEditText passwordAgain;

    @BindView(R.id.img_verify_code)
    AppCompatImageView verifyCode;

    @BindView(R.id.et_verify_code)
    MaterialEditText captchaCode;

    /**
     * 自定义标题
     * @return
     */
    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle()
                .setImmersive(true);
        titleBar.setBackgroundColor(Color.TRANSPARENT);
        titleBar.setTitle("");
        titleBar.setLeftImageDrawable(ResUtils.getVectorDrawable(getContext(), R.drawable.ic_login_close));
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
    protected int getLayoutId() {
        return R.layout.fragment_register;
    }

    @Override
    protected void initViews() {
        Log.i("verify","初始化验证码！");
        this.getVerifyCode();
    }

    /**
     * 页面按钮点击事件
     */
    @SingleClick
    @OnClick({R.id.img_verify_code,R.id.btn_register})
    public void onViewClicked(View view){
        switch (view.getId()) {
            case R.id.img_verify_code: getVerifyCode();
                break;
            case R.id.btn_register:
                if (!email.validate()){
                    XToastUtils.warning("邮箱不正确！");
                    return;
                }
                if (!password.getEditValue().equals(passwordAgain.getEditValue())){
                    XToastUtils.warning("两次输入密码不一致！");
                }
                //注册业务
                this.sendRegister();
        }
    }

    /**
     * 获取验证码
     */
    private void getVerifyCode() {
        //调用封装好的retrofit请求方法
        HttpEngine.getCaptcha(new Date().toString(),new Observer<BaseEntity>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity data) {
                Log.i("Captcha","获取验证码成功！");
                if (data.getCode()==0){
                    Bitmap bitmap=Util.stringtoBitmap(data.getData().toString().split(",")[1]);
                    System.out.println("转换图片成功");
                    verifyCode.setImageBitmap(bitmap);
                }

            }

            @Override
            public void onError(Throwable e) {
                //失败
                Log.i("retrofit==111=", "请求错误："+e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void sendRegister(){
        Map<String,String> params=new HashMap<>();
        params.put("Password",passwordAgain.getEditValue());
        params.put("captchaCode",captchaCode.getEditValue());
        params.put("userName",email.getEditValue());
        //调用封装好的retrofit请求方法
        HttpEngine.register(params,new Observer<BaseEntity>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity data) {
                Log.i("register","注册中！");
                if (data.getCode()!=0){
                    XToastUtils.warning(data.getMsg());
                    //重新获取验证码
                    getVerifyCode();
                }
                System.out.println(data);
            }

            @Override
            public void onError(Throwable e) {
                //失败
                Log.i("retrofit==111=", "请求错误："+e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
