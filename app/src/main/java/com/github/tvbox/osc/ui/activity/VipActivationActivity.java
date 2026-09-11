package com.github.tvbox.osc.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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
        
        // 纯代码创建界面，不用R.layout，不会闪退
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.parseColor("#7C4DFF"));
        
        TextView tv = new TextView(this);
        tv.setText("VIP激活");
        tv.setTextSize(30);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(-2, -2);
        root.addView(tv, lp1);
        
        EditText et = new EditText(this);
        et.setHint("请输入 8888");
        et.setBackgroundColor(Color.WHITE);
        et.setPadding(20,20,20,20);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(600, 120);
        lp2.topMargin = 50;
        root.addView(et, lp2);
        
        Button btn = new Button(this);
        btn.setText("立即激活");
        btn.setOnClickListener(v -> {
            if (RIGHT_CODE.equals(et.getText().toString().trim())) {
                sp.edit().putBoolean("isVip", true).apply();
                Toast.makeText(this, "激活成功", Toast.LENGTH_SHORT).show();
                goHome();
            } else {
                Toast.makeText(this, "激活码错误: 8888", Toast.LENGTH_SHORT).show();
            }
        });
        root.addView(btn, lp2);
        
        setContentView(root);
    }
    
    private void goHome() {
        try {
            // TVBox官方首页就叫 MainActivity
            Intent intent = new Intent(this, Class.forName("com.github.tvbox.osc.ui.activity.MainActivity"));
            startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent(this, Class.forName("com.github.tvbox.osc.ui.activity.HomeActivity"));
                startActivity(intent);
            } catch (Exception e2) {
                Toast.makeText(this, "找不到首页: " + e2.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        finish();
    }
}
