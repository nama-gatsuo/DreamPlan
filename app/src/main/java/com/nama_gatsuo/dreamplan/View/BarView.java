package com.nama_gatsuo.dreamplan.View;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.nama_gatsuo.dreamplan.R;
import com.nama_gatsuo.dreamplan.model.Task;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class BarView extends View {
    private Task task;

    private Paint mFillPaint1 = null;
    private Paint mFillPaint2 = null;
    private Paint shadow = null;

    private DateTime minDate;
    private DateTime maxDate;
    private DateTime start;
    private DateTime end;

    private int dx;
    private int dy;

    private int length;
    private int statusLength;
    private Rect rect;
    private Rect shadowRect;

    private int startX;
    private int startY;
    private int endX;
    private int endY;

    private int diff = 10;

    public BarView(Context context) {
        this(context, null);
    }

    public BarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);

        mFillPaint1 = new Paint();
        mFillPaint1.setColor(Color.GRAY);

        mFillPaint2 = new Paint();
        mFillPaint2.setColor(getResources().getColor(R.color.accent));

        shadow = new Paint();
        shadow.setAntiAlias(true);
        shadow.setStyle(Paint.Style.FILL);
        shadow.setColor(Color.argb(112, 0, 0, 0));
        BlurMaskFilter blur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
        shadow.setMaskFilter(blur);
    }

    public void setRange(DateTime minDate, DateTime maxDate) {
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    public void setDimen(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void setTask(Task task) {
        this.task = task;

        start = new DateTime().withMillis(task.getStartDate());
        end = new DateTime().withMillis(task.getEndDate());

        Duration minDate_start = new Duration(minDate, start);
        int dis_start = (int)minDate_start.getStandardDays();

        Duration start_end = new Duration(start, end);
        length = ((int)start_end.getStandardDays() + 1) * dx;

        int status = task.getStatus();
        statusLength = length * status / 10;

        startX = dis_start * dx;
        startY = dy / 3;
        endX = startX + length;
        endY = dy / 3 * 2;

        rect = new Rect(startX, startY, endX, endY);
        shadowRect = new Rect(startX+diff, startY+diff, endX+diff, endY+diff);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(shadowRect, shadow);
        canvas.drawRect(rect, mFillPaint2);
        canvas.drawRect(startX, startY, startX + statusLength, endY, mFillPaint1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);
    }
}
