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
import java.util.HashMap;
import java.util.Map;

import static club.heiqi.loger.MyLog.logger;
import static club.heiqi.util.FileManager.getFile;
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
    public int textureCoordVBOID;

    // ========== 静态字段 ==========
    public static final Vector4f DEFAULT_VERTEX_COLOR = new Vector4f(1.0f, 0.5f, 0.31f, 1.0f);
    public static final Vector3f DEFAULT_MATERIAL_AMBIENT = new Vector3f(1.0f, 0.5f, 0.31f);
    public static final Vector3f DEFAULT_MATERIAL_DIFFUSE = new Vector3f(1.0f, 0.5f, 0.31f);
    public static final Vector3f DEFAULT_MATERIAL_SPECULAR = new Vector3f(0.5f, 0.5f, 0.5f);
    public static final float DEFAULT_SHININESS = 32.0f;
    /**
     * 纹理缓存, 只存放注册完毕的纹理, 且全局共享; {注册名: 纹理对象}
     */
    public static final Map<String, Texture> TEXTURE_CACHE = new HashMap<>();
    public static final String DEFAULT_TEXTURE_PATH = "texture/test.png";
    public static final String DEFAULT_TEXTURE_REGISTRY_NAME = "default";
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
    /**
     * 纹理注册表, 纹理的注册名称为键, 纹理对象为值, 仅存放当前对象的纹理, 请不要在渲染时使用该字段! 它的注册ID可能为空/0
     */
    public Map<String, Texture> registryNameAndTexture = new HashMap<>();
    // ===== 材质属性 =====
    public Vector3f materialAmbient;
    public int materialDiffusePos = 0;
    public int materialSpecularPos = 1;
    public float shininess;
    // =====         =====
    public boolean isSetup = false;
    public boolean useEBO = false;
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
        // region  ===== 预处理顶点颜色 =====
        /*if (colors == null || colors.length != vertices.length) {
            colors = new float[vertices.length];
            for (int i = 0; i < (colors.length / 3); i++) {
                colors[i] = DEFAULT_VERTEX_COLOR.x;
                colors[i + 1] = DEFAULT_VERTEX_COLOR.y;
                colors[i + 2] = DEFAULT_VERTEX_COLOR.z;
            }
        }
        colorVBOID = createVBO(colors, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(2);*/
        // endregion ===== 预处理顶点颜色 =====
        // region  ===== 预处理纹理 =====
        if (textureCoords == null || textureCoords.length < 2) {
            textureCoords = DEFAULT_TEXTURE_COORDS; // 使用 0,0 1,0 1,1 0,1 填充矩形纹理
        }
        if (registryNameAndTexture.isEmpty()) {
            File textureF = getFile(DEFAULT_TEXTURE_PATH);
            createTexture(textureF, textureCoords, DEFAULT_TEXTURE_REGISTRY_NAME, GL_TEXTURE0);
            glEnableVertexAttribArray(2);
        } else {
            for (String registryName : registryNameAndTexture.keySet()) {
                Texture texture = registryNameAndTexture.get(registryName);
                File textureF = getFile(texture.path);
                createTexture(textureF, textureCoords, texture.registryName, texture.activeTexturePose);
                glEnableVertexAttribArray(2);
            }
        }
        // endregion ===== 预处理纹理 =====

        if (indices != null)  {
            eboID = createVBO(indices, GL_STATIC_DRAW);
            useEBO = true;
        }

        // ===== 材质处理 =====
        setupMaterial();
        if (materialAmbient == null) materialAmbient = DEFAULT_MATERIAL_AMBIENT;
        if (shininess == 0) shininess = DEFAULT_SHININESS;

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        isSetup = true;
    }

    @Override
    public void draw() {
        if (!isSetup) setup();
        glBindVertexArray(vaoID);
        if (hasTexture) {
            for (String registryName : registryNameAndTexture.keySet()) {
                useTexture(registryName);
            }
        }
        objShaderProgram.setUniform(VertexShader.UniformName.ModelTrans.name, transform.modelMatrix);
        // 设置材质
        objShaderProgram.setUniform(FragShader.UniformName.MATERIAL_DIFFUSE.name, materialDiffusePos);
        objShaderProgram.setUniform(FragShader.UniformName.MATERIAL_SPECULAR.name, materialSpecularPos);
        objShaderProgram.setUniform(FragShader.UniformName.MATERIAL_SHININESS.name, shininess);
        drawElement();
        glBindVertexArray(0);
    }

    public void drawElement() {
        if (useEBO)
            glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        else
            glDrawArrays(GL_TRIANGLES, 0, vertices.length / 3);
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

    public void createTexture(File textureF, float[] uv, String registryName, int activeTexturePose) {
        // 纹理缓存中已存在，则直接返回
        if (TEXTURE_CACHE.containsKey(registryName)) {
            logger.warn("纹理 {} 已经在缓存当中!", registryName);
            return;
        }

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image = stbi_load(textureF.getAbsolutePath(), width, height, channels, 4); // 4: RGBA
        try {
            textureCoordVBOID = createVBO(uv, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
            int textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glGenerateMipmap(GL_TEXTURE_2D);
            Texture texture = new Texture(textureF.getAbsolutePath(), registryName, textureID, activeTexturePose);
            TEXTURE_CACHE.put(registryName, texture);
            logger.info(TEXTURE_CACHE.values());
            hasTexture = true;
        } finally {
            if (image != null) {
                stbi_image_free(image);
            }
        }
    }

    public void useTexture(String registryName) {
        Texture texture = TEXTURE_CACHE.get(registryName);
        glActiveTexture(texture.activeTexturePose);
        glBindTexture(GL_TEXTURE_2D, texture.textureID);
    }

    public void setupMaterial() {

    }
}
