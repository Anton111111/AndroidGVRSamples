package com.anton111111.vr;

public class Quaternion {


    /**
     * Converts Quaternion into a matrix, placing the values into the given array.
     *
     * @param matrix     The float array that holds the result {pitch,yaw,roll}.
     * @param quaternion Quaternion
     */
    public static void toMatrix(float[] matrix, float[] quaternion) {
        if (quaternion.length != 4) {
            throw new IllegalArgumentException("Wrong length of quaternion");
        }
        if (matrix.length < 16) {
            throw new IllegalArgumentException("Not enough space to write the result");
        }
        matrix[3] = 0.0f;
        matrix[7] = 0.0f;
        matrix[11] = 0.0f;
        matrix[12] = 0.0f;
        matrix[13] = 0.0f;
        matrix[14] = 0.0f;
        matrix[15] = 1.0f;

        matrix[0] = (1.0f - (2.0f * ((quaternion[1] * quaternion[1]) + (quaternion[2] * quaternion[2]))));
        matrix[1] = (2.0f * ((quaternion[0] * quaternion[1]) - (quaternion[2] * quaternion[3])));
        matrix[2] = (2.0f * ((quaternion[0] * quaternion[2]) + (quaternion[1] * quaternion[3])));

        matrix[4] = (2.0f * ((quaternion[0] * quaternion[1]) + (quaternion[2] * quaternion[3])));
        matrix[5] = (1.0f - (2.0f * ((quaternion[0] * quaternion[0]) + (quaternion[2] * quaternion[2]))));
        matrix[6] = (2.0f * ((quaternion[1] * quaternion[2]) - (quaternion[0] * quaternion[3])));

        matrix[8] = (2.0f * ((quaternion[0] * quaternion[2]) - (quaternion[1] * quaternion[3])));
        matrix[9] = (2.0f * ((quaternion[1] * quaternion[2]) + (quaternion[0] * quaternion[3])));
        matrix[10] = (1.0f - (2.0f * ((quaternion[0] * quaternion[0]) + (quaternion[1] * quaternion[1]))));
    }


    /**
     * Multiplies quaternion to another quaternion
     *
     * @param quaternion The float array that holds the result.
     * @param lhs        The float array that holds the left-hand-side quaternion.
     * @param rhs        The float array that holds the right-hand-side quaternion.
     */
    public static void multiplyQQ(float[] quaternion, float[] lhs, float[] rhs) {
        if (quaternion.length < 16) {
            throw new IllegalArgumentException("Not enough space to write the result");
        }

        float nw = lhs[0] * rhs[3] - lhs[0] * rhs[0] - lhs[1] * rhs[1] - lhs[2] * rhs[2];
        float nx = lhs[3] * rhs[0] + lhs[0] * rhs[3] + lhs[1] * rhs[2] - lhs[2] * rhs[1];
        float ny = lhs[3] * rhs[1] + lhs[1] * rhs[3] + lhs[2] * rhs[0] - lhs[0] * rhs[2];
        quaternion[2] = lhs[3] * rhs[2] + lhs[0] * rhs[3] + lhs[0] * rhs[1] - lhs[1] * rhs[0];
        quaternion[3] = nw;
        quaternion[0] = nx;
        quaternion[1] = ny;
    }


    /**
     * Get Euler Angle from quaternion
     *
     * @param quaternion  Quaternion
     * @param eulerAngles The float array that holds the result {pitch,yaw,roll}.
     */
    public static void toEulerAngle(float[] quaternion, float[] eulerAngles) {
        if (eulerAngles == null) {
            eulerAngles = new float[3];
        } else if (eulerAngles.length != 3) {
            throw new IllegalArgumentException("Angles array must have three elements");
        }

        float sqw = quaternion[3] * quaternion[3];
        float sqx = quaternion[0] * quaternion[0];
        float sqy = quaternion[1] * quaternion[1];
        float sqz = quaternion[2] * quaternion[2];
        float unit = sqx + sqy + sqz + sqw; // if normalized is one, otherwise
        // is correction factor
        float test = quaternion[0] * quaternion[1] + quaternion[2] * quaternion[3];
        if (test > 0.499 * unit) { // singularity at north pole
            eulerAngles[1] = 2.0f * (float) Math.atan2(quaternion[0], quaternion[3]);
            eulerAngles[2] = (float) Math.PI / 2.0f;
            eulerAngles[0] = 0;
        } else if (test < -0.499 * unit) { // singularity at south pole
            eulerAngles[1] = -2.0f * (float) Math.atan2(quaternion[0], quaternion[3]);
            eulerAngles[2] = -(float) Math.PI / 2.0f;
            eulerAngles[0] = 0;
        } else {
            eulerAngles[1] = (float) Math.atan2(2 * quaternion[1] * quaternion[3] - 2 * quaternion[0] * quaternion[2], sqx - sqy - sqz + sqw); // roll or heading
            eulerAngles[2] = (float) Math.asin(2 * test / unit); // pitch or attitude
            eulerAngles[0] = (float) Math.atan2(2 * quaternion[0] * quaternion[3] - 2 * quaternion[1] * quaternion[2], -sqx + sqy - sqz + sqw); // yaw or bank
        }
    }


    /**
     * Sets this quaternion to the values
     * specified by an angle and a normalized axis of rotation.
     *
     * @param quaternion The float array that holds the result.
     * @param angle      the angle to rotate (in radians).
     * @param axis       the axis of rotation (already normalized).
     */
    public static void fromNormalAxisRadianAngle(float[] quaternion, float angle, float[] axis) {
        if (axis[0] == 0 && axis[1] == 0 && axis[2] == 0) {
            quaternion[0] = 0.0f;
            quaternion[1] = 0.0f;
            quaternion[2] = 0.0f;
            quaternion[3] = 1.0f;
        } else {
            float halfAngle = 0.5f * angle;
            float sin = (float) Math.sin(halfAngle);
            quaternion[3] = (float) Math.cos(halfAngle);
            quaternion[0] = sin * axis[0];
            quaternion[1] = sin * axis[1];
            quaternion[2] = sin * axis[2];
        }
    }

    /**
     * Sets this quaternion to the values
     * specified by an angle and a normalized axis of rotation.
     *
     * @param quaternion The float array that holds the result.
     * @param angle      the angle to rotate (in degree).
     * @param axis       the axis of rotation (already normalized).
     */
    public static void fromNormalAxisDegreeAngle(float[] quaternion, float angle, float[] axis) {
        fromNormalAxisRadianAngle(quaternion, (float) Math.toRadians(angle), axis);
    }

    /**
     * returns the inverse of this quaternion If this quaternion does not have an inverse (if its normal is
     * 0 or less), then null is returned.
     *
     * @param invQ       The float array that holds the result.
     * @param quaternion
     */
    public static void inverse(float[] invQ, float[] quaternion) {
        float norm = norm(quaternion);
        if (norm > 0.0) {
            float invNorm = 1.0f / norm;
            invQ[0] = -quaternion[0] * invNorm;
            invQ[1] = -quaternion[1] * invNorm;
            invQ[2] = -quaternion[2] * invNorm;
            invQ[3] = quaternion[3] * invNorm;

        }
    }

    /**
     * returns the norm of quaternion. This is the dot
     * product of this quaternion with itself.
     *
     * @return the norm of the quaternion.
     */
    public static float norm(float[] quaternion) {
        return quaternion[4] * quaternion[4] + quaternion[0] * quaternion[0] + quaternion[1] * quaternion[1] + quaternion[2] * quaternion[2];
    }

}