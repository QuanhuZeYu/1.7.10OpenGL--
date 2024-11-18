package club.heiqi.updater.render;

import club.heiqi.shader.ShaderProgram;
import club.heiqi.shader.VertexShader;
import club.heiqi.updater.AUpdate;
import club.heiqi.updater.render.plane.APlane;
import club.heiqi.updater.render.plane.Rectangle;
import club.heiqi.updater.render.plane.Triangle;
import club.heiqi.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL20.*;

public class Scene extends AUpdate {
    public ShaderProgram shaderProgram;

    public Matrix4f viewMatrix = new Matrix4f();
    public Matrix4f projectionMatrix = new Matrix4f();

    public List<APlane> planes = new ArrayList<>();

    public Scene(Window window) {
        super(window);
        this.shaderProgram = new ShaderProgram();
        viewMatrix = viewMatrix.lookAt(
                new Vector3f(0.0f, 0.0f, 1.0f),
                new Vector3f(0.0f, 0.0f, 0.0f),
                new Vector3f(0.0f, 1.0f, 0.0f)
                );
        projectionMatrix = projectionMatrix.perspective(90.0f, window.w / (float) window.h, 0.1f, 1000.0f);
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
        planes.add(rectangle);
        planes.add(triangle);
    }

    @Override
    public void update() {
        int currentProgramID = glGetInteger(GL_CURRENT_PROGRAM);
        glUseProgram(getProgramID());
        for (APlane plane : planes) {
            plane.draw();
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
