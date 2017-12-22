package com.anton111111.vr.widgets;

import android.content.Context;
import android.opengl.Matrix;

import com.anton111111.vr.Quaternion;

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

    /**
     * Create square
     * (Do not recommend use it. Better use quaternions)
     *
     * @param context
     * @param startCoords     coords of left bottom corner of square
     * @param startCoordsType type of startCoords (START_COORDS_TYPE_CENTER, START_COORDS_TYPE_LEFT_BOTTOM_CORNER)
     * @param width           width
     * @param height          height
     * @param pitch           the Euler pitch of rotation (in degree).
     * @param yaw             the Euler yaw of rotation (in degree).
     * @param roll            the Euler roll of rotation (in degree).
     */
    public Square(Context context, float[] startCoords, int startCoordsType,
                  float width, float height,
                  float pitch, float yaw, float roll) {
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
        setCenterCoords();


        float[] rotateM = new float[16];
        Matrix.setIdentityM(rotateM, 0);
        Matrix.translateM(rotateM, 0, centerCoords[0], centerCoords[1], centerCoords[2]);
        Matrix.rotateM(rotateM, 0, pitch, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(rotateM, 0, yaw, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(rotateM, 0, roll, 0.0f, 0.0f, 1.0f);
        Matrix.translateM(rotateM, 0, -centerCoords[0], -centerCoords[1], -centerCoords[2]);


        float[] v0 = rotateVertex(new float[]{
                lbx, lby, startCoords[2]
        }, rotateM);
        float[] v1 = rotateVertex(new float[]{
                lbx + width, lby, startCoords[2]
        }, rotateM);
        float[] v2 = rotateVertex(new float[]{
                lbx + width, lby + height, startCoords[2]
        }, rotateM);
        float[] v3 = rotateVertex(new float[]{
                lbx, lby + height, startCoords[2],
        }, rotateM);


        this.coords = new float[]{
                v0[0], v0[1], v0[2],
                v1[0], v1[1], v1[2],
                v2[0], v2[1], v2[2],
                v3[0], v3[1], v3[2],
        };

        init(context);
    }


    /**
     * Create square
     *
     * @param context
     * @param startCoords     coords of left bottom corner of square
     * @param startCoordsType type of startCoords (START_COORDS_TYPE_CENTER, START_COORDS_TYPE_LEFT_BOTTOM_CORNER)
     * @param width
     * @param height
     * @param quaternion      quaternion to rotate square
     */
    public Square(Context context, float[] startCoords, int startCoordsType, float width, float height, float[] quaternion) {
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
        setCenterCoords();


        float[] rotateM = new float[16];
        Matrix.setIdentityM(rotateM, 0);
        Matrix.translateM(rotateM, 0, centerCoords[0], centerCoords[1], centerCoords[2]);
        float[] qTM = new float[16];
        Quaternion.toMatrix(qTM, quaternion);
        Matrix.multiplyMM(rotateM, 0, rotateM, 0, qTM, 0);
        Matrix.translateM(rotateM, 0, -centerCoords[0], -centerCoords[1], -centerCoords[2]);

        float[] v0 = rotateVertex(new float[]{
                lbx, lby, startCoords[2]
        }, rotateM);
        float[] v1 = rotateVertex(new float[]{
                lbx + width, lby, startCoords[2]
        }, rotateM);
        float[] v2 = rotateVertex(new float[]{
                lbx + width, lby + height, startCoords[2]
        }, rotateM);
        float[] v3 = rotateVertex(new float[]{
                lbx, lby + height, startCoords[2],
        }, rotateM);


        this.coords = new float[]{
                v0[0], v0[1], v0[2],
                v1[0], v1[1], v1[2],
                v2[0], v2[1], v2[2],
                v3[0], v3[1], v3[2],
        };

        init(context);
    }

    private float[] rotateVertex(float[] coords, float[] rotateM) {
        float[] out = new float[4];
        Matrix.multiplyMV(out, 0, rotateM, 0, new float[]{
                coords[0], coords[1], coords[2], 1.0f
        }, 0);
        out[0] = out[0] / out[3];
        out[1] = out[1] / out[3];
        out[2] = out[2] / out[3];
        return new float[]{out[0], out[1], out[2]};
    }
}
