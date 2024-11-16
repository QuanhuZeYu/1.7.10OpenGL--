package club.heiqi.window;

import club.heiqi.updater.AUpdate;
import club.heiqi.updater.controller.KeyInput;
import org.lwjgl.opengl.GL11;

import java.util.LinkedHashSet;
import java.util.Set;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;

public class Window {
    public String title;
    public int w, h;
    public float fps;
    public long handle;

    public long markTime = 0;
    public int frames;

    public AUpdate keyInputController;
    public Set<AUpdate> renders = new LinkedHashSet<>();

    // 构造一个OpenGL窗口
    public Window(String title, int w, int h) {
        this.title = title;
        this.w = w;
        this.h = h;
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
        handle = glfwCreateWindow(w, h, title, 0, 0);
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
    }

    public void loop() {
        while (!glfwWindowShouldClose(handle)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            inputUpdate();
            renderUpdate();
            glfwSwapBuffers(handle);
            internalUpdate();
            glfwPollEvents();
        }
        // 释放资源
        glfwDestroyWindow(handle);
        glfwTerminate();
    }

    public void inputUpdate() {
        if (keyInputController == null) return;
        keyInputController.update();
    }

    public void renderUpdate() {
        if (glfwGetCurrentContext() != handle) {
            logger.error("OpenGL上下文无效");
            throw new IllegalStateException("OpenGL上下文无效");
        }
        for (AUpdate render : renders) {
            render.update();
        }
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
