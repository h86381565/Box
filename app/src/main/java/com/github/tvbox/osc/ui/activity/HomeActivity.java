package com.github.tvbox.osc.ui.activity;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
        @Override public void run() {
            if (tvDate!= null) {
                Date date = new Date();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat(getString(R.string.hm_date1) + " | " + getString(R.string.hm_date2));
                tvDate.setText(timeFormat.format(date));
            }
            mHandler.postDelayed(this, 1000);
        }
    };
    @Override protected int getLayoutResID() { return R.layout.activity_home; }
    boolean useCacheConfig = false;
    boolean HomeShow = false;
    @Override protected void init() {
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
        if (intent!= null && intent.getExtras()!= null) {
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
        if (mGridView!= null) {
            this.sortAdapter = new SortAdapter();
            this.mGridView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 0, false));
            this.mGridView.setSpacingWithMargins(0, AutoSizeUtils.dp2px(this.mContext, 10.0f));
            this.mGridView.setAdapter(this.sortAdapter);
            sortAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override public void onChanged() {
                    mGridView.post(() -> {
                        try {
                            View firstChild = Objects.requireNonNull(mGridView.getLayoutManager()).findViewByPosition(0);
                            if (firstChild!= null) { mGridView.setSelectedPosition(0); firstChild.requestFocus(); }
                        } catch (Exception ignore) {}
                    });
                }
            });
            this.mGridView.setOnItemListener(new TvRecyclerView.OnItemListener() {
                public void onItemPreSelected(TvRecyclerView tvRecyclerView, View view, int position) {
                    if (view!= null &&!HomeActivity.this.isDownOrUp) {
                        try {
                            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(250).start();
                            TextView textView = view.findViewById(R.id.tvTitle);
                            if (textView!= null) {
                                textView.getPaint().setFakeBoldText(false);
                                textView.setTextColor(getResources().getColor(R.color.color_FFFFFF_70));
                            }
                        } catch (Exception ignore) {}
                    }
                }
                public void onItemSelected(TvRecyclerView tvRecyclerView, View view, int position) {
                    if (view== null) return;
                    try {
                        HomeActivity.this.currentView = view;
                        HomeActivity.this.isDownOrUp = false;
                        HomeActivity.this.sortChange = true;
                        view.animate().scaleX(1.1f).scaleY(1.1f).setInterpolator(new BounceInterpolator()).setDuration(250).start();
                        TextView textView = view.findViewById(R.id.tvTitle);
                        if (textView!= null) {
                            textView.getPaint().setFakeBoldText(true);
                            textView.setTextColor(getResources().getColor(R.color.color_FFFFFF));
                        }
                        if (position == -1) position = 0;
                        MovieSort.SortData sortData = sortAdapter.getItem(position);
                        if (sortData!= null && sortData.filters!= null &&!sortData.filters.isEmpty()) showFilterIcon(sortData.filterSelectCount());
                        HomeActivity.this.sortFocusView = view;
                        HomeActivity.this.sortFocused = position;
                        mHandler.removeCallbacks(mDataRunnable);
                        mHandler.postDelayed(mDataRunnable, 200);
                    } catch (Exception ignore) {}
                }
                @Override public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                    if (itemView == null) return;
                    try {
                        MovieSort.SortData sortData = sortAdapter.getItem(position);
                        if (sortData== null) return;
                        if ("live".equals(sortData.id)) {
                            jumpActivity(LivePlayActivity.class);
                            return;
                        }
                        if (currentSelected!= position) {
                            sortFocused = position;
                            mHandler.removeCallbacks(mDataRunnable);
                            mHandler.post(mDataRunnable);
                        } else {
                            BaseLazyFragment baseLazyFragment = fragments.get(currentSelected);
                            if ((baseLazyFragment instanceof GridFragment) && sortData.filters!= null &&!sortData.filters.isEmpty()) {
                                ((GridFragment) baseLazyFragment).showFilter();
                            } else if (baseLazyFragment instanceof UserFragment) {
                                showSiteSwitch();
                            }
                        }
                    } catch (Exception ignore) {}
                }
            });
        }
        if (tvFind!= null) {
            tvFind.setFocusable(true);
            tvFind.setOnClickListener(v -> { try { jumpActivity(SearchActivity.class); } catch (Exception e) { Toast.makeText(this, "搜索打开失败", Toast.LENGTH_SHORT).show(); } });
        }
        if (tvWifi!= null) tvWifi.setOnClickListener(v -> { try { startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); }catch (Exception ignored){} });
        if (tvName!= null) tvName.setOnClickListener(v -> { FastClickCheckUtil.check(v); try { File dir = getCacheDir(); FileUtils.recursiveDelete(dir); dir = getExternalCacheDir(); FileUtils.recursiveDelete(dir); } catch (Exception ignore) {} Toast.makeText(HomeActivity.this, getString(R.string.hm_cache_del), Toast.LENGTH_SHORT).show(); });
        if (tvName!= null) tvName.setOnLongClickListener(v->{ reloadHome(); return true; });
        if (tvDraw!= null) tvDraw.setOnClickListener(v->{ jumpActivity(AppsActivity.class); });
        if (tvMenu!= null) tvMenu.setOnClickListener(v->{ jumpActivity(SettingActivity.class); });
        if (tvDate!= null) tvDate.setOnClickListener(v->{ try { startActivity(new Intent(Settings.ACTION_DATE_SETTINGS)); } catch (Exception ignore) {} });
        try { if (contentLayout!= null) setLoadSir(this.contentLayout); } catch (Exception ignore) {}
    }
    private boolean skipNextUpdate = false;
    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.sortResult.observe(this, absXml -> {
            if (skipNextUpdate) { skipNextUpdate = false; return; }
            showSuccess();
            sortAdapter.setNewData(DefaultConfig.adjustSort(ApiConfig.get().getHomeSourceBean().getKey(), absXml!= null && absXml.classes!= null && absXml.classes.sortList!= null? absXml.classes.sortList : new ArrayList<>(), true));
            initViewPager(absXml);
            SourceBean home = ApiConfig.get().getHomeSourceBean();
            if (HomeShow && tvName!= null && home!= null && home.getName()!= null &&!home.getName().isEmpty()) { tvName.setText(home.getName()); tvName.clearAnimation(); }
        });
    }
    private boolean dataInitOk = false;
    private boolean jarInitOk = false;
    private void initData() {
        if (mGridView!= null) mGridView.requestFocus();
        if (dataInitOk && jarInitOk) { sourceViewModel.getSort(ApiConfig.get().getHomeSourceBean().getKey()); return; }
        if (tvName!= null) tvNameAnimation(); showLoading();
        if (dataInitOk &&!jarInitOk) {
            if (!ApiConfig.get().getSpider().isEmpty()) {
                ApiConfig.get().loadJar(useCacheConfig, ApiConfig.get().getSpider(), new ApiConfig.LoadConfigCallback() {
                    @Override public void success() { jarInitOk = true; mHandler.postDelayed(() -> initData(), 50); }
                    @Override public void retry() {}
                    @Override public void error(String msg) { jarInitOk = true; dataInitOk = true; mHandler.postDelayed(() -> initData(),50); }
                });
            }
            return;
        }
        ApiConfig.get().loadConfig(useCacheConfig, new ApiConfig.LoadConfigCallback() {
            TipDialog dialog = null;
            @Override public void retry() { mHandler.post(() -> initData()); }
            @Override public void success() { dataInitOk = true; if (ApiConfig.get().getSpider().isEmpty()) jarInitOk = true; mHandler.postDelayed(() -> initData(), 50); }
            @Override public void error(String msg) {
                if ("-1".equalsIgnoreCase(msg)) { mHandler.post(() -> { dataInitOk = true; jarInitOk = true; initData(); }); return; }
                mHandler.post(() -> {
                    if (dialog == null) dialog = new TipDialog(HomeActivity.this, msg, getString(R.string.hm_retry), getString(R.string.hm_cancel), new TipDialog.OnListener() {
                        @Override public void left() { mHandler.post(() -> { initData(); dialog.hide(); }); }
                        @Override public void right() { dataInitOk = true; jarInitOk = true; mHandler.post(() -> { initData(); dialog.hide(); }); }
                        @Override public void cancel() { dataInitOk = true; jarInitOk = true; mHandler.post(() -> { initData(); dialog.hide(); }); }
                    });
                    if (!dialog.isShowing()) dialog.show();
                });
            }
        }, this);
    }
    private void initViewPager(AbsSortXml absXml) {
        try {
            fragments.clear();
            if (sortAdapter!= null && sortAdapter.getData().size() > 0) {
                for (MovieSort.SortData data : sortAdapter.getData()) {
                    if ("live".equals(data.id)) fragments.add(UserFragment.newInstance(null));
                    else if (data.id.equals("my0")) fragments.add(UserFragment.newInstance(null));
                    else fragments.add(GridFragment.newInstance(data));
                }
                pageAdapter = new HomePageAdapter(getSupportFragmentManager(), fragments);
                try { Field field = ViewPager.class.getDeclaredField("mScroller"); field.setAccessible(true); FixedSpeedScroller scroller = new FixedSpeedScroller(mContext, new AccelerateInterpolator()); field.set(mViewPager, scroller); scroller.setmDuration(300); } catch (Exception e) {}
                if (mViewPager!= null) { mViewPager.setPageTransformer(true, new DefaultTransformer()); mViewPager.setAdapter(pageAdapter); mViewPager.setCurrentItem(currentSelected, false); }
            }
        } catch (Exception ignore) {}
    }
    @Override public void onBackPressed() {
        if(isLoading()){ refreshEmpty(); return; }
        if (this.fragments.size() <= 0 || this.sortFocused >= this.fragments.size() || this.sortFocused < 0) { doExit(); return; }
        try {
            BaseLazyFragment b = this.fragments.get(this.sortFocused);
            if (b instanceof GridFragment) {
                if (((GridFragment) b).restoreView()) return;
                if (this.sortFocusView!= null &&!this.sortFocusView.isFocused()) this.sortFocusView.requestFocus();
                else if (this.sortFocused!= 0) { if (this.mGridView!= null) this.mGridView.setSelection(0); } else doExit();
            } else doExit();
        } catch (Exception ignore) { doExit(); }
    }
    private void doExit() {
        if (System.currentTimeMillis() - mExitTime < 2000) {
            AppManager.getInstance().finishAllActivity(); EventBus.getDefault().unregister(this); ControlManager.get().stopServer(); finish(); android.os.Process.killProcess(android.os.Process.myPid()); System.exit(0);
        } else { mExitTime = System.currentTimeMillis(); Toast.makeText(mContext, getString(R.string.hm_exit), Toast.LENGTH_SHORT).show(); }
    }
    @Override protected void onResume() { super.onResume(); mHandler.post(mRunnable); }
    @Override protected void onPause() { super.onPause(); mHandler.removeCallbacksAndMessages(null); }
    @Subscribe(threadMode = ThreadMode.MAIN) public void refresh(RefreshEvent event) { if (event.type == RefreshEvent.TYPE_PUSH_URL) { if (ApiConfig.get().getSource("push_agent")!= null) { Intent newIntent = new Intent(mContext, DetailActivity.class); newIntent.putExtra("id", (String) event.obj); newIntent.putExtra("sourceKey", "push_agent"); newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); startActivity(newIntent); } } }
    private void showFilterIcon(int count) { try { if (currentView == null) return; View v = currentView.findViewById(R.id.tvFilter); if (v == null) return; v.setVisibility(View.VISIBLE); } catch (Exception ignore) {} }
    private final Runnable mDataRunnable = new Runnable() { @Override public void run() { if (sortChange) { sortChange = false; if (sortFocused!= currentSelected) { currentSelected = sortFocused; if (mViewPager!= null) mViewPager.setCurrentItem(sortFocused, false); } } } };
    @Override public boolean dispatchKeyEvent(KeyEvent event) { if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_MENU) showSiteSwitch(); return super.dispatchKeyEvent(event); }
    @Override protected void onDestroy() { super.onDestroy(); try { EventBus.getDefault().unregister(this); } catch (Exception ignore) {} }
    void showSiteSwitch() {
        List<SourceBean> sites = new ArrayList<>();
        try { for (SourceBean sb : ApiConfig.get().getSourceBeanList()) if (sb.getHide() == 0) sites.add(sb); } catch (Exception ignore) {}
        if (sites.size() > 0) {
            SelectDialog<SourceBean> dialog = new SelectDialog<>(HomeActivity.this);
            int spanCount = (int) Math.floor(sites.size() / 10); if (spanCount <= 1) spanCount = 1; if (spanCount >= 3) spanCount = 3;
            TvRecyclerView tvRecyclerView = dialog.findViewById(R.id.list); if (tvRecyclerView!= null) tvRecyclerView.setLayoutManager(new V7GridLayoutManager(dialog.getContext(), spanCount));
            ConstraintLayout cl_root = dialog.findViewById(R.id.cl_root); if (cl_root!= null) { ViewGroup.LayoutParams clp = cl_root.getLayoutParams(); if (spanCount!= 1) clp.width = AutoSizeUtils.mm2px(dialog.getContext(), 400 + 260 * (spanCount - 1)); }
            dialog.setTip(getString(R.string.dia_source));
            dialog.setAdapter(tvRecyclerView, new SelectDialogAdapter.SelectDialogInterface<SourceBean>() {
                @Override public void click(SourceBean value, int pos) { ApiConfig.get().setSourceBean(value); reloadHome(); }
                @Override public String getDisplay(SourceBean val) { return val.getName(); }
            }, new DiffUtil.ItemCallback<SourceBean>() {
                @Override public boolean areItemsTheSame(@NonNull @NotNull SourceBean oldItem, @NonNull @NotNull SourceBean newItem) { return oldItem == newItem; }
                @Override public boolean areContentsTheSame(@NonNull @NotNull SourceBean oldItem, @NonNull @NotNull SourceBean newItem) { return oldItem.getKey().equals(newItem.getKey()); }
            }, sites, sites.indexOf(ApiConfig.get().getHomeSourceBean()));
            dialog.show();
        }
    }
    public void reloadHome() { Intent intent = new Intent(getApplicationContext(), HomeActivity.class); intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); Bundle bundle = new Bundle(); bundle.putBoolean("useCache", true); intent.putExtras(bundle); startActivity(intent); }
    private void refreshEmpty() { try { skipNextUpdate=true; showSuccess(); if (sortAdapter!= null) sortAdapter.setNewData(DefaultConfig.adjustSort(ApiConfig.get().getHomeSourceBean().getKey(), new ArrayList<>(), true)); initViewPager(null); if (tvName!= null) tvName.clearAnimation(); } catch (Exception ignore) {} }
    private void tvNameAnimation() { try { if (tvName == null) return; AlphaAnimation blinkAnimation = new AlphaAnimation(0.0f, 1.0f); blinkAnimation.setDuration(500); blinkAnimation.setStartOffset(20); blinkAnimation.setRepeatMode(Animation.REVERSE); blinkAnimation.setRepeatCount(Animation.INFINITE); tvName.startAnimation(blinkAnimation); } catch (Exception ignore) {} }
}
