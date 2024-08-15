package com.sky.xposed.rimet.contract;

import com.sky.xposed.rimet.data.model.PictureModel;
import com.sky.xposed.rimet.data.model.StationModel;
import com.sky.xposed.ui.base.BasePresenter;
import com.sky.xposed.ui.base.BaseView;

import java.util.List;

/***
 **  @author: JiChao
 **  @date: 2024/8/15 9:32
 **  @desc: 照片接口
 */
public interface PictureContract {
    interface View extends BaseView {

        /**
         * 成功
         * @param models
         */
        void onLoad(List<PictureModel> models);

        /**
         * 失败
         * @param msg
         */
        void onLoadFailed(String msg);

        /**
         * 成功
         * @param model
         */
        void onAdd(PictureModel model);

        /**
         * 失败
         * @param msg
         */
        void onAddFailed(String msg);

        /**
         * 成功
         */
        void onSaveSucceed();

        /**
         * 失败
         * @param msg
         */
        void onSaveFailed(String msg);
    }

    interface Presenter extends BasePresenter {

        /**
         * 加载
         */
        void load();

        /**
         * 添加
         */
        void add(String name);

        /**
         * 保存
         * @param models
         */
        void save(List<PictureModel> models);
    }
}
