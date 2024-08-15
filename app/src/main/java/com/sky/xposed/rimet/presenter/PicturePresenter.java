package com.sky.xposed.rimet.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sky.xposed.common.util.ConversionUtil;
import com.sky.xposed.core.interfaces.XCoreManager;
import com.sky.xposed.core.interfaces.XPreferences;
import com.sky.xposed.rimet.contract.PictureContract;
import com.sky.xposed.rimet.contract.StationContract;
import com.sky.xposed.rimet.data.DataException;
import com.sky.xposed.rimet.data.model.PictureModel;
import com.sky.xposed.rimet.data.model.StationModel;
import com.sky.xposed.rimet.task.SimpleTask;
import com.sky.xposed.rimet.util.ExecutorUtil;

import java.util.ArrayList;
import java.util.List;

/***
 **  @author: JiChao
 **  @date: 2024/8/15 9:52
 **  @desc: 照片
 */
public class PicturePresenter extends AbstractPresenter implements PictureContract.Presenter {

    private static final String KEY = "picture_list";

    private PictureContract.View mView;
    private XPreferences mPreferences;
    private Gson mGson;


    public PicturePresenter(XCoreManager coreManager, PictureContract.View view) {
        super(coreManager);
        mView = view;
        mPreferences = coreManager.getDefaultPreferences();
        mGson = new Gson();
    }

    @Override
    public void load() {

        SimpleTask<String, List<PictureModel>> task =
                SimpleTask.createTask(this::loadStation);
        task.setCompleteCallback(models -> mView.onLoad(models));
        task.setThrowableCallback(tr -> mView.onLoadFailed("加载信息失败!"));
        task.executeOnExecutor(ExecutorUtil.getBackExecutor(), KEY);
    }

    @Override
    public void add(String name) {

        SimpleTask<String, PictureModel> task =
                SimpleTask.createTask(this::addStation);
        task.setBaseView(mView);
        task.setCompleteCallback(model -> mView.onAdd(model));
        task.setThrowableCallback(tr -> {
            if (tr instanceof DataException) {
                mView.onAddFailed(tr.getMessage());
            } else {
                mView.onAddFailed("添加信息失败!");
            }
        });
        task.executeOnExecutor(ExecutorUtil.getBackExecutor(), name);
    }

    @Override
    public void save(List<PictureModel> models) {

        SimpleTask<String, Void> task =
                SimpleTask.createTask(param -> {
                    saveStation(param, models);
                    return null;
                });
        task.setCompleteCallback(aVoid -> mView.onSaveSucceed());
        task.setThrowableCallback(tr -> mView.onLoadFailed("保存信息失败!"));
        task.executeOnExecutor(ExecutorUtil.getBackExecutor(), KEY);
    }

    private List<PictureModel> loadStation(String key) {

        String value = mPreferences.getString(key, "");

        if (TextUtils.isEmpty(value)) return new ArrayList<>();

        return mGson.fromJson(value,
                new TypeToken<List<PictureModel>>() {
                }.getType());
    }

    private void saveStation(String key, List<PictureModel> models) {

        if (models == null) return;

        // 保存信息
        mPreferences.putString(key, mGson.toJson(models));
    }

    private PictureModel addStation(String name) throws Exception {
        //todo:
        Thread.sleep(2000); // 给UI一个效果

        TelephonyManager telephonyManager = (TelephonyManager)
                getContext().getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager == null) {
            throw new DataException("获取基站信息失败!");
        }

        // 返回值MCC + MNC
        String operator = telephonyManager.getNetworkOperator();
        int mcc = ConversionUtil.parseInt(operator.substring(0, 3));
        int mnc = ConversionUtil.parseInt(operator.substring(3));

        /*
            * MNC
            中国移动：00、02、04、07
            中国联通：01、06、09
            中国电信：03、05、11
            中国铁通：20
        */
        @SuppressLint("MissingPermission")
        CellLocation cellLocation = telephonyManager.getCellLocation();

        if (cellLocation == null) {
            throw new DataException("获取基站信息失败!");
        }

        int lac;
        int cellId;

        // 中国移动和中国联通获取LAC、CID的方式
        if (mnc != 3 && mnc != 5 && mnc != 11) {
            @SuppressLint("MissingPermission")
            GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
            lac = gsmCellLocation.getLac();
            cellId = gsmCellLocation.getCid();
        } else {
            @SuppressLint("MissingPermission")
            CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cellLocation;
            lac = cdmaCellLocation.getNetworkId();
            cellId = cdmaCellLocation.getBaseStationId();
        }

        if (lac == -1 || cellId == -1) {
            throw new DataException("获取照片信息失败!");
        }
        return new PictureModel(name, name, name);
    }
}
