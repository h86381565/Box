package com.github.tvbox.osc.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.Movie;
import com.github.tvbox.osc.util.ImgUtil;

import java.util.ArrayList;

import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * GridAdapter V3
 *
 * 重点：
 * 1. 图片加载统一交给 ImgUtil。
 * 2. Grid 模式明确给 Glide override 尺寸，减少大图解码压力。
 * 3. 每次复用 ViewHolder 时先交给 Glide placeholder，避免旧图片残留。
 * 4. 不修改分类/API逻辑。
 */
public class GridAdapter extends BaseQuickAdapter<Movie.Video, BaseViewHolder> {
    private boolean mShowList;
    private int defaultWidth;
    public ImgUtil.Style style;

    public GridAdapter(boolean showList, ImgUtil.Style style) {
        super(showList ? R.layout.item_list : R.layout.item_grid, new ArrayList<>());
        this.mShowList = showList;
        if (style != null) {
            if ("list".equals(style.type)) this.mShowList = true;
            this.defaultWidth = ImgUtil.getStyleDefaultWidth(style);
        }
        this.style = style;
    }

    @Override
    protected void convert(BaseViewHolder helper, Movie.Video item) {
        if (this.mShowList) {
            helper.setText(R.id.tvNote, item.note);
            helper.setText(R.id.tvName, item.name);

            ImageView ivThumb = helper.getView(R.id.ivThumb);
            if (!TextUtils.isEmpty(item.pic)) {
                String pic = item.pic.trim();
                ImgUtil.load(pic, ivThumb, 14);
            } else {
                ivThumb.setImageResource(R.drawable.img_loading_placeholder);
            }
            return;
        }

        TextView tvYear = helper.getView(R.id.tvYear);
        if (item.year <= 0) {
            tvYear.setVisibility(View.GONE);
        } else {
            tvYear.setText(String.valueOf(item.year));
            tvYear.setVisibility(View.VISIBLE);
        }

        TextView tvLang = helper.getView(R.id.tvLang);
        tvLang.setVisibility(View.GONE);

        TextView tvArea = helper.getView(R.id.tvArea);
        tvArea.setVisibility(View.GONE);

        if (TextUtils.isEmpty(item.note)) {
            helper.setVisible(R.id.tvNote, false);
        } else {
            helper.setVisible(R.id.tvNote, true);
            helper.setText(R.id.tvNote, item.note);
        }

        helper.setText(R.id.tvName, item.name);
        helper.setText(R.id.tvActor, item.actor);

        ImageView ivThumb = helper.getView(R.id.ivThumb);

        int newWidth = ImgUtil.defaultWidth;
        int newHeight = ImgUtil.defaultHeight;

        if (style != null) {
            newWidth = defaultWidth;
            if (style.ratio > 0) {
                newHeight = (int) (newWidth / style.ratio);
            }
        }

        if (!TextUtils.isEmpty(item.pic)) {
            String pic = item.pic.trim();

            // V3：明确限制解码尺寸，避免电视端下载/解码超大海报造成卡顿。
            ImgUtil.load(pic, ivThumb, 14, newWidth, newHeight);
        } else {
            ivThumb.setImageResource(R.drawable.img_loading_placeholder);
        }

        applyStyleToImage(ivThumb);
    }

    /**
     * 根据传入的 style 动态设置 ImageView 的高度：高度 = 宽度 / ratio
     */
    private void applyStyleToImage(final ImageView ivThumb) {
        if (style != null) {
            ViewGroup container = (ViewGroup) ivThumb.getParent();
            if (container == null) return;

            int width = defaultWidth;
            int height = style.ratio > 0 ? (int) (width / style.ratio) : ImgUtil.defaultHeight;

            ViewGroup.LayoutParams containerParams = container.getLayoutParams();
            containerParams.height = AutoSizeUtils.mm2px(mContext, height);
            containerParams.width = AutoSizeUtils.mm2px(mContext, width);
            container.setLayoutParams(containerParams);
        }
    }
}
