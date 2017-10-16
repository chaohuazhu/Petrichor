package work.petrichor.petrichor;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import github.mitscherlich.jpack.utils.GsonUtil;
import work.petrichor.petrichor.utils.helper.HttpHelper;
import work.petrichor.petrichor.utils.helper.SharedHelper;

public class MainActivity extends AppCompatActivity {

    public static final int USER_SIGN_UP_ACTION = 200;
    public static final int USER_SIGN_IN_ACTION = 201;

    private static final String TAG = "MainActivity";

    private String token = "";
    private boolean usingFaceLogin = false;

    //----------------------------------------------------------------------------------------------
    // UI component
    //----------------------------------------------------------------------------------------------

    // 布局控件
    private LinearLayout signInGroup;
    private LinearLayout signUpGroup;
    private LinearLayout signInPasswordRow;

    // 输入控件
    private EditText signInUserName;
    private EditText signInPassword;
    private EditText signUpUserName;
    private EditText signUpPassword;

    // 复选框
    private CheckBox cbUsingFaceLogin;

    // 用来处理网络回调的 handler
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            HttpHelper.NetworkResult result = GsonUtil.fromJson((String) msg.obj, HttpHelper.NetworkResult.class);
            switch (msg.what) {
                case USER_SIGN_IN_ACTION: {
                    if (result.getResult()) {
                        // 注册成功，将用户名和标识符写入本地缓存
                        makeToast("登录成功").show();
                        SharedHelper.put(getApplicationContext(), "token", result.getMessage());
                    } else {
                        if (result.getCode() == HttpHelper.NetworkResult.ERR_SERVER_FAIL) {
                            makeToast("登录失败").show();
                            signInUserName.setEnabled(true);
                            signInPassword.setEnabled(true);
                        } else if (result.getCode() == HttpHelper.NetworkResult.ERR_USER_NOT_EXIST) {
                            makeToast("用户未注册").show();
                            showSignUp(null);
                        } else if (result.getCode() == HttpHelper.NetworkResult.ERR_PSK_IS_NOT_SAME) {
                            makeToast("密码不正确").show();
                            signInUserName.setEnabled(true);
                            signInPassword.setEnabled(true);
                        }
                    }
                } break;
                case USER_SIGN_UP_ACTION: {
                    if (result.getResult()) {
                        // 注册成功，将用户 `TOKEN` 写入本地文件缓存
                        makeToast("注册成功").show();
                        SharedHelper.put(getApplicationContext(), "token", result.getMessage());
                        showSignIn(null);
                    } else {
                        if (result.getCode() == HttpHelper.NetworkResult.ERR_SERVER_FAIL)
                            makeToast("注册失败").show();
                        else if (result.getCode() == HttpHelper.NetworkResult.ERR_USER_EXIST) {
                            makeToast("用户已注册").show();
                            showSignIn(null);
                        }
                    }
                } break;
                default: makeToast("服务器提了一个问题").show(); break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图组件
        signInGroup = findViewById(R.id.groupSignIn);
        signUpGroup = findViewById(R.id.groupSignUp);
        signInUserName = findViewById(R.id.signInUserName);
        signInPassword = findViewById(R.id.signInPassword);
        signUpUserName = findViewById(R.id.signUpUserName);
        signUpPassword = findViewById(R.id.signUpPassword);
        signInPasswordRow = findViewById(R.id.rowPassword);
        cbUsingFaceLogin = findViewById(R.id.cbUsingFaceLogin);

        // 添加 `checkbox` 监听事件
        cbUsingFaceLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) signInPasswordRow.setVisibility(View.GONE);
                else signInPasswordRow.setVisibility(View.VISIBLE);

                usingFaceLogin = isChecked;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 从本地缓存文件中加载登录用户的 `token`
        String token = (String) SharedHelper.get(getApplicationContext(), "token", "");
        if (token != null && !token.isEmpty())
            this.token = token;

        // 从本地缓存文件中加载缓存的用户名
        String name = (String) SharedHelper.get(getApplicationContext(), "name", "");
        if (!(name == null || name.isEmpty()))
            signInUserName.setText(name);

        boolean is = cbUsingFaceLogin.isChecked();
        if (is) signInPasswordRow.setVisibility(View.GONE);
        else signInPasswordRow.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 判断返回的 `data` 是否包含数据
        if (null == data || null ==  data.getExtras()) return;

        String imageBase = "";

        try {
            imageBase = data.getExtras().getString("image_base");
            if (null == imageBase || imageBase.isEmpty())
                return;
        } catch (Exception e) {
            Log.e(TAG, "Intent result receive error!\n" + e);
        }

        switch (requestCode) {
            case USER_SIGN_IN_ACTION:
                if (usingFaceLogin)
                    // 使用人脸登录时，只需提供用户名和人脸数据
                    signIn(imageBase);
                else
                    signIn();
                break;
            case USER_SIGN_UP_ACTION:
                signUp(imageBase);
                break;
            default: break;
        }
    }

    /**
     * 登录
     * @param view  视图
     * **/
    public void doSignIn(View view) {
        String name = signInUserName.getText().toString();
        if (name.isEmpty()) {
            makeToast(R.string.no_empty_name).show();
            return;
        }

        if (usingFaceLogin)
            startActivityForResult(new Intent(this, FaceDetectionActivity.class), USER_SIGN_IN_ACTION);
        else {
            String password = signInPassword.getText().toString();
            if (password.isEmpty()) {
                makeToast(R.string.no_empty_psk).show();
                return;
            }
            signIn();
        }
    }

    /**
     * 注册
     * @param view  视图
     * **/
    public void doSignUp(View view) {
        String name = signUpUserName.getText().toString();
        if (name.isEmpty()) {
            makeToast(R.string.no_empty_name).show();
            return;
        }

        String password = signUpPassword.getText().toString();
        if (password.isEmpty()) {
            makeToast(R.string.no_empty_psk).show();
            return;
        }

        startActivityForResult(new Intent(this, JumpActivity.class), USER_SIGN_UP_ACTION);
    }

    /**
     * 显示注册框
     * @param view  视图
     * **/
    public void showSignUp(View view) {
        signInGroup.setVisibility(View.GONE);
        signUpGroup.setVisibility(View.VISIBLE);

        // 清空上一次输入
        signUpUserName.setText(R.string.empty_string);
        signUpPassword.setText(R.string.empty_string);
    }

    /**
     * 显示登录框
     * @param view  视图
     * **/
    public void showSignIn(View view) {
        signUpGroup.setVisibility(View.GONE);
        signInGroup.setVisibility(View.VISIBLE);
        signInUserName.setEnabled(true);
        signInPassword.setEnabled(true);

        String name = (String) SharedHelper.get(getApplicationContext(), "name", "");

        if (!(name == null || name.isEmpty()))
            signInUserName.setText(name);
    }

    private Toast makeToast(@StringRes int resId) {
        return Toast.makeText(getApplicationContext(), getApplication().getText(resId), Toast.LENGTH_LONG);
    }

    /**
     * 显示 Toast
     * @param   message 要显示的文本
     * **/
    private Toast makeToast(String message) {
        return Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
    }

    /**
     * 登录
     * {@link #signIn(String)}
     * **/
    private void signIn() {
        String name = signInUserName.getText().toString();
        String password = signInPassword.getText().toString();

        signInUserName.setEnabled(false);
        signInPassword.setEnabled(false);

        String url = "http://192.168.1.20:8080/user/signin";
        Map<String, String> params = new HashMap<>();

        SharedHelper.put(getApplicationContext(), "name", name);

        params.put("name", name);
        params.put("password", password);

        new HttpHelper(url, params).post(mHandler, USER_SIGN_IN_ACTION);
    }

    /**
     * 登录
     * @param imageBase Base64 编码的图像数据
     * **/
    private void signIn(String imageBase) {
        String name = signInUserName.getText().toString();

        signInUserName.setEnabled(false);
        signInPassword.setEnabled(false);

        String url = "http://192.168.1.20:8080/user/signin";
        Map<String, String> params = new HashMap<>();

        SharedHelper.put(getApplicationContext(), "name", name);

        params.put("name", name);
        params.put("image", imageBase);

        new HttpHelper(url, params).post(mHandler, USER_SIGN_IN_ACTION);
    }

    private void signUp(String imageBase) {
        String url = "http://192.168.1.20:8080/user/signup";
        Map<String, String> params = new HashMap<>();

        String name = signUpUserName.getText().toString();
        String password = signUpPassword.getText().toString();
        SharedHelper.put(getApplicationContext(), "name", name);

        params.put("name", name);
        params.put("password", password);
        params.put("image", imageBase);

        new HttpHelper(url, params).post(mHandler, USER_SIGN_UP_ACTION);
    }
}
