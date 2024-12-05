package club.heiqi.window;

import java.util.ArrayList;
import java.util.List;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    public String title;
    public int width, height;
    public float fps;
    public long handle;

    public long markTime = 0;
    public int frames;

    // 构造一个OpenGL窗口
    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        init();  // 初始化OpenGL
        createWindow();
    }

    public void init() {
    if (!glfwInit()) {
        throw new IllegalStateException("无法初始化 GLFW");
    }

    // 配置 OpenGL 版本
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); // OpenGL 主版本号
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3); // OpenGL 次版本号
    // 设置为兼容模式，支持即时模式的功能
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
    // 允许窗口调整大小
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
}

    public void createWindow() {
        handle = glfwCreateWindow(width, height, title, 0, 0);
        if (handle == 0) {
            glfwTerminate();
            logger.error("创建窗口失败");
            System.exit(1);
        }
        glfwMakeContextCurrent(handle); logger.info("使用 ${} 作为上下文", handle);
        if (glfwGetCurrentContext() != handle) {
            logger.error("当前上下文无效");
            throw new IllegalStateException("当前上下文无效");
        }
        glfwSwapInterval(GLFW_TRUE); // 垂直同步
        createCapabilities(); // 创建OpenGL上下文
        glfwShowWindow(handle);

        glfwSetWindowSizeCallback(handle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            glViewport(0, 0, width, height);
        });
    }

    public void loop() {
        while (!glfwWindowShouldClose(handle)) {
            // 清除颜色缓冲区设置，通常放在渲染前
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);  // 设置清除颜色为黑色
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);  // 清除颜色缓冲区和深度缓冲区
            logicUpdate();
            renderUpdate();
            glfwPollEvents();
            glfwSwapBuffers(handle);
            internalUpdate();
        }
        // 释放资源
        glfwDestroyWindow(handle);
        glfwTerminate();
    }

    public void logicUpdate() {

    }

    public void renderUpdate() {

    }

    public void internalUpdate() {
        calculateFPS();
    }

    /**
     * 以1s为间隔更新一次FPS统计
     */
    public void calculateFPS() {
        frames++;
        if (markTime == 0) {
            markTime = System.currentTimeMillis();
        }
        long curTime = System.currentTimeMillis();
        if (curTime - markTime < 1000) return;
        else {
            fps = (float) frames / ((float) (curTime - markTime) / 1000);
            frames = 0;
            markTime = System.currentTimeMillis();
            glfwSetWindowTitle(handle, title + " - FPS: " + fps);
        }
    }
}
