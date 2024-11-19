package club.heiqi.updater.render;

import club.heiqi.shader.ShaderProgram;
import club.heiqi.shader.VertexShader;
import club.heiqi.updater.AUpdate;
import club.heiqi.updater.controller.KeyInput;
import club.heiqi.updater.render.cube.Cube;
import club.heiqi.updater.render.plane.APlane;
import club.heiqi.updater.render.plane.Drawable;
import club.heiqi.updater.render.plane.Rectangle;
import club.heiqi.updater.render.plane.Triangle;
import club.heiqi.updater.render.transform.Camera;
import club.heiqi.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL20.*;

public class Scene extends AUpdate {
    public ShaderProgram shaderProgram;
    public Camera camera;

    public Matrix4f viewMatrix = new Matrix4f();
    public Matrix4f projectionMatrix;

    public List<Drawable> drawables = new ArrayList<>();

    public Scene(Window window) {
        super(window);
        this.shaderProgram = new ShaderProgram();
        camera = new Camera(this);
        viewMatrix = camera.viewMatrix;
        projectionMatrix = camera.projectionMatrix;
        int id = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(shaderProgram.programID);
        shaderProgram.setUniform(VertexShader.UniformName.View.name, viewMatrix);
        shaderProgram.setUniform(VertexShader.UniformName.Projection.name, projectionMatrix);
        glUseProgram(id);
        addItem();
    }

    public void addItem() {
        APlane rectangle = new Rectangle(window, this);
        rectangle.transform.setScale(0.5f);
        rectangle.transform.updateMatrix();
        APlane triangle = new Triangle(window, this);
        Cube cube = new Cube(window, this);
        drawables.add(rectangle);
        drawables.add(triangle);
        drawables.add(cube);
    }

    @Override
    public void update() {
        int currentProgramID = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(getProgramID());
        camera.update();
        for (Drawable drawable : drawables) {
            drawable.draw();
        }
        glUseProgram(currentProgramID);
    }

    public int getProgramID() {
        return shaderProgram.programID;
    }

    public void updateProjectionMatrix(float width, float height) {
        projectionMatrix = projectionMatrix.perspective(90.0f, width / height, 0.1f, 1000.0f);
        shaderProgram.setUniform(VertexShader.UniformName.Projection.name, projectionMatrix);
    }
}
