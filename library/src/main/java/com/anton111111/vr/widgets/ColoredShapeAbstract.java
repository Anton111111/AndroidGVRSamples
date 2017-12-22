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

abstract public class ColoredShapeAbstract<Shape> {

    protected final short[] VERTEX_INDEXES = new short[]{
            0, 1, 2, 0, 2, 3
    };

    protected final float DEFAULT_COLOR[] = {
            1.0f, 0.0f, 0.0f, 0.5f

    };


    protected float[] color = DEFAULT_COLOR;
    protected FloatBuffer verticesBuffer;
    protected ShortBuffer verticesIndexesBuffer;
    protected float[] centerCoords;
    protected float[] coords;

    public float[] getCoords() {
        return coords;
    }

    public short[] getVertexIndexes() {
        return VERTEX_INDEXES;
    }


    public Shape setColor(float[] color) {
        this.color = color;
        return (Shape) this;
    }

    public float[] getCenterCoords() {
        return centerCoords;
    }

    protected void setCenterCoords() {
        centerCoords = new float[]{
                coords[0] + ((coords[3] - coords[0]) / 2.0f),
                coords[1] + ((coords[7] - coords[1]) / 2.0f),
                coords[2] + (Math.abs(coords[8] - coords[2]) / 2.0f),
        };
    }


    protected void init(Context context) {
        setCenterCoords();
        initBuffers();
        initProgram(context);
    }

    protected void initBuffers() {
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
    }

    protected void initProgram(Context context) {
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

        GLHelper.checkGLError("Shape render");
    }

}
