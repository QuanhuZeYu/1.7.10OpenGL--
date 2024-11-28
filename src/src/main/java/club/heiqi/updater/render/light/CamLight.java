package club.heiqi.updater.render.light;

import club.heiqi.updater.render.Scene;
import club.heiqi.updater.render.plane.Drawable;
import club.heiqi.window.Window;
import org.joml.Vector3f;

import static club.heiqi.loger.MyLog.logger;

public class CamLight extends ALight implements Drawable {
    public CamLight(Window window, Scene scene) {
        super(window, scene);
        transform = camera.transform;
    }

    @Override
    public void setup() {
        Vector3f dir = camera.front;
        lightDirection.set(dir.x, dir.y, dir.z);
        initLightProp();
        initSpotLightProp();
        isSetup = true;
    }

    @Override
    public void draw() {
        Vector3f dir = camera.front;
        lightDirection.set(dir.x, dir.y, dir.z);
        lightPosition.set(camera.position.x, camera.position.y, camera.position.z);
        update();
    }
}
