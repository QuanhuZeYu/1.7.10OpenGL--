#version 330
out vec4 FragColor;

in vec4 vertexColor; // 从顶点着色器传来的输入变量（名称相同、类型相同）
in vec2 TexCoord;

uniform sampler2D samp1;

void main(){
    FragColor = texture(samp1, TexCoord) * vertexColor;
}