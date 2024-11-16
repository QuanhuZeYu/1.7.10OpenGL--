package club.heiqi.updater.render;

import club.heiqi.updater.AUpdate;
import club.heiqi.window.Window;
import org.lwjgl.opengl.GL11;

public class TestRender extends AUpdate {
    public Window window;
    public TestRender(Window window) {
        this.window = window;
    }

    @Override
    public void update() {
//        logger.info("render");
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2f(-0.5f, -0.5f);
        GL11.glVertex2f(0.5f, -0.5f);
        GL11.glVertex2f(0.0f, 0.5f);
        GL11.glEnd();
    }
}
