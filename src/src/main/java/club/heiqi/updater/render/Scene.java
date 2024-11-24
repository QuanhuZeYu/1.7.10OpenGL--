package club.heiqi.updater.render;

import club.heiqi.shader.FragShader;
import club.heiqi.shader.Shader;
import club.heiqi.shader.ShaderProgram;
import club.heiqi.shader.VertexShader;
import club.heiqi.updater.AUpdate;
import club.heiqi.updater.render.cube.Cube;
import club.heiqi.updater.render.cube.PlaneCube;
import club.heiqi.updater.render.light.CubeLight;
import club.heiqi.updater.render.plane.AMesh;
import club.heiqi.updater.render.plane.Drawable;
import club.heiqi.updater.render.plane.Rectangle;
import club.heiqi.updater.render.plane.Triangle;
import club.heiqi.updater.render.transform.Camera;
import club.heiqi.updater.render.transform.Transform;
import club.heiqi.window.Window;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL20.*;

public class Scene extends AUpdate {
    public ShaderProgram objShaderProgram;
    public ShaderProgram lightShaderProgram;
    public Camera camera;

    public Matrix4f viewMatrix = new Matrix4f();
    public Matrix4f projectionMatrix;

    public List<Drawable> drawables = new ArrayList<>();

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
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_DEPTH_TEST);
        objShaderProgram.setUniform(VertexShader.UniformName.View.name, viewMatrix);
        objShaderProgram.setUniform(VertexShader.UniformName.Projection.name, projectionMatrix);
        glUseProgram(id);
        addItem();
    }

    public void addItem() {
        AMesh rectangle = new Rectangle(window, this);
        rectangle.transform.setScale(0.5f);
        rectangle.transform.updateMatrix();
        AMesh triangle = new Triangle(window, this);
        PlaneCube cube = new PlaneCube(window, this);
        CubeLight light = new CubeLight(window, this);
        Cube cube1 = new Cube(window, this);
        light.camera = camera;
        cube1.transform.setPosition(-3f, -2f, -1f);
        cube1.transform.updateMatrix();
        logger.info("Transform: {}, {}, {}, {}", hash(rectangle.transform), hash(triangle.transform), hash(cube1.transform), hash(light.transform));
//        drawables.add(rectangle);
//        drawables.add(triangle);
//        drawables.add(cube);
        drawables.add(cube1);
        drawables.add(light);
    }

    @Override
    public void update() {
        camera.update();
        int id = glGetInteger(GL_CURRENT_PROGRAM);
        if (id != objShaderProgram.programID) glUseProgram(objShaderProgram.programID);
        glUseProgram(objShaderProgram.programID);
        for (Drawable drawable : drawables) {
            drawable.draw();
        }
    }

    public void updateProjectionMatrix(float width, float height) {
        projectionMatrix = projectionMatrix.perspective(90.0f, width / height, 0.1f, 1000.0f);
        objShaderProgram.setUniform(VertexShader.UniformName.Projection.name, projectionMatrix);
    }

    private int hash(Transform transform) {
        return System.identityHashCode(transform);
    }
}
