package com.nama_gatsuo.dreamplan.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.nama_gatsuo.dreamplan.R;



public class TriangleView extends View {
    private float centerX = 8.0f;
    private float centerY = 9.0f;
    private int dis = 6; // 原点から各頂点への距離
    private int[] degs = { 0, 120, 240 };
    private float scale;
    private final Paint mFillPaint;
    private final Path mPath = new Path();

    // Constructor
    public TriangleView(Context context) {
        this(context, null);
    }

    public TriangleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriangleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        scale = getContext().getResources().getDisplayMetrics().density;

        centerX = centerX * scale;
        centerY = centerY * scale;

        mFillPaint = new Paint();
        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(getResources().getColor(R.color.primary));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i : degs) {
            double x = Math.cos(i * Math.PI / 180) * dis * scale;
            double y = Math.sin(i * Math.PI / 180) * dis * scale;
            if (i == 0) {
                mPath.moveTo(centerX + (float) x, centerY + (float) y);
            } else {
                mPath.lineTo(centerX + (float) x, centerY + (float) y);
            }
        }
        mPath.close();
        canvas.drawPath(mPath, mFillPaint);
    }
}
