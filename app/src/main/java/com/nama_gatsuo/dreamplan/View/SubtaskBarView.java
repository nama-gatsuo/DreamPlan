package com.nama_gatsuo.dreamplan.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

public class SubtaskBarView extends BarView {
    public SubtaskBarView(Context context) {
        super(context);
    }

    public SubtaskBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubtaskBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Set Background.
        canvas.drawColor(Color.argb(20, 60, 92, 111));
        super.onDraw(canvas);
    }
}
