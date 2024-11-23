package club.heiqi.shader;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

public class ShaderProgram {
    public int programID;

    public VertexShader vertexShader;
    public FragShader fragShader;

    public ShaderProgram() {
        stbi_set_flip_vertically_on_load(true);
        vertexShader = new VertexShader();
        fragShader = new FragShader();
        programID = glCreateProgram();
        glAttachShader(programID, vertexShader.shaderID);
        glAttachShader(programID, fragShader.shaderID);
        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == 0) System.err.println("链接着色器失败: " + glGetProgramInfoLog(programID));
        glDeleteShader(vertexShader.shaderID);
        glDeleteShader(fragShader.shaderID);
    }

    public void setUniform(String uniformName, Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int location = glGetUniformLocation(programID, uniformName);
            if (location == -1) {
                throw new RuntimeException("无法找到uniform: " + uniformName);
            }
            FloatBuffer buffer = stack.mallocFloat(16);
            matrix.get(buffer);
            glUniformMatrix4fv(location, false, buffer);
        } catch (Exception e) {
            // 记录异常信息，可以根据需要调整日志级别
            logger.error("设置uniform: {} 失败: ", uniformName, e);
        }
    }

    public static int createVAO() {
        return glGenVertexArrays();
    }

    public static int createVBO(float[] data, int type) {
        int vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, type);
        return vboID;
    }

    public static int createVBO(FloatBuffer data, int type) {
        int vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, data, type);
        return vboID;
    }

    public static int createVBO(int[] data, int type) {
        int vboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, type);
        return vboID;
    }
}
