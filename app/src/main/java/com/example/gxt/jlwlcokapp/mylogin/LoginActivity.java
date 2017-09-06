package com.example.gxt.jlwlcokapp.mylogin;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gxt.jlwlcokapp.Main2Activity;
import com.example.gxt.jlwlcokapp.MainActivity;
import com.example.gxt.jlwlcokapp.R;
import com.example.gxt.jlwlcokapp.web.WebService;
import com.example.gxt.jlwlcokapp.web.WebServicePost;

//import com.example.web.WebServicePost;


public class LoginActivity extends Activity implements View.OnClickListener {
    // 登陆按钮
    private Button logbtn;
    private Button loginBtn;
    //private Button registerBtn;
    // 调试文本，注册文本
    private TextView infotv, regtv;
    // 显示用户名和密码
    private EditText username = null;
    private EditText password = null;
    private Spinner usertype;
    // 创建记住密码 checkbox
    private CheckBox checkBoxLogin = null;
    SharedPreferences sp = null;
    // 创建等待框
    private ProgressDialog dialog;
    // 返回的数据
    private String info;
    private String bname;
    private String building;
    private boolean flage = true;
    // 返回主线程更新数据
    private static Handler handler = new Handler();
    private Dialog mDialog;

    String[] usertp = new String[]{"学生", "老师", "管理"};
    private String[] ustp = new String[]{"0"};

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.accountEdittext);
        password = (EditText) findViewById(R.id.pwdEdittext);
        usertype = (Spinner) findViewById(R.id.usertypeSprinner);
        loginBtn = (Button) findViewById(R.id.login_in);
        infotv = (TextView) findViewById(R.id.info);
        usertype.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usertp));
        usertype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ustp[0] = usertp[i];
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp = this.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        init();

    }

    @Override
    public void onClick(View view) {
        // TODO 自动生成的方法存根
        // 检测网络，无法检测wife
        if (!checkNetwork()) {
            Toast toast = Toast.makeText(LoginActivity.this, "网络未连接", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            flage = false;
        }
        if (view == loginBtn) {
            String usnm = username.getText().toString();
            String pswd = password.getText().toString();

            String ustp1 = ustp[0];

            if (usnm.trim().equals("")) {
                Toast.makeText(LoginActivity.this, "请您输入账号！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pswd.trim().equals("")) {
                Toast.makeText(LoginActivity.this, "请您输入密码！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ustp1.trim().equals("")) {
                Toast.makeText(LoginActivity.this, "请您选择用户类型！", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean CheckBoxLogin = checkBoxLogin.isChecked();
            if (CheckBoxLogin) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("username", usnm);
                editor.putString("password", pswd);
                editor.putString("usertype", ustp1);
                editor.putBoolean("checkboxBoolean", true);
                editor.commit();
            }
            else {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("username", null);
                editor.putString("password", null);
                editor.putString("usertype", null);
                editor.putBoolean("checkboxBoolean", false);
                editor.commit();
            }
        }
        if (flage == true) {
            // 提示框
            //dialog = new ProgressDialog(MainActivity.this);
            showRoundProcessDialog(LoginActivity.this, R.layout.loading_process_dialog_anim);
            // 创建子线程，分别进行Get和Post传输
            new Thread(new MyThread()).start();
        }
    }

    // 子线程接收数据，主线程修改数据
    public String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    // 子线程接收数据，主线程修改数据
    public class MyThread implements Runnable {
        @Override
        public void run() {
            info = WebService.executeHttpGet(username.getText().toString(), password.getText().toString(), "HelloServlet");
            bname = WebServicePost.executeHttpPost(username.getText().toString(), password.getText().toString(),"RoomServlet"," ");
            building = WebServicePost.executeHttpPost(username.getText( ).toString(),password.getText().toString(),"JsonServlet","申请理由");

            handler.post(new Runnable() {
                @Override
                public void run() {
                    //dialog.dismiss();
                    infotv.setText(info);
                    // dialog.setMessage(info);
                    if (info.equals("学生") && info.equals(ustp[0])) {
                        Intent LogItn = new Intent();
                        LogItn.putExtra("identity", info);
                        LogItn.putExtra("bname", bname);
                        LogItn.putExtra( "usr",username.getText().toString() );
                        LogItn.putExtra( "building",building );
                        LogItn.setClass(LoginActivity.this, MainActivity.class);
                        mDialog.dismiss();
                        startActivity(LogItn);
                    }
                    else if (info.equals("老师") && info.equals(ustp[0])) {
                        Intent LogItn = new Intent();
                        LogItn.putExtra("identity", info);
                        LogItn.putExtra("bname", bname);
                        LogItn.putExtra( "usr",username.getText().toString() );
                        LogItn.putExtra( "building",building );
                        LogItn.setClass(LoginActivity.this, MainActivity.class);
                        mDialog.dismiss();
                        startActivity(LogItn);
                    }
                    else if (info.equals("管理") && info.equals(ustp[0])) {
                        Intent LogItn = new Intent();
                        LogItn.putExtra("identity", info);
                        LogItn.putExtra("bname", bname);
                        LogItn.putExtra( "usr",username.getText().toString() );
                        LogItn.putExtra( "building",building );
                        LogItn.setClass(LoginActivity.this, Main2Activity.class);
                        mDialog.dismiss();
                        startActivity(LogItn);
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "输入的信息有误，请确认信息重新登录！", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                        return;
                    }
                }
            });
        }
    }

    public void showRoundProcessDialog(Context mContext, int layout) {
        mDialog = new AlertDialog.Builder(mContext).create();
        mDialog.setTitle("提示");
        mDialog.show();
        // 注意此处要放在show之后 否则会报异常
        mDialog.setContentView(layout);
    }

    // 检测网络
    private boolean checkNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    public void init() {
        username = (EditText) findViewById(R.id.accountEdittext);
        password = (EditText) findViewById(R.id.pwdEdittext);
        usertype = (Spinner) findViewById(R.id.usertypeSprinner);
        checkBoxLogin = (CheckBox) findViewById(R.id.checkBoxLogin);
        loginBtn = (Button) findViewById(R.id.login_in);
        if (sp.getBoolean("checkboxBoolean", false)) {
            username.setText(sp.getString("username", null));
            password.setText(sp.getString("password", null));
            checkBoxLogin.setChecked(true);
        }
        loginBtn.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

/**
 * A activity_login screen that offers activity_login via account/password.
 */


