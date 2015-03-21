package com.nama_gatsuo.dreamplan.View;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class RightHorizontalScrollView extends HorizontalScrollView {
    private boolean isInitialized;

    public RightHorizontalScrollView(Context context, AttributeSet attr) {
        super(context, attr);
        isInitialized = false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isInitialized) {
            scrollTo(getWidth(), 0);
            isInitialized = true;
        }
        super.onDraw(canvas);
    }
}
