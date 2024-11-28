package club.heiqi.updater.controller;

import club.heiqi.updater.AUpdate;
import club.heiqi.window.Window;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.glfw.GLFW.*;

public class MouseInput extends AUpdate {
    public String deviceName = "MouseInput";

    public boolean isRegistered = false;

    public double preMouseX, preMouseY;
    public double mouseX, mouseY;
    public double deltaX, deltaY;

    public boolean inWindow = false;
    public boolean isLeftButtonPressed = false;
    public boolean isRightButtonPressed = false;
    public boolean isMiddleButtonPressed = false;

    public boolean isCursorLocked = false;

    public long windowHandle;

    public MouseInput(Window window) {
        super(window);
        this.windowHandle = window.handle;
    }

    public void register() {
        glfwSetCursorEnterCallback(windowHandle, (handle, entered) -> inWindow = entered);
        glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> {
            if (isCursorLocked) {
                glfwSetCursorPos(windowHandle, (double) this.window.width / 2, (double) this.window.height / 2);
            }
            deltaX = 0;
            deltaY = 0;
            mouseX = xpos;
            mouseY = ypos;
            if (inWindow) {
                deltaX = mouseX - preMouseX;
                deltaY = mouseY - preMouseY;
            }
            preMouseX = isCursorLocked? (double) this.window.width / 2 : mouseX;
            preMouseY = isCursorLocked? (double) this.window.height / 2 : mouseY;
        });
        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> {
            switch (button) {
                case GLFW_MOUSE_BUTTON_LEFT -> {
                    isLeftButtonPressed = action == GLFW_PRESS;
                }
                case GLFW_MOUSE_BUTTON_RIGHT -> {
                    isRightButtonPressed = action == GLFW_PRESS;
                }
                case GLFW_MOUSE_BUTTON_MIDDLE -> {
                    isMiddleButtonPressed = action == GLFW_PRESS;
                }
            }
        });
        double[] xpos = new double[1];
        double[] ypos = new double[1];
        glfwGetCursorPos(windowHandle, xpos, ypos);
        preMouseX = xpos[0];
        preMouseY = ypos[0];
    }

    @Override
    public void update() {
        logger.info("正在尝试注册鼠标控制器");
        if (!isRegistered) {
            register();
            isRegistered = isNeedUnload = true;
            logger.info("注册鼠标控制器成功");
        }
    }

    /**
     * 获取移动距离并重置(回调仅在移动时才会使用, 使用use可以保证delta在预期实现内)
     * @return
     */
    public float useDeltaX() {
        float result = (float) deltaX;
        deltaX = 0;
        return result;
    }

    public float useDeltaY() {
        float result = (float) deltaY;
        deltaY = 0;
        return result;
    }
}
