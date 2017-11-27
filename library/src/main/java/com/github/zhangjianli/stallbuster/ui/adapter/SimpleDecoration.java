package com.github.zhangjianli.stallbuster.ui.adapter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.zhangjianli.stallbuster.StallBuster;
import com.github.zhangjianli.stallbuster.utils.Utils;

/**
 * Created by jinlizhang on 19/11/2017.
 */

public class SimpleDecoration extends RecyclerView.ItemDecoration {

    private int mPadding;
    private Paint mPaint;

    public SimpleDecoration() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#dddddd"));
        mPadding = Utils.dp2px(StallBuster.getInstance().getApp(), 15);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = 1;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft() + mPadding;
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + 1F;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }
}
