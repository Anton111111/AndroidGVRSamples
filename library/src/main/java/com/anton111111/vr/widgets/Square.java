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

public class Square {

    private static final short[] VERTEX_INDEXES = new short[]{
            0, 1, 2, 0, 2, 3
    };

    private static final float COLOR[] = {
            1.0f, 0.0f, 0.0f, 0.5f

    };
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
     * @param coords  coords of square vertices (left bottom, right bottom, right top, left top)
     */
    public Square(Context context, float[] coords) {
        this.width = Math.abs(coords[0] - coords[3]);
        this.height = Math.abs(coords[1] - coords[4]);
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
        this.width = width;
        this.height = height;
        this.coords = new float[]{
                startCoords[0], startCoords[1], startCoords[2],
                startCoords[0] + width, startCoords[1], startCoords[2],
                startCoords[0] + width, startCoords[1] + height, startCoords[2],
                startCoords[0], startCoords[1] + height, startCoords[2],
        };
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

        ProgramHelper.initShapeColoredProgram(context);
    }

    public void render(float[] modelViewProjection) {
        Program program = ProgramHelper.getInstance().useProgram(ProgramHelper.PROGRAM_SHAPE_COLORED);
        GLES20.glVertexAttribPointer(
                program.getAttr(ProgramHelper.SHAPE_COLORED_ATTR_POSITION), 3,
                GLES20.GL_FLOAT, false,
                12, verticesBuffer);

        GLES20.glUniform4fv(program.getUniform(ProgramHelper.SHAPE_COLORED_UNIFORM_COLOR),
                1, color, 0);

        GLES20.glUniformMatrix4fv(program.getUniform(ProgramHelper.SHAPE_COLORED_UNIFORM_MVP),
                1, false, modelViewProjection, 0);

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, 6,
                GLES20.GL_UNSIGNED_SHORT, verticesIndexesBuffer);

        GLHelper.checkGLError("Square renderPanel");
    }
}
