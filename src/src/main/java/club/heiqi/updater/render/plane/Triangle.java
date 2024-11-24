package club.heiqi.updater.render.plane;

import club.heiqi.updater.render.Scene;
import club.heiqi.updater.render.transform.Transform;
import club.heiqi.window.Window;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class Triangle extends AMesh {
    public Triangle(Window window, Scene scene) {
        super(window, scene);
        vertices = new float[] {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.0f, 0.5f, 0.0f
        };
        colors = new float[]{
                1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f
        };
        indices = new int[]{
                0, 1, 2
        };
        texturePath = "texture/test2.jpg";
    }

    @Override
    public void drawElement() {
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }
}
