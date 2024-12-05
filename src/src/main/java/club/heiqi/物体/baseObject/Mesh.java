package club.heiqi.物体.baseObject;

import club.heiqi.接口.IBind;
import club.heiqi.接口.IDrawable;
import club.heiqi.接口.ISetup;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh implements IDrawable, ISetup, IBind {
    public Map<Integer, Material> materials = new HashMap<>(); // 索引: 材质

    public String meshName;
    public float[] vertices;
    public float[] normals;
    public float[] textureCoords;
    public float[] tangents;
    public float[] bitangents;
    public float[] colors;
    public int[] indices;
    public int aimMaterialIndex;

    public int vaoID;
    public int verticesVBO;
    public int normalsVBO;
    public int textureCoordsVBO;
    public int colorsVBO; // 使用RGBA颜色
    public int eboID;
    public boolean hasEBO = false;

    public Mesh() {

    }

    @Override
    public void draw() {
        bind();
        if (hasEBO) {
            glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        } else {
            glDrawArrays(GL_TRIANGLES, 0, vertices.length / 3);
        }
        glBindVertexArray(0);
    }

    public void setup() {
        vaoID = createVAO();
        glBindVertexArray(vaoID);

        verticesVBO = createVBO(vertices, GL_ARRAY_BUFFER, GL_STATIC_DRAW);
        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(3, GL_FLOAT, 0, 0);

        if (normals != null && normals.length > 0) {
            normalsVBO = createVBO(normals, GL_ARRAY_BUFFER, GL_STATIC_DRAW);
            glEnableClientState(GL_NORMAL_ARRAY);
            glNormalPointer(GL_FLOAT, 0, 0);
        }
        if (textureCoords != null && textureCoords.length > 0) {
            textureCoordsVBO = createVBO(textureCoords, GL_ARRAY_BUFFER, GL_STATIC_DRAW);
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            glTexCoordPointer(2, GL_FLOAT, 0, 0);
        }
        if (colors != null && colors.length > 0) {
            colorsVBO = createVBO(colors, GL_ARRAY_BUFFER, GL_STATIC_DRAW);
            glEnableClientState(GL_COLOR_ARRAY);
            glColorPointer(4, GL_FLOAT, 0, 0);
        }
        materials.forEach((index, material) -> {
            material.setup();
        });
        if (indices != null) {
            eboID = createEBO(indices, GL_STATIC_DRAW);
            hasEBO = true;
        }
        unbind();
    }

    /**
     * 开始绘制前绑定所需的各项属性
     */
    public void bind() {
        glBindVertexArray(vaoID);
        materials.forEach((index, material) -> {
            material.bind();
        });
    }

    public int createVAO() {
        vaoID = glGenVertexArrays();
        return vaoID;
    }

    public int createVBO(float[] data, int target, int type) {
        int vboID = glGenBuffers();
        glBindBuffer(target, vboID);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data).flip();
        glBufferData(target, buffer, type);
        return vboID;
    }

    public int createEBO(int[] data, int type) {
        int eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, type);
        return eboID;
    }

    /**
     * 解绑VAO - 解绑VBO; 防止误操作
     */
    public void unbind() {
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
