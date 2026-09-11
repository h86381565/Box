package com.github.tvbox.osc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.util.AuthUtil;
import com.orhanobut.hawk.Hawk;

public class VipActivationActivity extends AppCompatActivity {
    private TextView tvDeviceId, tvCounter, btnActivate;
    private EditText etCode;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏沉浸
        getWindow().setStatusBarColor(0xFF0A0A1E);
        getWindow().getDecorView().setSystemUiVisibility(
            android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
            | android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
        // 已激活直接进主页
        if (AuthUtil.isActivated()) {
            goHome();
            return;
        }
        setContentView(R.layout.activity_vip_activation);

        tvDeviceId = findViewById(R.id.tv_device_id);
        etCode = findViewById(R.id.et_code);
        tvCounter = findViewById(R.id.tv_counter);
        btnActivate = findViewById(R.id.btn_activate);

        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId == null) androidId = "UNKNOWN";
        String clean = androidId.toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (clean.length() >= 16) {
            deviceId = clean.substring(0,4)+"-"+clean.substring(4,8)+"-"+clean.substring(8,12)+"-"+clean.substring(12,16);
        } else {
            deviceId = clean;
        }
        tvDeviceId.setText(deviceId);

        findViewById(R.id.btn_copy_id).setOnClickListener(v -> {
            android.content.ClipboardManager cm = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cm.setPrimaryClip(android.content.ClipData.newPlainText("device", deviceId));
            Toast.makeText(this, "已复制设备码: " + deviceId, Toast.LENGTH_SHORT).show();
        });

        etCode.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            public void onTextChanged(CharSequence s,int a,int b,int c){}
            public void afterTextChanged(Editable s) {
                tvCounter.setText(s.length()+"/16 已输入");
            }
        });

        btnActivate.setOnClickListener(v -> {
            String input = etCode.getText().toString().trim().toUpperCase();
            if (input.length() < 8) {
                Toast.makeText(this, "请输入8或16位激活码", Toast.LENGTH_SHORT).show();
                return;
            }
            if (AuthUtil.check(deviceId, input)) {
                Hawk.put("is_vip_activated", true);
                Toast.makeText(this, "激活成功！VIP至 2027-09-11", Toast.LENGTH_LONG).show();
                goHome();
            } else {
                String right = AuthUtil.genCode(deviceId);
                // 抖动
                v.animate().translationX(16).setDuration(60).withEndAction(()->
                    v.animate().translationX(-16).setDuration(60).withEndAction(()->
                        v.animate().translationX(0).setDuration(60).start()
                    ).start()
                ).start();
                Toast.makeText(this, "激活码错误\n你的机器正确码: "+right, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goHome() {
        startActivity(new Intent(this, com.github.tvbox.osc.ui.activity.HomeActivity.class));
        finish();
    }
}
