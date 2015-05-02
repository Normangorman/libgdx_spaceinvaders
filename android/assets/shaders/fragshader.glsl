#ifdef GL_ES
precision mediump float;
#endif

//"in" varyings from our vertex shader
varying vec4 vColor;
varying vec2 vTexCoord;

uniform sampler2D u_texture;

// Uniforms set by me:
uniform float iGlobalTime;
uniform vec2 iResolution;
uniform vec2 iMouse;

void main() {
    vec2 pos = gl_FragCoord.xy / iResolution.xy;
    vec2 mouse_pos = iMouse.xy / iResolution.xy;

    float red = length(mouse_pos - pos);

    gl_FragColor = vec4(red, 0.0, 0.0, 1.0);
}