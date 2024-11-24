#version 330
layout (location = 0) in vec3 aPos; // 位置变量的属性位置值为0
layout (location = 1) in vec3 aColor;
layout (location = 2) in vec2 aTexCoord;
//layout (location = 3) in mat4 aTransform;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec4 vertexColor; // 为片段着色器指定一个颜色输出
out vec2 TexCoord;

void main()
{
    gl_Position = projection * view * model * vec4(aPos, 1.0); // 使用变换矩阵
    vertexColor = vec4(1.0);
    TexCoord = aTexCoord;
}