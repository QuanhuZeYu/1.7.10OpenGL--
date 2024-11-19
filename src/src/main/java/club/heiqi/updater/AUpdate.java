package club.heiqi.updater;

import club.heiqi.window.Window;

import java.util.UUID;

public abstract class AUpdate {
    public UUID uuid = UUID.randomUUID();
    public Window window;

    public boolean isNeedUnload = false;

    public AUpdate(Window window) {
        this.window = window;
    }
    public void update() {};

    @Override
    public int hashCode() {
        // 使用uuid计算哈希
        return uuid.hashCode();
    }
}
