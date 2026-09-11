package com.github.tvbox.osc.util;
import android.content.Context;
import android.provider.Settings;
import com.orhanobut.hawk.Hawk;
import java.security.MessageDigest;

public class AuthUtil {
    public static String SECRET = "vip2026";
    public static String getDeviceId(Context ctx) {
        String androidId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId == null) androidId = "UNKNOWN";
        return androidId.toUpperCase();
    }
    public static String genCode(String deviceId) {
        String all = deviceId + SECRET;
        String md = md5(all);
        if (md.length() < 8) return md.toUpperCase();
        return md.substring(0, 8).toUpperCase();
    }
    public static boolean isActivated() {
        return Hawk.get("is_vip_activated", false);
    }
    public static void saveActivated() {
        Hawk.put("is_vip_activated", true);
    }
    private static String md5(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return input;
        }
    }
}
