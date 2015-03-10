package com.nama_gatsuo.dreamplan.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.nama_gatsuo.dreamplan.R;



public class TriangleView extends View {
    private float centerX = 8;
    private float centerY = 9;
    private int dis = 8; // 原点から各頂点への距離
    private int[] degs = { 0, 120, 240 };
    private float scale;


    // Constructor
    public TriangleView(Context context) {
        super(context);
    }

    public TriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TriangleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TriangleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        scale = getContext().getResources().getDisplayMetrics().density;
        centerX = centerX * scale;
        centerY = centerY * scale;

        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.accent));
        p.setStyle(Paint.Style.FILL_AND_STROKE);

        Path path = new Path();
        for (int i : degs) {
            double x = Math.cos(i * Math.PI / 180) * dis * scale;
            double y = Math.sin(i * Math.PI / 180) * dis * scale;
            if (i == 0) {
                path.moveTo(centerX + (float) x, centerY + (float) y);
            } else {
                path.lineTo(centerX + (float) x, centerY + (float) y);
            }
        }
        path.close();
        canvas.drawPath(path, p);
    }
}
