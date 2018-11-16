package es.source.code.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.regex.Pattern;

import es.source.code.R;
import es.source.code.callback.OnDataFinishedListener;
import es.source.code.gson.ResultM;
import es.source.code.model.User;
import es.source.code.AppGlobal;
import es.source.code.util.HttpConnectionUtil;
import es.source.code.util.SharedPreferenceUtil;


public class LoginOrRegister extends BaseActivity {

    private static final Pattern pattern = Pattern.compile(AppGlobal.REGEX_ACCOUNT);
    private SharedPreferenceUtil sharedPreferenceUtil;
    private User user;
    private EditText Name_tx;
    private EditText Password_tx;
    private Button Login_btn;
    private Button Register_btn;
    private Button Back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);
        initData();
    }

    private void initData(){
        Name_tx = (EditText)findViewById(R.id.name);
        Password_tx = (EditText)findViewById(R.id.password);
        Login_btn = (Button)findViewById(R.id.btn_login);
        Register_btn = (Button)findViewById(R.id.btn_register);
        Back_btn = (Button)findViewById(R.id.btn_back);

        sharedPreferenceUtil = SharedPreferenceUtil.getInstance(mContext);
        int loginStatus=sharedPreferenceUtil.getLoginStatus();
        user = sharedPreferenceUtil.getUser();
        if(user!=null && user.getUserName()!=null){
            Name_tx.setText(user.getUserName());
            Password_tx.requestFocus();
            Login_btn.setVisibility(View.VISIBLE);Register_btn.setVisibility(View.GONE);
        } else {
            Login_btn.setVisibility(View.VISIBLE);
            Register_btn.setVisibility(View.VISIBLE);
        }
        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        Register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });
        Back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBack();
            }
        });
    }



    @Override
    public void onBackPressed() {
        doBack();
    }

    private void doLogin(){
        final String name = Name_tx.getText().toString().trim();
        final String password = Password_tx.getText().toString().trim();
        showProgress(R.string.dialog_login);

        if(isValid(name,password)){
            String url = getString(R.string.login_or_register_url)
                    +"?username="+name+"&mypassword="+password+"&loginOrRegister=login";
            HttpConnectionUtil httpConnectionUtil = HttpConnectionUtil.getInstance(mContext);
            httpConnectionUtil.sendMessageByPost(url);
            httpConnectionUtil.setOnDataFinishedListener(new OnDataFinishedListener() {
                @Override
                public void onDataSuccess(Object t) {
                    dismissProgress();
                    String url = (String)t;
                    System.out.println("doLogin:1: onDataSuccess: "+url);
                    ResultM resultM = new Gson().fromJson(url,ResultM.class);
                    int resultcode = resultM.resultcode;
                    String mess = resultM.message;
                    System.out.println("doLogin:2: onDataSuccess: "+mess);
                    if(resultcode==1){
                        User loginUser = new User(name,password,true);
                        user = loginUser;
                        sharedPreferenceUtil = SharedPreferenceUtil.getInstance(mContext);
                        sharedPreferenceUtil.setUser(loginUser);
                        //仅仅起传值的作用
                        sharedPreferenceUtil.setLoginStatus(AppGlobal.SPValue.LOGIN_SUCCESS);
                        Intent intent = new Intent(mContext,MainScreen.class);
                        intent.putExtra(AppGlobal.IntentKey.LOGIN_STATUS, AppGlobal.IntentValue.LOGIN_SUCCESS);
                        setResult(AppGlobal.ResultCode.LOGIN_OR_REGISTER,intent);
                        finish();
                    }else{
                        showToast(" "+mess);
                    }
                }
                @Override
                public void onDataFail() {
                    dismissProgress();
                    showToast("网络请求失败");
                }
            });




        }else{
            dismissProgress();
            showToast(R.string.toast_account_not_valid);
        }
    }

    private void doRegister(){
        final String name = Name_tx.getText().toString().trim();
        final String password = Password_tx.getText().toString().trim();
        showProgress(R.string.dialog_register);
        if(isValid(name,password)){
            String url = getString(R.string.login_or_register_url)+"?username="+name+"&mypassword="+password+"&loginOrRegister=register";
            HttpConnectionUtil httpConnectionUtil = HttpConnectionUtil.getInstance(mContext);
            httpConnectionUtil.sendMessageByPost(url);
            httpConnectionUtil.setOnDataFinishedListener(new OnDataFinishedListener() {
                @Override
                public void onDataSuccess(Object t) {
                    dismissProgress();
                    String url = (String)t;
                    System.out.println("doRegister: onDataSuccess: "+url);
                    ResultM resultM = new Gson().fromJson(url,ResultM.class);
                    int resultcode = resultM.resultcode;
                    String mess = resultM.message;
                    System.out.println("doRegister: onDataSuccess: "+mess);
                    if(resultcode==1){
                        User loginUser = new User(name,password,false);
                        user = loginUser;
                        sharedPreferenceUtil = SharedPreferenceUtil.getInstance(mContext);
                        sharedPreferenceUtil.setUser(loginUser);
                        //仅仅起传值的作用
                        sharedPreferenceUtil.setLoginStatus(AppGlobal.SPValue.LOGIN_SUCCESS);
                        Intent intent = new Intent(mContext,MainScreen.class);
                        intent.putExtra(AppGlobal.IntentKey.LOGIN_STATUS, AppGlobal.IntentValue.LOGIN_SUCCESS);
                        setResult(AppGlobal.ResultCode.LOGIN_OR_REGISTER,intent);
                        finish();
                    }else{
                        showToast(" "+mess);
                    }
                }

                @Override
                public void onDataFail() {
                    dismissProgress();
                    showToast("网络请求失败");
                }
            });


        }else{
            dismissProgress();
            showToast(R.string.toast_account_not_valid);
        }
    }

    private boolean isValid(String name,String password){
        return pattern.matcher(name).matches()&&pattern.matcher(password).matches();
    }

    private void doBack(){
        Intent intent = new Intent(mContext, MainScreen.class);
        intent.putExtra(AppGlobal.IntentKey.LOGIN_STATUS, AppGlobal.IntentValue.LOGIN_RETURN);
        setResult(AppGlobal.ResultCode.LOGIN_OR_REGISTER, intent);
        finish();
    }


}
