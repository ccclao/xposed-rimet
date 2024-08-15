package com.sky.xposed.rimet.data.model;

import java.io.Serializable;

/***
 **  @author: JiChao
 **  @date: 2024/8/15 9:33
 **  @desc: 照片模型类
 */
public class PictureModel implements Serializable {
    private String mName;

    private String mTime;

    private String mFilePath;

    public PictureModel() {
    }

    public PictureModel(String mName, String mTime, String mFilePath) {
        this.mName = mName;
        this.mTime = mTime;
        this.mFilePath = mFilePath;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }
}
