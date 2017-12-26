precision mediump float;

uniform vec4 u_Color;
varying vec3 v_Position;

void main() {

    float thickness = 0.02;

    if ((abs(v_Position.x) > 0.0 && abs(v_Position.x)*10.0 - floor(abs(v_Position.x)*10.0) > thickness) &&
        (abs(v_Position.z) > 0.0 && abs(v_Position.z)*10.0 - floor(abs(v_Position.z)*10.0) > thickness)) {
        discard;
    }
    if ((abs(v_Position.y) > 0.0 && abs(v_Position.y)*10.0 - floor(abs(v_Position.y)*10.0) > thickness) &&
        (abs(v_Position.z) > 0.0 && abs(v_Position.z)*10.0 - floor(abs(v_Position.z)*10.0) > thickness)) {
        discard;
    }

    gl_FragColor = u_Color;
}