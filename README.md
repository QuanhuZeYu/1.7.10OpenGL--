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
