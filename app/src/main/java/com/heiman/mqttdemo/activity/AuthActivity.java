package com.heiman.mqttdemo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.heiman.mqttdemo.Constant;
import com.heiman.mqttdemo.HmApplication;
import com.heiman.mqttdemo.R;
import com.heiman.mqttdemo.back.Dialogback;
import com.heiman.mqttdemo.base.Code;
import com.heiman.mqttdemo.base.Login;
import com.heiman.mqttdemo.util.SharedPreferencesUtil;
import com.heiman.mqttsdk.http.HmHttpManage;
import com.heiman.utils.HmUtils;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

/**
 * @Author : 肖力 by mac
 * @Time :  2017/12/12 上午8:50
 * @Description :
 * @Modify record :
 */
public class AuthActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        onLogin();
    }

    private void onLogin() {
        String email = SharedPreferencesUtil.queryValue(Constant.SAVE_EMAIL_ID);
        String passwprd = SharedPreferencesUtil.queryValue(Constant.SAVE_PASSWPRD_ID);
        if (HmUtils.isEmptyString(email) && HmUtils.isEmptyString(passwprd)) {
            setAccess("没有注册账户!");
        }
        HmHttpManage.getInstance().onLogin(email, passwprd, new Dialogback<Login>(this) {
            @Override
            public void onSuccess(Response<Login> response) {
                Logger.i(response.message());
                Logger.i(response.body().toString());
                Logger.i(response.code() + "");
                Logger.i(response.body().getAccess_token());
                if (HmUtils.isEmptyString(response.body().getAccess_token())) {
                    setAccess("账户或密码错误");
                    return;
                }
                HmApplication.setLogin(response.body());
                HmHttpManage.setAccessToken(response.body().getAccess_token());
                HmHttpManage.setUserId(response.body().getUserId() + "");

                openActivity(MainActivity.class);
                finish();
            }

            @Override
            public void onError(Response<Login> response) {

                if (response != null) {
                    Logger.i(response.message());
//                    if (HmUtils.isEmptyString(response.body().toString())) {
//                    Logger.i(response.body().toString());
//                    }
                    Logger.i(response.code() + "");
                    Logger.i(response.getException() + "");
                    Toast.makeText(AuthActivity.this, response.getException().toString(), Toast.LENGTH_SHORT).show();
                }
                setAccess("账户或密码错误");
            }
        });
    }

    private void setAccess(String msg) {
        View view = LayoutInflater.from(this).inflate(R.layout.set_accessid,
                null);
        ((TextView) view.findViewById(R.id.set_accessid_title))
                .setText(msg);
        final EditText edit_company_id = view.findViewById(R.id.edit_company_id);
        final EditText edit_email = view.findViewById(R.id.edit_email);
        final EditText edit_passwd = view.findViewById(R.id.edit_passwd);
        edit_company_id.setText("1");
        edit_passwd.setText(SharedPreferencesUtil.queryValue(Constant.SAVE_PASSWPRD_ID));
        edit_email.setText(SharedPreferencesUtil.queryValue(Constant.SAVE_EMAIL_ID));
        new AlertDialog.Builder(this)
                .setView(view)
                .setNegativeButton(getResources().getString(R.string.Determine), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String compayId = edit_company_id.getText().toString().trim();
                        String mail = edit_email.getText().toString().trim();
                        String passwd = edit_passwd.getText().toString().trim();
                        SharedPreferencesUtil.keepShared(Constant.SAVE_COMPANY_ID, compayId);
                        SharedPreferencesUtil.keepShared(Constant.SAVE_EMAIL_ID, mail);
                        SharedPreferencesUtil.keepShared(Constant.SAVE_PASSWPRD_ID, passwd);
                        registerUser();
                    }
                })
                .setNeutralButton(getResources().getString(R.string.Signout), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AuthActivity.this.finish();

                    }
                }).setCancelable(false).show();
    }

    private void registerUser() {
        registerUserByMail(SharedPreferencesUtil.queryValue(Constant.SAVE_EMAIL_ID), SharedPreferencesUtil.queryValue(Constant.SAVE_PASSWPRD_ID), false);
    }

    private void registerUserByMail(String uid, String pwd, boolean isActivation) {
        HmHttpManage.getInstance().onRegister(uid, pwd, isActivation, new Dialogback<Code<Object>>(this) {
            @Override
            public void onSuccess(Response<Code<Object>> response) {
                Logger.i(response.message());
                Logger.i(response.body().toString());
                Logger.i(response.code() + "");
                onLogin();
            }

            @Override
            public void onError(Response<Code<Object>> response) {
                Logger.i(response.message());
//                Logger.i(response.body());
                Logger.i(response.code() + "");
                Logger.i(response.getException() + "");
                Toast.makeText(AuthActivity.this, response.getException().toString(), Toast.LENGTH_SHORT).show();
                if ("java.lang.IllegalStateException: Email/cell phone has been registered".equals(response.getException().toString())) {
                    onLogin();
                    return;
                }
                setAccess("你输入的账号有毛病");
            }
        });
    }

}
