package cn.wearbbs.opener.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.wearbbs.opener.R;
import cn.wearbbs.opener.util.WiFiAdbUtil;

public class MainActivity extends AppCompatActivity {
    Boolean open = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(WiFiAdbUtil.isOpen()){
            TextView tv_ip = findViewById(R.id.tv_ip);
            tv_ip.setVisibility(View.VISIBLE);
            tv_ip.setText("adb connect " + WiFiAdbUtil.getIpAddress() + ":5555");
            Button btn_open = findViewById(R.id.btn_open);
            btn_open.setText("关闭");
            open = true;
        }
    }
    public void onClick(View view){
        if(!WiFiAdbUtil.isMobile(this)){
            if(WiFiAdbUtil.haveRoot()){
                if(open){
                    TextView tv_ip = findViewById(R.id.tv_ip);
                    tv_ip.setVisibility(View.GONE);
                    WiFiAdbUtil.closeAdb();
                    Button btn_open = findViewById(R.id.btn_open);
                    btn_open.setText("开启");
                    open = false;
                }
                else{
                    TextView tv_ip = findViewById(R.id.tv_ip);
                    tv_ip.setVisibility(View.VISIBLE);
                    tv_ip.setText("adb connect " + WiFiAdbUtil.getIpAddress() + ":5555");
                    WiFiAdbUtil.openAdb();
                    Button btn_open = findViewById(R.id.btn_open);
                    btn_open.setText("关闭");
                    open = true;
                }
            }
            else{
                Toast.makeText(MainActivity.this,"您没有Root权限，无法打开WiFiAdb",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(MainActivity.this,"您正在使用流量，无法打开WiFiAdb",Toast.LENGTH_SHORT).show();
        }
    }

}