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
import com.github.tvbox.osc.bean.VodInfo;
import com.github.tvbox.osc.data.AppDataManager;
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
    private View tvPlayerSetting;
    private View tvHistory;
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
        try { int hid = getResources().getIdentifier("tvHistory", "id", getPackageName()); if (hid != 0) this.tvHistory = findViewById(hid); else this.tvHistory = findViewById(R.id.tvHistory); } catch (Exception e) { try { this.tvHistory = findViewById(R.id.tvHistory); } catch (Exception ignore) {} }
        this.tvDraw = findViewById(R.id.tvDrawer);
        this.tvMenu = findViewById(R.id.tvMenu);
        this.tvPlayerSetting = findViewById(R.id.tvPlayerSetting);
        this.contentLayout = findViewById(R.id.contentLayout);
        this.tvDate = findViewById(R.id.tvDate);
        this.mGridView = findViewById(R.id.mGridViewCategory);
        if (this.mGridView == null) this.mGridView = findViewById(R.id.mGridView);
        this.mViewPager = findViewById(R.id.mViewPager);
        this.sortAdapter = new SortAdapter();
        if (this.mGridView!= null) {
            this.mGridView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 1, false));
            this.mGridView.setSpacingWithMargins(0, AutoSizeUtils.dp2px(this.mContext, 10.0f));
            this.mGridView.setAdapter(this.sortAdapter);
            this.sortAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override public void onChanged() {
                    super.onChanged();
                    try {
                        if (sortAdapter.getData().size() > 0 && sortFocused < sortAdapter.getData().size()) {
                            mGridView.post(() -> {
                                try {
                                    View firstChild = Objects.requireNonNull(mGridView.getLayoutManager()).findViewByPosition(0);
                                    if (firstChild!= null && currentSelected == 0) { mGridView.setSelectedPosition(0); firstChild.requestFocus(); }
                                } catch (Exception ignore) {}
                            });
                        }
                    } catch (Exception ignore) {}
                }
            });
            this.mGridView.setOnItemListener(new TvRecyclerView.OnItemListener() {
                @Override public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                    try { sortFocused = position; sortFocusView = itemView; } catch (Exception ignore) {}
                }
                @Override public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                    try {
                        sortFocused = position;
                        sortFocusView = itemView;
                        if (position != currentSelected) { sortChange = true; mHandler.removeCallbacks(mDataRunnable); mHandler.postDelayed(mDataRunnable, 200); }
                    } catch (Exception ignore) {}
                }
                @Override public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                    try {
                        sortFocused = position;
                        sortFocusView = itemView;
                        if (position != currentSelected) { sortChange = true; mHandler.removeCallbacks(mDataRunnable); mHandler.post(mDataRunnable); }
                    } catch (Exception ignore) {}
                }
            });
        }
        if (tvFind!= null) {
            tvFind.setOnClickListener(v -> { FastClickCheckUtil.check(v); startActivity(new Intent(this, SearchActivity.class)); });
        }
        if (tvPlayerSetting!= null) {
            tvPlayerSetting.setOnClickListener(v -> { FastClickCheckUtil.check(v); startActivity(new Intent(this, SettingActivity.class)); });
        }
        if (tvHistory!= null) {
            tvHistory.setFocusable(true);
            tvHistory.setClickable(true);
            tvHistory.setOnClickListener(v -> { FastClickCheckUtil.check(v); openHistory(); });
        }
        if (tvMenu!= null) {
            tvMenu.setOnClickListener(v -> { FastClickCheckUtil.check(v); showSiteSwitch(); });
        }
    }
    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.sortResult.observe(this, absXml -> {
            try {
                List<MovieSort.SortData> customList = buildCustomSortList(absXml);
                sortAdapter.setNewData(customList);
                initViewPager(absXml, customList);
                showSuccess();
                if (tvName!= null) tvName.clearAnimation();
            } catch (Exception e) {
                e.printStackTrace();
                showSuccess();
            }
        });
    }
    private void initData() {
        try {
            if (useCacheConfig) {
                AbsSortXml absXml = ApiConfig.get().getCacheConfig();
                if (absXml!= null && absXml.classes!= null && absXml.classes.sortList!= null) {
                    List<MovieSort.SortData> customList = buildCustomSortList(absXml);
                    sortAdapter.setNewData(customList);
                    initViewPager(absXml, customList);
                    showSuccess();
                    return;
                }
            }
            tvNameAnimation();
            sourceViewModel.getSort(ApiConfig.get().getHomeSourceBean());
        } catch (Exception e) {
            showSuccess();
        }
    }

    private List<MovieSort.SortData> buildCustomSortList(AbsSortXml absXml) {
        List<MovieSort.SortData> result = new ArrayList<>();
        List<MovieSort.SortData> sourceList = new ArrayList<>();
        try {
            if (absXml!= null && absXml.classes!= null && absXml.classes.sortList!= null) {
                sourceList = absXml.classes.sortList;
            }
        } catch (Exception ignore) {}

        MovieSort.SortData home = new MovieSort.SortData();
        home.id = "home";
        home.name = "首页推荐";
        home.flag = "1";
        result.add(home);

        result.add(createSortData("电影", findTid(sourceList, new String[]{"电影"}), "movie"));
        result.add(createSortData("电视剧", findTid(sourceList, new String[]{"电视剧","电视","连续剧"}), "tv"));
        result.add(createSortData("综艺", findTid(sourceList, new String[]{"综艺"}), "variety"));
        result.add(createSortData("动漫", findTid(sourceList, new String[]{"动漫","动画","动漫片"}), "anime"));
        result.add(createSortData("短视频", findTid(sourceList, new String[]{"短剧","短视频"}), "short"));

        MovieSort.SortData live = new MovieSort.SortData();
        live.id = "live";
        live.name = "电视直播";
        live.flag = "1";
        result.add(live);

        result.add(createSortData("少儿", findTid(sourceList, new String[]{"少儿","儿童","教育","启蒙","小学","初中","高中"}), "child"));
        return result;
    }

    private MovieSort.SortData createSortData(String name, String tid, String fallback) {
        MovieSort.SortData data = new MovieSort.SortData();
        data.name = name;
        if (tid != null && !tid.isEmpty()) {
            data.id = tid;
        } else {
            data.id = fallback;
        }
        data.flag = "1";
        return data;
    }

    private String findTid(List<MovieSort.SortData> list, String[] keywords) {
        if (list == null) return null;
        for (String kw : keywords) {
            for (MovieSort.SortData d : list) {
                if (d.name != null && d.name.contains(kw)) {
                    return d.id;
                }
            }
        }
        return null;
    }

    private void initViewPager(AbsSortXml absXml, List<MovieSort.SortData> customList) {
        try {
            fragments.clear();
            if (customList!= null && customList.size() > 0) {
                for (MovieSort.SortData data : customList) {
                    if ("live".equals(data.id)) {
                        fragments.add(UserFragment.newInstance(null));
                    } else if ("home".equals(data.id)) {
                        MovieSort.SortData homeData = new MovieSort.SortData();
                        if (absXml!= null && absXml.classes!= null && absXml.classes.sortList!= null && absXml.classes.sortList.size() > 0) {
                            homeData = absXml.classes.sortList.get(0);
                            homeData.name = "首页推荐";
                        } else {
                            homeData.id = data.id;
                            homeData.name = data.name;
                        }
                        fragments.add(GridFragment.newInstance(homeData));
                    } else {
                        fragments.add(GridFragment.newInstance(data));
                    }
                }
                pageAdapter = new HomePageAdapter(getSupportFragmentManager(), fragments);
                try { Field field = ViewPager.class.getDeclaredField("mScroller"); field.setAccessible(true); FixedSpeedScroller scroller = new FixedSpeedScroller(mContext, new AccelerateInterpolator()); field.set(mViewPager, scroller); scroller.setmDuration(300); } catch (Exception e) {}
                if (mViewPager!= null) { mViewPager.setPageTransformer(true, new DefaultTransformer()); mViewPager.setAdapter(pageAdapter); mViewPager.setCurrentItem(currentSelected, false); }
            }
        } catch (Exception ignore) {}
    }

    private void initViewPager(AbsSortXml absXml) {
        try {
            List<MovieSort.SortData> customList = buildCustomSortList(absXml);
            initViewPager(absXml, customList);
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
    public void reloadHome() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean("useCache", false);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void refreshEmpty() { try { skipNextUpdate=true; showSuccess(); if (sortAdapter!= null) sortAdapter.setNewData(new ArrayList<>()); initViewPager(null, new ArrayList<>()); if (tvName!= null) tvName.clearAnimation(); } catch (Exception ignore) {} }
    private void tvNameAnimation() { try { if (tvName == null) return; AlphaAnimation blinkAnimation = new AlphaAnimation(0.0f, 1.0f); blinkAnimation.setDuration(500); blinkAnimation.setStartOffset(20); blinkAnimation.setRepeatMode(Animation.REVERSE); blinkAnimation.setRepeatCount(Animation.INFINITE); tvName.startAnimation(blinkAnimation); } catch (Exception ignore) {} }

    void openHistory() {
        try {
            List<VodInfo> history = null;
            try { history = AppDataManager.get().getAllVodRecord(); } catch (Exception ignore) {}
            if (history != null && history.size() > 0) {
                showHistoryDialog(history);
                return;
            }
            if (fragments.size() > 0) {
                for (int i=0;i<fragments.size();i++) {
                    if (fragments.get(i) instanceof UserFragment) {
                        sortFocused = i;
                        mHandler.removeCallbacks(mDataRunnable);
                        mHandler.post(mDataRunnable);
                        Toast.makeText(this, "已切换到观看历史/直播", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            Toast.makeText(this, "暂无观看记录，看过影片后这里会显示", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "打开历史失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showHistoryDialog(List<VodInfo> history) {
        try {
            SelectDialog<VodInfo> dialog = new SelectDialog<>(HomeActivity.this);
            dialog.setTip("观看历史 ("+history.size()+")");
            TvRecyclerView tvRecyclerView = dialog.findViewById(R.id.list);
            if (tvRecyclerView!= null) tvRecyclerView.setLayoutManager(new V7LinearLayoutManager(dialog.getContext(), 1, false));
            dialog.setAdapter(tvRecyclerView, new SelectDialogAdapter.SelectDialogInterface<VodInfo>() {
                @Override public void click(VodInfo value, int pos) {
                    try {
                        Intent intent = new Intent(mContext, DetailActivity.class);
                        intent.putExtra("id", value.id);
                        intent.putExtra("sourceKey", value.sourceKey);
                        startActivity(intent);
                        dialog.dismiss();
                    } catch (Exception e) {
                        Toast.makeText(mContext, "打开失败", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public String getDisplay(VodInfo val) { 
                    try { return val.name + " - " + (val.playNote!=null?val.playNote:""); } catch (Exception e) { return val.name; }
                }
            }, new DiffUtil.ItemCallback<VodInfo>() {
                @Override public boolean areItemsTheSame(@NonNull @NotNull VodInfo oldItem, @NonNull @NotNull VodInfo newItem) { return oldItem.id.equals(newItem.id); }
                @Override public boolean areContentsTheSame(@NonNull @NotNull VodInfo oldItem, @NonNull @NotNull VodInfo newItem) { return oldItem.id.equals(newItem.id); }
            }, history, 0);
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "显示历史失败: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
