package com.anton111111.vr.widgets;

import android.content.Context;
import android.opengl.GLES20;

import com.anton111111.vr.GLHelper;
import com.anton111111.vr.program.Program;
import com.anton111111.vr.program.ProgramHelper;

public class Grid extends Square {

    public static final int START_COORDS_TYPE_CENTER = Square.START_COORDS_TYPE_CENTER;
    public static final int START_COORDS_TYPE_LEFT_BOTTOM_CORNER = Square.START_COORDS_TYPE_LEFT_BOTTOM_CORNER;


    @Override
    public Grid setColor(float[] color) {
        return (Grid) super.setColor(color);
    }

    public Grid(Context context, float[] coords) {
        super(context, coords);
    }

    public Grid(Context context, float[] startCoords, float width, float height) {
        super(context, startCoords, width, height);
    }

    public Grid(Context context, float[] startCoords, int startCoordsType, float width, float height) {
        super(context, startCoords, startCoordsType, width, height);
    }

    public Grid(Context context, float[] startCoords, int startCoordsType, float width, float height, float pitch, float yaw, float roll) {
        super(context, startCoords, startCoordsType, width, height, pitch, yaw, roll);
    }

    public Grid(Context context, float[] startCoords, int startCoordsType, float width, float height, float[] quaternion) {
        super(context, startCoords, startCoordsType, width, height, quaternion);
    }

    @Override
    protected void initProgram(Context context) {
        ProgramHelper.initGridColoredProgram(context);
    }

    @Override
    public void render(float[] modelViewProjection) {
        Program program = ProgramHelper.getInstance().useProgram(ProgramHelper.PROGRAM_GRID_COLORED);
        GLES20.glVertexAttribPointer(
                program.getAttr(ProgramHelper.GRID_COLORED_ATTR_POSITION), 3,
                GLES20.GL_FLOAT, false,
                12, verticesBuffer);

        GLES20.glUniform4fv(program.getUniform(ProgramHelper.GRID_COLORED_UNIFORM_COLOR),
                1, color, 0);

        GLES20.glUniformMatrix4fv(program.getUniform(ProgramHelper.GRID_COLORED_UNIFORM_MVP),
                1, false, modelViewProjection, 0);

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, 6,
                GLES20.GL_UNSIGNED_SHORT, verticesIndexesBuffer);

        GLHelper.checkGLError("Grid render");
    }
}
