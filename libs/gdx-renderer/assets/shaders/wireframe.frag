#version 150

const vec3 u_wirecolor = vec3(1.0, 1.0, 0.0);
const vec3 u_fillcolor = vec3(0.0, 0.0, 0.0);
varying vec3 dist;
void main()
{
    // Undo perspective correction.
    //vec3 dist_vec = dist * gl_FragCoord.w;

    // Wireframe rendering is better like this:
    vec3 dist_vec = dist;

    // Compute the shortest distance to the edge
    float d = min(dist_vec[0], min(dist_vec[1], dist_vec[2]));

    // Compute line intensity and then fragment color
    float I = exp2(-2.0*d*d);

    gl_FragColor.rgb = I* u_wirecolor + (1.0 - I)* u_fillcolor;

//    antialiasing
    if (d > 0.1)
        discard;

    gl_FragColor.a = 0.5;
}