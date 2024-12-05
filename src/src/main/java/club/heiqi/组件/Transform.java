package club.heiqi.组件;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
    public Vector3f position;
    public Quaternionf rotation;
    public Vector3f scale;

    public Matrix4f matrix4f;

    public Transform() {
        position = new Vector3f(0, 0, 0);
        rotation = new Quaternionf().identity();
        scale = new Vector3f(1);
        matrix4f = new Matrix4f();
        updateMatrix();
    }

    // 更新变换矩阵
    public void updateMatrix() {
        matrix4f.identity()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
    }

    // 设置位置
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        updateMatrix();
    }

    // 设置旋转（使用四元数）
    public void setRotation(Quaternionf quat) {
        rotation.set(quat);
        updateMatrix();
    }

    // 设置缩放
    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
        updateMatrix();
    }

    /**
     * 平移变换
     * @param x
     * @param y
     * @param z
     */
    public void translate(float x, float y, float z) {
        position.add(x, y, z);
        updateMatrix();
    }

    /**
     * 旋转变换（绕任意轴旋转)
     * @param angle
     * @param axisX
     * @param axisY
     * @param axisZ
     */
    public void rotate(float angle, float axisX, float axisY, float axisZ) {
        Quaternionf quat = new Quaternionf().rotateAxis(angle, axisX, axisY, axisZ);
        rotation.mul(quat);
        updateMatrix();
    }

    /**
     * 旋转变换（绕向量旋转)
     * @param angle
     * @param axis
     */
    public void rotate(float angle, Vector3f axis) {
        rotate(angle, axis.x, axis.y, axis.z);
    }

    /**
     * 缩放变换
     * @param factor
     */
    public void scale(float factor) {
        scale.mul(factor);
        updateMatrix();
    }
}
