package com.sky.xposed.rimet.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sky.xposed.rimet.data.model.PictureModel;
import com.sky.xposed.rimet.data.model.StationModel;
import com.sky.xposed.ui.base.BaseListAdapter;
import com.sky.xposed.ui.view.TextItemView;

/***
 **  @author: JiChao
 **  @date: 2024/8/15 9:38
 **  @desc: 照片适配器
 */
public class PictureAdapter extends BaseListAdapter<PictureModel> {

    public PictureAdapter(Context context) {
        super(context);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent, int viewType) {

        TextItemView itemView = new TextItemView(getContext());
        itemView.getNameView().setSingleLine(false);
        itemView.getNameView().setMaxLines(2);

        return itemView;
    }

    @Override
    public ViewHolder<PictureModel> onCreateViewHolder(View view, int viewType) {
        return new PictureAdapter.PictureHolder(view, this);
    }

    private class PictureHolder extends ViewHolder<PictureModel> {

        private TextItemView mTextItemView;

        PictureHolder(View itemView, BaseListAdapter<PictureModel> baseListAdapter) {
            super(itemView, baseListAdapter);
        }

        @Override
        public void onInitialize() {
            super.onInitialize();
            mTextItemView = (TextItemView) mItemView;
        }

        @Override
        public void onBind(int position, int viewType) {

            PictureModel item = getItem(position);

            mTextItemView.setName(item.getTime());
            mTextItemView.setDesc(item.getName());
        }
    }
}
