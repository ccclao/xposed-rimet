package com.sky.xposed.rimet.plugin;

import android.hardware.Camera;
import android.net.wifi.WifiManager;

import com.sky.xposed.common.util.Alog;
import com.sky.xposed.core.interfaces.XCoreManager;
import com.sky.xposed.rimet.XConstant;
import com.sky.xposed.rimet.plugin.base.BaseDingPlugin;

import de.robv.android.xposed.XC_MethodHook;

/***
 **  @author: JiChao
 **  @date: 2024/8/15 9:05
 **  @desc: 粘片模块
 */
public class PicturePlugin extends BaseDingPlugin {

    public PicturePlugin(XCoreManager coreManager) {
        super(coreManager);
    }

    @Override
    public void hook() {
        Alog.d(this.getClass().getName(), " Loading and init pugin....");
        findMethod(Camera.class, "takePicture", Camera.ShutterCallback.class, Camera.PictureCallback.class, Camera.PictureCallback.class)
                .before(this::handlerTakePictureResults);

        findMethod(Camera.class, "takePicture", Camera.ShutterCallback.class, Camera.PictureCallback.class, Camera.PictureCallback.class, Camera.PictureCallback.class)
                .before(this::handlerTakePictureResults);
    }


    private void handlerTakePictureResults(XC_MethodHook.MethodHookParam param) throws Exception {
        if (isEnable(XConstant.Key.ENABLE_VIRTUAL_PICTURE)) {
            // 只在照片替换开启才处理
//            param.setResult(true);
            Alog.d(this.getClass().getName(), "handlerTakePictureResults");
        }
    }
}
