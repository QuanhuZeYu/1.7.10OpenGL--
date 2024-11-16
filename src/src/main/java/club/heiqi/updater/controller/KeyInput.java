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
        if (deviceName != null && !deviceName.isEmpty()) this.deviceName = deviceName;
        this.window = window;
    }

    public void register() {
        glfwSetKeyCallback(window.handle, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                pressedKeys.add(key);
                logger.info("按下键码: ${} 键名: ${}", key, glfwGetKeyName(key, scancode));
                logger.info(pressedKeys);
            }
            else if (action == GLFW_RELEASE) {
                pressedKeys.remove(key);
                logger.info("释放键码: ${} 键名: ${}", key, glfwGetKeyName(key, scancode));
                logger.info(pressedKeys);
            }
        });
    }

    @Override
    public void update() {
        if (!isRegistered) register();
    }
}
