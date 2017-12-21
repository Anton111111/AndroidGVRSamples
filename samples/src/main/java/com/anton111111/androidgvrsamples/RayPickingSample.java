package com.anton111111.androidgvrsamples;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.os.Bundle;
import android.widget.TextView;

import com.anton111111.vr.StringUtil;
import com.anton111111.vr.program.ProgramHelper;
import com.anton111111.vr.raypicking.RayPicking;
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
import java.util.Locale;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;


public class RayPickingSample extends GvrActivity
        implements GvrView.StereoRenderer {

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    private static final float CURSOR_WIDTH = 0.001f;
    private static final float CURSOR_HEIGHT = 0.001f;
    private static final float CURSOR_Z = -0.1f;

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

    private static final float CAMERA_Z = 0.01f;
    private float[] viewMatrix = new float[16];
    private float[] eulerAngles = new float[3];
    private float anglePitch = 0.0f;
    private float angleYaw = 0.0f;
    private List<Square> squares = new ArrayList<>();
    private int viewWidth = -1;
    private int viewHeight = -1;
    private float cursorX = -1;
    private float cursorY = -1;
    private TextView statusView;
    private TextView status2View;
    private Square cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ray_picking_sample_activity);
        GvrView gvrView = findViewById(R.id.gvr_view);
        gvrView.setStereoModeEnabled(true);
        gvrView.setRenderer(this);
        gvrView.setTransitionViewEnabled(true);

        // Enable Cardboard-trigger feedback with Daydream headsets. This is a simple way of supporting
        // Daydream controller input for basic interactions using the existing Cardboard trigger API.
        gvrView.enableCardboardTriggerEmulation();

        if (gvrView.setAsyncReprojectionEnabled(true)) {
            // Async reprojection decouples the app framerate from the display framerate,
            // allowing immersive interaction even at the throttled clockrates set by
            // sustained performance mode.
            AndroidCompat.setSustainedPerformanceMode(this, true);
        }
        setGvrView(gvrView);
        statusView = findViewById(R.id.status);
        status2View = findViewById(R.id.status2);
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, CAMERA_Z,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f);

        headTransform.getEulerAngles(eulerAngles, 0);

        float theta = -eulerAngles[1];
        float phi = -eulerAngles[0];
        anglePitch = (float) Math.toDegrees(phi);
        angleYaw = (float) Math.toDegrees(theta);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusView.setText(String.format(Locale.getDefault(),
                        "ViewPort: %dx%d\nCursor: %.2fx%.2f\nangleYaw: %.2f\nanglePitch: %.2f",
                        viewWidth, viewHeight,
                        cursorX, cursorY,
                        angleYaw, anglePitch));
            }
        });
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
        Matrix.rotateM(modelMatrix, 0, anglePitch, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, angleYaw, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);

        for (Square square : squares) {
            square.render(modelViewProjection);
        }

        renderCursor(eye);

        //Check is some object under cursor
        if (eye.getType() <= 1 &&
                cursorX > 0 && cursorY > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status2View.setText("");
                }
            });
            for (Square square : squares) {
                String status2 = "";
                float[] coords = square.getCoords();
                float[] point = RayPicking.rayPicking(viewWidth, viewHeight, cursorX, cursorY,
                        modelViewMatrix, projectionMatrix, coords, square.getVertexIndexes());
                if (point != null) {
                    status2 += "Square:\n " + StringUtil.formatMatrix(coords, 3) + "\n";
                    status2 += "Intersection point: " + StringUtil.formatMatrix(point, 3) + "\n";
                    final String finalStatus = status2;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status2View.setText(finalStatus);
                        }
                    });
                    break;
                }
            }
        }
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
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f);

        for (int i = 0; i < COLORS.length / 4; i++) {
            int colorOffset = i * 4;
            Square square = new Square(this,
                    new float[]{
                            new Random().nextFloat() * 0.8f - 0.4f, //from -0.4 to 0.4
                            new Random().nextFloat() * 0.8f - 0.4f, //from -0.4 to 0.4
                            new Random().nextFloat() * 0.5f - 0.7f //from -0.7 to -0.2
                    },
                    new Random().nextFloat() * 0.1f + 0.1f, ////from 0.1 to 0.2
                    new Random().nextFloat() * 0.1f + 0.1f);
            square.setColor(Arrays.copyOfRange(COLORS, colorOffset, colorOffset + 4));
            squares.add(square);
        }

        cursor = new Square(this,
                new float[]{
                        0.0f - CURSOR_WIDTH, 0.0f - CURSOR_HEIGHT, CURSOR_Z
                },
                CURSOR_WIDTH, CURSOR_HEIGHT
        );
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
