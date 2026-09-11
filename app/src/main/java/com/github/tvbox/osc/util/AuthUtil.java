package com.github.tvbox.osc.util;
import android.content.Context;
import android.provider.Settings;
import com.orhanobut.hawk.Hawk;
import java.security.MessageDigest;

public class AuthUtil {
    public static String SECRET = "vip2026"; // 你的密钥，自己改复杂点
    public static String getDeviceId(Context ctx) {
        String id = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        return id!= null? id.toUpperCase() : "UNKNOWN";
    }
    public static String genCode(String deviceId) {
        return md5(deviceId + SECRET).substring(0,8).toUpperCase();
    }
    public static boolean isActivated() {
        return Hawk.get("is_vip_activated", false);
    }
    public static void saveActivated() {
        Hawk.put("is_vip_activated", true);
    }
    private static String md5(String ) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { return s; }
    }
}
