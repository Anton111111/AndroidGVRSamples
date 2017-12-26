package com.anton111111.vr.widgets;

import android.content.Context;
import android.opengl.GLES20;

import com.anton111111.vr.GLHelper;
import com.anton111111.vr.program.Program;
import com.anton111111.vr.program.ProgramHelper;

public class Circle extends ColoredShapeAbstract<Circle> {

    private final float radius;
    private final float thickness;


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
        this.coords = new float[]{
                centerCoords[0] - radius, centerCoords[1] - radius, centerCoords[2],
                centerCoords[0] + radius, centerCoords[1] - radius, centerCoords[2],
                centerCoords[0] + radius, centerCoords[1] + radius, centerCoords[2],
                centerCoords[0] - radius, centerCoords[1] + radius, centerCoords[2]
        };
        init(context);
    }


    @Override
    protected void initProgram(Context context) {
        ProgramHelper.initCircleColoredProgram(context);
    }

    @Override
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

        GLHelper.checkGLError("Circle render");
    }
}
