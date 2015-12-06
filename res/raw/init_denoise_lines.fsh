// find_denoise_lines.fsh

#ifdef GL_ES
precision highp float;
#endif

varying vec2 v_tex_coord;

uniform sampler2D s_original;
uniform sampler2D s_square_avg;

uniform float u_sigma;
uniform int u_points;

void main()
{
    float thres = 3.0*u_sigma/sqrt(float(u_points));
   
    vec4 avg = texture2D(s_square_avg, v_tex_coord);
    vec4 pix = texture2D(s_original, v_tex_coord);
    
    float dev = length(pix-avg)/sqrt(float(u_points));
    
    float use_pix = step( thres, dev );
    
    gl_FragColor = mix( vec4(avg.rgb, thres ) , vec4(pix.rgb, dev ), use_pix);
}


