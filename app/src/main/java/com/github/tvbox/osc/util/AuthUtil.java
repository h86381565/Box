package com.github.tvbox.osc.util;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import com.orhanobut.hawk.Hawk;
import java.security.MessageDigest;

public class AuthUtil {
    public static String SECRET = "vip2026";

    public static String getDeviceId(Context ctx) {
        try {
            String androidId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (androidId == null) androidId = "UNKNOWN_DEVICE";
            return androidId.toUpperCase();
        } catch (Exception e) {
            return "UNKNOWN_DEVICE";
        }
    }

    public static String genCode(String deviceId) {
        return md5(deviceId + SECRET).substring(0,8).toUpperCase();
    }

    public static boolean isActivated(Context ctx) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences("vip", Context.MODE_PRIVATE);
            if (sp.getBoolean("isVip", false)) {
                long expire = sp.getLong("expire", 0);
                if (expire > System.currentTimeMillis()/1000) return true;
            }
        } catch (Exception ignore) {}
        try {
            return Hawk.get("is_vip_activated", false);
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean isActivated() {
        try { return Hawk.get("is_vip_activated", false); } catch (Exception e) { return false; }
    }

    public static void saveActivated(Context ctx) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences("vip", Context.MODE_PRIVATE);
            long expire = System.currentTimeMillis()/1000 + 10L*365*24*3600;
            sp.edit().putBoolean("isVip", true).putLong("expire", expire).apply();
        } catch (Exception ignore) {}
        try { Hawk.put("is_vip_activated", true); } catch (Exception ignore) {}
    }

    public static void saveActivated() {
        try { Hawk.put("is_vip_activated", true); } catch (Exception ignore) {}
    }

    private static String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { return s; }
    }
}
