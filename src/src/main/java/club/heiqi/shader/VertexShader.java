package club.heiqi.shader;

import club.heiqi.util.FileManager;

import java.io.File;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.opengl.GL20.*;

public class VertexShader {
    public int shaderID;

    public enum UniformName{
        ModelTrans("model"),
        View("view"),
        Projection("projection");
        public final String name;
        UniformName(String name) {
            this.name = name;
        }
    }


    public VertexShader() {
        shaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(shaderID, getShaderString());
        glCompileShader(shaderID);
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0) logger.error("编译着色器失败: {}",glGetShaderInfoLog(shaderID));
    }

    public String getShaderString() {
        File shaderF = FileManager.getFile("shader/vertex.vs");
        return FileManager.readFile(shaderF);
    }
}
