package club.heiqi.updater.render.plane;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Rectangle extends APlane{
    public long time = System.currentTimeMillis();
    public int eboID;
    public int vaoID;
    public int vertexVBOID;
    public int colorVBOID;

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

    public int[] indices = {
            0, 1, 2,
            2, 3, 0
    };

    public Rectangle() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vertexVBOID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBOID);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        colorVBOID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, colorVBOID);
        buffer = BufferUtils.createFloatBuffer(colors.length);
        buffer.put(colors).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    @Override
    public void draw() {
        glBindVertexArray(vaoID);
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        updateColor();
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
        glBindVertexArray(0);
    }
}
