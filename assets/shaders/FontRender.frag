#version 130

uniform sampler2D fontSprite;

in vec2 uv;
out vec4 color;

void main() {
    vec2 uv_ndc = vec2(uv.x, uv.y);

	color = texture(fontSprite, uv_ndc).rgba;
    color =  vec4(1.0, 0.0, 0.0, color.a);

}




