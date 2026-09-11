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
        String clean = androidId.toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (clean.length() >= 16) {
            return clean.substring(0,4)+"-"+clean.substring(4,8)+"-"+clean.substring(8,12)+"-"+clean.substring(12,16);
        }
        return clean.toUpperCase();
    }
    
    public static String genCode(String deviceId) {
        String clean = deviceId.replace("-","").trim().toUpperCase();
        String all = clean + SECRET;
        String md = md5(all);
        if (md.length() < 16) return md.toUpperCase();
        return md.substring(0, 16).toUpperCase();
    }
    
    public static boolean check(String deviceId, String inputCode) {
        if (inputCode == null) return false;
        String input = inputCode.trim().toUpperCase();
        String code16 = genCode(deviceId);
        String code8 = code16.substring(0,8);
        return input.equals(code16) || input.equals(code8);
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
            byte[] bytes = digest.digest(input.getBytes("utf-8"));
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
