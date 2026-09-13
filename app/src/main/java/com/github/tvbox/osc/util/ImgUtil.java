package com.github.tvbox.osc.util;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ImgUtil {
    // 修复 Glide 5.x: RequestListener<Bitmap> -> RequestListener<Drawable>
    // 原来 getListener 返回 Bitmap，现在返回 Drawable，调用处用 addListener

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
    // 其他原有代码保持不变，调用处：
    // 把 .listener(getListener(...)) 改成 .addListener(getListener(...))
}
