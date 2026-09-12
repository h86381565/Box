package com.github.tvbox.osc.ui.activity;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.base.BaseActivity;
import com.github.tvbox.osc.base.BaseLazyFragment;
import com.github.tvbox.osc.bean.AbsSortXml;
import com.github.tvbox.osc.bean.MovieSort;
import com.github.tvbox.osc.bean.SourceBean;
import com.github.tvbox.osc.event.RefreshEvent;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.ui.adapter.HomePageAdapter;
import com.github.tvbox.osc.ui.adapter.SelectDialogAdapter;
import com.github.tvbox.osc.ui.adapter.SortAdapter;
import com.github.tvbox.osc.ui.dialog.SelectDialog;
import com.github.tvbox.osc.ui.dialog.TipDialog;
import com.github.tvbox.osc.ui.fragment.GridFragment;
import com.github.tvbox.osc.ui.fragment.UserFragment;
import com.github.tvbox.osc.ui.tv.widget.DefaultTransformer;
import com.github.tvbox.osc.ui.tv.widget.FixedSpeedScroller;
import com.github.tvbox.osc.ui.tv.widget.NoScrollViewPager;
import com.github.tvbox.osc.ui.tv.widget.ViewObj;
import com.github.tvbox.osc.util.AppManager;
import com.github.tvbox.osc.util.AuthUtil;
import com.github.tvbox.osc.util.DefaultConfig;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.FileUtils;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.LOG;
import com.github.tvbox.osc.util.MD5;
import com.github.tvbox.osc.viewmodel.SourceViewModel;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import me.jessyan.autosize.utils.AutoSizeUtils;

public class HomeActivity extends BaseActivity {
    private static Resources res;
    private View currentView;
    private LinearLayout topLayout;
    private LinearLayout contentLayout;
    private TextView tvName;
    private View tvWifi;
    private View tvFind;
    private View tvStyle;
    private View tvDraw;
    private View tvMenu;
    private TextView tvDate;
    private TvRecyclerView mGridView;
    private NoScrollViewPager mViewPager;
    private SourceViewModel sourceViewModel;
    private SortAdapter sortAdapter;
    private HomePageAdapter pageAdapter;
    private final List<BaseLazyFragment> fragments = new ArrayList<>();
    private boolean isDownOrUp = false;
    private boolean sortChange = false;
    private int currentSelected = 0;
    private int sortFocused = 0;
    public View sortFocusView = null;
    private final Handler mHandler = new Handler();
    private long mExitTime = 0;
    private final Runnable mRunnable = new Runnable() {
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void run() {
            Date date = new Date();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat timeFormat = new SimpleDateFormat(getString(R.string.hm_date1) + " | " + getString(R.string.hm_date2));
            tvDate.setText(timeFormat.format(date));
            mHandler.postDelayed(this, 1000);
        }
    };
    @Override protected int getLayoutResID() { return R.layout.activity_home; }
    boolean useCacheConfig = false;
    boolean HomeShow = false;
    @Override
    protected void init() {
        try { Hawk.init(this).build(); } catch (Exception ignore) {}
        try { HomeShow = Hawk.get(HawkConfig.HOME_SHOW_SOURCE, false); } catch (Exception e) { HomeShow = false; }
        try { AuthUtil.saveActivated(this); } catch (Exception e) { try { AuthUtil.saveActivated(); } catch (Exception ignore) {} }
        res = getResources();
        EventBus.getDefault().register(this);
        ControlManager.get().startServer();
        App.startWebserver();
        initView();
        initViewModel();
        useCacheConfig = false;
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            useCacheConfig = bundle.getBoolean("useCache", false);
        }
        initData();
    }
    public static Resources getRes() { return res; }
    private void initView() {
        this.topLayout = findViewById(R.id.topLayout);
        this.tvName = findViewById(R.id.tvName);
        this.tvWifi = findViewById(R.id.tvWifi);
        this.tvFind = findViewById(R.id.tvFind);
        this.tvStyle = findViewById(R.id.tvStyle);
        this.tvDraw = findViewById(R.id.tvDrawer);
        this.tvMenu = findViewById(R.id.tvMenu);
        this.tvDate = findViewById(R.id.tvDate);
        this.contentLayout = findViewById(R.id.contentLayout);
        this.mGridView = findViewById(R.id.mGridViewCategory);
        this.mViewPager = findViewById(R.id.mViewPager);
        this.sortAdapter = new SortAdapter();
        this.mGridView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 0, false));
        this.mGridView.setSpacingWithMargins(0, AutoSizeUtils.dp2px(this.mContext, 10.0f));
        this.mGridView.setAdapter(this.sortAdapter);
        sortAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                mGridView.post(() -> {
                    View firstChild = Objects.requireNonNull(mGridView.getLayoutManager()).findViewByPosition(0);
                    if (firstChild != null) { mGridView.setSelectedPosition(0); firstChild.requestFocus(); }
                });
            }
        });
        this.mGridView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            public void onItemPreSelected(TvRecyclerView tvRecyclerView, View view, int position) {
                if (view != null && !HomeActivity.this.isDownOrUp) {
                    view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(250).start();
                    TextView textView = view.findViewById(R.id.tvTitle);
                    textView.getPaint().setFakeBoldText(false);
                    textView.setTextColor(HomeActivity.this.getResources().getColor(R.color.color_FFFFFF_70));
                    textView.invalidate();
                    view.findViewById(R.id.tvFilter).setVisibility(View.GONE);
                }
            }
            public void onItemSelected(TvRecyclerView tvRecyclerView, View view, int position) {
                if (view != null) {
                    HomeActivity.this.currentView = view;
                    HomeActivity.this.isDownOrUp = false;
                    HomeActivity.this.sortChange = true;
                    view.animate().scaleX(1.1f).scaleY(1.1f).setInterpolator(new BounceInterpolator()).setDuration(250).start();
                    TextView textView = view.findViewById(R.id.tvTitle);
                    textView.getPaint().setFakeBoldText(true);
                    textView.setTextColor(HomeActivity.this.getResources().getColor(R.color.color_FFFFFF));
                    textView.invalidate();
                    if (position == -1) { position = 0; HomeActivity.this.mGridView.setSelection(0); }
                    MovieSort.SortData sortData = sortAdapter.getItem(position);
                    if (null != sortData && !sortData.filters.isEmpty()) { showFilterIcon(sortData.filterSelectCount()); }
                    HomeActivity.this.sortFocusView = view;
                    HomeActivity.this.sortFocused = position;
                    mHandler.removeCallbacks(mDataRunnable);
                    mHandler.postDelayed(mDataRunnable, 200);
                }
            }
            @Override public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                if (itemView != null && currentSelected == position) {
                    BaseLazyFragment baseLazyFragment = fragments.get(currentSelected);
                    if ((baseLazyFragment instanceof GridFragment) && !sortAdapter.getItem(position).filters.isEmpty()) { ((GridFragment) baseLazyFragment).showFilter(); }
                    else if (baseLazyFragment instanceof UserFragment) { showSiteSwitch(); }
                }
            }
        });
        this.mGridView.setOnInBorderKeyEventListener(new TvRecyclerView.OnInBorderKeyEventListener() {
            public boolean onInBorderKeyEvent(int direction, View view) {
                if (direction == View.FOCUS_UP) {
                    BaseLazyFragment baseLazyFragment = fragments.get(sortFocused);
                    if ((baseLazyFragment instanceof GridFragment)) { ((GridFragment) baseLazyFragment).forceRefresh(); }
                }
                if (direction != View.FOCUS_DOWN) { return false; }
                BaseLazyFragment baseLazyFragment = fragments.get(sortFocused);
                if (!(baseLazyFragment instanceof GridFragment)) { return false; }
                return !((GridFragment) baseLazyFragment).isLoad();
            }
        });
        tvName.setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            File dir = getCacheDir(); FileUtils.recursiveDelete(dir); dir = getExternalCacheDir(); FileUtils.recursiveDelete(dir);
            Toast.makeText(HomeActivity.this, getString(R.string.hm_cache_del), Toast.LENGTH_SHORT).show();
            if(dataInitOk && jarInitOk){
                String cspCachePath = FileUtils.getFilePath()+"/csp/";
                String jar=ApiConfig.get().getHomeSourceBean().getJar();
                String jarUrl=!jar.isEmpty()?jar:ApiConfig.get().getSpider();
                File cspCacheDir = new File(cspCachePath + MD5.string2MD5(jarUrl)+".jar");
                if (!cspCacheDir.exists()){ reloadHome(); return; }
                new Thread(() -> { try { FileUtils.deleteFile(cspCacheDir); ApiConfig.get().clearJarLoader(); reloadHome(); } catch (Exception e) { e.printStackTrace(); } }).start();
            }
        });
        tvName.setOnLongClickListener(v->{ reloadHome(); return true; });
        tvWifi.setOnClickListener(view->{ try { startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); }catch (Exception ignored){} });
        tvFind.setOnClickListener(view->{ jumpActivity(SearchActivity.class); });
        tvStyle.setOnClickListener(view->{ try { Hawk.put(HawkConfig.HOME_REC_STYLE, !Hawk.get(HawkConfig.HOME_REC_STYLE, false)); if (Hawk.get(HawkConfig.HOME_REC_STYLE, false)) { UserFragment.tvHotListForGrid.setVisibility(View.VISIBLE); UserFragment.tvHotListForLine.setVisibility(View.GONE); Toast.makeText(HomeActivity.this, getString(R.string.hm_style_grid), Toast.LENGTH_SHORT).show(); try { if (tvStyle instanceof ImageView) ((ImageView)tvStyle).setImageResource(R.drawable.hm_up_down); } catch (Exception ignore) {} } else { UserFragment.tvHotListForGrid.setVisibility(View.GONE); UserFragment.tvHotListForLine.setVisibility(View.VISIBLE); Toast.makeText(HomeActivity.this, getString(R.string.hm_style_line), Toast.LENGTH_SHORT).show(); try { if (tvStyle instanceof ImageView) ((ImageView)tvStyle).setImageResource(R.drawable.hm_left_right); } catch (Exception ignore) {} } } catch (Exception ex) {} });
        tvDraw.setOnClickListener(view->{ jumpActivity(AppsActivity.class); });
        tvMenu.setOnClickListener(view->{ jumpActivity(SettingActivity.class); });
        tvMenu.setOnLongClickListener(view->{ startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null))); return true; });
        tvDate.setOnClickListener(view->{ startActivity(new Intent(Settings.ACTION_DATE_SETTINGS)); });
        setLoadSir(this.contentLayout);
    }
    public static void homeRecf() { try { int homeRec = Hawk.get(HawkConfig.HOME_REC, -1); int limit = 2; if (homeRec == limit) homeRec = -1; homeRec++; Hawk.put(HawkConfig.HOME_REC, homeRec); } catch (Exception ignore) {} }
    public static boolean reHome(Context appContext) { Intent intent = new Intent(appContext, HomeActivity.class); intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); Bundle bundle = new Bundle(); bundle.putBoolean("useCache", true); intent.putExtras(bundle); appContext.startActivity(intent); return true; }
    private boolean skipNextUpdate = false;
    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.sortResult.observe(this, absXml -> {
                if (skipNextUpdate) { skipNextUpdate = false; return; }
                showSuccess();
                if (absXml != null && absXml.classes != null && absXml.classes.sortList != null) { sortAdapter.setNewData(DefaultConfig.adjustSort(ApiConfig.get().getHomeSourceBean().getKey(), absXml.classes.sortList, true)); }
                else { sortAdapter.setNewData(DefaultConfig.adjustSort(ApiConfig.get().getHomeSourceBean().getKey(), new ArrayList<>(), true)); }
                initViewPager(absXml);
                SourceBean home = ApiConfig.get().getHomeSourceBean();
                if (HomeShow) { if (home != null && home.getName() != null && !home.getName().isEmpty()) tvName.setText(home.getName()); tvName.clearAnimation(); }
        });
    }
    private boolean dataInitOk = false;
    private boolean jarInitOk = false;
    boolean isNetworkAvailable() { ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE); NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo(); return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting(); }
    private void initData() {
        if (isNetworkAvailable()) {
            try {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI) { try { if (tvWifi instanceof android.widget.ImageView) ((android.widget.ImageView)tvWifi).setImageDrawable(res.getDrawable(R.drawable.hm_wifi)); } catch (Exception ignore) {} }
                else if (cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE) { try { if (tvWifi instanceof android.widget.ImageView) ((android.widget.ImageView)tvWifi).setImageDrawable(res.getDrawable(R.drawable.hm_mobile)); } catch (Exception ignore) {} }
                else if (cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_ETHERNET) { try { if (tvWifi instanceof android.widget.ImageView) ((android.widget.ImageView)tvWifi).setImageDrawable(res.getDrawable(R.drawable.hm_lan)); } catch (Exception ignore) {} }
            } catch (Exception ignore) {}
        }
        try { if (tvStyle instanceof android.widget.ImageView) { if (Hawk.get(HawkConfig.HOME_REC_STYLE, false)) { ((android.widget.ImageView)tvStyle).setImageResource(R.drawable.hm_up_down); } else { ((android.widget.ImageView)tvStyle).setImageResource(R.drawable.hm_left_right); } } } catch (Exception ignore) {}
        mGridView.requestFocus();
        if (dataInitOk && jarInitOk) { sourceViewModel.getSort(ApiConfig.get().getHomeSourceBean().getKey()); try { if (Hawk.get(HawkConfig.HOME_DEFAULT_SHOW, false)) { jumpActivity(LivePlayActivity.class); } } catch (Exception ignore) {} return; }
        tvNameAnimation(); showLoading();
        if (dataInitOk && !jarInitOk) {
            if (!ApiConfig.get().getSpider().isEmpty()) {
                ApiConfig.get().loadJar(useCacheConfig, ApiConfig.get().getSpider(), new ApiConfig.LoadConfigCallback() {
                    @Override public void success() { jarInitOk = true; mHandler.postDelayed(() -> { if (!useCacheConfig) { Toast.makeText(HomeActivity.this, getString(R.string.hm_ok), Toast.LENGTH_SHORT).show(); } initData(); }, 50); }
                    @Override public void retry() {}
                    @Override public void error(String msg) { jarInitOk = true; dataInitOk = true; mHandler.postDelayed(() -> { if ("".equals(msg)) Toast.makeText(HomeActivity.this, getString(R.string.hm_notok), Toast.LENGTH_SHORT).show(); else Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show(); initData(); },50); }
                });
            }
            return;
        }
        ApiConfig.get().loadConfig(useCacheConfig, new Api
