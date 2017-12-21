package com.anton111111.androidgvrsamples;


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.anton111111.vr.Quaternion;
import com.anton111111.vr.StringUtil;
import com.anton111111.vr.program.ProgramHelper;
import com.anton111111.vr.raypicking.RayPicking;
import com.anton111111.vr.widgets.Axis;
import com.anton111111.vr.widgets.Circle;
import com.anton111111.vr.widgets.Line;
import com.anton111111.vr.widgets.Square;
import com.google.vr.sdk.base.AndroidCompat;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;

public class DrawInCursorPositionSample extends GvrActivity
        implements GvrView.StereoRenderer {

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    private static final float CURSOR_RADIUS = 0.06f;
    private static final float CURSOR_THICKNESS = 0.015f;
    private static final float CURSOR_Z = -3.0f;

    private static final float CURSOR_COLORS[] = {
            1.0f, 1.0f, 1.0f, 0.8f,
    };


    private static final float COLORS[] = {
            1.0f, 0.0f, 0.0f, 0.5f,
            0.0f, 1.0f, 0.0f, 0.5f,
            0.0f, 0.0f, 1.0f, 0.5f,
            1.0f, 1.0f, 0.0f, 0.5f,
            1.0f, 0.0f, 1.0f, 0.5f,
    };

    private float[] quaternion = new float[4];
    private float[] viewMatrix = new float[16];
    private float[] eulerAngles = new float[3];
    private float anglePitch = 0.0f;
    private float angleYaw = 0.0f;
    private float eyeZ = 0.8f;
    private List<Square> squares = new ArrayList<>();
    private boolean isLocated = false;
    private int viewWidth;
    private int viewHeight;
    private Circle cursor;
    private float cursorX = -1.0f;
    private float cursorY = -1.0f;
    private Vibrator vibrator;
    private boolean isNeedAddSquare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_in_cursor_position_sample_activity);

        GvrView gv = findViewById(R.id.gvr_view);
        gv.setRenderer(this);
        gv.setTransitionViewEnabled(true);

        // Enable Cardboard-trigger feedback with Daydream headsets. This is a simple way of supporting
        // Daydream controller input for basic interactions using the existing Cardboard trigger API.
        gv.enableCardboardTriggerEmulation();

        if (gv.setAsyncReprojectionEnabled(true)) {
            // Async reprojection decouples the app framerate from the display framerate,
            // allowing immersive interaction even at the throttled clockrates set by
            // sustained performance mode.
            AndroidCompat.setSustainedPerformanceMode(this, true);
        }
        setGvrView(gv);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        headTransform.getQuaternion(quaternion, 0);
        Quaternion.toEulerAngle(quaternion, eulerAngles);
        anglePitch = (float) Math.toDegrees(-eulerAngles[0]); // around X
        angleYaw = (float) Math.toDegrees(-eulerAngles[1]); // around Y
    }

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        float[] projectionMatrix = eye.getPerspective(Z_NEAR, Z_FAR);
        float[] modelMatrix = new float[16];
        float[] modelViewMatrix = new float[16];
        float[] modelViewProjection = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, angleYaw, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, anglePitch, 1.0f, 0.0f, 0.0f);

        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);
        axis.render(modelViewProjection);
        renderCursor(eye);
        if (isNeedAddSquare) {
            addSquare(projectionMatrix);
        }
        for (Square square : squares) {
            square.render(modelViewProjection);
        }
    }

    private void addSquare(float[] projectionMatrix) {
        isNeedAddSquare = false;
        if (cursorX <= 0 || cursorY <= 0 || viewWidth <= 0 || viewHeight <= 0) {
            return;
        }

        int[] viewport = {0, 0, viewWidth, viewHeight};
        int colorIndex = new Random().nextInt(COLORS.length / 4);
        float width = new Random().nextFloat() * 0.1f + 0.1f; //from -0.4 to 0.4
        float height = new Random().nextFloat() * 0.1f + 0.1f; //from -0.4 to 0.4
        //float z = new Random().nextFloat() * 0.5f - 0.7f; //from -0.7 to -0.2
        float z = -0.5f;


        float[] modelMatrix = new float[16];
        float[] out = new float[4];
        float[] modelViewMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, angleYaw, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, anglePitch, 1.0f, 0.0f, 0.0f);
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        float[] intersect = RayPicking.rayPicking(viewWidth, viewHeight, cursorX, cursorY,
                modelViewMatrix, projectionMatrix,
                new float[]{
                        -1f, -1f, z,
                        1f, -1f, z,
                        1f, 1f, z,
                        -1f, 1f, z
                },
                new short[]{0, 1, 2, 0, 2, 3});
        if (intersect == null) {
            Log.e("Yo", "I: null");
            return;
        }
        Log.e("Yo", "I: " + StringUtil.formatMatrix(intersect));

//        Matrix.multiplyMV(out, 0, modelMatrix, 0,
//                new float[]{cursor.getCenterCoords()[0], cursor.getCenterCoords()[1], cursor.getCenterCoords()[2], 1.0f}, 0);
//        out[0] = out[0] / out[3];
//        out[1] = out[1] / out[3];
//        out[2] = out[2] / out[3];
//        Log.e("Yo", "CW: " + StringUtil.formatMatrix(out));

//        float[] win = new float[3];
//        GLU.gluProject(out[0], out[1], out[2],
//                modelViewMatrix, 0, projectionMatrix, 0,
//                viewport, 0,
//                win, 0);

        //Log.e("Yo", "C: " + cursorX + "x" + cursorY);
        //Log.e("Yo", "1: " + StringUtil.formatMatrix(win));

        Square s = new Square(this, new float[]{intersect[0], intersect[1], intersect[2]}, Square.START_COORDS_TYPE_CENTER,
                width, height);
        s.setColor(Arrays.copyOfRange(COLORS, colorIndex * 4, (colorIndex + 1) * 4));
        squares.add(s);
//
//        float zn = ((Z_FAR + Z_NEAR) / (Z_FAR - Z_NEAR) + (1.0f / -zr) * ((-2.0f * Z_FAR * Z_NEAR) / (Z_FAR - Z_NEAR))) * 0.5f + 0.5f;
//
//
//        GLU.gluUnProject(win[0], win[1], win[2],
//                modelMatrix, 0, projectionMatrix, 0,
//                viewport, 0,
//                out, 0);
//        out[0] = out[0] / out[3];
//        out[1] = out[1] / out[3];
//        out[2] = out[2] / out[3];
//        Log.e("Yo", "2: " + z + " = " + StringUtil.formatMatrix(out));
//


    }


    @Override
    public void onCardboardTrigger() {
        isNeedAddSquare = true;
        // Always give user feedback.
        vibrator.vibrate(100);
    }

    private void renderCursor(Eye eye) {
        GLES20.glDepthFunc(GLES20.GL_ALWAYS);
        GLES20.glDepthMask(false);
        float[] projectionMatrix = eye.getPerspective(Z_NEAR, Z_FAR);
        float[] modelMatrix = new float[16];
        float[] modelViewMatrix = new float[16];
        float[] modelViewProjection = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);
        cursor.render(modelViewProjection);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glDepthMask(true);

        if (eye.getType() <= 1 && cursorX < 0 && cursorY < 0 && viewWidth > 0 && viewHeight > 0) {
            int[] viewport = {0, 0, viewWidth, viewHeight};
            float[] coords = cursor.getCoords();
            float[] screenCoords = new float[3];
            GLU.gluProject(coords[0], coords[1], coords[2],
                    modelViewMatrix, 0,
                    projectionMatrix, 0,
                    viewport, 0,
                    screenCoords, 0
            );
            cursorX = screenCoords[0];
            cursorY = screenCoords[1];
        }
    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        this.viewWidth = width;
        this.viewHeight = height;

    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, eyeZ,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f);

        cursor = new Circle(this,
                new float[]{
                        0.0f, 0.0f, CURSOR_Z
                }, CURSOR_RADIUS, CURSOR_THICKNESS);

        cursor.setColor(CURSOR_COLORS);

    }

    @Override
    public void onRendererShutdown() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ProgramHelper.getInstance().clean();
    }
}
