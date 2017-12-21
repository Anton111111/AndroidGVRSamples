package com.anton111111.vr.widgets;

import android.content.Context;
import android.opengl.GLES20;

import com.anton111111.vr.GLHelper;
import com.anton111111.vr.program.Program;
import com.anton111111.vr.program.ProgramHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Circle {

    private static final short[] VERTEX_INDEXES = new short[]{
            0, 1, 2, 0, 2, 3
    };

    private static final float COLOR[] = {
            1.0f, 0.0f, 0.0f, 0.5f

    };
    private final float radius;
    private final float thickness;
    private final float[] centerCoords;
    private float width;
    private float height;
    private float[] coords;
    private FloatBuffer verticesBuffer;
    private ShortBuffer verticesIndexesBuffer;

    private float[] color = COLOR;


    public void setColor(float[] color) {
        this.color = color;
    }

    public float[] getCoords() {
        return coords;
    }

    public short[] getVertexIndexes() {
        return VERTEX_INDEXES;
    }

    /**
     * Create square
     *
     * @param context
     * @param centerCoords coords of center
     * @param radius       radius
     * @param thickness    thickness
     */
    public Circle(Context context, float[] centerCoords, float radius, float thickness) {
        this.thickness = thickness;
        this.radius = radius;
        this.centerCoords = centerCoords;
        this.coords = new float[]{
                centerCoords[0] - radius, centerCoords[1] - radius, centerCoords[2],
                centerCoords[0] + radius, centerCoords[1] - radius, centerCoords[2],
                centerCoords[0] + radius, centerCoords[1] + radius, centerCoords[2],
                centerCoords[0] - radius, centerCoords[1] + radius, centerCoords[2]
        };
        this.width = Math.abs(coords[0] - coords[3]);
        this.height = Math.abs(coords[1] - coords[4]);
        init(context);
    }


    private void init(Context context) {
        ByteBuffer bbcv = ByteBuffer.allocateDirect(coords.length * 4);
        bbcv.order(ByteOrder.nativeOrder());
        verticesBuffer = bbcv.asFloatBuffer();
        verticesBuffer.put(coords);
        verticesBuffer.position(0);

        ByteBuffer bbSVIB = ByteBuffer.allocateDirect(VERTEX_INDEXES.length * 2);
        bbSVIB.order(ByteOrder.nativeOrder());
        verticesIndexesBuffer = bbSVIB.asShortBuffer();
        verticesIndexesBuffer.put(VERTEX_INDEXES);
        verticesIndexesBuffer.position(0);

        ProgramHelper.initCircleColoredProgram(context);
    }

    public void render(float[] modelViewProjection) {
        Program program = ProgramHelper.getInstance().useProgram(ProgramHelper.PROGRAM_CIRCLE_COLORED);
        GLES20.glVertexAttribPointer(
                program.getAttr(ProgramHelper.CIRCLE_COLORED_ATTR_POSITION), 3,
                GLES20.GL_FLOAT, false,
                12, verticesBuffer);

        GLES20.glUniform3fv(program.getUniform(ProgramHelper.PROGRESS_CIRCLE_UNIFORM_CENTER),
                1, centerCoords, 0);

        GLES20.glUniform1f(program.getUniform(ProgramHelper.CIRCLE_COLORED_UNIFORM_RADIUS),
                radius);
        GLES20.glUniform1f(program.getUniform(ProgramHelper.CIRCLE_COLORED_UNIFORM_THICKNESS),
                thickness);
        GLES20.glUniform4fv(program.getUniform(ProgramHelper.CIRCLE_COLORED_UNIFORM_COLOR),
                1, color, 0);

        GLES20.glUniformMatrix4fv(program.getUniform(ProgramHelper.CIRCLE_COLORED_UNIFORM_MVP),
                1, false, modelViewProjection, 0);

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, 6,
                GLES20.GL_UNSIGNED_SHORT, verticesIndexesBuffer);

        GLHelper.checkGLError("Circle renderPanel");
    }
}
