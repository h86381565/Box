@Override
protected void init() {
    // 终极防闪退：Hawk没初始化就自己初始化
    try {
        com.orhanobut.hawk.Hawk.init(this).build();
    } catch (Exception ignore) {}
    try {
        HomeShow = com.orhanobut.hawk.Hawk.get(HawkConfig.HOME_SHOW_SOURCE, false);
    } catch (Exception e) {
        HomeShow = false;
    }
    try {
        AuthUtil.saveActivated(this);
    } catch (Exception e) {
        try { AuthUtil.saveActivated(); } catch (Exception ignore) {}
    }
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
