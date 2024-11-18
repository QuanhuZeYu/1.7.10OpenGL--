package club.heiqi.updater.render.transform;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
    public Vector3f position;
    public Quaternionf quaternionf;
    public Vector3f scale;
    public Matrix4f modelMatrix;

    public Transform() {
        position = new Vector3f().zero();
        quaternionf = new Quaternionf();
        scale = new Vector3f(1, 1, 1);
        modelMatrix = new Matrix4f().identity();
    }

    public void addPosition(float x, float y, float z) {
        position.add(x, y, z);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void setRotation(float x, float y, float z) {
        quaternionf.identity().rotateXYZ(x, y, z);
    }

    public void setRotation(float x, float y, float z, EulerRotateAxis axis) {
        x = (float) Math.toRadians(x);
        y = (float) Math.toRadians(y);
        z = (float) Math.toRadians(z);
        switch (axis) {
            case XYZ -> {
                quaternionf.identity().rotateXYZ(x, y, z);
                return;
            }
            case YXZ -> {
                quaternionf.identity().rotateYXZ(y, x, z);
                return;
            }
            case ZYX -> {
                quaternionf.identity().rotateZYX(z, y, x);
                return;
            }
        }
    }

    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
    }

    public Matrix4f updateMatrix() {
        modelMatrix.identity()
                .translate(position)
                .rotate(quaternionf)
                .scale(scale);
        return modelMatrix;
    }

    public void reset() {
        position.zero();
        quaternionf.identity();
        scale.set(1, 1, 1);
    }
}
