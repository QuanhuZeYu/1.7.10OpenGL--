package club.heiqi.shader;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    public int programID;

    public VertexShader vertexShader;
    public FragShader fragShader;

    public ShaderProgram() {
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
