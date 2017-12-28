package com.heiman.mqttdemo.back;

import android.app.Activity;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.lzy.okgo.request.base.Request;
import com.orhanobut.logger.Logger;


/**
 * @Author : 肖力 by mac
 * @Time :  2017/9/2 下午2:30
 * @Description :
 * @Modify record :
 */
public abstract class Dialogback<T> extends JsonCallback<T> {
    private KProgressHUD hud = null;

    private void initDialog(Activity activity) {
        try {
            if (hud == null) {
                hud = KProgressHUD.create(activity)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).show();
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

    }

    protected Dialogback(Activity activity) {
        super();
        initDialog(activity);
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
//        Map<String, String> params = new HashMap<String, String>();
//        if (!SmartHomeUtils.isEmptyString(RemotebleHttpManage.getInstance().user_email)) {
//            params.put("user_email", RemotebleHttpManage.getInstance().user_email);
//        }
//        request.params(params);
        try {
            if (hud != null && !hud.isShowing()) {
                hud.show();
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }

    }

    @Override
    public void onFinish() {
        //网络请求结束后关闭对话框
        if (hud != null && hud.isShowing()) {
            try {
                hud.dismiss();
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }

        }
    }

    public void dismissHUMProgress() {
        if (hud != null) {
            try {
                hud.dismiss();
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
            hud = null;
        }
    }

}
