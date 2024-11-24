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
        this.position = new Vector3f().zero();
        this.quaternionf = new Quaternionf();
        this.scale = new Vector3f(1, 1, 1);
        this.modelMatrix = new Matrix4f().identity();
    }

    public void addPosition(float x, float y, float z) {
        this.position.add(x, y, z);
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public void addRotation(float x, float y, float z) {
        this.quaternionf.rotateXYZ(x, y, z);
    }

    public void setRotation(float x, float y, float z) {
        this.quaternionf.identity().rotateXYZ(x, y, z);
    }

    public void setRotation(float x, float y, float z, EulerRotateAxis axis) {
        x = (float) Math.toRadians(x);
        y = (float) Math.toRadians(y);
        z = (float) Math.toRadians(z);
        switch (axis) {
            case XYZ -> {
                this.quaternionf.identity().rotateXYZ(x, y, z);
            }
            case YXZ -> {
                this.quaternionf.identity().rotateYXZ(y, x, z);
            }
            case ZYX -> {
                this.quaternionf.identity().rotateZYX(z, y, x);
            }
        }
    }

    public void setScale(float scale) {
        setScale(scale, scale, scale);
    }

    public void setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
    }

    public Matrix4f updateMatrix() {
        this.modelMatrix.identity()
                .translate(this.position)
                .rotate(this.quaternionf)
                .scale(this.scale);
        return this.modelMatrix;
    }

    public void reset() {
        this.position.zero();
        this.quaternionf.identity();
        this.scale.set(1, 1, 1);
    }
}
