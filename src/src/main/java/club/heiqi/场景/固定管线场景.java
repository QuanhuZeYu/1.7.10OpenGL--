package club.heiqi.场景;

import club.heiqi.window.Window;
import club.heiqi.接口.IDrawable;
import club.heiqi.接口.IUpdate;
import club.heiqi.物体.精妙背包;
import club.heiqi.组件.Camera;

import java.util.ArrayList;
import java.util.List;

public class 固定管线场景 implements IUpdate, IDrawable {
    public Window window;
    public Camera camera;

    public List<IDrawable> drawables = new ArrayList<>();

    public 固定管线场景(Window window) {
        this.window = window;
        camera = new Camera(window.width, window.height);
        addItem();
    }

    @Override
    public void update() {
        camera.update();
    }

    @Override
    public void draw() {
        for (IDrawable drawable : drawables) {
            drawable.draw();
        }
    }

    public void addItem() {
        精妙背包 bag = new 精妙背包();

        drawables.add(bag);
    }
}
