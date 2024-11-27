package club.heiqi.updater.render.light;

import club.heiqi.shader.FragShader;
import club.heiqi.shader.ShaderProgram;
import club.heiqi.shader.VertexShader;
import club.heiqi.updater.render.Scene;
import club.heiqi.updater.render.cube.Cube;
import club.heiqi.updater.render.Camera;
import club.heiqi.window.Window;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class CubeLight extends Cube {
    public static final Vector3f DEFAULT_LIGHT_COLOR = new Vector3f(1.0f, 1.0f, 1.0f);
    public static final Vector3f DEFAULT_DIFFUSE_COLOR = new Vector3f(0.5f).mul(DEFAULT_LIGHT_COLOR);
    public static final Vector3f DEFAULT_AMBIENT_COLOR = new Vector3f(0.0f).mul(DEFAULT_DIFFUSE_COLOR);
    public static final Vector3f DEFAULT_SPECULAR_COLOR = new Vector3f(1.0f);
    public static final float    DEFAULT_LIGHT_CONSTANT = 1.0f;
    public static final float    DEFAULT_LIGHT_LINEAR = 0.07f;
    public static final float    DEFAULT_LIGHT_QUADRATIC = 0.017f;

    public ShaderProgram lightShaderProgram;
    public Camera camera;

    public Vector3f lightColor;
    public Vector4f lightDirection;
    public Vector3f lightDiffuseColor;
    public Vector3f lightAmbientColor;
    public Vector3f lightSpecularColor;
    public float lightConstant;
    public float lightLinear;
    public float lightQuadratic;

    public CubeLight(Window window, Scene scene) {
        super(window, scene);
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
        if (shininess == 0) shininess = DEFAULT_SHININESS;

        // region   ===== 光源属性处理 =====
        if (lightDiffuseColor == null) lightDiffuseColor = DEFAULT_DIFFUSE_COLOR;
        if (lightAmbientColor == null) lightAmbientColor = DEFAULT_AMBIENT_COLOR;
        if (lightSpecularColor == null) lightSpecularColor = DEFAULT_SPECULAR_COLOR;
        if (lightConstant == 0) lightConstant = DEFAULT_LIGHT_CONSTANT;
        if (lightLinear == 0) lightLinear = DEFAULT_LIGHT_LINEAR;
        if (lightQuadratic == 0) lightQuadratic = DEFAULT_LIGHT_QUADRATIC;
        // endregion===== 光源属性处理 =====
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

//        lightDirection = new Vector4f(-0.2f, -1.0f, -0.3f, 0f);
        isSetup = true;
    }

    @Override
    public void draw() {
        if (!isSetup) setup();
        if (lightColor == null) lightColor = DEFAULT_LIGHT_COLOR;

        glUseProgram(objShaderProgram.programID);
        objShaderProgram.setUniform(FragShader.UniformName.LIGHT_AMBIENT.name, lightAmbientColor);
        objShaderProgram.setUniform(FragShader.UniformName.LIGHT_DIFFUSE.name, lightDiffuseColor);
        objShaderProgram.setUniform(FragShader.UniformName.LIGHT_SPECULAR.name, lightSpecularColor);
        if (lightDirection != null) {
            objShaderProgram.setUniform(FragShader.UniformName.LIGHT_POS.name, lightDirection);
        } else {
            Vector4f lightPos = new Vector4f(transform.position.x, transform.position.y, transform.position.z, 1f);
            objShaderProgram.setUniform(FragShader.UniformName.LIGHT_POS.name, lightPos);
        }
        objShaderProgram.setUniform(FragShader.UniformName.LIGHT_CONSTANT.name, lightConstant);
        objShaderProgram.setUniform(FragShader.UniformName.LIGHT_LINEAR.name, lightLinear);
        objShaderProgram.setUniform(FragShader.UniformName.LIGHT_QUADRATIC.name, lightQuadratic);
        glUseProgram(lightShaderProgram.programID);

        glBindVertexArray(vaoID);
        lightShaderProgram.setUniform(VertexShader.UniformName.ModelTrans.name, transform.modelMatrix);
        lightShaderProgram.setUniform(VertexShader.UniformName.View.name, camera.viewMatrix);
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
