package com.nama_gatsuo.dreamplan.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.nama_gatsuo.dreamplan.R;

public class ProjectStatusView extends View {

    private int status;

    private final Paint bgPaint;
    private final Paint textBgPaint;
    private final Paint charPaint;

    private float scale;
    private float textSize = 12.0f;

    public ProjectStatusView(Context context) {
        this(context, null);
    }

    public ProjectStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProjectStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scale = getContext().getResources().getDisplayMetrics().density;

        textSize = textSize * scale;

        bgPaint = new Paint();

        textBgPaint = new Paint();

        charPaint = new Paint();
        charPaint.setAntiAlias(true);
        charPaint.setColor(Color.WHITE);
        charPaint.setTextSize(textSize);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String text = null;
        switch (status) {
            case 0:
                bgPaint.setColor(Color.argb(112, 64, 64, 64));
                textBgPaint.setColor(Color.GRAY);
                text = "Not yet started";
                canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
                break;
            case 1:
                textBgPaint.setColor(getResources().getColor(R.color.primary));
                text =  "In progress";
                break;
            case 2:
                bgPaint.setColor(Color.argb(112, 255, 113, 128));
                textBgPaint.setColor(getResources().getColor(R.color.accent));
                text = "Comleted!";
                canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
                break;
            default:
                throw new RuntimeException("wtf?");
        }
        canvas.drawRect(0, 0, 96 * scale, 20 * scale, textBgPaint);
        canvas.drawText(text, 10 * scale, 14 * scale, charPaint);
    }
}
