package com.github.tvbox.osc.util;

import static com.bumptech.glide.load.resource.bitmap.VideoDecoder.FRAME_OPTION;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.bean.SourceBean;
import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * ImgUtil V3
 *
 * 针对 TV 端海报加载慢/灰色占位的问题：
 * 1. 不再手动设置 HTTP Host，避免部分图片服务器/CDN因 Host 不匹配而拒绝请求。
 * 2. 默认 User-Agent 改为固定值，不再每次随机 UA。
 *    随机 UA 会让 Glide 的 header cache key 不稳定，导致同一海报反复无法命中缓存。
 * 3. 使用 DiskCacheStrategy.ALL，保留原图数据和转换后的资源缓存。
 * 4. GridAdapter 可传入 override 尺寸，降低电视端图片解码压力。
 * 5. 图片失败写入 Logcat，方便继续定位具体图片域名。
 * 6. 保留原有 @Headers / @Cookie / @User-Agent / @Referer 兼容方式。
 */
public class ImgUtil {
    private static final String TAG = "ImgUtilV3";

    public static int defaultWidth = 244;
    public static int defaultHeight = 320;

    // 固定 UA：避免每次随机 UA 导致 Glide cache key 不稳定。
    private static final String DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Linux; Android 13; TV) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/120.0 Mobile Safari/537.36";

    public static class Style {
        public float ratio;
        public String type;

        public Style(float ratio, String type) {
            this.ratio = ratio;
            this.type = type;
        }
    }

    public static Style initStyle() {
        try {
            if (ApiConfig.get() == null) return null;

            SourceBean bean = ApiConfig.get().getHomeSourceBean();
            if (bean == null) return null;

            String bStyle = bean.getStyle();
            if (TextUtils.isEmpty(bStyle)) return null;

            try {
                JSONObject jsonObject = new JSONObject(bStyle);
                float ratio = (float) jsonObject.getDouble("ratio");
                String type = jsonObject.getString("type");
                return new Style(ratio, type);
            } catch (JSONException e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static int spanCountByStyle(Style style, int defaultCount) {
        int spanCount = defaultCount;
        if (style == null) return spanCount;

        if ("rect".equals(style.type)) {
            if (style.ratio >= 1.7) {
                spanCount = 3;
            } else if (style.ratio >= 1.3) {
                spanCount = 4;
            }
        } else if ("list".equals(style.type)) {
            spanCount = 1;
        }

        return spanCount;
    }

    public static int getStyleDefaultWidth(Style style) {
        if (style == null) return 280;

        int styleDefaultWidth = 280;
        if (style.ratio < 1) styleDefaultWidth = 214;
        if (style.ratio > 1.7) styleDefaultWidth = 380;

        return styleDefaultWidth;
    }

    public static void load(String url, ImageView view, int roundingRadius) {
        load(url, view, roundingRadius, 0, 0);
    }

    public static void load(String url, ImageView view, int roundingRadius,
                            int newWidth, int newHeight) {
        if (view == null) return;

        view.setScaleType(ImageView.ScaleType.CENTER);

        if (TextUtils.isEmpty(url)) {
            view.setImageResource(R.drawable.img_loading_placeholder);
            return;
        }

        url = url.trim();
        if (url.length() == 0) {
            view.setImageResource(R.drawable.img_loading_placeholder);
            return;
        }

        if (roundingRadius == 0) roundingRadius = 1;

        RequestOptions requestOptions = new RequestOptions()
                .format(DecodeFormat.PREFER_RGB_565)
                // V3：缓存原始数据 + 转换后的资源。
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .transform(new CenterCrop(), new RoundedCorners(roundingRadius));

        if (newWidth > 0 && newHeight > 0) {
            requestOptions = requestOptions.override(newWidth, newHeight);
        }

        Object glideUrl = getUrl(url);
        if (glideUrl == null) {
            view.setImageResource(R.drawable.img_loading_placeholder);
            return;
        }

        Glide.with(App.getInstance())
                .load(glideUrl)
                .placeholder(R.drawable.img_loading_placeholder)
                .error(R.drawable.img_loading_placeholder)
                .listener(getListener(view, ImageView.ScaleType.FIT_XY, url))
                .apply(requestOptions)
                .into(view);
    }

    public static void loadVideoScreenshot(String uri, ImageView imageView, long frameTimeMicros) {
        RequestOptions requestOptions = RequestOptions.frameOf(frameTimeMicros * 1000)
                .set(FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST)
                .transform(new CenterCrop(), new RoundedCorners(10));

        Glide.with(App.getInstance())
                .load(uri)
                .skipMemoryCache(true)
                .apply(requestOptions)
                .into(imageView);
    }

    public static String getDiskCacheStrategyName(int index) {
        String[] names = new String[] {
                "[NONE] 关闭",
                "[RESOURCE] 转换图片",
                "[DATA] 原始图片 ",
                "[ALL] 原始图片和转换图片",
                "[AUTOMATIC] 自动"
        };
        return names[index];
    }

    public static DiskCacheStrategy getDiskCacheStrategy(int index) {
        switch (index) {
            case 1:
                return DiskCacheStrategy.RESOURCE;
            case 2:
                return DiskCacheStrategy.DATA;
            case 3:
                return DiskCacheStrategy.ALL;
            case 4:
                return DiskCacheStrategy.AUTOMATIC;
            default:
                return DiskCacheStrategy.NONE;
        }
    }

    /**
     * 将带有 @Headers/@Cookie/@User-Agent/@Referer 的图片 URL
     * 转换成 GlideUrl。
     *
     * V3 关键修改：
     * - 不再手动加入 Host。
     * - 默认 UA 固定，避免随机 UA 破坏 Glide 缓存命中。
     */
    private static Object getUrl(String url) {
        if (url == null) return null;

        if (url.startsWith("data:")) return url;

        url = DefaultConfig.checkReplaceProxy(url);
        if (TextUtils.isEmpty(url)) return null;

        String header = null;
        String referer = null;
        String ua = DEFAULT_USER_AGENT;
        String cookie = null;

        // 豆瓣图片兼容逻辑保留。
        if (url.contains("doubanio.com")
                && !url.contains("@Referer=")
                && !url.contains("@User-Agent=")) {
            url += "@Referer=https://api.douban.com/@User-Agent=" + ua;
        }

        if (url.contains("@Headers=")) {
            header = url.split("@Headers=")[1].split("@")[0];
            try {
                header = URLDecoder.decode(header, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.w(TAG, "Header URL decode failed", e);
            }
        }

        if (url.contains("@Cookie=")) {
            cookie = url.split("@Cookie=")[1].split("@")[0];
        }

        if (url.contains("@User-Agent=")) {
            ua = url.split("@User-Agent=")[1].split("@")[0];
        }

        if (url.contains("@Referer=")) {
            referer = url.split("@Referer=")[1].split("@")[0];
        }

        url = url.split("@")[0];

        if (TextUtils.isEmpty(url)) return null;

        LazyHeaders.Builder builder = new LazyHeaders.Builder();

        if (!TextUtils.isEmpty(header)) {
            try {
                JsonObject jsonInfo = new Gson().fromJson(header, JsonObject.class);
                if (jsonInfo != null) {
                    for (String key : jsonInfo.keySet()) {
                        if (jsonInfo.get(key) != null && !jsonInfo.get(key).isJsonNull()) {
                            String val = jsonInfo.get(key).getAsString();
                            if (!TextUtils.isEmpty(key) && val != null) {
                                builder.addHeader(key, val);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Invalid @Headers JSON: " + header, e);
            }
        } else {
            if (!TextUtils.isEmpty(cookie)) {
                builder.addHeader(HttpHeaders.COOKIE, cookie);
            }

            if (!TextUtils.isEmpty(ua)) {
                builder.addHeader(HttpHeaders.USER_AGENT, ua);
            }

            if (!TextUtils.isEmpty(referer)) {
                builder.addHeader(HttpHeaders.REFERER, referer);
            }
        }

        // V3 特意不再 builder.addHeader(HttpHeaders.HOST, ...);
        // Host 应由底层 HTTP 客户端根据实际 URL 自动生成。

        try {
            new URL(url); // 仅用于提前验证 URL 格式。
        } catch (MalformedURLException e) {
            Log.w(TAG, "Invalid image URL: " + url, e);
            return null;
        }

        return new GlideUrl(url, builder.build());
    }

    private static RequestListener<Bitmap> getListener(
            ImageView view,
            ImageView.ScaleType scaleType,
            String originalUrl) {

        return new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(
                    @Nullable GlideException e,
                    Object model,
                    Target<Bitmap> target,
                    boolean isFirstResource) {

                view.setScaleType(scaleType);
                view.setImageResource(R.drawable.img_loading_placeholder);

                Log.w(TAG, "Image load failed: " + originalUrl, e);

                // false：让 Glide 自己继续执行 error/placeholder 处理。
                return false;
            }

            @Override
            public boolean onResourceReady(
                    Bitmap resource,
                    Object model,
                    Target<Bitmap> target,
                    DataSource dataSource,
                    boolean isFirstResource) {

                view.setScaleType(scaleType);
                return false;
            }
        };
    }
}
