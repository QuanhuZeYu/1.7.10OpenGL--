package club.heiqi.updater.render;

import club.heiqi.updater.AUpdate;
import club.heiqi.updater.render.plane.Drawable;
import club.heiqi.window.Window;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class QZIMGUI extends AUpdate implements Drawable {
    public ImGuiImplGlfw imgui;
    public QZIMGUI(Window window) {
        super(window);
        imgui = new ImGuiImplGlfw();
        imgui.init(window.handle, true);
    }

    @Override
    public void draw() {
        // 新的一帧
        imgui.newFrame();
    }
}
