package com.github.tvbox.osc.base;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.multidex.MultiDexApplication;

import com.github.catvod.crawler.JarLoader;
import com.github.catvod.crawler.JsLoader;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.callback.EmptyCallback;
import com.github.tvbox.osc.callback.LoadingCallback;
import com.github.tvbox.osc.data.AppDataManager;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.util.EpgUtil;
import com.github.tvbox.osc.util.FileUtils;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.LocaleHelper;
import com.github.tvbox.osc.util.LOG;
import com.github.tvbox.osc.util.OkGoHelper;
import com.github.tvbox.osc.util.PlayerHelper;
import com.github.tvbox.osc.util.SubtitleHelper;
import com.hjq.permissions.XXPermissions;
import com.kingja.loadsir.core.LoadSir;
import com.orhanobut.hawk.Hawk;
import com.p2p.P2PClass;
import com.whl.quickjs.android.QuickJSLoader;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

public class App extends MultiDexApplication {
    private static App instance;
    private static P2PClass p;
    public static String burl;
    private static String dashData;
    public static ViewPump viewPump = null;
    private static Server server = null;
    private final Handler handler;

    public App() {
        instance = this;
        handler = HandlerCompat.createAsync(Looper.getMainLooper());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SubtitleHelper.initSubtitleColor(this);
        initParams();
        initLocale();
        OkGoHelper.init();
        XXPermissions.setCheckMode(false);
        EpgUtil.init();
        ControlManager.init(this);
        AppDataManager.init();
        LoadSir.beginBuilder()
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .commit();
        AutoSizeConfig.getInstance().setCustomFragment(true).getUnitsManager()
                .setSupportDP(false)
                .setSupportSP(false)
                .setSupportSubunits(Subunits.MM);
        int screenWidth = AutoSizeConfig.getInstance().getScreenWidth();
        int screenHeight = AutoSizeConfig.getInstance().getScreenHeight();
        if (screenWidth < screenHeight) {
            AutoSizeConfig.getInstance().setScreenWidth(screenHeight);
            AutoSizeConfig.getInstance().setScreenHeight(screenWidth);
        }
        PlayerHelper.init();
        FileUtils.cleanPlayerCache();
        QuickJSLoader.init();
        String extStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        File fontFile = new File(extStorageDir + "/tvbox.ttf");
        if (fontFile.exists()) {
            viewPump = ViewPump.builder()
                    .addInterceptor(new CalligraphyInterceptor(
                            new CalligraphyConfig.Builder()
                                    .setDefaultFontPath(fontFile.getAbsolutePath())
                                    .setFontAttrId(R.attr.fontPath)
                                    .build()))
                    .build();
        }
    }

    public static P2PClass getp2p() {
        try {
            if (p == null) {
                p = new P2PClass(FileUtils.getExternalCachePath());
            }
            return p;
        } catch (Exception e) {
            LOG.e(e.toString());
            return null;
        }
    }

    private void initParams() {
        Hawk.init(this).build();
        Hawk.put(HawkConfig.DEBUG_OPEN, false);

        // ===== 核心修复：解决 Read timed out 和 源地址为空 =====
        putDefault(HawkConfig.API_URL, "https://cdn.jsdelivr.net/gh/h86381565/test-TV@main/fantaiying.json");

        // 首页选项
        putDefault(HawkConfig.HOME_SHOW_SOURCE, true);
        putDefault(HawkConfig.HOME_SEARCH_POSITION, false);
        putDefault(HawkConfig.HOME_MENU_POSITION, true);
        putDefault(HawkConfig.HOME_REC, 1);
        putDefault(HawkConfig.HOME_NUM, 4);
        // 播放器选项
        putDefault(HawkConfig.SHOW_PREVIEW, true);
        putDefault(HawkConfig.PLAY_SCALE, 0);
        putDefault(HawkConfig.BACKGROUND_PLAY_TYPE, 0);
        putDefault(HawkConfig.PLAY_TYPE, 1);
        putDefault(HawkConfig.IJK_CODEC, "硬解码");
        // 系统选项
        putDefault(HawkConfig.HOME_LOCALE, 0);
        putDefault(HawkConfig.THEME_SELECT, 0);
        putDefault(HawkConfig.SEARCH_VIEW, 1);
        putDefault(HawkConfig.PARSE_WEBVIEW, true);
        putDefault(HawkConfig.DOH_URL, 0);
    }

    private void initLocale() {
        if (Hawk.get(HawkConfig.HOME_LOCALE, 0) == 0) {
            LocaleHelper.setLocale(App.this, "zh");
        } else {
            LocaleHelper.setLocale(App.this, "");
        }
    }

    public static App getInstance() {
        return instance;
    }

    private void putDefault(String key, Object value) {
        if (!Hawk.contains(key)) {
            Hawk.put(key, value);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        JsLoader.destroy();
    }

    public void setDashData(String data) {
        dashData = data;
    }

    public String getDashData() {
        return dashData;
    }

    public static void startWebserver() {
        if (server != null) return;
        server = AndServer
                .webServer(instance)
                .port(12345)
                .timeout(60, TimeUnit.SECONDS)
                .listener(new Server.ServerListener() {
                    @Override public void onStarted() {}
                    @Override public void onStopped() {}
                    @Override public void onException(Exception e) {}
                }).build();
        server.startup();
    }

    public static void post(Runnable runnable) {
        getInstance().handler.post(runnable);
    }

    public static void post(Runnable runnable, long delayMillis) {
        getInstance().handler.removeCallbacks(runnable);
        if (delayMillis >= 0) getInstance().handler.postDelayed(runnable, delayMillis);
    }
}
