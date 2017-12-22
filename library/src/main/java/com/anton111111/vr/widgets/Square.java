package com.anton111111.vr.widgets;

import android.content.Context;


public class Square extends ColoredShapeAbstract<Square> {

    public static final int START_COORDS_TYPE_CENTER = 1;
    public static final int START_COORDS_TYPE_LEFT_BOTTOM_CORNER = 2;


    /**
     * Create square
     *
     * @param context
     * @param coords  coords of square vertices (left bottom, right bottom, right top, left top)
     */
    public Square(Context context, float[] coords) {
        this.coords = coords;
        init(context);
    }

    /**
     * Create square
     *
     * @param context
     * @param startCoords coords of left bottom corner of square
     * @param width
     * @param height
     */
    public Square(Context context, float[] startCoords, float width, float height) {
        this(context, startCoords, START_COORDS_TYPE_LEFT_BOTTOM_CORNER, width, height);
    }

    /**
     * Create square
     *
     * @param context
     * @param startCoords     coords of left bottom corner of square
     * @param startCoordsType type of startCoords (START_COORDS_TYPE_CENTER, START_COORDS_TYPE_LEFT_BOTTOM_CORNER)
     * @param width
     * @param height
     */
    public Square(Context context, float[] startCoords, int startCoordsType, float width, float height) {
        float lbx = startCoords[0];
        float lby = startCoords[1];
        if (startCoordsType == START_COORDS_TYPE_CENTER) {
            lbx -= width / 2.0f;
            lby -= height / 2.0f;
        }
        this.coords = new float[]{
                lbx, lby, startCoords[2],
                lbx + width, lby, startCoords[2],
                lbx + width, lby + height, startCoords[2],
                lbx, lby + height, startCoords[2],
        };
        init(context);
    }

}
