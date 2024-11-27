package club.heiqi.shader;

import club.heiqi.util.FileManager;

import java.io.File;

import static org.lwjgl.opengl.GL20.*;

public class FragShader extends Shader {
    public enum UniformName {
        VIEW_POS("viewPos"),
        LIGHT_AMBIENT("light.ambient"),
        LIGHT_DIFFUSE("light.diffuse"),
        LIGHT_SPECULAR("light.specular"),
        LIGHT_POS("light.position"),
        LIGHT_SPOT_DIR("light.direction"),
        LIGHT_CONSTANT("light.constant"),
        LIGHT_LINEAR("light.linear"),
        LIGHT_QUADRATIC("light.quadratic"),
        LIGHT_SPOT_CUTOFF("light.cutOff"),
        LIGHT_IS_SPOT("light.isSpot"),

        MATERIAL_DIFFUSE("material.diffuse"),
        MATERIAL_SPECULAR("material.specular"),
        MATERIAL_SHININESS("material.shininess"),
        ;

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
