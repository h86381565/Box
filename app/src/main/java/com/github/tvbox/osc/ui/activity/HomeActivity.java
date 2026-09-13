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
        this.tvDraw = findViewById(R.id.tvDrawer);
        this.tvMenu = findViewById(R.id.tvMenu);
        try { int pid = getResources().getIdentifier("tvPlayerSetting", "id", getPackageName()); if (pid != 0) this.tvPlayerSetting = findViewById(pid); else this.tvPlayerSetting = findViewById(R.id.tvPlayerSetting); } catch (Exception e) { try { this.tvPlayerSetting = findViewById(R.id.tvPlayerSetting); } catch (Exception ignore) {} }
        try { int hid = getResources().getIdentifier("tvHistory", "id", getPackageName()); if (hid != 0) this.tvHistory = findViewById(hid); else this.tvHistory = findViewById(R.id.tvHistory); } catch (Exception e) { try { this.tvHistory = findViewById(R.id.tvHistory); } catch (Exception ignore) {} }
        this.tvDate = findViewById(R.id.tvDate);
        this.contentLayout = findViewById(R.id.contentLayout);
        this.mGridView = findViewById(R.id.mGridViewCategory);
        this.mViewPager = findViewById(R.id.mViewPager);
        if (mGridView!= null) {
            this.sortAdapter = new SortAdapter();
            this.mGridView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 1, false));
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
                        // 修复：点左边不弹出数据源，点首页推荐也不弹
                        if (currentSelected!= position) {
                            sortFocused = position;
                            mHandler.removeCallbacks(mDataRunnable);
                            mHandler.post(mDataRunnable);
                        } else {
                            // 已经在当前分类，再点一次：如果是GridFragment就打开筛选
                            BaseLazyFragment baseLazyFragment = null;
                            try { baseLazyFragment = fragments.get(currentSelected); } catch (Exception ignore) {}
                            if ((baseLazyFragment instanceof GridFragment) && sortData.filters!= null &&!sortData.filters.isEmpty()) {
                                ((GridFragment) baseLazyFragment).showFilter();
                            }
                            // 以前这里如果是UserFragment会弹showSiteSwitch，导致点首页推荐弹出数据源，已移除
                        }
                    } catch (Exception ignore) {}
                }
            });
        }
        if (tvFind!= null) {
            tvFind.setFocusable(true);
            tvFind.setOnClickListener(v -> { try { jumpActivity(SearchActivity.class); } catch (Exception e) { Toast.makeText(this, "搜索打开失败", Toast.LENGTH_SHORT).show(); } });
        }
        if (tvWifi!= null) { tvWifi.setFocusable(true); tvWifi.setOnClickListener(v -> { try { jumpActivity(SearchActivity.class); } catch (Exception e) {} }); }
        if (tvPlayerSetting!= null) {
            tvPlayerSetting.setFocusable(true);
            tvPlayerSetting.setClickable(true);
            // 恢复VIP设置，单击VIP，长按系统设置
            tvPlayerSetting.setOnClickListener(v -> {
                FastClickCheckUtil.check(v);
                try {
                    showPlayerSetting();
                } catch (Exception e) {
                    try { jumpActivity(SettingActivity.class); } catch (Exception ignore) {}
                }
            });
            tvPlayerSetting.setOnLongClickListener(v -> { try { jumpActivity(SettingActivity.class); } catch (Exception ignore) {} return true; });
        }
        // 筛选功能不要了，已在xml gone，这里不绑定
        try {
            View tvFilter = findViewById(getResources().getIdentifier("tvFilter", "id", getPackageName()));
            if (tvFilter != null) { tvFilter.setVisibility(View.GONE); }
        } catch (Exception ignore) {}
        if (tvName!= null) tvName.setOnClickListener(v -> { FastClickCheckUtil.check(v); try { File dir = getCacheDir(); FileUtils.recursiveDelete(dir); dir = getExternalCacheDir(); FileUtils.recursiveDelete(dir); } catch (Exception ignore) {} Toast.makeText(HomeActivity.this, getString(R.string.hm_cache_del), Toast.LENGTH_SHORT).show(); });
        if (tvName!= null) tvName.setOnLongClickListener(v->{ reloadHome(); return true; });
        if (tvDraw!= null) { tvDraw.setFocusable(true); tvDraw.setOnClickListener(v->{ jumpActivity(AppsActivity.class); }); }
        if (tvMenu!= null) { tvMenu.setFocusable(true); tvMenu.setClickable(true); tvMenu.setOnClickListener(v->{ FastClickCheckUtil.check(v); jumpActivity(SettingActivity.class); }); }
        // 长按系统设置也能调解码器，收费版方便调试
        if (tvMenu!= null) { tvMenu.setOnLongClickListener(v->{ showPlayerSetting(); return true; }); }
        if (tvDate!= null) { tvDate.setFocusable(false); tvDate.setClickable(false); tvDate.setFocusableInTouchMode(false); tvDate.setOnClickListener(null); }
        // 历史按钮 - 左侧观看记录 + 顶部历史，只留历史
        if (tvHistory!= null) {
            tvHistory.setFocusable(true);
            tvHistory.setClickable(true);
            tvHistory.setOnClickListener(v -> { FastClickCheckUtil.check(v); openHistory(); });
        }
        // 顶部历史按钮，只留历史，其他主页/直播/搜索/推送/收藏/设置已在xml gone
        try {
            View tvHistoryTop = findViewById(getResources().getIdentifier("tvHistoryTop", "id", getPackageName()));
            if (tvHistoryTop != null) {
                tvHistoryTop.setFocusable(true);
                tvHistoryTop.setClickable(true);
                tvHistoryTop.setOnClickListener(v -> { FastClickCheckUtil.check(v); openHistory(); });
            }
        } catch (Exception ignore) {}
        // 筛选功能不要了，强制隐藏
        try {
            View tvFilter = findViewById(getResources().getIdentifier("tvFilter", "id", getPackageName()));
            if (tvFilter != null) { tvFilter.setVisibility(View.GONE); tvFilter.setFocusable(false); tvFilter.setClickable(false); tvFilter.setOnClickListener(null); }
        } catch (Exception ignore) {}
        try {
            View tvFind = findViewById(getResources().getIdentifier("tvFind", "id", getPackageName()));
            if (tvFind != null) { tvFind.setVisibility(View.GONE); }
        } catch (Exception ignore) {}
        try {
            View tvPlayerSetting = findViewById(getResources().getIdentifier("tvPlayerSetting", "id", getPackageName()));
            if (tvPlayerSetting != null) { tvPlayerSetting.setVisibility(View.GONE); }
        } catch (Exception ignore) {}
        // 高级导航居中已在布局里通过item居中实现
        try { if (contentLayout!= null) setLoadSir(this.contentLayout); } catch (Exception ignore) {}
        // 隐藏左下角系统设置按钮，收费版只用顶部一个设置
        try { View v = findViewById(getResources().getIdentifier("tvBottomSetting", "id", getPackageName())); if (v!=null) v.setVisibility(View.GONE); } catch (Exception ignore) {}
        try { if (tvMenu!=null) tvMenu.setVisibility(View.GONE); } catch (Exception ignore) {}
        try { if (tvDraw!=null) tvDraw.setVisibility(View.GONE); } catch (Exception ignore) {}
    }


private void showPlayerSetting() {
        try {
            // 恢复VIP设置，全部用try包起来，防止Hawk没初始化导致设置内容无
            List<String> items = new ArrayList<>();
            String hint = "智能推荐";
            try { hint = getDeviceBestHint(); } catch (Exception ignore) {}
            String playerName = "IJK";
            try { playerName = getCurrentPlayerName(); } catch (Exception ignore) {}
            String decode = "硬解";
            try { decode = Hawk.get("PLAY_USE_SOFT", false) ? "软解" : "硬解"; } catch (Exception ignore) {}
            String adFilter = "开启 ★";
            try { adFilter = Hawk.get("PARSE_AD_FILTER", true) ? "开启 ★" : "关闭"; } catch (Exception ignore) {}
            String render = "TextureView";
            try { render = Hawk.get(HawkConfig.PLAY_RENDER, "TextureView"); } catch (Exception ignore) {}
            String search = "缩略图";
            try { search = Hawk.get("SEARCH_DISPLAY", "缩略图"); } catch (Exception ignore) {}
            String sniff = "系统自带";
            try { sniff = Hawk.get("SNIFF_WEBVIEW", "系统自带"); } catch (Exception ignore) {}
            String dns = "关闭";
            try { dns = Hawk.get("SECURE_DNS", false) ? "开启" : "关闭"; } catch (Exception ignore) {}

            items.add("★ 自动选择最优解码 (" + hint + ")");
            items.add("播放器内核: " + playerName);
            items.add("解码方式: " + decode);
            items.add("广告过滤: " + adFilter);
            items.add("---------- 高级设置 ----------");
            items.add("渲染方式: " + render);
            items.add("搜索展示: " + search);
            items.add("嗅探Webview: " + sniff);
            items.add("安全DNS: " + dns);
            items.add("切换线路");
            items.add("清理缓存");
            items.add("应用管理");
            items.add("观看记录");
            items.add("筛选全部影视");
            SelectDialog<String> dialog = new SelectDialog<>(this);
            dialog.setTip("ULTRA BOX PRO 设置");
            TvRecyclerView rv = dialog.findViewById(R.id.list);
            if (rv != null) rv.setLayoutManager(new V7LinearLayoutManager(dialog.getContext(), 1, false));
            dialog.setAdapter(rv, new SelectDialogAdapter.SelectDialogInterface<String>() {
                @Override public void click(String value, int pos) {
                    dialog.dismiss();
                    if (pos == 0) autoSelectBestDecoder();
                    else if (pos == 1) showPlayerTypeSwitch();
                    else if (pos == 2) showDecodeSwitch();
                    else if (pos == 3) {
                        try {
                            boolean cur = Hawk.get("PARSE_AD_FILTER", true);
                            Hawk.put("PARSE_AD_FILTER", !cur);
                            Toast.makeText(HomeActivity.this, !cur ? "广告过滤已开启" : "广告过滤已关闭", Toast.LENGTH_SHORT).show();
                        } catch (Exception ignore) {}
                    }
                    else if (pos == 5) showRenderSwitch();
                    else if (pos == 6) showSearchDisplaySwitch();
                    else if (pos == 7) showSniffSwitch();
                    else if (pos == 8) {
                        try {
                            boolean cur = Hawk.get("SECURE_DNS", false);
                            Hawk.put("SECURE_DNS", !cur);
                            Toast.makeText(HomeActivity.this, "安全DNS: " + (!cur ? "开启" : "关闭"), Toast.LENGTH_SHORT).show();
                        } catch (Exception ignore) {}
                    }
                    else if (pos == 9) showSiteSwitch();
                    else if (pos == 10) {
                        try { File dir = getCacheDir(); FileUtils.recursiveDelete(dir); dir = getExternalCacheDir(); FileUtils.recursiveDelete(dir); } catch (Exception ignore) {}
                        Toast.makeText(HomeActivity.this, "缓存已清理", Toast.LENGTH_SHORT).show();
                    }
                    else if (pos == 11) { try { jumpActivity(AppsActivity.class); } catch (Exception e) {} }
                    else if (pos == 12) { openHistory(); }
                    else if (pos == 13) { showAllFilter(); }
                }
                @Override public String getDisplay(String val) { return val; }
            }, new DiffUtil.ItemCallback<String>() {
                @Override public boolean areItemsTheSame(@NonNull String o, @NonNull String n) { return o.equals(n); }
                @Override public boolean areContentsTheSame(@NonNull String o, @NonNull String n) { return o.equals(n); }
            }, items, -1);
            dialog.show();
        } catch (Exception e) {
            // 兜底：最简单的VIP设置，保证不出现设置内容无
            try {
                List<String> items = new ArrayList<>();
                items.add("播放器内核: IJK");
                items.add("解码方式: 硬解");
                items.add("切换线路");
                items.add("清理缓存");
                items.add("观看记录");
                SelectDialog<String> dialog = new SelectDialog<>(this);
                dialog.setTip("ULTRA BOX PRO 设置");
                TvRecyclerView rv = dialog.findViewById(R.id.list);
                if (rv != null) rv.setLayoutManager(new V7LinearLayoutManager(dialog.getContext(), 1, false));
                dialog.setAdapter(rv, new SelectDialogAdapter.SelectDialogInterface<String>() {
                    @Override public void click(String v,int p){ dialog.dismiss(); if(p==0) showPlayerTypeSwitch(); else if(p==1) showDecodeSwitch(); else if(p==2) showSiteSwitch(); else if(p==4) openHistory(); }
                    @Override public String getDisplay(String val){return val;}
                }, new DiffUtil.ItemCallback<String>(){@Override public boolean areItemsTheSame(@NonNull String a,@NonNull String b){return a.equals(b);} @Override public boolean areContentsTheSame(@NonNull String a,@NonNull String b){return a.equals(b);}}, items, -1);
                dialog.show();
            } catch (Exception ignore) {}
        }
    }

    private void showAllFilter() {
        try {
            // 模仿爱奇艺筛选：点开后像第二张图那样，按类型/地区/时间查找
            List<String> filters = new ArrayList<>();
            filters.add("全部");
            filters.add("电视剧");
            filters.add("中剧");
            filters.add("短剧");
            filters.add("电影");
            filters.add("综艺");
            filters.add("动漫");
            filters.add("少儿");
            filters.add("漫剧");
            filters.add("纪录片");
            filters.add("知识");
            filters.add("---------- 类型 ----------");
            filters.add("喜剧");
            filters.add("爱情");
            filters.add("动作");
            filters.add("动画");
            filters.add("恐怖");
            filters.add("惊悚");
            filters.add("枪战");
            filters.add("科幻");
            filters.add("战争");
            filters.add("犯罪");
            filters.add("悬疑");
            filters.add("奇幻");
            filters.add("剧情");
            filters.add("青春");
            filters.add("冒险");
            filters.add("家庭");
            filters.add("警匪");
            filters.add("历史");
            filters.add("武侠");
            filters.add("灾难");
            filters.add("传记");
            filters.add("伦理");
            filters.add("运动");
            filters.add("音乐");
            filters.add("魔幻");
            filters.add("歌舞");
            filters.add("戏曲");
            filters.add("玄幻");
            filters.add("悲剧");
            filters.add("西部");
            filters.add("史诗");
            filters.add("---------- 地区 ----------");
            filters.add("内地");
            filters.add("中国香港");
            filters.add("中国台湾");
            filters.add("美国");
            filters.add("韩国");
            filters.add("日本");
            filters.add("英国");
            filters.add("---------- 时间 ----------");
            filters.add("即将上线");
            filters.add("2026");
            filters.add("2025");
            filters.add("2024");
            filters.add("2023");
            filters.add("2022");
            filters.add("2021");
            filters.add("2020");
            filters.add("10年代");
            filters.add("00年代");
            filters.add("90年代");
            filters.add("80年代");
            SelectDialog<String> d = new SelectDialog<>(this);
            d.setTip("筛选 - 按类型/地区/时间查找");
            TvRecyclerView rv = d.findViewById(R.id.list);
            if (rv != null) rv.setLayoutManager(new V7LinearLayoutManager(d.getContext(), 1, false));
            d.setAdapter(rv, new SelectDialogAdapter.SelectDialogInterface<String>() {
                @Override public void click(String v, int p) {
                    try {
                        if (v.startsWith("----------")) return;
                        String parent = "电影片";
                        if (v.equals("电视剧") || v.equals("中剧") || v.equals("短剧")) parent = "连续剧";
                        else if (v.equals("综艺")) parent = "综艺片";
                        else if (v.equals("动漫") || v.equals("少儿") || v.equals("漫剧")) parent = "动漫";
                        else if (v.equals("纪录片") || v.equals("知识")) parent = "连续剧";
                        else if (v.equals("电影")) parent = "电影片";
                        else if (v.equals("全部")) parent = "首页推荐";
                        if (sortAdapter != null) {
                            for (int i=0;i<sortAdapter.getData().size();i++) {
                                MovieSort.SortData sd = sortAdapter.getData().get(i);
                                if (sd.name != null && sd.name.equals(parent)) {
                                    sortFocused = i;
                                    mHandler.removeCallbacks(mDataRunnable);
                                    mHandler.post(mDataRunnable);
                                    break;
                                }
                            }
                        }
                        if (!v.equals(parent) && !v.equals("全部")) {
                            try {
                                Intent it = new Intent(HomeActivity.this, SearchActivity.class);
                                it.putExtra("keyword", v);
                                startActivity(it);
                            } catch (Exception ignore) {
                                Toast.makeText(HomeActivity.this, "筛选: " + v, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, "已切换: " + v, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ignore) {}
                    d.dismiss();
                }
                @Override public String getDisplay(String val){return val;}
            }, new DiffUtil.ItemCallback<String>(){@Override public boolean areItemsTheSame(@NonNull String a,@NonNull String b){return a.equals(b);} @Override public boolean areContentsTheSame(@NonNull String a,@NonNull String b){return a.equals(b);}}, filters, 0);
            d.show();
        } catch (Exception ignore) {}
    }

    private void openHistory() {
        try {
            // 换回普通版TVBox历史功能：跳官方HistoryActivity，不是对话框
            // 普通版就是你截图那种：顶部标题 历史记录 + 右上角删除/排序图标，空页面深蓝背景
            try {
                Intent it = new Intent(this, com.github.tvbox.osc.ui.activity.HistoryActivity.class);
                startActivity(it);
                return;
            } catch (Exception ignore) {}
            try {
                Intent it = new Intent();
                it.setClassName(this, "com.github.tvbox.osc.ui.activity.HistoryActivity");
                startActivity(it);
                return;
            } catch (Exception ignore) {}
        } catch (Exception e) {
            try {
                Toast.makeText(this, "正在打开历史记录...", Toast.LENGTH_SHORT).show();
                Intent it = new Intent();
                it.setClassName(this, "com.github.tvbox.osc.ui.activity.HistoryActivity");
                startActivity(it);
            } catch (Exception ignore) {
                try { jumpActivity(com.github.tvbox.osc.ui.activity.SearchActivity.class); } catch (Exception ex) {}
            }
        }
    }

    private void showRenderSwitch() {
        try {
            List<String> list = new ArrayList<>(); list.add("TextureView"); list.add("SurfaceView");
            int cur = "SurfaceView".equals(Hawk.get(HawkConfig.PLAY_RENDER, "TextureView")) ? 1 : 0;
            SelectDialog<String> d = new SelectDialog<>(this); d.setTip("渲染方式");
            TvRecyclerView rv = d.findViewById(R.id.list); if (rv!=null) rv.setLayoutManager(new V7LinearLayoutManager(d.getContext(),1,false));
            d.setAdapter(rv, new SelectDialogAdapter.SelectDialogInterface<String>() {
                @Override public void click(String v,int p){ Hawk.put(HawkConfig.PLAY_RENDER, v); Toast.makeText(HomeActivity.this,"已切换: "+v,Toast.LENGTH_SHORT).show(); d.dismiss();}
                @Override public String getDisplay(String val){return val;}
            }, new DiffUtil.ItemCallback<String>(){@Override public boolean areItemsTheSame(@NonNull String a,@NonNull String b){return a.equals(b);} @Override public boolean areContentsTheSame(@NonNull String a,@NonNull String b){return a.equals(b);}}, list, cur); d.show();
        } catch (Exception ignore){}
    }

    private void showSearchDisplaySwitch() {
        try {
            List<String> list = new ArrayList<>(); list.add("缩略图"); list.add("列表");
            String curStr = Hawk.get("SEARCH_DISPLAY", "缩略图"); int cur = "列表".equals(curStr) ? 1 : 0;
            SelectDialog<String> d = new SelectDialog<>(this); d.setTip("搜索展示");
            TvRecyclerView rv = d.findViewById(R.id.list); if (rv!=null) rv.setLayoutManager(new V7LinearLayoutManager(d.getContext(),1,false));
            d.setAdapter(rv, new SelectDialogAdapter.SelectDialogInterface<String>() {
                @Override public void click(String v,int p){ Hawk.put("SEARCH_DISPLAY", v); Toast.makeText(HomeActivity.this,"已切换: "+v,Toast.LENGTH_SHORT).show(); d.dismiss();}
                @Override public String getDisplay(String val){return val;}
            }, new DiffUtil.ItemCallback<String>(){@Override public boolean areItemsTheSame(@NonNull String a,@NonNull String b){return a.equals(b);} @Override public boolean areContentsTheSame(@NonNull String a,@NonNull String b){return a.equals(b);}}, list, cur); d.show();
        } catch (Exception ignore){}
    }

    private void showSniffSwitch() {
        try {
            List<String> list = new ArrayList<>(); list.add("系统自带"); list.add("XWalk");
            String curStr = Hawk.get("SNIFF_WEBVIEW", "系统自带"); int cur = "XWalk".equals(curStr) ? 1 : 0;
            SelectDialog<String> d = new SelectDialog<>(this); d.setTip("嗅探Webview");
            TvRecyclerView rv = d.findViewById(R.id.list); if (rv!=null) rv.setLayoutManager(new V7LinearLayoutManager(d.getContext(),1,false));
            d.setAdapter(rv, new SelectDialogAdapter.SelectDialogInterface<String>() {
                @Override public void click(String v,int p){ Hawk.put("SNIFF_WEBVIEW", v); Toast.makeText(HomeActivity.this,"已切换: "+v,Toast.LENGTH_SHORT).show(); d.dismiss();}
                @Override public String getDisplay(String val){return val;}
            }, new DiffUtil.ItemCallback<String>(){@Override public boolean areItemsTheSame(@NonNull String a,@NonNull String b){return a.equals(b);} @Override public boolean areContentsTheSame(@NonNull String a,@NonNull String b){return a.equals(b);}}, list, cur); d.show();
        } catch (Exception ignore){}
    }


    private void autoSelectBestDecoder() {
        try {
            boolean isLowRam = false; boolean is4K = false;
            try {
                android.app.ActivityManager am = (android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                if (am != null) isLowRam = am.isLowRamDevice();
                android.util.DisplayMetrics dm = getResources().getDisplayMetrics();
                is4K = dm.widthPixels >= 3840 || dm.heightPixels >= 2160;
            } catch (Exception ignore) {}
            int best; boolean soft; String reason;
            if (is4K) { best = 2; soft = false; reason = "检测到4K，已设为 Exo+硬解"; }
            else if (isLowRam || android.os.Build.VERSION.SDK_INT < 24) { best = 1; soft = true; reason = "老设备，已设为 IJK+软解"; }
            else { best = 1; soft = false; reason = "已设为 IJK+硬解 平衡"; }
            Hawk.put(HawkConfig.PLAY_TYPE, best);
            Hawk.put("PLAY_USE_SOFT", soft);
            Toast.makeText(this, reason, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Hawk.put(HawkConfig.PLAY_TYPE, 1); Hawk.put("PLAY_USE_SOFT", false);
        }
    }

    private String getDeviceBestHint() {
        try {
            android.util.DisplayMetrics dm = getResources().getDisplayMetrics();
            if (dm.widthPixels >= 3840) return "推荐: Exo硬解";
            android.app.ActivityManager am = (android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null && am.isLowRamDevice()) return "推荐: IJK软解";
            return "推荐: IJK硬解";
        } catch (Exception e) { return "智能推荐"; }
    }

    private String getCurrentPlayerName() {
        try {
            int type = Hawk.get(HawkConfig.PLAY_TYPE, 1);
            String ext = Hawk.get("EXT_PLAY_TYPE", "");
            if (!ext.isEmpty()) return ext;
            if (type == 0) return "系统";
            if (type == 1) return Hawk.get("PLAY_USE_SOFT", false) ? "IJK软解" : "IJK硬解";
            if (type == 2) return Hawk.get("PLAY_USE_SOFT", false) ? "Exo软解" : "Exo硬解";
            return "IJK硬解";
        } catch (Exception e) { return "IJK硬解"; }
    }

    private void showPlayerTypeSwitch() {
        try {
            List<String> players = new ArrayList<>();
            players.add("系统");
            players.add("IJK - 默认");
            players.add("Exo - 4K推荐");
            players.add("阿里");
            players.add("MX Player");
            players.add("Reex Player");
            players.add("Kodi");
            players.add("IJK硬解 - 平衡");
            players.add("IJK软解 - 兼容");
            int type = Hawk.get(HawkConfig.PLAY_TYPE, 1);
            String ext = Hawk.get("EXT_PLAY_TYPE", "");
            boolean isSoft = Hawk.get("PLAY_USE_SOFT", false);
            int cur = 1;
            if (type == 0) cur = 0;
            else if (type == 1 && (ext == null || ext.isEmpty()) && !isSoft) cur = 1;
            else if (type == 2 && (ext == null || ext.isEmpty())) cur = 2;
            else if ("ALI".equals(ext)) cur = 3;
            else if ("MX".equals(ext)) cur = 4;
            else if ("REEX".equals(ext)) cur = 5;
            else if ("KODI".equals(ext)) cur = 6;
            else if (type == 1 && !isSoft) cur = 7;
            else if (type == 1 && isSoft) cur = 8;

            SelectDialog<String> dialog = new SelectDialog<>(this);
            dialog.setTip("请选择默认播放器");
            TvRecyclerView rv = dialog.findViewById(R.id.list);
            if (rv != null) rv.setLayoutManager(new V7LinearLayoutManager(dialog.getContext(), 1, false));
            dialog.setAdapter(rv, new SelectDialogAdapter.SelectDialogInterface<String>() {
                @Override public void click(String value, int pos) {
                    if (pos == 0) { Hawk.put(HawkConfig.PLAY_TYPE, 0); Hawk.put("EXT_PLAY_TYPE", ""); }
                    else if (pos == 1) { Hawk.put(HawkConfig.PLAY_TYPE, 1); Hawk.put("PLAY_USE_SOFT", false); Hawk.put("EXT_PLAY_TYPE", ""); }
                    else if (pos == 2) { Hawk.put(HawkConfig.PLAY_TYPE, 2); Hawk.put("EXT_PLAY_TYPE", ""); }
                    else if (pos == 3) { Hawk.put(HawkConfig.PLAY_TYPE, 1); Hawk.put("EXT_PLAY_TYPE", "ALI"); }
                    else if (pos == 4) { Hawk.put(HawkConfig.PLAY_TYPE, 1); Hawk.put("EXT_PLAY_TYPE", "MX"); }
                    else if (pos == 5) { Hawk.put(HawkConfig.PLAY_TYPE, 1); Hawk.put("EXT_PLAY_TYPE", "REEX"); }
                    else if (pos == 6) { Hawk.put(HawkConfig.PLAY_TYPE, 1); Hawk.put("EXT_PLAY_TYPE", "KODI"); }
                    else if (pos == 7) { Hawk.put(HawkConfig.PLAY_TYPE, 1); Hawk.put("PLAY_USE_SOFT", false); Hawk.put("EXT_PLAY_TYPE", ""); }
                    else if (pos == 8) { Hawk.put(HawkConfig.PLAY_TYPE, 1); Hawk.put("PLAY_USE_SOFT", true); Hawk.put("EXT_PLAY_TYPE", ""); }
                    Toast.makeText(HomeActivity.this, "已切换为: " + value, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                @Override public String getDisplay(String val) { return val; }
            }, new DiffUtil.ItemCallback<String>() {
                @Override public boolean areItemsTheSame(@NonNull String o, @NonNull String n) { return o.equals(n); }
                @Override public boolean areContentsTheSame(@NonNull String o, @NonNull String n) { return o.equals(n); }
            }, players, cur);
            dialog.show();
        } catch (Exception ignore) {}
    }

    private void showDecodeSwitch() {
        try {
            List<String> decodes = new ArrayList<>();
            decodes.add("硬解码 - 省电秒开 ★");
            decodes.add("软解码 - 兼容");
            decodes.add("自动切换 (推荐)");
            int mode = Hawk.get("DECODE_MODE", 0);
            SelectDialog<String> dialog = new SelectDialog<>(this);
            dialog.setTip("解码方式");
            TvRecyclerView rv = dialog.findViewById(R.id.list);
            if (rv != null) rv.setLayoutManager(new V7LinearLayoutManager(dialog.getContext(), 1, false));
            dialog.setAdapter(rv, new SelectDialogAdapter.SelectDialogInterface<String>() {
                @Override public void click(String value, int pos) {
                    if (pos == 0) Hawk.put("PLAY_USE_SOFT", false);
                    else if (pos == 1) Hawk.put("PLAY_USE_SOFT", true);
                    else if (pos == 2) { Hawk.put("PLAY_USE_SOFT", false); Hawk.put(HawkConfig.PLAY_TYPE, 2); }
                    Toast.makeText(HomeActivity.this, "已切换: " + value, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                @Override public String getDisplay(String val) { return val; }
            }, new DiffUtil.ItemCallback<String>() {
                @Override public boolean areItemsTheSame(@NonNull String o, @NonNull String n) { return o.equals(n); }
                @Override public boolean areContentsTheSame(@NonNull String o, @NonNull String n) { return o.equals(n); }
            }, decodes, mode);
            dialog.show();
        } catch (Exception ignore) {}
    }
    private boolean skipNextUpdate
 = false;
    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.sortResult.observe(this, absXml -> {
            if (skipNextUpdate) { skipNextUpdate = false; return; }
            showSuccess();
            List<MovieSort.SortData> original = new ArrayList<>();
            try {
                if (absXml!= null && absXml.classes!= null && absXml.classes.sortList!= null) {
                    original = absXml.classes.sortList;
                }
            } catch (Exception e) { original = new ArrayList<>(); }

            // ========= 固定非凡影视为主页，7大类全部读取非凡影视 =========
            try {
                SourceBean home = ApiConfig.get().getHomeSourceBean();
                if (home == null || !"ffzy_hd".equals(home.getKey())) {
                    for (SourceBean sb : ApiConfig.get().getSourceBeanList()) {
                        if ("ffzy_hd".equals(sb.getKey())) {
                            ApiConfig.get().setSourceBean(sb);
                            break;
                        }
                    }
                }
                try {
                    Hawk.put("CATEGORY_SOURCE", new java.util.HashMap<String, String>() {{
                        put("首页推荐", "ffzy_hd");
                        put("电影片", "ffzy_hd");
                        put("连续剧", "ffzy_hd");
                        put("综艺片", "ffzy_hd");
                        put("少儿", "ffzy_hd");
                        put("动漫", "ffzy_hd");
                        put("短剧", "ffzy_hd");
                    }});
                    // 换回截图那样的二级筛选
                    Hawk.put("DIANYING_8CLASS", "动作片,喜剧片,爱情片,科幻片,恐怖片,剧情片,战争片,伦理片");
                    Hawk.put("LIANXUJU_8CLASS", "国产剧,香港剧,韩国剧,欧美剧,记录片,台湾剧,日本剧,泰国剧");
                    Hawk.put("ZONGYI_4CLASS", "大陆综艺,港台综艺,日韩综艺,欧美综艺");
                    Hawk.put("DONGMAN_6CLASS", "国产动漫,日韩动漫,欧美动漫,港台动漫,海外动漫,伦理片,短剧");
                    Hawk.put("SHAOER_2CLASS", "国内少儿,国外少儿");
                    Hawk.put("DUANJU_1CLASS", "国内短剧");
                    Hawk.put("FILTER_STYLE", "old_grid"); // 标记用旧版网格筛选，像截图
                } catch (Exception ignore) {}
            } catch (Exception ignore) {}

            // 2. 永久7个全部非凡影视：
            // 首页推荐("")=今年最近新电视剧、电影
            // 电影片(1)=动作片/喜剧片/爱情片/科幻片/恐怖片/剧情片/战争片/伦理片
            // 连续剧(2)=国产剧/香港剧/韩国剧/欧美剧/纪录片/台湾剧/日本剧
            // 综艺片(3)=大陆综艺/港台综艺/日韩综艺/欧美综艺
            // 少儿(4)=国内少儿/国外少儿
            // 动漫(5)=国产/日韩/欧美/港台/海外动漫
            // 短剧(6)=国内短剧
            // 修复：电影片/连续剧等找不到数据，电影数据跑到短剧，因为写死了1-6，非凡真实tid不是1-6
            // 改为从接口返回的original里按名字匹配真实id
            // 2. 永久7个全部非凡影视，使用接口返回的真实tid，解决电影数据跑到短剧，没找到数据
            // 首页推荐("")=今年最近新电视剧、电影
            // 电影片=动作片/喜剧片/爱情片/科幻片/恐怖片/剧情片/战争片/伦理片 (ffzy tid 1)
            // 连续剧=国产剧/香港剧/韩国剧/欧美剧/纪录片/台湾剧/日本剧 (tid 2)
            // 综艺片=大陆综艺/港台综艺/日韩综艺/欧美综艺 (tid 3)
            // 少儿=国内少儿/国外少儿 (tid 4/29)
            // 动漫=国产/日韩/欧美/港台/海外动漫 (tid 4/5)
            // 短剧=国内短剧 (tid 6/短剧)
            List<MovieSort.SortData> locked = new ArrayList<>();
            // 恢复成这样的导航 - 按你4张截图：图1动漫片/动作片... 图2国产剧... 图3泰国剧... 图4欧美综艺...
            String[][] clean = new String[][]{
                {"", "首页推荐"},
                {"1", "电影片"},
                {"2", "连续剧"},
                {"3", "综艺片"},
                {"5", "动漫片"},
                // 图1：动作片/喜剧片/爱情片/科幻片/恐怖片/剧情片/战争片
                {"1", "动作片"},
                {"1", "喜剧片"},
                {"1", "爱情片"},
                {"1", "科幻片"},
                {"1", "恐怖片"},
                {"1", "剧情片"},
                {"1", "战争片"},
                // 图2：国产剧/香港剧/韩国剧/欧美剧/记录片/台湾剧/日本剧/海外剧
                {"2", "国产剧"},
                {"2", "香港剧"},
                {"2", "韩国剧"},
                {"2", "欧美剧"},
                {"2", "记录片"},
                {"2", "台湾剧"},
                {"2", "日本剧"},
                {"2", "海外剧"},
                // 图3+图4：泰国剧/大陆综艺/港台综艺/日韩综艺/欧美综艺/国产动漫/日韩动漫/欧美动漫/港台动漫/海外动漫/伦理片/短剧
                {"2", "泰国剧"},
                {"3", "大陆综艺"},
                {"3", "港台综艺"},
                {"3", "日韩综艺"},
                {"3", "欧美综艺"},
                {"5", "国产动漫"},
                {"5", "日韩动漫"},
                {"5", "欧美动漫"},
                {"5", "港台动漫"},
                {"5", "海外动漫"},
                {"1", "伦理片"},
                {"6", "短剧"},
                {"4", "少儿"}
            };
            for (int idx=0; idx<clean.length; idx++) {
                String[] kv = clean[idx];
                String wantId = kv[0];
                String wantName = kv[1];
                MovieSort.SortData found = null;
                for (MovieSort.SortData o : original) {
                    if (o == null || o.name == null) continue;
                    String n = o.name.trim();
                    if (wantName.equals("电影片") && (n.contains("电影") || n.equals("电影片"))) { found = o; break; }
                    if (wantName.equals("连续剧") && (n.contains("连续剧") || n.contains("电视剧"))) { found = o; break; }
                    if (wantName.equals("综艺片") && (n.contains("综艺") || n.equals("综艺片"))) { found = o; break; }
                    if (wantName.equals("动漫片") && n.contains("动漫")) { found = o; break; }
                    if (wantName.equals("少儿") && n.contains("少儿")) { found = o; break; }
                    if (wantName.equals("短剧") && n.contains("短剧")) { found = o; break; }
                    // 子分类全部用主分类的真实id，靠filter区分
                    if (wantName.equals("动作片") || wantName.equals("喜剧片") || wantName.equals("爱情片") || wantName.equals("科幻片") || wantName.equals("恐怖片") || wantName.equals("剧情片") || wantName.equals("战争片") || wantName.equals("伦理片")) {
                        if (n.contains("电影")) { found = o; break; }
                    }
                    if (wantName.equals("国产剧") || wantName.equals("香港剧") || wantName.equals("韩国剧") || wantName.equals("欧美剧") || wantName.equals("记录片") || wantName.equals("台湾剧") || wantName.equals("日本剧") || wantName.equals("海外剧") || wantName.equals("泰国剧")) {
                        if (n.contains("连续剧") || n.contains("电视剧")) { found = o; break; }
                    }
                    if (wantName.equals("大陆综艺") || wantName.equals("港台综艺") || wantName.equals("日韩综艺") || wantName.equals("欧美综艺")) {
                        if (n.contains("综艺")) { found = o; break; }
                    }
                    if (wantName.equals("国产动漫") || wantName.equals("日韩动漫") || wantName.equals("欧美动漫") || wantName.equals("港台动漫") || wantName.equals("海外动漫")) {
                        if (n.contains("动漫")) { found = o; break; }
                    }
                    if (wantName.equals("首页推荐") && (n.contains("推荐") || n.contains("首页"))) { found = o; break; }
                }
                MovieSort.SortData sd = new MovieSort.SortData();
                if (found != null) {
                    sd.id = found.id;
                    sd.name = wantName;
                    try { sd.filters = found.filters; } catch (Exception ignore) {}
                } else {
                    sd.id = wantId;
                    sd.name = wantName;
                }
                if (wantName.equals("首页推荐")) sd.id = "home_latest_2025_2026";
                // 记录filter
                try { if (!wantName.equals("首页推荐") && !wantName.equals("电影片") && !wantName.equals("连续剧") && !wantName.equals("综艺片") && !wantName.equals("动漫片")) Hawk.put("FILTER_" + wantName, wantName); } catch (Exception ignore) {}
                locked.add(sd);
            }

            List<MovieSort.SortData> list = locked;
            try { Hawk.put("LOCKED_SORT_LIST", list); } catch (Exception ignore) {}

            sortAdapter.setNewData(list);
            initViewPager(absXml);
            SourceBean homeBean = ApiConfig.get().getHomeSourceBean();
            if (HomeShow && tvName!= null && homeBean!= null && homeBean.getName()!= null &&!homeBean.getName().isEmpty()) { tvName.setText(homeBean.getName()); tvName.clearAnimation(); }
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
                    MovieSort.SortData real = new MovieSort.SortData();
                    if ("home_latest_2025_2026".equals(data.id)) {
                        real.id = ""; // 首页推荐 = 今年最近新电视剧、电影
                        real.name = data.name;
                    } else if ("4".equals(data.id) && "少儿".equals(data.name)) {
                        real.id = "4"; // 少儿 = 国内1-16岁动画片 1-16岁
                        real.name = "少儿";
                        // 标记少儿内容，后续可通过搜索关键词过滤
                        try { Hawk.put("SHAOER_FILTER", "少儿动画 儿童 1-16岁 益智"); } catch (Exception ignore) {}
                    } else if ("5".equals(data.id)) {
                        real.id = "5"; // 动漫 = 国产/日韩/欧美/港台/海外动漫 (wogg tid 5)
                        real.name = "动漫";
                        try { Hawk.put("DONGMAN_FILTER", "国产动漫 日韩动漫 欧美动漫 港台动漫 海外动漫"); } catch (Exception ignore) {}
                    } else {
                        real.id = data.id;
                        real.name = data.name;
                    }
                    fragments.add(GridFragment.newInstance(real));
                }
                pageAdapter = new HomePageAdapter(getSupportFragmentManager(), fragments);
                try { Field field = ViewPager.class.getDeclaredField("mScroller"); field.setAccessible(true); FixedSpeedScroller scroller = new FixedSpeedScroller(mContext, new AccelerateInterpolator()); field.set(mViewPager, scroller); scroller.setmDuration(300); } catch (Exception e) {}
                if (mViewPager!= null) {
                    mViewPager.setOffscreenPageLimit(7);
                    mViewPager.setPageTransformer(true, new DefaultTransformer());
                    mViewPager.setAdapter(pageAdapter);
                    mViewPager.setCurrentItem(currentSelected, false);
                }
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
                else if (this.sortFocused!= 0) {
                    sortFocused = 0;
                    mHandler.removeCallbacks(mDataRunnable);
                    mHandler.post(mDataRunnable);
                    try { if (mGridView != null) mGridView.setSelection(0); } catch (Exception ignore) {}
                    return;
                } else doExit();
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
    private final Runnable mDataRunnable = new Runnable() {
        @Override public void run() {
            if (sortChange) {
                sortChange = false;
                if (sortFocused != currentSelected) {
                    currentSelected = sortFocused;
                    try {
                        String catName = "";
                        if (sortAdapter != null && sortFocused < sortAdapter.getData().size()) {
                            catName = sortAdapter.getData().get(sortFocused).name;
                        }
                        String target = "ffzy_hd";
                        if ("少儿".equals(catName)) target = "wogg_4k";
                        else if ("动漫".equals(catName)) target = "wogg_4k";
                        else if ("短剧".equals(catName)) target = "yangzi";
                        for (SourceBean sb : ApiConfig.get().getSourceBeanList()) {
                            if (target.equals(sb.getKey())) {
                                ApiConfig.get().setSourceBean(sb);
                                break;
                            }
                        }
                    } catch (Exception ignore) {}
                    if (mViewPager != null) mViewPager.setCurrentItem(sortFocused, false);
                }
            }
        }
    };
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
    private void refreshEmpty() { try { skipNextUpdate=true; showSuccess(); if (sortAdapter!= null) sortAdapter.setNewData(DefaultConfig.adjustSort(ApiConfig.get().getHomeSourceBean().getKey(), new ArrayList<>(), true)); initViewPager(null); if (tvName!= null) tvName.clearAnimation(); } catch (Exception ignore) {} }
    private void tvNameAnimation() { try { if (tvName == null) return; AlphaAnimation blinkAnimation = new AlphaAnimation(0.0f, 1.0f); blinkAnimation.setDuration(500); blinkAnimation.setStartOffset(20); blinkAnimation.setRepeatMode(Animation.REVERSE); blinkAnimation.setRepeatCount(Animation.INFINITE); tvName.startAnimation(blinkAnimation); } catch (Exception ignore) {} }
}
