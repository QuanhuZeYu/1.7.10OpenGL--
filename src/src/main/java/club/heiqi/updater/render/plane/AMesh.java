package club.heiqi.updater.render.plane;

import club.heiqi.shader.FragShader;
import club.heiqi.shader.ShaderProgram;
import club.heiqi.shader.VertexShader;
import club.heiqi.updater.render.Scene;
import club.heiqi.updater.render.transform.Transform;
import club.heiqi.window.Window;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public abstract class AMesh implements Drawable{
    public long time = System.currentTimeMillis();
    public int eboID;
    public int vaoID;
    public int vertexVBOID;
    public int normalVBOID;
    public int colorVBOID;
    public int textureID;
    public int textureCoordVBOID;

    // ========== 静态字段 ==========
    public static final Vector4f DEFAULT_VERTEX_COLOR = new Vector4f(1.0f, 0.5f, 0.31f, 1.0f);
    public static final Vector3f DEFAULT_MATERIAL_AMBIENT = new Vector3f(1.0f, 0.5f, 0.31f);
    public static final Vector3f DEFAULT_MATERIAL_DIFFUSE = new Vector3f(1.0f, 0.5f, 0.31f);
    public static final Vector3f DEFAULT_MATERIAL_SPECULAR = new Vector3f(0.5f, 0.5f, 0.5f);
    public static final float DEFAULT_SHININESS = 32.0f;
    public static final float[] DEFAULT_TEXTURE_COORDS = {
            0.0f, 0.0f, // 左下角
            1.0f, 0.0f, // 右下角
            1.0f, 1.0f, // 右上角
            0.0f, 1.0f  // 左上角
    };
    public static final Vector3f DEFAULT_OBJECT_COLOR = new Vector3f(1.0f, 0.5f, 0.31f);
    // ==========          ==========

    public float[] vertices;
    public float[] normals;
    public float[] colors;
    public float[] textureCoords;
    public int[] indices;
    public String texturePath;
    // ===== 材质属性 =====
    public Vector3f materialAmbient;
    public Vector3f materialDiffuse;
    public Vector3f materialSpecular;
    public float shininess;
    // =====         =====
    public boolean isSetup = false;
    public boolean hasTexture = false;

    public Window window;
    public int programID;
    public Scene scene;
    public ShaderProgram objShaderProgram;
    public Matrix4f viewMatrix;
    public Transform transform;

    public AMesh(Window window, Scene scene) {
        this.window = window;
        this.scene = scene;
        objShaderProgram = scene.objShaderProgram;
        programID = scene.objShaderProgram.programID;
        viewMatrix = scene.viewMatrix;
        transform = new Transform();
    }

    public void setup() {
        vaoID = createVAO();
        glBindVertexArray(vaoID);

        vertexVBOID = createVBO(vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        if (normals != null) {
            normalVBOID = createVBO(normals, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
            glEnableVertexAttribArray(1);
        }
        // 预处理顶点颜色
        if (colors == null || colors.length != vertices.length) {
            colors = new float[vertices.length];
            for (int i = 0; i < (colors.length / 3); i++) {
                colors[i] = DEFAULT_VERTEX_COLOR.x;
                colors[i + 1] = DEFAULT_VERTEX_COLOR.y;
                colors[i + 2] = DEFAULT_VERTEX_COLOR.z;
            }
        }
        colorVBOID = createVBO(colors, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(2);
        // 预处理纹理坐标
        if (textureCoords == null || textureCoords.length < 2) {
            textureCoords = DEFAULT_TEXTURE_COORDS;
        }
        if (texturePath != null) {
            File textureF = new File(texturePath);
            createTexture(textureF, textureCoords);
            glEnableVertexAttribArray(3);
        }

        eboID = createVBO(indices, GL_STATIC_DRAW);

        // ===== 材质处理 =====
        setupMaterial();
        if (materialAmbient == null) materialAmbient = DEFAULT_MATERIAL_AMBIENT;
        if (materialDiffuse == null) materialDiffuse = DEFAULT_MATERIAL_DIFFUSE;
        if (materialSpecular == null) materialSpecular = DEFAULT_MATERIAL_SPECULAR;
        if (shininess == 0) shininess = DEFAULT_SHININESS;

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        isSetup = true;
    }

    @Override
    public void draw() {
        if (!isSetup) setup();
        glBindVertexArray(vaoID);
        if (hasTexture) glBindTexture(GL_TEXTURE_2D, textureID);
        objShaderProgram.setUniform(VertexShader.UniformName.ModelTrans.name, transform.modelMatrix);
        // 设置材质
        objShaderProgram.setUniform(FragShader.UniformName.MATERIAL_AMBIENT.name, materialAmbient);
        objShaderProgram.setUniform(FragShader.UniformName.MATERIAL_DIFFUSE.name, materialDiffuse);
        objShaderProgram.setUniform(FragShader.UniformName.MATERIAL_SPECULAR.name, materialSpecular);
        objShaderProgram.setUniform(FragShader.UniformName.MATERIAL_SHININESS.name, shininess);
        drawElement();
        glBindVertexArray(0);
    }

    public void drawElement() {
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }

    public int createVAO() {
        vaoID = glGenVertexArrays();
        return vaoID;
    }

    public int createVBO(@NotNull float[] data, int type) {
        int vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, type);
        return vboID;
    }

    public int createVBO(FloatBuffer data, int type) {
        int vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, data, type);
        return vboID;
    }

    public int createVBO(int[] data, int type) {
        int vboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, type);
        return vboID;
    }

    public void createTexture(File textureF, float[] uv) {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image = stbi_load(textureF.getAbsolutePath(), width, height, channels, 4); // 4: RGBA
        try {
            textureCoordVBOID = createVBO(uv, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
            textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glGenerateMipmap(GL_TEXTURE_2D);
            hasTexture = true;
        } finally {
            if (image != null) {
                stbi_image_free(image);
            }
        }
    }

    public void setupMaterial() {

    }
}
