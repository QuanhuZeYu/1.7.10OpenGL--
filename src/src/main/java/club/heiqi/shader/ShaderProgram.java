package club.heiqi.shader;

import static org.lwjgl.opengl.GL20.*;
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
}
