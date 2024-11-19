package club.heiqi;

import club.heiqi.updater.AUpdate;
import club.heiqi.updater.controller.KeyInput;
import club.heiqi.updater.controller.MouseInput;
import club.heiqi.updater.render.Scene;
import club.heiqi.updater.render.TestRender;
import club.heiqi.window.Window;

public class Main {
    public static void main(String[] args) {
        Window mainWindow = new Window("HeiQi", 1280, 720);
        mainWindow.keyInputController = new KeyInput("键盘", mainWindow);
        mainWindow.mouseInputController = new MouseInput(mainWindow);
        mainWindow.logicUpdaters.add(mainWindow.keyInputController);
        mainWindow.logicUpdaters.add(mainWindow.mouseInputController);

        AUpdate testRender = new TestRender(mainWindow);
        AUpdate scene = new Scene(mainWindow);
        // 渲染器
        mainWindow.renders.add(testRender);
        mainWindow.renders.add(scene);

        mainWindow.loop();
    }
}