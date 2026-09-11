package com.github.tvbox.osc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VipActivationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("vip", MODE_PRIVATE);
        if (sp.getBoolean("isVip", false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.parseColor("#7C4DFF"));
        root.setPadding(50,50,50,50);

        TextView tv = new TextView(this);
        tv.setText("VIP影视激活");
        tv.setTextSize(32);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        root.addView(tv);

        EditText et = new EditText(this);
        et.setHint("请输入 8888");
        et.setBackgroundColor(Color.WHITE);
        et.setTextColor(Color.BLACK);
        et.setPadding(30,30,30,30);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(600, 130);
        lp.topMargin = 40;
        root.addView(et, lp);

        Button btn = new Button(this);
        btn.setText("立即激活");
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(600, 130);
        lp2.topMargin = 20;
        root.addView(btn, lp2);

        TextView err = new TextView(this);
        err.setTextColor(Color.YELLOW);
        err.setGravity(Gravity.CENTER);
        root.addView(err);

        btn.setOnClickListener(v -> {
            if ("8888".equals(et.getText().toString().trim())) {
                sp.edit().putBoolean("isVip", true).apply();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                err.setText("激活码错误，请输入8888");
            }
        });
        setContentView(root);
    }
}
