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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("vip", MODE_PRIVATE);
        if (sp.getBoolean("isVip", false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_vip_activation);
        EditText et = findViewById(R.id.et_code);
        Button btn = findViewById(R.id.btn_active);
        btn.setOnClickListener(v -> {
            if ("8888".equals(et.getText().toString().trim())) {
                sp.edit().putBoolean("isVip", true).apply();
                Toast.makeText(getApplicationContext(), "激活成功", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "激活码错误，请输入8888", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
