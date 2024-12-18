package club.heiqi.updater.render;

import club.heiqi.shader.FragShader;
import club.heiqi.shader.Shader;
import club.heiqi.shader.ShaderProgram;
import club.heiqi.shader.VertexShader;
import club.heiqi.updater.AUpdate;
import club.heiqi.updater.render.cube.Cube;
import club.heiqi.updater.render.light.CamLight;
import club.heiqi.updater.render.light.CubeLight;
import club.heiqi.updater.render.plane.Drawable;
import club.heiqi.window.Window;
import org.joml.Matrix4f;
import org.joml.Random;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL20.*;

public class Scene extends AUpdate {
    public Random rand = new Random();
    public long lastTime = System.nanoTime();
    public boolean needUpdateProjectionMatrix = false;

    public ShaderProgram objShaderProgram;
    public ShaderProgram lightShaderProgram;
    public Camera camera;

    public Matrix4f viewMatrix = new Matrix4f();
    public Matrix4f projectionMatrix;

    public List<Drawable> drawables = new ArrayList<>();
    public List<AUpdate> updates = new ArrayList<>();

    public Scene(Window window) {
        super(window);
        Shader vShader = new VertexShader("shader/vertex.vs");
        Shader lightFShader = new FragShader("shader/light.frag");
        Shader fShader = new FragShader("shader/frag.fs");
        List<Shader> objShaders = new ArrayList<>();
        List<Shader> lightShaders = new ArrayList<>();
        objShaders.add(vShader); objShaders.add(fShader);
        lightShaders.add(vShader); lightShaders.add(lightFShader);
        this.objShaderProgram = new ShaderProgram(objShaders);
        this.lightShaderProgram = new ShaderProgram(lightShaders);
        // region 从固定管线中获取矩阵
        /*float[] view = new float[16];
        glGetFloatv(GL_MODELVIEW_MATRIX, view);
        float[] proj = new float[16];
        glGetFloatv(GL_PROJECTION_MATRIX, proj);*/
        // endregion
        camera = new Camera(this);
        camera.transform.setPosition(0, 0, 0);
        camera.transform.updateMatrix();
        viewMatrix = camera.viewMatrix;
        projectionMatrix = camera.projectionMatrix;
        // region 向固定管线传输矩阵
        FloatBuffer buff = BufferUtils.createFloatBuffer(16);
        var view = viewMatrix.get(buff);
        glMatrixMode(GL_MODELVIEW);
        glLoadMatrixf(view);
        var proj = projectionMatrix.get(buff);
        glMatrixMode(GL_PROJECTION);
        glLoadMatrixf(proj);
        // endregion
        int id = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(objShaderProgram.programID);
//        glEnable(GL_CULL_FACE);
//        glCullFace(GL_BACK);
        glEnable(GL_DEPTH_TEST);
        objShaderProgram.setUniform(VertexShader.UniformName.View.name, viewMatrix);
        objShaderProgram.setUniform(VertexShader.UniformName.Projection.name, projectionMatrix);
        glUseProgram(id);
        addItem();
    }

    public void addItem() {
        CubeLight light = new CubeLight(window, this);
        CamLight camLight = new CamLight(window, this);
        light.camera = camera;
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            Cube cube = new Cube(window, this);

            // 随机生成半径 r 和角度 theta
            float r = 10 + rand.nextFloat() * (64 - 10); // 半径范围 [16, 64]
            float theta = rand.nextFloat() * (float) (2 * Math.PI); // 角度范围 [0, 2π]

            // 转换为笛卡尔坐标
            float x = r * (float) Math.cos(theta);
            float z = r * (float) Math.sin(theta);
            float y = -10 + rand.nextFloat() * (10 + 10); // 高度随机范围 [0, 10]

            float size = 0.1f + rand.nextFloat() * (3 - 0.1f);

            boolean pm = rand.nextFloat() > 0.5f;

            // 设置 Cube 的位置和旋转
            cube.transform.addPosition(x, y, z);
            if (pm)
                cube.transform.addRotation(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
            else
                cube.transform.addRotation(-rand.nextFloat(), -rand.nextFloat(), -rand.nextFloat());
            cube.transform.setScale(size);
            cube.transform.updateMatrix();

            // 添加到绘制列表
            drawables.add(cube);
        }
        logger.info("立方体添加完毕!");
//        drawables.add(light);
        drawables.add(camLight);
    }

    @Override
    public void update() {
        camera.update(); updateProjectionMatrix();
        int id = glGetInteger(GL_CURRENT_PROGRAM);
        if (id != objShaderProgram.programID) glUseProgram(objShaderProgram.programID);
        glUseProgram(objShaderProgram.programID);
        for (Drawable drawable : drawables) {
            drawable.draw();
            if (!(drawable instanceof CubeLight) && drawable instanceof Cube) {
                updateCube((Cube) drawable);
            }
        }
        lastTime = System.nanoTime();
    }

    public void updateProjectionMatrix() {
        if (!needUpdateProjectionMatrix) return;
        projectionMatrix = projectionMatrix.perspective(camera.fov, (float) window.width / window.height, camera.zNear, camera.zFar);
        glUseProgram(objShaderProgram.programID);
        objShaderProgram.setUniform(VertexShader.UniformName.Projection.name, projectionMatrix);
        camera.updateViewMatrix();
        int error = glGetError();
        if (error != GL_NO_ERROR) {
            logger.error("更新投影矩阵时发生错误: {}", error);
        }
        logger.info("投影矩阵: {}", projectionMatrix);
        needUpdateProjectionMatrix = false;
    }

    private void updateCube(Cube cube) {
        boolean f1 = rand.nextFloat() > 0.5f;
        boolean f2 = rand.nextFloat() > 0.5f;
        boolean f3 = rand.nextFloat() > 0.5f;
        float x, y, z;
        if (f1) x = rand.nextFloat() * 0.01f;
        else x = -rand.nextFloat() * 0.01f;
        if (f2) y = rand.nextFloat() * 0.01f;
        else y = -rand.nextFloat() * 0.01f;
        if (f3) z = rand.nextFloat() * 0.01f;
        else z = -rand.nextFloat() * 0.01f;
        cube.transform.addRotation(x, y, z);
        cube.transform.updateMatrix();
    }
}
