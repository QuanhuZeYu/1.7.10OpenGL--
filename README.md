# 1.7.10 OpenGL渲染研究测试

本仓库为测试OpenGL在1.7.10中渲染准备

## OpenGL设置要点

```java
// 设置为兼容模式，支持即时模式的功能
glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
```

即时模式的特征为:
使用glBegin()和glEnd()来渲染，而不是使用VAO等特性来渲染。

即时模式固定管线可以直接使用视图矩阵来进行渲染变换

## 测试shader是否能在即时模式下使用

即时模式可以和着色器渲染一起使用, 需要注意的是在使用完着色器之后需要重置为默认着色器 `glUseProgram(0)` 
如果存在多个着色器可能需要先获取当前着色器的id `id = glGetInteger(GL_CURRENT_PROGRAM)` 在使用完后重置为当前着色器 `glUseProgram(id)`
