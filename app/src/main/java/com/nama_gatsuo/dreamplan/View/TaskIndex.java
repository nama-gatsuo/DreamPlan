package com.nama_gatsuo.dreamplan.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.nama_gatsuo.dreamplan.R;


public class TaskIndex extends View {
    private float height = 30.0f;
    private float width = 30.0f;
    private float scale;

    private final Paint mFillPaint;

    public TaskIndex(Context context) {
        this(context, null);
    }

    public TaskIndex(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskIndex(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scale = getContext().getResources().getDisplayMetrics().density;

        // メンバに対して画面密度をかける
        height = height * scale;
        width = width * scale;

        // オブジェクトの生成はコンストラクタで行い、onDraw()で行わない
        mFillPaint = new Paint();
        mFillPaint.setColor(getResources().getColor(R.color.primary_dark));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(width/8, height/8, width*5/8, height*5/8, mFillPaint);
        canvas.drawRect(width*3/8, height*3/8, width*7/8, height*7/8, mFillPaint);
    }
}
