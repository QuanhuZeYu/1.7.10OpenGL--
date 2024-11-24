#version 330
out vec4 FragColor;

in vec4 vertexColor; // 从顶点着色器传来的输入变量（名称相同、类型相同）
in vec2 TexCoord;

uniform sampler2D samp1;

uniform vec3 objectColor;
uniform vec3 lightColor;

void main(){
    FragColor = vec4(objectColor * lightColor, 1.0);
}