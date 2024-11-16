package club.heiqi.shader;

import club.heiqi.util.FileManager;

import java.io.File;

import static org.lwjgl.opengl.GL20.*;

public class FragShader {
    public int shaderID;

    public FragShader() {
        shaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(shaderID, getShaderString());
        glCompileShader(shaderID);
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0) System.err.println("编译着色器失败: " + glGetShaderInfoLog(shaderID));
    }

    public String getShaderString() {
        File shaderF = FileManager.getFile("shader/frag.fs");
        return FileManager.readFile(shaderF);
    }
}
