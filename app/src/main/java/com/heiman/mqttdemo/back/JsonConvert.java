package com.heiman.mqttdemo.back;

import com.google.gson.stream.JsonReader;
import com.heiman.mqttdemo.base.Code;
import com.heiman.mqttdemo.base.SimpleResponse;
import com.heiman.mqttsdk.http.HmHttpConstant;
import com.heiman.mqttsdk.http.HmHttpManage;
import com.heiman.utils.Convert;
import com.lzy.okgo.convert.Converter;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @Author : 肖力 by mac
 * @Time :  2017/9/2 下午2:31
 * @Description :
 * @Modify record :
 */
public class JsonConvert<T> implements Converter<T> {

    private Type type;
    private Class<T> clazz;

    public JsonConvert(Type type) {
        this.type = type;
    }

    public JsonConvert(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象，生成onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertResponse(Response response) throws Throwable {

        if (type == null) {
            if (clazz == null) {
                // 如果没有通过构造函数传进来，就自动解析父类泛型的真实类型（有局限性，继承后就无法解析到）
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                return parseClass(response, clazz);
            }
        }

        if (type instanceof ParameterizedType) {
            return parseParameterizedType(response, (ParameterizedType) type);
        } else if (type instanceof Class) {
            return parseClass(response, (Class<?>) type);
        } else {
            return parseType(response, type);
        }
    }

    private T parseClass(Response response, Class<?> rawType) throws Exception {
        if (rawType == null) return null;
        ResponseBody body = response.body();
        if (body == null) return null;
        JsonReader jsonReader = new JsonReader(body.charStream());

        if (rawType == String.class) {
            //noinspection unchecked
            return (T) body.string();
        } else if (rawType == JSONObject.class) {
            //noinspection unchecked
            return (T) new JSONObject(body.string());
        } else if (rawType == JSONArray.class) {
            //noinspection unchecked
            return (T) new JSONArray(body.string());
        } else {
            T t = Convert.fromJson(jsonReader, rawType);
            response.close();
            return t;
        }
    }

    private T parseType(Response response, Type type) throws Exception {
        if (type == null) return null;
        ResponseBody body = response.body();
        if (body == null) return null;
        JsonReader jsonReader = new JsonReader(body.charStream());

        // 泛型格式如下： new JsonCallback<任意JavaBean>(this)
        T t = Convert.fromJson(jsonReader, type);
        response.close();
        return t;
    }

    private T parseParameterizedType(Response response, ParameterizedType type) throws Exception {
        if (type == null) return null;
        ResponseBody body = response.body();
        if (body == null) return null;
        JsonReader jsonReader = new JsonReader(body.charStream());

        Type rawType = type.getRawType();                     // 泛型的实际类型
        Type typeArgument = type.getActualTypeArguments()[0]; // 泛型的参数
        if (rawType != Code.class) {
            // 泛型格式如下： new JsonCallback<外层BaseBean<内层JavaBean>>(this)
            T t = Convert.fromJson(jsonReader, type);
            response.close();
            return t;
        } else {
            if (typeArgument == Void.class) {
                // 泛型格式如下：
                SimpleResponse simpleResponse = Convert.fromJson(jsonReader, SimpleResponse.class);
                response.close();
                //noinspection unchecked
                return (T) simpleResponse.toLzyResponse();
            } else {
                // 泛型格式如下： new JsonCallback<LzyResponse<内层JavaBean>>(this)
                Code httpCode = Convert.fromJson(jsonReader, type);
                response.close();
                //这里的0是以下意思
                //一般来说服务器会和客户端约定一个数表示成功，其余的表示失败，这里根据实际情况修改
                //直接将服务端的错误信息抛出，onError中可以获取
                int code = httpCode.code;
                switch (code) {
                    case HmHttpConstant.SUCCESS:
                    case 0:
                        //noinspection unchecked
                        return (T) httpCode;
                    case HmHttpConstant.ERROR_434626:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434627:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434628:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434629:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434630:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434631:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434632:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434633:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434634:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434635:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434636:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434637:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434638:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434639:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434640:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434641:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434642:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434643:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434644:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434645:
                        throw new IllegalStateException(httpCode.msg);
                    case HmHttpConstant.ERROR_434646:
                        throw new IllegalStateException(httpCode.msg);
                    default:
                        Logger.e("错误代码：" + code + "，错误信息：" + httpCode.msg);
                        throw new IllegalStateException("错误代码：" + code + "，错误信息：" + httpCode.msg);
                }
            }
        }
    }
}

