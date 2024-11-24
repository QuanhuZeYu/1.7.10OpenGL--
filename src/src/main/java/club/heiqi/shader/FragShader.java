package club.heiqi.shader;

import club.heiqi.util.FileManager;

import java.io.File;

import static org.lwjgl.opengl.GL20.*;

public class FragShader extends Shader {
    public enum UniformName {
        OBJECT_COLOR("objectColor"),
        LIGHT_COLOR("lightColor");

        public final String name;
        UniformName(String name) {
            this.name = name;
        }
    }

    public FragShader(String filePath) {
        createShader(filePath);
    }

    @Override
    public void createShader(String filePath) {
        shaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(shaderID, getShaderString(filePath));
        glCompileShader(shaderID);
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0) System.err.println("编译着色器失败: " + glGetShaderInfoLog(shaderID));
    }

    @Override
    public String getShaderString(String filePath) {
        File shaderF = FileManager.getFile(filePath);
        return FileManager.readFile(shaderF);
    }
}
