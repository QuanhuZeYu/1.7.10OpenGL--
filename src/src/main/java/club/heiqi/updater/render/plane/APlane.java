package club.heiqi.updater.render.plane;

import club.heiqi.updater.AUpdate;
import club.heiqi.updater.render.ShaderRender;
import club.heiqi.updater.render.transform.Transform;
import club.heiqi.window.Window;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public abstract class APlane {
    enum UniformName{
        Transform("transform"),;
        public final String name;
        UniformName(String name) {
            this.name = name;
        }
    }
    public long time = System.currentTimeMillis();
    public int eboID;
    public int vaoID;
    public int vertexVBOID;
    public int colorVBOID;
    public int textureID;
    public int textureCoordVBOID;

    public Window window;
    public int programID;

    public Transform transform;

    public APlane(Window window) {
        this.window = window;
        for (AUpdate update : window.renders) {
            if (update instanceof ShaderRender) {
                programID = ((ShaderRender) update).shaderProgram.programID;
                break;
            }
        }
        transform = new Transform();


    }

    public void draw() {

    }

    public int createVAO() {
        vaoID = glGenVertexArrays();
        return vaoID;
    }

    public int createVBO(float[] data, int type) {
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

        textureCoordVBOID = createVBO(uv, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glGenerateMipmap(GL_TEXTURE_2D);
        if (image != null) {
            stbi_image_free(image);
        }
    }

    public void setUniform(String uniformName, Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int location = glGetUniformLocation(programID, uniformName);
            glUniformMatrix4fv(location, false, matrix.get(stack.mallocFloat(16)));
        }
    }
}
