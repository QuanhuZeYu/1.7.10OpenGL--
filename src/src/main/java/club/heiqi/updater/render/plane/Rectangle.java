package club.heiqi.updater.render.plane;

import club.heiqi.updater.render.ShaderRender;
import club.heiqi.util.FileManager;
import club.heiqi.window.Window;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Rectangle extends APlane{

    public float[] vertices = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f
    };

    public float[] colors = {
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f
    };

    float[] textureCoords = {
        0.0f, 0.0f, // 左下角
        1.0f, 0.0f, // 右下角
        1.0f, 1.0f, // 右上角
        0.0f, 1.0f  // 左上角
    };

    public int[] indices = {
            0, 1, 2,
            2, 3, 0
    };

    public Rectangle(Window window, ShaderRender shaderRender) {
        super(window, shaderRender);
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

//        transform.addPosition(-0.5f, -0.5f, 0);
//        transform.updateMatrix();
        transform.setScale(0.5f, 0.5f, 0);
        transform.updateMatrix();
    }

    @Override
    public void drawElement() {
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        updateColor();
        updatePosition();
    }

    public void updateColor() {
        long i = System.currentTimeMillis() - time;
        float a = (float) ((Math.sin(i / 1000.0) / 2) +0.5);
        float b = (float) ((Math.cos(i / 1000.0) / 2) +0.5);
        float[] newColors = {
                a, b, 0,
                0, a, b,
                b, 0, a,
                a, b, 0
        };
        glBindBuffer(GL_ARRAY_BUFFER, colorVBOID);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(newColors.length);
        buffer.put(newColors).flip();
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void updatePosition() {
        long i = System.currentTimeMillis() - time;
        float a = (float) (Math.sin(i / 1000.0));
        float b = (float) (Math.cos(i / 1000.0));
        transform.setPosition(a, b, 0);
        transform.setRotation(a, b, 0);
        transform.updateMatrix();
    }
}
