package club.heiqi.modelLoader;

import club.heiqi.shader.ShaderProgram;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class MeshData {
    public int vao, ebo;
    public int vbo;

    public VertexData vertices;
    public TextureData textures;

    public MeshData(VertexData vertices, TextureData textures) {
        this.vertices = vertices;
        this.textures = textures;
        setup();
    }

    public void setup() {
        vao = ShaderProgram.createVAO();
        glBindVertexArray(vao);

        // 顶点数据
        if (vertices.getVertices() != null) {
            vbo = ShaderProgram.createVBO(vertices.getVertices(), GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES * 3, 0);
            glEnableVertexAttribArray(0);

            /*glEnableClientState(GL_VERTEX_ARRAY);
            FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.getVertices().length);
            buffer.put(vertices.getVertices()).flip();
            glVertexPointer(3, GL_FLOAT, 0, buffer);*/
        } else {
            throw new RuntimeException("顶点数据为空");
        }

//         法线数据
        if (vertices.getNormal() != null) {
            vbo = ShaderProgram.createVBO(vertices.getNormal(), GL_STATIC_DRAW);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, Float.BYTES * 3, 0);
            glEnableVertexAttribArray(1);

            /*glEnableClientState(GL_NORMAL_ARRAY);
            FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.getNormal().length);
            buffer.put(vertices.getNormal()).flip();
            glNormalPointer(GL_FLOAT, 0, buffer);*/
        }
//
//         纹理坐标数据
//        if (vertices.getTextureCoords() != null) {
//            /*float[] textureCoords = vertices.getTextureCoords();
//            FloatBuffer buffer = BufferUtils.createFloatBuffer(textureCoords.length);
//            buffer.put(textureCoords).flip();
//            glTexCoordPointer(2, GL_FLOAT, 0, buffer);*/

//            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
//            FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.getTextureCoords().length);
//            buffer.put(vertices.getTextureCoords()).flip();
//            glTexCoordPointer(2, GL_FLOAT, 0, buffer);
//        }
//
        // 颜色数据
//        if (vertices.getColors() != null) {
//            /*vbo = ShaderProgram.createVBO(vertices.getColors(), GL_STATIC_DRAW);
//            glVertexAttribPointer(1, 4, GL_FLOAT, false, Float.BYTES * 4, 0);
//            glEnableVertexAttribArray(1);*/
//
//            glEnableClientState(GL_COLOR_ARRAY);
//            FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.getColors().length);
//            buffer.put(vertices.getColors()).flip();
//            glColorPointer(4, GL_FLOAT, 0, buffer);
//        }

        // 元素索引数据
        if (vertices.indices != null && !vertices.indices.isEmpty()) {
            ebo = ShaderProgram.createVBO(vertices.getIndices(), GL_STATIC_DRAW);
        } else {
            throw new RuntimeException("元素索引数据为空");
        }

        // 解绑 VAO 和 VBO
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static float[] toFloatArray(@NotNull List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static int[] toIntArray(@NotNull List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
