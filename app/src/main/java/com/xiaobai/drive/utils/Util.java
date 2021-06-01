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

package com.xiaobai.drive.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import com.google.gson.internal.LinkedTreeMap;
import com.xiaobai.drive.R;
import com.xiaobai.drive.entity.FileItemEntity;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 工具类
 */
public class Util {
    public static Bitmap stringtoBitmap(String string){
        //将字符串转换成Bitmap类型
        Bitmap bitmap=null;
        try {
            byte[]bitmapArray;
            bitmapArray= Base64.decode(string, Base64.DEFAULT);
            bitmap= BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<FileItemEntity> toList(List<LinkedTreeMap<String, Object>> list){
        List<FileItemEntity> files=new ArrayList<>();
        list.forEach(i -> {
            FileItemEntity fileItemEntity = new FileItemEntity();
            fileItemEntity.setFileName(i.get("name").toString());
            fileItemEntity.setTips(i.get("size") + "|" + i.get("date"));
            fileItemEntity.setImageSrc("2");
            fileItemEntity.setId(i.get("id").toString());
            fileItemEntity.setType(i.get("type").toString());
            files.add(fileItemEntity);
        });
        return files;
    }

    public static void setItem(RecyclerViewHolder holder,FileItemEntity model,String str){
        holder.text(R.id.fileName, model.getFileName());
        holder.text(R.id.tips, model.getTips());
        if (model.getType().equals("dir")){
            holder.image(R.id.headPortrait,R.drawable.folder);
        }else{
            if (str.equals("mp4")||str.equals("rmvb")||str.equals("avi")||str.equals("mov")||str.equals("rm")||str.equals("flv")){
                holder.image(R.id.headPortrait,R.drawable.video);
            }else if (str.equals("mp3")||str.equals("flac")||str.equals("wav")){
                holder.image(R.id.headPortrait,R.drawable.audio);
            }else if (str.equals("txt")||str.equals("doc")||str.equals("docx")){
                holder.image(R.id.headPortrait,R.drawable.text);
            }else if (str.equals("png")||str.equals("jpg")||str.equals("gif")||str.equals("jpeg")){
                holder.image(R.id.headPortrait,R.drawable.pic);
            }else if (str.equals("rar")||str.equals("zip")||str.equals("7z")||str.equals("tar")){
                holder.image(R.id.headPortrait,R.drawable.yasuo);
            }else{
                holder.image(R.id.headPortrait,R.drawable.unknow);
            }
        }
    }

    /**
     * 拼接地址
     * @param strings
     * @return
     */
    public static String linkPath(Stack<String> strings){
        String str="";
        for (int i = 0; i < strings.size(); i++) {
            str+=strings.get(i)+"/";
        }
        return str.substring(0,str.length() - 1);
    }
}
