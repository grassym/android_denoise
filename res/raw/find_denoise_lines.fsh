// find_denoise_lines.fsh

#ifdef GL_ES
precision highp float;
#endif

varying vec2 v_tex_coord;

uniform sampler2D s_result;
uniform sampler2D s_original;
uniform sampler2D s_square_avg;

uniform float u_sigma;
uniform vec2 u_square;
uniform float u_line_angle;
uniform int u_points;

#define M_PI 3.1415926
#define M_PI_2 1.5707963

void main()
{
    //float line_sigma = u_sigma / sqrt(u_points);
    
    vec2 sht = (u_square/2.0)*vec2(cos(u_line_angle), sin(u_line_angle));
    vec2 p1 = v_tex_coord + sht, p2 = v_tex_coord - sht;
    vec4 line_sum = vec4(0.0);
    vec2 lstep = (p2-p1)/float(u_points);
    
    vec4 line_sum_left = vec4(0.0), line_sum_right = vec4(0.0);
    for(int i = 0; i <= u_points/2; ++i)
    {
        vec4 cp = texture2D(s_original, p1 + lstep*float(i));
        line_sum_left += cp;
        line_sum += cp;
    }
    for(int i = u_points/2; i < u_points; ++i)
    {
        vec4 cp = texture2D(s_original, p1 + lstep*float(i));
        line_sum_right += cp;
        line_sum += cp;
    }
    line_sum -= texture2D(s_original, v_tex_coord);
    
    line_sum /= float(u_points);
    line_sum_left /= float(u_points/2);
    line_sum_right /= float(u_points/2);
    
    float not_line = step(3.0*u_sigma/sqrt(float(u_points/2)), length(line_sum_left-line_sum_right));
    
    vec4 diff = (line_sum - texture2D(s_square_avg, v_tex_coord));
    
    float dev = length(diff);
    vec4 prev = texture2D(s_result, v_tex_coord);
    
    float good_line = step( prev.a, dev )*(1.0-not_line);
    
    gl_FragColor = mix( prev, vec4(line_sum.rgb, dev), good_line);
}


