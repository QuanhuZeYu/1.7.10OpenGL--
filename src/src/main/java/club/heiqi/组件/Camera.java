package club.heiqi.组件;

import club.heiqi.接口.IUpdate;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.opengl.GL11.*;

public class Camera implements IUpdate {
    public Transform transform;
    public float fov = 45;
    public float zNear = 0.01f, zFar = 1000;
    public int width, height;
    public Vector3f up, right, forward;
    public Vector3f tempVec = new Vector3f();

    public Matrix4f viewMatrix;
    public Matrix4f projectionMatrix;
    public FloatBuffer tempMat4Buffer;

    public Camera(int width, int height) {
        this.width = width;
        this.height = height;
        transform = new Transform();
        transform.setPosition(3.5f, 2.5f, 4f);
        up = new Vector3f();
        right = new Vector3f();
        forward = new Vector3f();
        viewMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
        tempMat4Buffer = BufferUtils.createFloatBuffer(16);
        setViewMatrix();
        setProjectionMatrix(width, height);
    }

    @Override
    public void update() {
        setViewMatrix();
    }

    public void setViewMatrix() {
        forward.set(0, 0, -1).rotate(transform.rotation);
        right.set(1, 0, 0).rotate(transform.rotation);
        up.set(0, 1, 0).rotate(transform.rotation);
        viewMatrix.identity().
                lookAt(transform.position,
                        transform.position.add(forward, tempVec.set(0)), // 通过相机位置和前向量计算看向的点
                        up);
        viewMatrix.m11(viewMatrix.m11() * -1);
        tempMat4Buffer = viewMatrix.get(tempMat4Buffer);
        glMatrixMode(GL_MODELVIEW);
        glLoadMatrixf(tempMat4Buffer);
    }

    public void setProjectionMatrix(int width, int height) {
        float aspect = (float) width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspect, zNear, zFar);
        projectionMatrix.m11(projectionMatrix.m11() * -1); // 垂直翻转
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            projectionMatrix.get(buffer);
            glMatrixMode(GL_PROJECTION);
            glLoadMatrixf(buffer);
        }
    }
}
