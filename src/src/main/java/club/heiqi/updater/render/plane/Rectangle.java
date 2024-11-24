package club.heiqi.updater.render.plane;

import club.heiqi.updater.render.Scene;
import club.heiqi.util.FileManager;
import club.heiqi.window.Window;

import java.io.File;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Rectangle extends AMesh {
    public Rectangle(Window window, Scene shaderRender) {
        super(window, shaderRender);
        vertices = new float[]{
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.5f, 0.5f, 0.0f,
                -0.5f, 0.5f, 0.0f
        };
        colors = new float[]{
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                01.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
        };
        indices = new int[]{
                0, 1, 2,
                2, 3, 0
        };
        textureCoords = new float[]{
                0.0f, 0.0f, // 左下角
                1.0f, 0.0f, // 右下角
                1.0f, 1.0f, // 右上角
                0.0f, 1.0f  // 左上角
        };
        texturePath = "texture/test.png";

        File texture = FileManager.getFile("texture/test.png");
        vaoID = createVAO();
        glBindVertexArray(vaoID);

        vertexVBOID = createVBO(vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        colorVBOID = createVBO(colors, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        createTexture(texture, textureCoords);

        eboID = createVBO(indices, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
