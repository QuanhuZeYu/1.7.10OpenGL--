package club.heiqi.updater.render.plane;

import club.heiqi.util.FileManager;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Triangle extends APlane{
    public int eboID;
    public int vaoID;
    public int vertexVBOID;
    public int colorVBOID;
    public int textureID;
    public int textureCoordVBOID;

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

    public Triangle() {
        File textureF = FileManager.getFile("texture/test2.jpg");
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image = stbi_load(textureF.getAbsolutePath(), width, height, channels, 4);

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

        textureCoordVBOID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, textureCoordVBOID);
        buffer = BufferUtils.createFloatBuffer(textureCoords.length);
        buffer.put(textureCoords).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);

        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        // 上传纹理数据到 GPU
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(),
             0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glGenerateMipmap(GL_TEXTURE_2D);

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0); // 启用顶点位置
        glEnableVertexAttribArray(1); // 启用顶点颜色
        glEnableVertexAttribArray(2); // 启用纹理UV坐标
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        if (image != null) {
            stbi_image_free(image);
        }
    }

    @Override
    public void draw() {
        glBindVertexArray(vaoID);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }
}
