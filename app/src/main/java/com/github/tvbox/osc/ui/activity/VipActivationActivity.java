package com.github.tvbox.osc.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.tvbox.osc.R;

public class VipActivationActivity extends AppCompatActivity {
    private final String RIGHT_CODE = "8888";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("vip", MODE_PRIVATE);
        if (sp.getBoolean("isVip", false)) {
            goHome();
            return;
        }
        try {
            setContentView(R.layout.activity_vip_activation);
        } catch (Exception e){
            // 如果布局文件还没上传，用代码创建界面，防止闪退
            android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            layout.setGravity(android.view.Gravity.CENTER);
            layout.setBackgroundColor(android.graphics.Color.parseColor("#7C4DFF"));
            
            EditText et = new EditText(this);
            et.setId(R.id.et_activation_code);
            et.setHint("请输入 8888");
            et.setBackgroundColor(android.graphics.Color.WHITE);
            android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(600, 120);
            lp.topMargin = 50;
            layout.addView(et, lp);
            
            Button btn = new Button(this);
            btn.setId(R.id.btn_activate);
            btn.setText("立即激活");
            btn.setOnClickListener(v -> {
                String code = et.getText().toString().trim();
                if (RIGHT_CODE.equals(code)) {
                    sp.edit().putBoolean("isVip", true).apply();
                    goHome();
                } else {
                    Toast.makeText(this, "激活码错误，请输入8888", Toast.LENGTH_SHORT).show();
                }
            });
            layout.addView(btn, lp);
            setContentView(layout);
            return;
        }

        EditText etCode = findViewById(R.id.et_activation_code);
        Button btnActivate = findViewById(R.id.btn_activate);
        if(etCode == null || btnActivate == null) { goHome(); return; }
        
        btnActivate.setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();
            if (RIGHT_CODE.equals(code)) {
                sp.edit().putBoolean("isVip", true).apply();
                Toast.makeText(this, "激活成功", Toast.LENGTH_SHORT).show();
                goHome();
            } else {
                Toast.makeText(this, "激活码错误: 8888", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void goHome() {
        try {
            // 自动找首页，TVBox有3个可能的首页名字
            Class<?> homeClass = null;
            try { homeClass = Class.forName("com.github.tvbox.osc.ui.activity.HomeActivity"); } catch (Exception e){}
            if(homeClass == null) { try { homeClass = Class.forName("com.github.tvbox.osc.ui.activity.MainActivity"); } catch (Exception e){} }
            if(homeClass == null) { try { homeClass = Class.forName("com.github.tvbox.osc.ui.activity.SplashActivity"); } catch (Exception e){} }
            
            if(homeClass != null){
                startActivity(new Intent(this, homeClass));
            }
        } catch (Exception e){
            Toast.makeText(this, "首页跳转失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
