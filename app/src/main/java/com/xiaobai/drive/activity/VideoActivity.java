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

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.xiaobai.drive.R;
import com.xiaobai.drive.core.BaseActivity;
import com.xiaobai.drive.utils.XToastUtils;
import com.xuexiang.xutil.app.IntentUtils;

import java.util.HashMap;
import java.util.Map;

public class VideoActivity extends BaseActivity {

    StandardGSYVideoPlayer videoPlayer;

    OrientationUtils orientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        String data=getIntent().getStringExtra("data");
//        XToastUtils.toast(data);
        Gson gson=new Gson();
        Map<String,String> map=new HashMap<>();
        map=gson.fromJson(data,Map.class);
        setContentView(R.layout.freagment_video);
        init(map.get("url"),map.get("title"));
    }

    private void init(String url,String title) {
        videoPlayer =  (StandardGSYVideoPlayer)findViewById(R.id.my_video);
        String source1 = url;
        videoPlayer.setUp(source1, true, title);

        //????????????
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.drawable.video);
        videoPlayer.setThumbImageView(imageView);
        //??????title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //???????????????
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //????????????
        orientationUtils = new OrientationUtils(this, videoPlayer);
        //????????????????????????,????????????????????????????????????????????????
        videoPlayer.getFullscreenButton().setOnClickListener(v -> orientationUtils.resolveByClick());
        //????????????????????????
        videoPlayer.setIsTouchWiget(true);
        //????????????????????????
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        videoPlayer.startPlayLogic();
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed() {
        //?????????????????????
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.getFullscreenButton().performClick();
            return;
        }
        //????????????
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }


//    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {
//        @Override
//        public void onCompletion(MediaPlayer mp) {
//            Toast.makeText( VideoActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
//        }
//    }
}
