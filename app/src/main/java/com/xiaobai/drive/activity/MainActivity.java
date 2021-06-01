/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xiaobai.drive.R;
import com.xiaobai.drive.core.BaseActivity;
import com.xiaobai.drive.fragment.home.HomeFragment;
import com.xiaobai.drive.fragment.lately.LatelyFragment;
import com.xiaobai.drive.fragment.transfer.TransferFragment;
import com.xiaobai.drive.fragment.mine.MineFragment;
import com.xiaobai.drive.utils.XToastUtils;
import com.xiaobai.drive.widget.GuideTipsDialog;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.utils.Utils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.ClickUtils;
import com.xuexiang.xutil.common.CollectionUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;

/**
 * 程序主页面,只是一个简单的Tab例子
 *
 * @author xuexiang
 * @since 2019-07-07 23:53
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener, ClickUtils.OnClick2ExitListener, Toolbar.OnMenuItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    /**
     * 底部导航栏
     */
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;
    /**
     * 侧边栏
     */
//    @BindView(R.id.nav_view)
//    NavigationView navView;
//    @BindView(R.id.drawer_layout)
//    DrawerLayout drawerLayout;

    private String[] mTitles;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();

        initListeners();
    }

    @Override
    protected boolean isSupportSlideBack() {
        return false;
    }

    private void initViews() {
        mTitles = ResUtils.getStringArray(R.array.home_titles);
        toolbar.setTitle(mTitles[0]);
//        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(this);

        initHeader();

        //主页内容填充
        BaseFragment[] fragments = new BaseFragment[]{
                new HomeFragment(),
                new MineFragment(),
                new TransferFragment(),
                new LatelyFragment()
        };
        FragmentAdapter<BaseFragment> adapter = new FragmentAdapter<>(getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(mTitles.length - 1);
        viewPager.setAdapter(adapter);

        GuideTipsDialog.showTips(this);
    }

    private void initHeader() {
//        navView.setItemIconTintList(null);
//        View headerView = navView.getHeaderView(0);
//        LinearLayout navHeader = headerView.findViewById(R.id.nav_header);
//        RadiusImageView ivAvatar = headerView.findViewById(R.id.iv_avatar);
//        TextView tvAvatar = headerView.findViewById(R.id.tv_avatar);
//        TextView tvSign = headerView.findViewById(R.id.tv_sign);

//        if (Utils.isColorDark(ThemeUtils.resolveColor(this, R.attr.colorAccent))) {
//            tvAvatar.setTextColor(Colors.WHITE);
//            tvSign.setTextColor(Colors.WHITE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                ivAvatar.setImageTintList(ResUtils.getColors(R.color.xui_config_color_white));
//            }
//        } else {
//            tvAvatar.setTextColor(ThemeUtils.resolveColor(this, R.attr.xui_config_color_title_text));
//            tvSign.setTextColor(ThemeUtils.resolveColor(this, R.attr.xui_config_color_explain_text));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                ivAvatar.setImageTintList(ResUtils.getColors(R.color.xui_config_color_gray_3));
//            }
//        }
        // TODO: 2019-10-09 初始化数据
//        String str = UserInfoCache.get(this,"userInfo","").toString();
//        System.out.println(str);
//        Gson gson = new Gson();
//        Map<String, Object> map = new HashMap<String, Object>();
//        map=gson.fromJson(str, map.getClass());
//        System.out.println(map.get("nickname"));
//        String avatarUrl=getString(R.string.avatar)+map.get("id")+"/s";
//        //得到可用的图片
//        Bitmap bitmap = getHttpBitmap(avatarUrl);
//        ivAvatar.setImageBitmap(bitmap);
////        ivAvatar.setImageResource(R.drawable.ic_default_head);
//        tvAvatar.setText(map.get("nickname").toString());
////        tvSign.setText("这个家伙很懒，什么也没有留下～～");
//        navHeader.setOnClickListener(this);
    }

    protected void initListeners() {
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//
//        //侧边栏点击事件
//        navView.setNavigationItemSelectedListener(menuItem -> {
//            if (menuItem.isCheckable()) {
//                drawerLayout.closeDrawers();
//                return handleNavigationItemSelected(menuItem);
//            } else {
//                switch (menuItem.getItemId()) {
//                    case R.id.nav_settings:
//                        openNewPage(SettingsFragment.class);
//                        break;
//                    case R.id.nav_about:
//                        openNewPage(AboutFragment.class);
//                        break;
//                    default:
//                        XToastUtils.toast("点击了:" + menuItem.getTitle());
//                        break;
//                }
//            }
//            return true;
//        });

        //主页事件监听
        viewPager.addOnPageChangeListener(this);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    /**
     * 处理侧边栏点击事件
     *
     * @param menuItem
     * @return
     */
    private boolean handleNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = CollectionUtils.arrayIndexOf(mTitles, menuItem.getTitle());
        if (index != -1) {
            toolbar.setTitle(menuItem.getTitle());
            viewPager.setCurrentItem(index, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_privacy:
                Utils.showPrivacyDialog(this, null);
                break;
            default:
                break;
        }
        return false;
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_header:
                XToastUtils.toast("点击头部！");
                break;
            default:
                break;
        }
    }

    //=============ViewPager===================//

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        MenuItem item = bottomNavigation.getMenu().getItem(position);
        toolbar.setTitle(item.getTitle());
        item.setChecked(true);

//        updateSideNavStatus(item);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    //================Navigation================//

    /**
     * 底部导航栏点击事件
     *
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = CollectionUtils.arrayIndexOf(mTitles, menuItem.getTitle());
        System.out.println(index);
        if (index != -1) {
            toolbar.setTitle(menuItem.getTitle());
            viewPager.setCurrentItem(index, false);
            return true;
        }
        return false;
    }

    /**
     * 更新侧边栏菜单选中状态
     *
     * @param menuItem
     */
//    private void updateSideNavStatus(MenuItem menuItem) {
//        MenuItem side = navView.getMenu().findItem(menuItem.getItemId());
//        if (side != null) {
//            side.setChecked(true);
//        }
//    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ClickUtils.exitBy2Click(2000, this);
        }
        return true;
    }

    @Override
    public void onRetry() {
        XToastUtils.toast("再按一次退出程序");
    }

    @Override
    public void onExit() {
        XUtil.exitApp();
    }

    /**
     * 获取网落图片资源
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        System.out.println("头像地址："+url);
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return bitmap;

    }


}
