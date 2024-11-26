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

## 常错点(错题本)

1. 无论在何处使用着色器相关的函数, 都要确定OpenGL启用了对应的着色器ID -- 时刻问自己当前OpenGL是个什么状态
例如设置uniform 传输顶点数据都要确保着色器ID是正确的
2. 在初始化OpenGL的时候有些内容是需要绑定OpenGL上下文之后才能进行设置的, 有些还需要启动OpenGL能力之后才可以操作
3. 当渲染出来的物体是黑色或者看不见的时候, 首先检查着色器编写是否正确, 再检查传入到着色器的变量是否正确, 再检查是否真的传入到着色器(检查是否启用了`glEnableVertexAttribArray(index);`), 再检查顶点数据定义是否正确(`glVertexAttribPointer(起始位置或者第几个数组, 每个顶点需要选取数组内的数据数量, 数组内定义的数据类型(FLOAT...), 始终为false, 3 * Float.BYTES(每个顶点占用的数据大小, 根据前面定义的3 FLOAT填写该值), 0(始终为0));`)
4. 在多着色程序下要确定着色程序ID是否正确, use正确的着色程序ID

## 测试在shader中混合使用固定管线矩阵