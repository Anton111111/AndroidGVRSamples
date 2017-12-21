package com.anton111111.androidgvrsamples;


import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.widget.TextView;

import com.anton111111.vr.Quaternion;
import com.anton111111.vr.program.ProgramHelper;
import com.anton111111.vr.widgets.Square;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;

public class RotationLimitSample extends GvrActivity
        implements GvrView.StereoRenderer {

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    private static final float PITCH_LIMIT_MIN = -45f;
    private static final float PITCH_LIMIT_MAX = 45f;
    private static final float YAW_LIMIT_MIN = -45f;
    private static final float YAW_LIMIT_MAX = 45f;


    private static final float[] COORDS = new float[]{
            //front
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,

            //right
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,

            //back
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,


            //left
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,

            //upper
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,

            //bottom
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,

    };

    private static final float COLORS[] = {
            1.0f, 0.0f, 0.0f, 0.7f,
            0.0f, 1.0f, 0.0f, 0.7f,
            0.0f, 0.0f, 1.0f, 0.7f,
            1.0f, 1.0f, 0.0f, 0.7f,
            1.0f, 0.0f, 1.0f, 0.7f,
            0.0f, 1.0f, 1.0f, 0.7f

    };

    private float[] quaternion = new float[4];
    private float[] viewMatrix = new float[16];
    private float[] eulerAngles = new float[3];
    private float anglePitch = 0.0f;
    private float angleYaw = 0.0f;
    private float eyeZ = 0.8f;
    private List<Square> squares = new ArrayList<>();
    private TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rotation_sample_activity);
        GvrView gv = findViewById(R.id.gvr_view);
        setGvrView(gv);
        gv.setRenderer(this);
        statusView = findViewById(R.id.status_text);

    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        headTransform.getQuaternion(quaternion, 0);
        Quaternion.toEulerAngle(quaternion, eulerAngles);
        anglePitch = (float) Math.toDegrees(-eulerAngles[0]); // around X
        angleYaw = (float) Math.toDegrees(-eulerAngles[1]); // around Y

        angleYaw = (angleYaw < YAW_LIMIT_MIN) ? YAW_LIMIT_MIN : angleYaw;
        angleYaw = (angleYaw > YAW_LIMIT_MAX) ? YAW_LIMIT_MAX : angleYaw;

        anglePitch = (anglePitch < PITCH_LIMIT_MIN) ? PITCH_LIMIT_MIN : anglePitch;
        anglePitch = (anglePitch > PITCH_LIMIT_MAX) ? PITCH_LIMIT_MAX : anglePitch;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusView.setText(
                        String.format(Locale.getDefault(),
                                "\nangleYaw: %.2f\nanglePitch: %.2f",
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
        float[] view = new float[16];
        float[] modelViewMatrix = new float[16];
        float[] modelViewProjection = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, angleYaw, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, anglePitch, 1.0f, 0.0f, 0.0f);

        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);

        for (Square s : squares) {
            s.render(modelViewProjection);
        }
    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int i, int i1) {

    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, eyeZ,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f);
        for (int i = 0; i < 6; i++) {
            Square s = new Square(this, Arrays.copyOfRange(COORDS, i * 12, (i + 1) * 12));
            s.setColor(Arrays.copyOfRange(COLORS, i * 4, (i + 1) * 4));
            squares.add(s);
        }

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
