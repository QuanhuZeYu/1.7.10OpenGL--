package club.heiqi;

import club.heiqi.updater.AUpdate;
import club.heiqi.updater.controller.KeyInput;
import club.heiqi.updater.render.ShaderRender;
import club.heiqi.updater.render.TestRender;
import club.heiqi.window.Window;

public class Main {
    public static void main(String[] args) {
        Window mainWindow = new Window("HeiQi", 1280, 720);
        KeyInput keyInputController = new KeyInput("键盘", mainWindow);
        mainWindow.keyInputController = keyInputController;

        AUpdate testRender = new TestRender(mainWindow);
        AUpdate shaderRender = new ShaderRender(mainWindow);
        // 渲染器
        mainWindow.renders.add(testRender);
        mainWindow.renders.add(shaderRender);

        mainWindow.loop();
    }
}