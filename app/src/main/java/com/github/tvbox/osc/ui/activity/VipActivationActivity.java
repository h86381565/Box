package com.github.tvbox.osc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.tvbox.osc.util.AuthUtil;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class VipActivationActivity extends Activity {

    // 最终固定公钥，和你桌面 index.html 里的私钥是同一对
    // 把旧的 QCg/N4... 删掉，换成这个
private static final String PUBLIC_KEY_B64 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDByzfIpXi1QNbGKorQhmHSQ5TTxQOQDEtP/LIFmjqxbB2yxSg0Nk6KGDrDnYUQ+TQiYqcg/bUX0hpTNa9+Ks0JZ8ayH9Cf6C10peccu4MiiRLFbX2qsRL1iD0vXj7XgMloB14jNuOB5lEHehDtgxYF+p+5TxQKQKpfjvR0FzlaFwIDAQAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("vip", MODE_PRIVATE);
        long savedExpire = sp.getLong("expire", 0);
        boolean isVip = sp.getBoolean("isVip", false);
        if (isVip && savedExpire > System.currentTimeMillis()/1000) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }
        String deviceId = AuthUtil.getDeviceId(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.parseColor("#7C4DFF"));
        root.setPadding(50,50,50,50);
        TextView tv = new TextView(this);
        tv.setText("VIP影视激活 - 自定义天数版");
        tv.setTextSize(24);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        root.addView(tv);
        TextView tvDevice = new TextView(this);
        tvDevice.setText("设备码(复制给卖家):\n" + deviceId);
        tvDevice.setTextSize(12);
        tvDevice.setTextColor(Color.parseColor("#E0E0E0"));
        tvDevice.setGravity(Gravity.CENTER);
        tvDevice.setTextIsSelectable(true);
        tvDevice.setPadding(0,30,0,20);
        root.addView(tvDevice);
        EditText et = new EditText(this);
        et.setHint("粘贴卖家发的激活码(长串)");
        et.setBackgroundColor(Color.WHITE);
        et.setTextColor(Color.BLACK);
        et.setPadding(20,20,20,20);
        et.setTextSize(11);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 160);
        lp.topMargin = 10;
        root.addView(et, lp);
        Button btn = new Button(this);
        btn.setText("立即激活");
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.parseColor("#4A2AC7"));
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(600, 130);
        lp2.topMargin = 25;
        lp2.gravity = Gravity.CENTER;
        root.addView(btn, lp2);
        TextView err = new TextView(this);
        err.setTextColor(Color.YELLOW);
        err.setGravity(Gravity.CENTER);
        err.setPadding(0,20,0,0);
        root.addView(err);
        btn.setOnClickListener(v -> {
            String code = et.getText().toString();
            long expire = verifyAndGetExpire(code, deviceId, err);
            if (expire > 0) {
                sp.edit().putBoolean("isVip", true).putLong("expire", expire).apply();
                AuthUtil.saveActivated();
                Toast.makeText(this, "激活成功，有效期到：" + new java.util.Date(expire*1000L).toLocaleString(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        });
        setContentView(root);
    }

    private long verifyAndGetExpire(String code, String currentDeviceId, TextView errView) {
        try {
            String cleanCode = code.replaceAll("\\s", "").trim();
            if (cleanCode.isEmpty()) { errView.setText("请输入激活码"); return 0; }
            String decoded;
            try {
                decoded = new String(Base64.decode(cleanCode, Base64.DEFAULT), "UTF-8");
            } catch(Exception e) {
                decoded = new String(Base64.decode(cleanCode, Base64.URL_SAFE | Base64.NO_WRAP), "UTF-8");
            }
            String[] parts = decoded.split("\\|");
            if (parts.length!= 3) { errView.setText("激活码格式错误，请复制完整，当前分割出" + parts.length + "段"); return 0; }
            long expire = Long.parseLong(parts[0].trim());
            String deviceCodeInCode = parts[1].trim();
            String sigB64 = parts[2].trim().replaceAll("\\s","");
            String normCurrent = currentDeviceId.replace("-", "").replace(" ", "").toUpperCase();
            String normInCode = deviceCodeInCode.replace("-", "").replace(" ", "").toUpperCase();
            if (!normInCode.equals(normCurrent)) {
                errView.setText("设备码不匹配\n本机:" + currentDeviceId + "\n激活码内:" + deviceCodeInCode);
                return 0;
            }
            if (System.currentTimeMillis() / 1000 > expire) {
                errView.setText("激活码已过期");
                return 0;
            }
            PublicKey pubKey = getPublicKey();
            byte[] sigBytes = Base64.decode(sigB64, Base64.DEFAULT);
            String[] tryDatas = new String[]{
                expire + "|" + deviceCodeInCode,
                expire + "|" + normInCode,
                expire + "|" + currentDeviceId
            };
            for (String data : tryDatas) {
                try {
                    Signature sig = Signature.getInstance("SHA256withRSA");
                    sig.initVerify(pubKey);
                    sig.update(data.getBytes("UTF-8"));
                    if (sig.verify(sigBytes)) {
                        return expire;
                    }
                } catch (Exception ignore) {}
            }
            errView.setText("签名验证失败，激活码无效");
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            errView.setText("验证出错: " + e.getMessage());
            return 0;
        }
    }

    private PublicKey getPublicKey() throws Exception {
        String raw = PUBLIC_KEY_B64.replace("-----BEGIN PUBLIC KEY-----", "")
               .replace("-----END PUBLIC KEY-----", "")
               .replaceAll("\\s", "");
        byte[] keyBytes = Base64.decode(raw, Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
