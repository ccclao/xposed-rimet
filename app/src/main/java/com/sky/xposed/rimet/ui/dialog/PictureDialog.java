package com.sky.xposed.rimet.ui.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sky.xposed.common.util.CollectionUtil;
import com.sky.xposed.common.util.ToastUtil;
import com.sky.xposed.rimet.XConstant;
import com.sky.xposed.rimet.contract.PictureContract;
import com.sky.xposed.rimet.contract.StationContract;
import com.sky.xposed.rimet.data.model.PictureModel;
import com.sky.xposed.rimet.data.model.StationModel;
import com.sky.xposed.rimet.presenter.PicturePresenter;
import com.sky.xposed.rimet.presenter.StationPresenter;
import com.sky.xposed.rimet.ui.adapter.PictureAdapter;
import com.sky.xposed.rimet.ui.adapter.StationAdapter;
import com.sky.xposed.rimet.ui.util.DialogUtil;
import com.sky.xposed.ui.base.BasePluginDialog;
import com.sky.xposed.ui.util.DisplayUtil;
import com.sky.xposed.ui.util.LayoutUtil;
import com.sky.xposed.ui.util.PermissionUtil;
import com.sky.xposed.ui.util.ViewUtil;
import com.sky.xposed.ui.view.PluginFrameLayout;

import java.util.ArrayList;
import java.util.List;

/***
 **  @author: JiChao
 **  @date: 2024/8/15 9:31
 **  @desc: 照片历史弹窗
 */
public class PictureDialog extends BasePluginDialog implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, PictureContract.View {

    private ListView mListView;
    private PictureAdapter mAdapter;
    private PictureContract.Presenter mPresenter;

    private List<PictureModel> mPictureModels = new ArrayList<>();

    @Override
    public void createView(PluginFrameLayout frameView) {

        LinearLayout.LayoutParams params = LayoutUtil.newWrapLinearLayoutParams();
        params.leftMargin = DisplayUtil.dip2px(getContext(), 15);
        params.topMargin = DisplayUtil.dip2px(getContext(), 10);
        params.bottomMargin = DisplayUtil.dip2px(getContext(), 5);

        TextView tvTip = new TextView(getContext());
        tvTip.setLayoutParams(params);
        tvTip.setText("点击选择照片,长按可删除照片！");
        tvTip.setTextSize(12);

        frameView.addSubView(tvTip);

        mListView = ViewUtil.newListView(getContext());

        frameView.addSubView(mListView);
    }

    @Override
    protected PluginFrameLayout onCreatePluginFrame() {
        return createLinePluginFrame();
    }

    @Override
    protected void initView(View view, Bundle args) {
        super.initView(view, args);

        getTitleView().setElevation(DisplayUtil.DIP_4);

        showBack();
        setTitle("照片列表");
        showMoreMenu();

        mAdapter = new PictureAdapter(getContext());

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mListView.setAdapter(mAdapter);

        mPresenter = new PicturePresenter(getCoreManager(), this);
        mPresenter.load();
    }

    @Override
    public void onCreateMoreMenu(Menu menu) {
        super.onCreateMoreMenu(menu);

        menu.add(0, 1, 0, "添加");
        menu.add(0, 2, 0, "清空");
        menu.add(0, 3, 0, "导入");
        menu.add(0, 4, 0, "导出");
    }

    @Override
    public boolean onMoreItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (1 == itemId) {

            if (mPictureModels.size() > 10) {
                showMessage("最多添加10条信息!");
                return true;
            }

            if (!checkPermission()) {
                return true;
            }

            String alias = getDefaultPreferences().getString(XConstant.Key.LAST_ALIAS);
            DialogUtil.showEditDialog(getContext(),
                    "提示", alias, "请输入保存的别名", (view, value) -> {

                        if (TextUtils.isEmpty(value)) {
                            showMessage("输入的信息不能为空!");
                            return;
                        }
                        // 保存名称
                        getDefaultPreferences().putString(XConstant.Key.LAST_ALIAS, value);

                        // 添加
                        mPresenter.add(value);
                    });
            return true;
        } else if (2 == itemId) {
            // 清空
            DialogUtil.showDialog(getContext(),
                    "提示", "\n是否清空列表所有信息!", (dialog, which) -> {

                        if (mPictureModels != null) {
                            mPictureModels.clear();
                            mPresenter.save(mPictureModels);
                        }
                    });
            return true;
        } else if (3 == itemId) {
            // 导入
            ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            CharSequence models = cm.getText();
            if (!TextUtils.isEmpty(models)) {
                JsonArray array = new JsonParser().parse(models.toString()).getAsJsonArray();
                for (final JsonElement elem : array) {
                    PictureModel tmpModel = new Gson().fromJson(elem, PictureModel.class);
                    boolean isContain = false;
                    for (PictureModel model : mPictureModels) {
                        if (model.hashCode() == tmpModel.hashCode() || model.getName().equals(tmpModel.getName())) {
                            isContain = true;
                        }
                    }
                    if (!isContain) mPictureModels.add(tmpModel);
                }
                mPresenter.save(mPictureModels);
                ToastUtil.show("照片配置导入成功！");
            }
            return true;
        } else if (4 == itemId) {
            // 导出
            DialogUtil.showDialog(getContext(),
                    "提示", "\n是否导出照片配置？", (dialog, which) -> {
                        if (mPictureModels != null) {
                            String jsonString = new Gson().toJson(mPictureModels);
                            ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            // 将文本内容放到系统剪贴板里。
                            cm.setText(jsonString);
                            ToastUtil.show("照片信息导出到粘贴板成功！");
                        }
                    });
            return true;
        }
        return super.onMoreItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        PictureModel model = mPictureModels.get(position);

        Bundle data = new Bundle();
        data.putSerializable(XConstant.Key.DATA, model);

        setResult(Activity.RESULT_OK, data);
        dismiss();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        DialogUtil.showDialog(getContext(),
                "提示", "\n是否删除该照片信息?", (dialog, which) -> {
                    // 删除信息并保存
                    mPictureModels.remove(position);
                    mPresenter.save(mPictureModels);
                });
        return true;
    }

    @Override
    public void onLoad(List<PictureModel> models) {

        mPictureModels.clear();

        if (CollectionUtil.isNotEmpty(models)) {
            mPictureModels.addAll(models);
        }

        mAdapter.setItems(mPictureModels);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadFailed(String msg) {
        showMessage(msg);
    }

    @Override
    public void onAdd(PictureModel model) {

        mPictureModels.add(model);
        mPresenter.save(mPictureModels);

        mAdapter.setItems(mPictureModels);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAddFailed(String msg) {
        showMessage(msg);
    }

    @Override
    public void onSaveSucceed() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveFailed(String msg) {
        showMessage(msg);
    }

    private boolean checkPermission() {
        //需要获取读取图片权限 存储权限
        if (PermissionUtil.checkPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && PermissionUtil.checkPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            PermissionUtil.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    99);
            return false;
        }
        return true;
    }
}
