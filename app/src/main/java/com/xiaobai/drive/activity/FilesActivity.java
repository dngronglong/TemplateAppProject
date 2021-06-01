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

package com.xiaobai.drive.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.CheckBox;

import com.xiaobai.drive.MyApp;
import com.xiaobai.drive.R;
import com.xiaobai.drive.core.BaseActivity;
import com.xiaobai.drive.fragment.file.FilesFragment;
import com.xiaobai.drive.utils.BackHandledFragment;
import com.xiaobai.drive.utils.BackHandledInterface;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xutil.display.Colors;

import butterknife.BindView;

public class FilesActivity extends BaseActivity implements BackHandledInterface {

    protected  BackHandledFragment mBackHandedFragment;

    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp= (MyApp) getApplicationContext();
        openPage(FilesFragment.class, getIntent().getExtras());
    }

    @Override
    protected boolean isSupportSlideBack() {
        return false;
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    @Override
    protected void initStatusBarStyle() {
        StatusBarUtils.initStatusBarStyle(this, false, Colors.WHITE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Log.e("按键", "onBackPressed  22222 : 按下了返回键");
            if(mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()){
                if(getSupportFragmentManager().getBackStackEntryCount()-1 == 0){
                    super.onBackPressed();
                }else{
                    getSupportFragmentManager().popBackStack();
                }
                //弹出最后一个路径元素
                myApp.path.pop();

            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }



//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return KeyboardUtils.onDisableBackKeyDown(keyCode) && super.onKeyDown(keyCode, event);
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        //点击两次返回退出应用
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            // 这里根据自己的需求进行返回按钮的事件操作
//            if (Jzvd.backPress()) {
//                return true;
//            }
//            if ((System.currentTimeMillis() - mExitTime) > 2000) {
//                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                mExitTime = System.currentTimeMillis();
//            } else {
//                AppManager.getAppManager().AppExit(MainActivity.this);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
