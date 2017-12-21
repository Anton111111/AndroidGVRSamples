package com.anton111111.androidgvrsamples.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Anton Potekhin (Anton.Potekhin@gmail.com) on 18.12.17.
 */
public class GridView extends View {
    public GridView(Context context) {
        super(context);
    }

    public GridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#660000FF"));
        float stepX = getWidth() / 40;
        float stepY = getHeight() / 20;
        for (int x = 0; x < 40; x++) {
            canvas.drawLine(x * stepX, 0, x * stepX, getHeight(), paint);
        }
        for (int y = 0; y < 20; y++) {
            canvas.drawLine(0, y * stepY, getWidth(), y * stepY, paint);
        }

        paint.setColor(Color.parseColor("#66FF0000"));
        canvas.drawLine(0, stepY * 10, getWidth(), stepY * 10, paint);
        canvas.drawLine(stepX * 10, 0, stepX * 10, getHeight(), paint);
        canvas.drawLine(stepX * 30, 0, stepX * 30, getHeight(), paint);
        paint.setColor(Color.parseColor("#66FFFFFF"));
        canvas.drawLine(stepX * 20, 0, stepX * 20, getHeight(), paint);
    }
}
