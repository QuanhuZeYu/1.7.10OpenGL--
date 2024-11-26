package club.heiqi.updater.render.light;

import club.heiqi.shader.FragShader;
import club.heiqi.shader.ShaderProgram;
import club.heiqi.shader.VertexShader;
import club.heiqi.updater.render.Scene;
import club.heiqi.updater.render.cube.Cube;
import club.heiqi.updater.render.Camera;
import club.heiqi.window.Window;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class CubeLight extends Cube {
    public static final Vector3f DEFAULT_LIGHT_COLOR = new Vector3f(1.0f, 1.0f, 1.0f);
    public static final Vector3f DEFAULT_DIFFUSE_COLOR = new Vector3f(0.5f).mul(DEFAULT_LIGHT_COLOR);
    public static final Vector3f DEFAULT_AMBIENT_COLOR = new Vector3f(0.0f).mul(DEFAULT_DIFFUSE_COLOR);
    public static final Vector3f DEFAULT_SPECULAR_COLOR = new Vector3f(1.0f);

    public ShaderProgram lightShaderProgram;
    public Camera camera;

    public Vector3f lightColor;

    public CubeLight(Window window, Scene scene) {
        super(window, scene);
        texturePath = null;
        lightShaderProgram = scene.lightShaderProgram;
        transform.setPosition(0f, 0f, 0f);
        transform.updateMatrix();
    }

    @Override
    public void setup() {
        vaoID = createVAO();
        glBindVertexArray(vaoID);

        vertexVBOID = createVBO(vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        // region   ===== 预处理法线 =====
        /*if (normals != null) {
            normalVBOID = createVBO(normals, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
            glEnableVertexAttribArray(1);
        }*/
        // endregion ===== 预处理法线 =====
        // region  ===== 预处理顶点颜色 =====
        /*if (colors == null || colors.length != vertices.length) {
            colors = new float[vertices.length];
            for (int i = 0; i < (colors.length / 3); i++) {
                colors[i] = DEFAULT_VERTEX_COLOR.x;
                colors[i + 1] = DEFAULT_VERTEX_COLOR.y;
                colors[i + 2] = DEFAULT_VERTEX_COLOR.z;
            }
        }
        colorVBOID = createVBO(colors, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(2);*/
        // endregion ===== 预处理顶点颜色 =====
        // region  ===== 预处理纹理坐标 =====
        /*if (textureCoords == null || textureCoords.length < 2) {
            textureCoords = DEFAULT_TEXTURE_COORDS;
        }
        if (texturePath != null) {
            File textureF = new File(texturePath);
            createTexture(textureF, textureCoords);
            glEnableVertexAttribArray(3);
        }*/
        // endregion ===== 预处理纹理坐标 =====

        /*eboID = createVBO(indices, GL_STATIC_DRAW);*/

        // ===== 材质处理 =====
        setupMaterial();
        if (materialAmbient == null) materialAmbient = DEFAULT_MATERIAL_AMBIENT;
        if (materialDiffuse == null) materialDiffuse = DEFAULT_MATERIAL_DIFFUSE;
        if (materialSpecular == null) materialSpecular = DEFAULT_MATERIAL_SPECULAR;
        if (shininess == 0) shininess = DEFAULT_SHININESS;

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        isSetup = true;
    }

    @Override
    public void draw() {
        if (!isSetup) setup();
        if (lightColor == null) lightColor = DEFAULT_LIGHT_COLOR;

        glUseProgram(objShaderProgram.programID);
        objShaderProgram.setUniform(FragShader.UniformName.LIGHT_AMBIENT.name, DEFAULT_AMBIENT_COLOR);
        objShaderProgram.setUniform(FragShader.UniformName.LIGHT_DIFFUSE.name, DEFAULT_DIFFUSE_COLOR);
        objShaderProgram.setUniform(FragShader.UniformName.LIGHT_SPECULAR.name, DEFAULT_SPECULAR_COLOR);
        objShaderProgram.setUniform(FragShader.UniformName.LIGHT_POS.name, transform.position);
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
