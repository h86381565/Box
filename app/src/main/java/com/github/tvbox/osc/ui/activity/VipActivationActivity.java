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
import com.github.tvbox.osc.R; 

public class VipActivationActivity extends AppCompatActivity {
    private final String RIGHT_CODE = "8888";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            SharedPreferences sp = getSharedPreferences("vip", MODE_PRIVATE);
            if (sp.getBoolean("isVip", false)) {
                goHome();
                return;
            }
            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setGravity(Gravity.CENTER);
            root.setBackgroundColor(Color.parseColor("#7C4DFF"));
            root.setPadding(50,50,50,50);

            TextView tv = new TextView(this);
            tv.setText("VIP影视 - 激活");
            tv.setTextSize(28);
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER);
            root.addView(tv);

            EditText et = new EditText(this);
            et.setHint("请输入激活码 8888");
            et.setBackgroundColor(Color.WHITE);
            et.setTextColor(Color.BLACK);
            et.setPadding(30,30,30,30);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(600, 130);
            lp.topMargin = 60;
            root.addView(et, lp);

            Button btn = new Button(this);
            btn.setText("立即激活");
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(600, 130);
            lp2.topMargin = 30;
            root.addView(btn, lp2);

            btn.setOnClickListener(v -> {
                if (RIGHT_CODE.equals(et.getText().toString().trim())) {
                    sp.edit().putBoolean("isVip", true).apply();
                    Toast.makeText(this, "激活成功", Toast.LENGTH_SHORT).show();
                    goHome();
                } else {
                    Toast.makeText(this, "错误，请输入8888", Toast.LENGTH_SHORT).show();
                }
            });
            setContentView(root);
        } catch (Exception e) {
            Toast.makeText(this, "崩溃: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    private void goHome() {
        try {
            startActivity(new Intent(this, HomeActivity.class));
        } catch (Exception e) {
            Toast.makeText(this, "找不到HomeActivity: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
