package com.nama_gatsuo.dreamplan.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.nama_gatsuo.dreamplan.R;

public class SubtaskIndex extends View {
    private float height = 30.0f;
    private float width = 30.0f;
    private float scale;

    private final Paint mFillPaint;

    public SubtaskIndex(Context context) {
        this(context, null);
    }

    public SubtaskIndex(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubtaskIndex(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scale = getContext().getResources().getDisplayMetrics().density;

        // メンバに対して画面密度をかける
        height = height * scale;
        width = width * scale;

        // オブジェクトの生成はコンストラクタで行い、onDraw()で行わない
        mFillPaint = new Paint();
        mFillPaint.setColor(getResources().getColor(R.color.primary));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(width/4, height/4, width*3/4, height*3/4, mFillPaint);
    }
}
