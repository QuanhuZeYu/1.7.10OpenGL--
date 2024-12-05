package club.heiqi;

import club.heiqi.window.Window;
import club.heiqi.场景.固定管线场景;

public class Main {
    public static void main(String[] args) {
        Window mainWindow = new Window("HeiQi", 1280, 720);

        固定管线场景 scene = new 固定管线场景(mainWindow);

        mainWindow.updateList.add(scene);

        // 开始窗口循环
        mainWindow.loop();
    }
}