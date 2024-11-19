package club.heiqi.updater.controller;

import club.heiqi.updater.AUpdate;
import club.heiqi.window.Window;

import java.util.*;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.glfw.GLFW.*;

public class KeyInput extends AUpdate {
    public String deviceName = "KeyInput";

    public boolean isRegistered = false;

    public Set<Integer> pressedKeys = new LinkedHashSet<>();

    public KeyInput(String deviceName, Window window) {
        super(window);
        if (deviceName != null && !deviceName.isEmpty()) this.deviceName = deviceName;
    }

    public void register() {
        glfwSetKeyCallback(window.handle, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                pressedKeys.add(key);
            }
            else if (action == GLFW_RELEASE) {
                pressedKeys.remove(key);
            }
        });
    }

    @Override
    public void update() {
        logger.info("尝试注册键盘控制器");
        if (!isRegistered) {
            register();
            isRegistered = isNeedUnload = true;
            logger.info("注册键盘控制器成功");
        }
    }
}
