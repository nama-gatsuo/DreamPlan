package com.nama_gatsuo.dreamplan.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nama_gatsuo.dreamplan.R;

public class StatusView extends View {
    private int status;
    private float scale;
    private float centerX = 30;
    private float centerY = 30;
    private float radius = 20;
    private float textSize = 24;
    float[] ps = { 9, 30, 23, 44, 23, 44, 51, 16 };

    public StatusView(Context context, int status) {
        super(context);
        this.status = status;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        scale = getContext().getResources().getDisplayMetrics().density;

        centerX = centerX * scale;
        centerY = centerY * scale;

        if (status == 10) {
            // チェックマークを描画
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setStrokeCap(Paint.Cap.SQUARE);
            p.setColor(getResources().getColor(R.color.accent));
            p.setStrokeWidth(10 * scale);

            for (int i = 0; i < ps.length; i++) {
                ps[i] = ps[i] * scale;
            }
            canvas.drawLines(ps, p);
        } else {
            radius = radius * scale;
            textSize = textSize * scale;

            // ペイント初期化
            Paint pcircle = new Paint();
            pcircle.setAntiAlias(true);

            Paint pstring = new Paint();
            pstring.setAntiAlias(true);
            pstring.setTextSize(18.0f * scale);

            // 色の設定
            switch (status) {
                case 0:
                    pcircle.setColor(getResources().getColor(R.color.status_0));
                    pstring.setColor(Color.BLACK);
                    break;
                case 1:
                    pcircle.setColor(getResources().getColor(R.color.status_1));
                    pstring.setColor(Color.BLACK);
                    break;
                case 2:
                    pcircle.setColor(getResources().getColor(R.color.status_2));
                    pstring.setColor(Color.BLACK);
                    break;
                case 3:
                    pcircle.setColor(getResources().getColor(R.color.status_3));
                    pstring.setColor(Color.BLACK);
                    break;
                case 4:
                    pcircle.setColor(getResources().getColor(R.color.status_4));
                    pstring.setColor(Color.BLACK);
                    break;
                case 5:
                    pcircle.setColor(getResources().getColor(R.color.status_5));
                    pstring.setColor(Color.BLACK);
                    break;
                case 6:
                    pcircle.setColor(getResources().getColor(R.color.status_6));
                    pstring.setColor(Color.WHITE);
                    break;
                case 7:
                    pcircle.setColor(getResources().getColor(R.color.status_7));
                    pstring.setColor(Color.WHITE);
                    break;
                case 8:
                    pcircle.setColor(getResources().getColor(R.color.status_8));
                    pstring.setColor(Color.WHITE);
                    break;
                case 9:
                    pcircle.setColor(getResources().getColor(R.color.status_9));
                    pstring.setColor(Color.WHITE);
                    break;
            }
            canvas.drawCircle(centerX, centerY, radius, pcircle);
            canvas.drawText(String.valueOf(status * 10) + "%", 15 * scale, 36 * scale, pstring);
        }
    }
}
