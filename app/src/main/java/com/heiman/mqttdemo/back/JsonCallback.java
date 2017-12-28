package com.heiman.mqttdemo.back;

import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.base.Request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * @Author : 肖力 by mac
 * @Time :  2017/9/2 下午2:30
 * @Description :
 * @Modify record :
 */
public abstract class JsonCallback<T> extends AbsCallback<T> {

    private Type type;
    private Class<T> clazz;
    public JsonCallback(Type type) {
        this.type = type;
    }

    public JsonCallback() {
        this.clazz = clazz;
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        // 主要用于在所有请求之前添加公共的请求头或请求参数
        // 例如登录授权的 token
        // 使用的设备信息
        // 可以随意添加,也可以什么都不传
        // 还可以在这里对所有的参数进行加密，均在这里实现
//        TelephonyManager tm = (TelephonyManager) context
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        request.params("user_email", RemotebleHttpManage.getInstance().user_email)
//                .params("user_pwd", RemotebleHttpManage.getInstance().user_pwd)
//                .params("project_code", RemotebleHttpManage.getInstance().project_code)
//                .params("pa_key", RemotebleHttpManage.getInstance().pa_key)
//                .params("customer_code", RemotebleHttpManage.getInstance().customer_code)
//                .params("imei", tm.getDeviceId());


    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertResponse(Response response) throws Throwable {

        if (type == null) {
            if (clazz == null) {
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                JsonConvert<T> convert = new JsonConvert<>(clazz);
                return convert.convertResponse(response);
            }
        }

        JsonConvert<T> convert = new JsonConvert<>(type);
        return convert.convertResponse(response);
    }
}

