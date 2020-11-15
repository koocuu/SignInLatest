package com.jxchexie.signin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.StringUtils;
import com.gc.materialdesign.views.ButtonRectangle;
import com.jxchexie.api.Api;
import com.jxchexie.bean.ResponseLogin;
import com.jxchexie.utils.Code;
import com.jxchexie.utils.Constant;
import com.jxchexie.utils.JsonUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private String realCode;
    private ButtonRectangle btn_register;
    private MaterialEditText username_register;
    private MaterialEditText password_register;
    private MaterialEditText code_register;
    private ImageView varify_Code;
    private String str_username;
    private String str_Code;
    private String str_password;
    private MaterialDialog md;
    private ResponseLogin login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        //将验证码用图片形式显示
        varify_Code.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
        initEvent();
    }




    private void initView(){
        btn_register=(ButtonRectangle)findViewById(R.id.btn_register);
        username_register=(MaterialEditText)findViewById(R.id.username_register);
        password_register=(MaterialEditText)findViewById(R.id.password_register);
        varify_Code = (ImageView)findViewById(R.id.mCode);
        code_register = (MaterialEditText)findViewById(R.id.input_code);
    }
    private void initEvent(){
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser();
            }
        });
        varify_Code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                varify_Code.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
            }
        });
    }
    /*用户名密码校验*/
    public void checkUser(){
        /*判断网络是否连接*/
        if(!NetworkUtils.isConnected(RegisterActivity.this)){
            Toast.makeText(RegisterActivity.this,"网络未连接...",Toast.LENGTH_SHORT).show();
            return;
        }
        str_username=username_register.getText().toString();
        str_password=password_register.getText().toString();
        str_Code = code_register.getText().toString().toLowerCase();
        if(StringUtils.isSpace(str_username)){
            Toast.makeText(RegisterActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtils.isSpace(str_password)){
            Toast.makeText(RegisterActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtils.isSpace(str_Code)){
            Toast.makeText(RegisterActivity.this,"验证码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!str_Code.equals(realCode)){


            Toast.makeText(RegisterActivity.this, "验证码错误,请重新输入", Toast.LENGTH_SHORT).show();
            return;

        }
        else {

            /*显示提示正在登录对话框*/
            md=new MaterialDialog.Builder(this)
                    .title("提示")
                    .content("注册中，请稍后...")
                    .progress(true, 0)
                    .show();

            /*构造请求体*/
            HashMap<String, String> params = new HashMap<>();
            params.put("username", str_username);
            params.put("password", str_password);
            JSONObject jsonObject = new JSONObject(params);
            /*发送登录请求*/
            OkGo.post(Api.REGISTER)//
                    .tag(this)//
                    .upJson(jsonObject.toString())//
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            /*关闭提示框*/
                            login=new ResponseLogin();
                            login= JsonUtils.fromJson(s,ResponseLogin.class);
                            md.dismiss();
                            if(login.getStatus().equals(Constant.SUCCESS)){
                                RegisterActivity.this.finish();
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            }

                            else{

                                if(login.getMsg().equals(Constant.ERROR_SYSTEM)){
                                    Toast.makeText(RegisterActivity.this,"系统错误",Toast.LENGTH_SHORT).show();
                                    return;
                                }if(login.getMsg().equals(Constant.ERROR_USER_EXIST)){

                                        Toast.makeText(RegisterActivity.this, "用户名已被注册", Toast.LENGTH_SHORT).show();
                                        return;

                                }
                            }
                        }




                });






    }
}}
