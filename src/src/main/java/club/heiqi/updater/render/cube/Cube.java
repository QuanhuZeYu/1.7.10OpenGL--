package club.heiqi.updater.render.cube;

import club.heiqi.updater.render.Scene;
import club.heiqi.updater.render.plane.AMesh;
import club.heiqi.updater.render.plane.Texture;
import club.heiqi.window.Window;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Cube extends AMesh {
    public Cube(Window window, Scene scene) {
        super(window, scene);
        vertices = new float[] {
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,

                -0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,

                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,

                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,

                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f, -0.5f,

                -0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
        };
        normals = new float[]{
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,

                0.0f,  0.0f,  1.0f,
                0.0f,  0.0f,  1.0f,
                0.0f,  0.0f,  1.0f,
                0.0f,  0.0f,  1.0f,
                0.0f,  0.0f,  1.0f,
                0.0f,  0.0f,  1.0f,

                -1.0f,  0.0f,  0.0f,
                -1.0f,  0.0f,  0.0f,
                -1.0f,  0.0f,  0.0f,
                -1.0f,  0.0f,  0.0f,
                -1.0f,  0.0f,  0.0f,
                -1.0f,  0.0f,  0.0f,

                1.0f,  0.0f,  0.0f,
                1.0f,  0.0f,  0.0f,
                1.0f,  0.0f,  0.0f,
                1.0f,  0.0f,  0.0f,
                1.0f,  0.0f,  0.0f,
                1.0f,  0.0f,  0.0f,

                0.0f, -1.0f,  0.0f,
                0.0f, -1.0f,  0.0f,
                0.0f, -1.0f,  0.0f,
                0.0f, -1.0f,  0.0f,
                0.0f, -1.0f,  0.0f,
                0.0f, -1.0f,  0.0f,

                0.0f,  1.0f,  0.0f,
                0.0f,  1.0f,  0.0f,
                0.0f,  1.0f,  0.0f,
                0.0f,  1.0f,  0.0f,
                0.0f,  1.0f,  0.0f,
                0.0f,  1.0f,  0.0f,
        };
        textureCoords = new float[] {
                0.0f,  0.0f,
                1.0f,  0.0f,
                1.0f,  1.0f,
                1.0f,  1.0f,
                0.0f,  1.0f,
                0.0f,  0.0f,

                0.0f,  0.0f,
                1.0f,  0.0f,
                1.0f,  1.0f,
                1.0f,  1.0f,
                0.0f,  1.0f,
                0.0f,  0.0f,

                1.0f,  0.0f,
                1.0f,  1.0f,
                0.0f,  1.0f,
                0.0f,  1.0f,
                0.0f,  0.0f,
                1.0f,  0.0f,

                1.0f,  0.0f,
                1.0f,  1.0f,
                0.0f,  1.0f,
                0.0f,  1.0f,
                0.0f,  0.0f,
                1.0f,  0.0f,

                0.0f,  1.0f,
                1.0f,  1.0f,
                1.0f,  0.0f,
                1.0f,  0.0f,
                0.0f,  0.0f,
                0.0f,  1.0f,

                0.0f,  1.0f,
                1.0f,  1.0f,
                1.0f,  0.0f,
                1.0f,  0.0f,
                0.0f,  0.0f,
                0.0f,  1.0f
        };
        Texture boxDiffuse = new Texture("texture/boxDiff.png", "boxDiff", Texture.TexturePos.DIFFUSE.pos);
        Texture boxSpecular = new Texture("texture/boxSpec.png", "boxSpec", Texture.TexturePos.SPECULAR.pos);
        registryNameAndTexture.put(boxDiffuse.registryName, boxDiffuse);
        registryNameAndTexture.put(boxSpecular.registryName, boxSpecular);
    }

    @Override
    public void setup() {
        super.setup();
    }

    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public void drawElement() {
        super.drawElement();
    }
}
