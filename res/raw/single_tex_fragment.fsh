#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_tex_coord;
uniform sampler2D s_texture;
uniform vec4 u_color;

void main()
{
    gl_FragColor = texture2D(s_texture, v_tex_coord)*u_color;
}
