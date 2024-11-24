package club.heiqi.shader;

import club.heiqi.util.FileManager;

import java.io.File;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.opengl.GL20.*;

public class VertexShader extends Shader{
    public enum UniformName {
        ModelTrans("model"),
        View("view"),
        Projection("projection");

        public final String name;
        UniformName(String name) {
            this.name = name;
        }
    }

    public VertexShader(String filePath) {
        createShader(filePath);
    }

    @Override
    public void createShader(String filePath) {
        shaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(shaderID, getShaderString(filePath));
        glCompileShader(shaderID);
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0) logger.error("编译着色器失败: {}",glGetShaderInfoLog(shaderID));
    }

    @Override
    public String getShaderString(String filePath) {
        File shaderF = FileManager.getFile(filePath);
        return FileManager.readFile(shaderF);
    }
}
