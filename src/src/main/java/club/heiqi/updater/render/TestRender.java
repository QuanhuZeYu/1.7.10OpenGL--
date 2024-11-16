package club.heiqi.updater.render;

import club.heiqi.updater.AUpdate;
import club.heiqi.window.Window;
import org.lwjgl.opengl.GL11;

public class TestRender extends AUpdate {
    public Window window;

    public TestRender(Window window) {
        super(window);
    }

    @Override
    public void update() {
        // 清除颜色缓冲区
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // 设置模型视图矩阵
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        // 应用变换：平移 -> 旋转 -> 缩放
        GL11.glTranslatef(0.5f, 0.5f, 0.0f); // 平移到屏幕右上角
        GL11.glRotatef((float) Math.toRadians(45.0f), 0.0f, 0.0f, 1.0f); // 绕 Z 轴旋转 45 度
        GL11.glScalef(0.5f, 0.5f, 1.0f); // 缩小一半

        // 绘制三角形
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glColor3f(1.0f, 0.0f, 0.0f); // 顶点1：红色
        GL11.glVertex2f(-0.5f, -0.5f);
        GL11.glColor3f(0.0f, 1.0f, 0.0f); // 顶点2：绿色
        GL11.glVertex2f(0.5f, -0.5f);
        GL11.glColor3f(0.0f, 0.0f, 1.0f); // 顶点3：蓝色
        GL11.glVertex2f(0.0f, 0.5f);
        GL11.glEnd();
        GL11.glLoadIdentity(); // 恢复为单位矩阵
    }
}
