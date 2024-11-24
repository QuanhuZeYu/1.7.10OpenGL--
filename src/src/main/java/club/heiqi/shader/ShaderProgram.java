package club.heiqi.shader;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

public class ShaderProgram {
    public int programID;

    public List<Shader> shaderCache = new ArrayList<>();

    public ShaderProgram() {
        stbi_set_flip_vertically_on_load(true);
        programID = glCreateProgram();
    }

    public ShaderProgram(List<Shader> shaders) {
        stbi_set_flip_vertically_on_load(true);
        programID = glCreateProgram();
        linkShader(shaders);
    }

    public void attachShader(Shader shader) {
        glAttachShader(programID, shader.shaderID);
    }

    public void linkShader(List<Shader> shaders) {
        for (Shader shader : shaders) {
            attachShader(shader);
            shaderCache.add(shader);
        }
        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == 0) System.err.println("链接着色器失败: " + glGetProgramInfoLog(programID));
        for (Shader shader : shaders) {
            glDeleteShader(shader.shaderID);
        }
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

    public void setUniform(String uniformName, Vector3f vector3f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int location = glGetUniformLocation(programID, uniformName);
            if (location == -1) {
                throw new RuntimeException("无法找到uniform: " + uniformName);
            }
            glUniform3f(location, vector3f.x, vector3f.y, vector3f.z);
        } catch (Exception e) {
            // 记录异常信息，可以根据需要调整日志级别
            logger.error("设置uniform: {} 失败: ", uniformName, e);
        }
    }
}
