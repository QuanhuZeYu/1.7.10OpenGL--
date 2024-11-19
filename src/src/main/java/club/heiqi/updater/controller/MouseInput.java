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
    public boolean isCursorVisible = true;

    public long windowHandle;

    public MouseInput(Window window) {
        super(window);
        this.windowHandle = window.handle;
    }

    public void register() {
        glfwSetCursorEnterCallback(windowHandle, (handle, entered) -> inWindow = entered);
        glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> {
            deltaX = 0;
            deltaY = 0;
            mouseX = xpos;
            mouseY = ypos;
            if (preMouseX > 0 && preMouseY > 0 && inWindow) {
                deltaX = mouseX - preMouseX;
                deltaY = mouseY - preMouseY;
            }
            preMouseX = mouseX;
            preMouseY = mouseY;
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
}
