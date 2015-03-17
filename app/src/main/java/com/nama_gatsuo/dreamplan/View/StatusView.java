package com.nama_gatsuo.dreamplan.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.nama_gatsuo.dreamplan.R;

public class StatusView extends View {
    private int status;
    private float scale;
    private float centerX = 30.0f;
    private float centerY = 30.0f;
    private float radius = 20.0f;
    private float textSize = 18.0f;
    float[] ps = { 9.0f, 30.0f, 23.0f, 44.0f, 23.0f, 44.0f, 51.0f, 16.0f };

    private final Paint mPathPaint;
    private final Paint mFillPaint;
    private final Paint mCharPaint;

    public StatusView(Context context) {
        this(context, null);
    }

    public StatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scale = getContext().getResources().getDisplayMetrics().density;

        // メンバに対して画面密度をかける
        centerX = centerX * scale;
        centerY = centerY * scale;
        radius = radius * scale;
        textSize = textSize * scale;

        for (int i = 0; i < ps.length; i++) {
            ps[i] = ps[i] * scale;
        }

        // オブジェクトの生成はコンストラクタで行い、onDraw()で行わない
        mFillPaint = new Paint();
        mFillPaint.setAntiAlias(true);

        mCharPaint = new Paint();
        mCharPaint.setAntiAlias(true);
        mCharPaint.setTextSize(textSize);

        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPathPaint.setColor(getResources().getColor(R.color.accent));
        mPathPaint.setStrokeWidth(10.0f * scale);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (status == 10) {
            // チェックマークを描画
            canvas.drawLines(ps, mPathPaint);
        } else {
            // 円の設定
            switch (status) {
                case 0:
                    mFillPaint.setColor(getResources().getColor(R.color.status_0));
                    mCharPaint.setColor(Color.BLACK);
                    break;
                case 1:
                    mFillPaint.setColor(getResources().getColor(R.color.status_1));
                    mCharPaint.setColor(Color.BLACK);
                    break;
                case 2:
                    mFillPaint.setColor(getResources().getColor(R.color.status_2));
                    mCharPaint.setColor(Color.BLACK);
                    break;
                case 3:
                    mFillPaint.setColor(getResources().getColor(R.color.status_3));
                    mCharPaint.setColor(Color.BLACK);
                    break;
                case 4:
                    mFillPaint.setColor(getResources().getColor(R.color.status_4));
                    mCharPaint.setColor(Color.BLACK);
                    break;
                case 5:
                    mFillPaint.setColor(getResources().getColor(R.color.status_5));
                    mCharPaint.setColor(Color.BLACK);
                    break;
                case 6:
                    mFillPaint.setColor(getResources().getColor(R.color.status_6));
                    mCharPaint.setColor(Color.WHITE);
                    break;
                case 7:
                    mFillPaint.setColor(getResources().getColor(R.color.status_7));
                    mCharPaint.setColor(Color.WHITE);
                    break;
                case 8:
                    mFillPaint.setColor(getResources().getColor(R.color.status_8));
                    mCharPaint.setColor(Color.WHITE);
                    break;
                case 9:
                    mFillPaint.setColor(getResources().getColor(R.color.status_9));
                    mCharPaint.setColor(Color.WHITE);
                    break;
            }
            canvas.drawCircle(centerX, centerY, radius, mFillPaint);
            canvas.drawText(String.valueOf(status * 10) + "%", 15.0f * scale, 36.0f * scale, mCharPaint);
        }
    }
}
