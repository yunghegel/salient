#version 150

uniform vec3 u_wirecolor;

uniform vec3 u_fillcolor;

varying vec3 dist;

varying vec3 v_position;

uniform vec3 u_cameraPosition;

uniform float u_alphaCutoff;

uniform float u_alpha;

uniform int u_useDistanceFalloff;

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

    float dst = distance(v_position, u_cameraPosition);
    float falloff = 0.5;

    if (u_useDistanceFalloff == 0)
    {
        if (d>u_alphaCutoff)
        {
            discard;
        }
        gl_FragColor.a = u_alpha;
        return;
    }

    if (d>u_alphaCutoff * (1.0 / (1.0 + dst * falloff)))
    {
        discard;
    }

    gl_FragColor.a = u_alpha;
}