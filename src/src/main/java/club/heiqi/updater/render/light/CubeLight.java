package club.heiqi.updater.render.light;

import club.heiqi.shader.FragShader;
import club.heiqi.shader.ShaderProgram;
import club.heiqi.shader.VertexShader;
import club.heiqi.updater.render.Scene;
import club.heiqi.updater.render.cube.Cube;
import club.heiqi.updater.render.cube.PlaneCube;
import club.heiqi.updater.render.transform.Camera;
import club.heiqi.updater.render.transform.Transform;
import club.heiqi.window.Window;
import org.joml.Vector3f;

import java.io.File;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class CubeLight extends Cube {
    public static final Vector3f DEFAULT_LIGHT_COLOR = new Vector3f(1.0f, 1.0f, 1.0f);

    public ShaderProgram lightShaderProgram;
    public Camera camera;

    public Vector3f lightColor;

    public CubeLight(Window window, Scene scene) {
        super(window, scene);
        texturePath = null;
        lightShaderProgram = scene.lightShaderProgram;
    }

    @Override
    public void setup() {
        int id = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(lightShaderProgram.programID);
        vaoID = createVAO();
        glBindVertexArray(vaoID);

        vertexVBOID = createVBO(vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        // 预处理顶点颜色
        if (colors == null || colors.length != vertices.length) {
            colors = new float[vertices.length];
            for (int i = 0; i < (colors.length / 3); i++) {
                colors[i] = DEFAULT_LIGHT_COLOR.x;
                colors[i + 1] = DEFAULT_LIGHT_COLOR.y;
                colors[i + 2] = DEFAULT_LIGHT_COLOR.z;
            }
        }
        colorVBOID = createVBO(colors, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
        // 预处理纹理坐标
        if (textureCoords == null || textureCoords.length < 2) {
            textureCoords = DEFAULT_TEXTURE_COORDS;
        }
        if (texturePath != null) {
            File textureF = new File(texturePath);
            createTexture(textureF, textureCoords);
        }

        eboID = createVBO(indices, GL_STATIC_DRAW);

        if (objectColor == null) objectColor = DEFAULT_OBJECT_COLOR;

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        isSetup = true;
//        glUseProgram(id);
    }

    @Override
    public void draw() {
        if (!isSetup) setup();
        if (lightColor == null) lightColor = DEFAULT_LIGHT_COLOR;
        glUseProgram(objShaderProgram.programID);
        objShaderProgram.setUniform(FragShader.UniformName.LIGHT_COLOR.name, lightColor);
        glUseProgram(lightShaderProgram.programID);
        glBindVertexArray(vaoID);
        if (hasTexture) glBindTexture(GL_TEXTURE_2D, textureID);
        lightShaderProgram.setUniform(VertexShader.UniformName.ModelTrans.name, transform.modelMatrix);
        lightShaderProgram.setUniform(VertexShader.UniformName.View.name, viewMatrix);
        lightShaderProgram.setUniform(VertexShader.UniformName.Projection.name, camera.projectionMatrix);
        drawElement();
        glBindVertexArray(0);
        glUseProgram(objShaderProgram.programID);
    }

    @Override
    public void drawElement() {
        super.drawElement();
    }
}
