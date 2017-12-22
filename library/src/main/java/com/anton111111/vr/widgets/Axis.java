package com.anton111111.vr.widgets;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Axis {


    private static final float COLOR_X[] = {
            0.0f, 1.0f, 0.0f, 1.0f

    };
    private static final float COLOR_Y[] = {
            1.0f, 0.0f, 0.0f, 1.0f

    };

    private static final float COLOR_Z[] = {
            0.0f, 0.0f, 1.0f, 1.0f

    };

    private static final float COLOR_PLANE_Y[] = {
            1.0f, 0.0f, 0.0f, 0.1f

    };

    private static final float COLOR_PLANE_X[] = {
            0.0f, 1.0f, 0.0f, 0.1f

    };


    private List<Line> axisList = new ArrayList<>();
    private List<Square> axisPlainsList = new ArrayList<>();

    /**
     * Create axis
     *
     * @param context
     */
    public Axis(Context context) {
        axisList.add(new Line(context, new float[]{
                -1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
        }).setColor(COLOR_X));
        axisList.add(new Line(context, new float[]{
                0.0f, -1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
        }).setColor(COLOR_Y));
        axisList.add(new Line(context, new float[]{
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, 1.0f,
        }).setColor(COLOR_Z));

        axisPlainsList.add(new Square(context, new float[]{
                0.0f, -100.0f, 100.0f,
                0.0f, -100.0f, -100.0f,
                0.0f, 100.0f, -100.0f,
                0.0f, 100.0f, 100.0f,
        }).setColor(COLOR_PLANE_Y));

        axisPlainsList.add(new Square(context, new float[]{
                -1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, -1.0f,
                -1.0f, 0.0f, -1.0f,
        }).setColor(COLOR_PLANE_X));
    }


    public void render(float[] modelViewProjection) {
        for (Line axis : axisList) {
            axis.render(modelViewProjection);
        }
        for (Square p : axisPlainsList) {
            p.render(modelViewProjection);
        }
    }
}
