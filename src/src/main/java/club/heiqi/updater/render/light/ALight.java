package club.heiqi.updater.render.light;

import club.heiqi.shader.FragShader;
import club.heiqi.shader.ShaderProgram;
import club.heiqi.shader.VertexShader;
import club.heiqi.updater.AUpdate;
import club.heiqi.updater.render.Camera;
import club.heiqi.updater.render.Scene;
import club.heiqi.updater.render.transform.Transform;
import club.heiqi.window.Window;
import org.joml.Vector3f;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class ALight extends AUpdate {
    public static final Vector3f DEFAULT_LIGHT_COLOR = new Vector3f(1.0f, 1.0f, 1.0f);
    public static final Vector3f DEFAULT_DIFFUSE_COLOR = new Vector3f(0.8f).mul(DEFAULT_LIGHT_COLOR);
    public static final Vector3f DEFAULT_AMBIENT_COLOR = new Vector3f(0.25f).mul(DEFAULT_DIFFUSE_COLOR);
    public static final Vector3f DEFAULT_SPECULAR_COLOR = new Vector3f(1.0f);
    public static final float    DEFAULT_LIGHT_CONSTANT = 1.0f;
    public static final float    DEFAULT_LIGHT_LINEAR = 0.09f;
    public static final float    DEFAULT_LIGHT_QUADRATIC = 0.032f;
    public static final float    DEFAULT_CUTOFF = (float) Math.toRadians(12.5f);
    public static final float    DEFAULT_OUTER_CUTOFF = (float) Math.toRadians(17.5f);

    public ShaderProgram lightShaderProgram;
    public ShaderProgram objectShaderProgram;
    public Camera camera;
    public Window window;
    public Scene scene;
    public Transform transform;

    public Vector3f lightPosition = new Vector3f();
    public Vector3f lightDirection = new Vector3f();
    public Vector3f lightDiffuseColor;
    public Vector3f lightAmbientColor;
    public Vector3f lightSpecularColor;
    public float lightConstant;
    public float lightLinear;
    public float lightQuadratic;
    public float cutoff;
    public float outCutoff;
    public boolean isSetup = false;
    public boolean isSpotLight = false;
    public boolean isDirectionLight = false;

    public ALight(Window window, Scene scene) {
        super(window);
        this.window = window;
        this.scene = scene;
        this.camera = scene.camera;
        lightShaderProgram = scene.lightShaderProgram;
        objectShaderProgram = scene.objShaderProgram;
        transform = new Transform();
    }

    public void setup() {
    }

    @Override
    public void update() {
        if (!isSetup) setup();
        glUseProgram(lightShaderProgram.programID);
        // 设置灯光属性
        lightShaderProgram.setUniform(VertexShader.UniformName.ModelTrans.name, transform.modelMatrix);
        lightShaderProgram.setUniform(VertexShader.UniformName.View.name, camera.viewMatrix);
        lightShaderProgram.setUniform(VertexShader.UniformName.Projection.name, camera.projectionMatrix);
        // 设置灯光作用于物体的属性
        glUseProgram(objectShaderProgram.programID);
        objectShaderProgram.setUniform(FragShader.UniformName.LIGHT_POS.name, lightPosition);
        objectShaderProgram.setUniform(FragShader.UniformName.LIGHT_AMBIENT.name, lightAmbientColor);
        objectShaderProgram.setUniform(FragShader.UniformName.LIGHT_DIFFUSE.name, lightDiffuseColor);
        objectShaderProgram.setUniform(FragShader.UniformName.LIGHT_SPECULAR.name, lightSpecularColor);
        objectShaderProgram.setUniform(FragShader.UniformName.LIGHT_IS_SPOT.name, isSpotLight);
        objectShaderProgram.setUniform(FragShader.UniformName.LIGHT_IS_DIRECTION.name, isDirectionLight);
        if (isSpotLight) {
            objectShaderProgram.setUniform(FragShader.UniformName.LIGHT_SPOT_CUTOFF.name, cutoff);
            objectShaderProgram.setUniform(FragShader.UniformName.LIGHT_SPOT_CUTOFF.name, outCutoff);
            objectShaderProgram.setUniform(FragShader.UniformName.LIGHT_SPOT_DIR.name, lightDirection);
            objectShaderProgram.setUniform(FragShader.UniformName.LIGHT_CONSTANT.name, lightConstant);
            objectShaderProgram.setUniform(FragShader.UniformName.LIGHT_LINEAR.name, lightLinear);
            objectShaderProgram.setUniform(FragShader.UniformName.LIGHT_QUADRATIC.name, lightQuadratic);
        }
    }

    public void initLightProp() {
        if (lightDiffuseColor == null) lightDiffuseColor = DEFAULT_DIFFUSE_COLOR;
        if (lightAmbientColor == null) lightAmbientColor = DEFAULT_AMBIENT_COLOR;
        if (lightSpecularColor == null) lightSpecularColor = DEFAULT_SPECULAR_COLOR;
        logger.info("光强度: {漫射: {}, 环境光: {}, 镜面: {}}", lightDiffuseColor, lightAmbientColor, lightSpecularColor);
    }

    public void initSpotLightProp() {
        if (lightConstant == 0) lightConstant = DEFAULT_LIGHT_CONSTANT;
        if (lightLinear == 0) lightLinear = DEFAULT_LIGHT_LINEAR;
        if (lightQuadratic == 0) lightQuadratic = DEFAULT_LIGHT_QUADRATIC;
        if (cutoff == 0) cutoff = DEFAULT_CUTOFF;
        if (outCutoff == 0) outCutoff = DEFAULT_OUTER_CUTOFF;
        logger.info("光数据: {Constant: {}, Linear: {}, Quadratic: {}, cutoff: {}, 方向: {}}", lightConstant, lightLinear, lightQuadratic, cutoff, lightDirection);
        isSpotLight = true;
    }
}
