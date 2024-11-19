package club.heiqi.updater.render.cube;

import club.heiqi.updater.render.Scene;
import club.heiqi.updater.render.plane.Drawable;
import club.heiqi.updater.render.plane.Rectangle;
import club.heiqi.window.Window;

import static org.lwjgl.opengl.GL11.*;

public class Cube implements Drawable {
    public Rectangle front;
    public Rectangle back;
    public Rectangle left;
    public Rectangle right;
    public Rectangle top;
    public Rectangle bottom;

    public Cube(Window window, Scene scene) {
        this.front = new Rectangle(window, scene);
        this.back = new Rectangle(window, scene);
        this.left = new Rectangle(window, scene);
        this.right = new Rectangle(window, scene);
        this.top = new Rectangle(window, scene);
        this.bottom = new Rectangle(window, scene);
        bottom.transform.setRotation((float) Math.toRadians(90), 0, 0);
        bottom.transform.setPosition(0, -0.5f, -0.5f);
        bottom.transform.updateMatrix();
        top.transform.setRotation((float) Math.toRadians(-90), 0, 0);
        top.transform.setPosition(0, 0.5f, -0.5f);
        top.transform.updateMatrix();
        left.transform.setRotation(0, (float) Math.toRadians(-90), 0);
        left.transform.setPosition(-0.5f, 0, -0.5f);
        left.transform.updateMatrix();
        right.transform.setRotation(0, (float) Math.toRadians(90), 0);
        right.transform.setPosition(0.5f, 0, -0.5f);
        right.transform.updateMatrix();
        back.transform.setRotation(0, (float) Math.toRadians(180), 0);
        back.transform.setPosition(0, 0, -1);
        back.transform.updateMatrix();
    }

    @Override
    public void draw() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_DEPTH_TEST);
        front.draw();
        back.draw();
        left.draw();
        right.draw();
        top.draw();
        bottom.draw();
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
    }
}
