package com.github.tvbox.osc.util;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.base.App;

public class ImgUtil {
    public static int defaultWidth = 300;
    public static int defaultHeight = 400;

    public static class Style {
        public float ratio = 1.5f;
        public int width = defaultWidth;
        public int height = defaultHeight;
    }

    public static Style initStyle() {
        Style style = new Style();
        style.ratio = 0.75f;
        style.width = defaultWidth;
        style.height = defaultHeight;
        return style;
    }

    public static void load(String url, ImageView view, int radius) {
        load(url, view, radius, defaultWidth, defaultHeight);
    }

    public static void load(String url, ImageView view, int radius, int width, int height) {
        try {
            if (view == null) return;
            RequestOptions options = new RequestOptions();
            if (radius > 0) {
                options = options.transform(new com.bumptech.glide.load.resource.bitmap.RoundedCorners(radius));
            }
            if (width > 0 && height > 0) {
                options = options.override(width, height);
            }
            Glide.with(view.getContext())
                    .load(url)
                    .apply(options)
                    .placeholder(R.drawable.bg_black)
                    .error(R.drawable.bg_black)
                    .addListener(getListener(view, ImageView.ScaleType.FIT_XY, url))
                    .into(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 修复 Glide 5.x 编译：RequestListener<Bitmap> -> RequestListener<Drawable>，用 addListener
    public static RequestListener<Drawable> getListener(final View view, final ImageView.ScaleType scaleType, final String url) {
        return new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (view instanceof ImageView) {
                    ((ImageView) view).setScaleType(scaleType);
                }
                return false;
            }
        };
    }
}
