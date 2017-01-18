precision mediump float;

varying vec3 N;
const vec3 lightDir = vec3(0., 0., -1.);

void main()
{
    float light = .3 + .7 * max( 0., dot(normalize(N), lightDir));
	gl_FragColor = light * vec4(1.0);
}
