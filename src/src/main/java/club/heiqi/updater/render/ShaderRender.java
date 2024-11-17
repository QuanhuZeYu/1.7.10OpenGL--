package club.heiqi.updater.render;

import club.heiqi.shader.FragShader;
import club.heiqi.shader.ShaderProgram;
import club.heiqi.shader.VertexShader;
import club.heiqi.updater.AUpdate;
import club.heiqi.updater.render.plane.APlane;
import club.heiqi.updater.render.plane.Rectangle;
import club.heiqi.updater.render.plane.Triangle;
import club.heiqi.window.Window;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class ShaderRender extends AUpdate {
    public boolean isInit = false;

    public ShaderProgram shaderProgram;

    public List<APlane> planes = new ArrayList<>();

    public ShaderRender(Window window) {
        super(window);
    }

    public void init() {
        shaderProgram = new ShaderProgram();
        APlane rectangle = new Rectangle(window);
        APlane triangle = new Triangle(window);
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
