
// 修复 Glide 5.x 编译错：RequestListener<Bitmap> -> RequestListener<Drawable>
// 把 getListener 返回类型改成 Drawable，并在 onResourceReady 里处理
// 替换 ImgUtil.java 第160-180行左右

    private static RequestListener<Drawable> getListener(final View view, final ImageView.ScaleType scaleType, final String url) {
        return new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                // 如果原来需要Bitmap，这里转一下，兼容旧逻辑
                if (view instanceof ImageView) {
                    ((ImageView) view).setScaleType(scaleType);
                }
                return false;
            }
        };
    }

    // 调用处把 .listener(...) 改成 .addListener(...)
    // 原来：
    // .listener(getListener(view, ImageView.ScaleType.FIT_XY, url))
    // 改成：
    // .addListener(getListener(view, ImageView.ScaleType.FIT_XY, url))
