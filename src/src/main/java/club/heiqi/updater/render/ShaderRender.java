package club.heiqi.updater.render;

import club.heiqi.shader.ShaderProgram;
import club.heiqi.updater.AUpdate;
import club.heiqi.updater.render.plane.APlane;
import club.heiqi.updater.render.plane.Rectangle;
import club.heiqi.updater.render.plane.Triangle;
import club.heiqi.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

public class ShaderRender extends AUpdate {
    public boolean isInit = false;

    public ShaderProgram shaderProgram;

    public Matrix4f viewMatrix = new Matrix4f();
    public Matrix4f projectionMatrix = new Matrix4f();

    public List<APlane> planes = new ArrayList<>();

    public ShaderRender(Window window) {
        super(window);
        viewMatrix = viewMatrix.lookAt(
                new Vector3f(0.0f, 0.0f, 1.0f),
                new Vector3f(0.0f, 0.0f, 0.0f),
                new Vector3f(0.0f, 1.0f, 0.0f)
                );
        projectionMatrix = projectionMatrix.perspective(90.0f, window.w / (float) window.h, 0.1f, 1000.0f);
    }

    public void init() {
        shaderProgram = new ShaderProgram();
        APlane rectangle = new Rectangle(window, this);
        APlane triangle = new Triangle(window, this);
        planes.add(rectangle);
        planes.add(triangle);
    }

    @Override
    public void update() {
        if (!isInit) {
            init();
            isInit = true;
        }
        glUseProgram(shaderProgram.programID);
        for (APlane plane : planes) {
            plane.draw();
        }
        glUseProgram(0);
    }
}
