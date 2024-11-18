package club.heiqi.updater.render.plane;

import club.heiqi.updater.render.Scene;
import club.heiqi.util.FileManager;
import club.heiqi.window.Window;

import java.io.File;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Triangle extends APlane{

    public float[] vertices = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.0f, 0.5f, 0.0f
    };
    public float[] colors = {
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f
    };
    float[] textureCoords = {
        0.0f, 0.0f, // 左下角
        1.0f, 0.0f, // 右下角
        1.0f, 1.0f, // 右上角
        0.0f, 1.0f  // 左上角
    };
    public int[] indices = {
            0, 1, 2
    };

    public Triangle(Window window, Scene scene) {
        super(window, scene);
        File textureF = FileManager.getFile("texture/test2.jpg");

        vaoID = createVAO();
        glBindVertexArray(vaoID);

        vertexVBOID = createVBO(vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        colorVBOID = createVBO(colors, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        createTexture(textureF, textureCoords);

        eboID = createVBO(indices, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0); // 启用顶点位置
        glEnableVertexAttribArray(1); // 启用顶点颜色
        glEnableVertexAttribArray(2); // 启用纹理UV坐标
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void drawElement() {
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }
}
