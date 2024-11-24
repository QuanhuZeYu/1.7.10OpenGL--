package club.heiqi.updater.render.cube;

import club.heiqi.updater.render.Scene;
import club.heiqi.updater.render.plane.AMesh;
import club.heiqi.updater.render.transform.Transform;
import club.heiqi.window.Window;

import static org.lwjgl.opengl.GL11.*;

public class Cube extends AMesh {
    public Cube(Window window, Scene scene) {
        super(window, scene);
        vertices = new float[] {
                0.5f, 0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
        };
        indices = new int[] {
                0, 4, 6, 2,
                3, 2, 6, 7,
                7, 6, 4, 5,
                5, 1, 3, 7,
                1, 0, 2, 3,
                5, 4, 0, 1
        };
        texturePath = "texture/test.png";
    }

    @Override
    public void drawElement() {
        glDrawElements(GL_QUADS, indices.length, GL_UNSIGNED_INT, 0);
    }
}
