package com.github.tvbox.osc.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.orhanobut.hawk.Hawk;

public class AuthUtil {
    private static final String PREF_NAME = "vip";
    private static final String KEY_ACTIVATED = "activated";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_EXPIRE = "expire_time";

    public static void saveActivated(Context context) {
        try {
            if (context != null) {
                SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                sp.edit().putBoolean(KEY_ACTIVATED, true).putLong(KEY_EXPIRE, System.currentTimeMillis() + 365L*24*60*60*1000*10).apply();
            }
        } catch (Exception ignore) {}
        try { Hawk.put(KEY_ACTIVATED, true); } catch (Exception ignore) {}
    }
    public static void saveActivated() {
        try { Hawk.put(KEY_ACTIVATED, true); } catch (Exception ignore) {}
    }
    public static boolean isActivated(Context context) {
        try {
            if (context != null) {
                SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                if (sp.getBoolean(KEY_ACTIVATED, false)) return true;
            }
        } catch (Exception ignore) {}
        try { return Hawk.get(KEY_ACTIVATED, false); } catch (Exception e) { return false; }
    }
    public static boolean isActivated() {
        try { return Hawk.get(KEY_ACTIVATED, false); } catch (Exception e) { return false; }
    }
}
