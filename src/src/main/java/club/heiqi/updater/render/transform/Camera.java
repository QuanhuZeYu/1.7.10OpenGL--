package club.heiqi.updater.render.transform;

import club.heiqi.shader.ShaderProgram;
import club.heiqi.shader.VertexShader;
import club.heiqi.updater.AUpdate;
import club.heiqi.updater.controller.KeyInput;
import club.heiqi.updater.controller.MouseInput;
import club.heiqi.updater.render.Scene;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera extends AUpdate {
    public Transform trans;
    public Vector3f position;
    public Quaternionf rotation;
    public Vector3f up, right, front;
    public float speed = 0.02f;
    public float sensitivity = 5f;
    public Vector3f moveVec, tempVec;
    public Quaternionf tempQuat;

    public float yaw = 0.0f; // 临时变量, 每帧都会更新
    public float pitch = 0.0f;

    public ShaderProgram shaderProgram;
    public KeyInput keyInput;
    public MouseInput mouseInput;
    public Matrix4f viewMatrix;
    public Matrix4f invertViewMatrix;
    public Matrix4f projectionMatrix = new Matrix4f();

    public Camera(Scene scene) {
        super(scene.window);
        shaderProgram = scene.shaderProgram;
        this.keyInput = (KeyInput) scene.window.keyInputController;
        this.mouseInput = (MouseInput) scene.window.mouseInputController;
        trans = new Transform();
        trans.position = new Vector3f(0, 0, 1);
        position = trans.position;
        rotation = trans.quaternionf;
        viewMatrix = new Matrix4f();
        invertViewMatrix = new Matrix4f();
        projectionMatrix = projectionMatrix.perspective(45.0f, window.w / (float) window.h, 0.1f, 1000.0f);
        tempQuat = new Quaternionf();
        front = new Vector3f();
        right = new Vector3f();
        moveVec = new Vector3f();
        tempVec = new Vector3f();
        up = new Vector3f();
        updateViewMatrix();
    }

    public void updateViewMatrix() {
        front.set(0, 0, -1).rotate(rotation);
        right.set(1, 0, 0).rotate(rotation);
        up.set(0, 1, 0).rotate(rotation);
        viewMatrix.identity().
                lookAt(position,
                        position.add(front, tempVec.set(0)), // 通过相机位置和前向量计算看向的点
                        up);
        invertViewMatrix.set(viewMatrix).invert();
        shaderProgram.setUniform(VertexShader.UniformName.View.name, viewMatrix);
    }

    public void addRotation(float x, float y) {
        Quaternionf yaw = tempQuat.identity().rotateY((float) Math.toRadians(y));
        rotation.identity().mul(yaw);
        Quaternionf pitch = tempQuat.identity().rotateX((float) Math.toRadians(x));
        rotation.mul(pitch);
        updateViewMatrix();
    }

    @Override
    public void update() {
        if (mouseInput.isMiddleButtonPressed) {
            mouseInput.isCursorLocked = !mouseInput.isCursorLocked;
            glfwSetInputMode(window.handle, GLFW_CURSOR, mouseInput.isCursorLocked ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
        }
        moveCamera();
        rotateCamera();
    }

    public void rotateCamera() {
        if (mouseInput.isLeftButtonPressed) {
            boolean rotX = mouseInput.deltaX != 0;
            boolean rotY = mouseInput.deltaY != 0;
            if (rotX) {
                yaw -= (float) (mouseInput.deltaX * sensitivity);
            }
            if (rotY) {
                pitch -= (float) (mouseInput.deltaY * sensitivity);
            }
            addRotation((float) Math.toRadians(pitch), (float) Math.toRadians(yaw));
        }
    }

    public void moveCamera() {
        moveVec.set(0, 0, 0);
        // 检测键盘输入，并累加对应方向的移动矢量
        if (keyInput.pressedKeys.contains(GLFW_KEY_W)) { // 前
            viewMatrix.positiveZ(tempVec).negate(); // 前方向
            moveVec.add(tempVec);
        }
        if (keyInput.pressedKeys.contains(GLFW_KEY_S)) { // 后
            viewMatrix.positiveZ(tempVec); // 后方向
            moveVec.add(tempVec);
        }
        if (keyInput.pressedKeys.contains(GLFW_KEY_A)) { // 左
            viewMatrix.positiveX(tempVec).negate(); // 左方向
            moveVec.add(tempVec);
        }
        if (keyInput.pressedKeys.contains(GLFW_KEY_D)) { // 右
            viewMatrix.positiveX(tempVec); // 右方向
            moveVec.add(tempVec);
        }
        if (keyInput.pressedKeys.contains(GLFW_KEY_SPACE)) { // 上
            tempVec.set(0, 1, 0);
            moveVec.add(tempVec);
        }
        if (keyInput.pressedKeys.contains(GLFW_KEY_LEFT_CONTROL)) { // 下
            tempVec.set(0, -1, 0);
            moveVec.add(tempVec);
        }
        // 归一化移动向量，防止斜向移动加速
        if (moveVec.lengthSquared() > 0) { // 确保移动向量非零
            moveVec.normalize().mul(speed); // 按速度缩放
            position.add(moveVec);          // 更新摄像机位置
            updateViewMatrix();             // 更新视图矩阵
        }
    }
}
