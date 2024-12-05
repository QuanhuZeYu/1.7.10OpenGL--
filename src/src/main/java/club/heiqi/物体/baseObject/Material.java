package club.heiqi.物体.baseObject;

import club.heiqi.接口.IBind;
import club.heiqi.接口.ISetup;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class Material implements ISetup, IBind {
    public static List<Material> materials = new ArrayList<>();

    /**
     * 该材质下的所有贴图纹理集合；该项可能为null
     */
    @Nullable
    public Map<String, Texture> textures = new HashMap<>(); // 目前String暂无作用

    public int materialID;
    public float[] ambient;
    public float[] diffuse;
    public float[] specular;
    public float[] emissive;
    public float shininess;

    public void setup() {
        if (textures != null) {
            textures.forEach((key, texture) -> {
                if (texture.textureID == -1)
                    texture.setup();
            });
        }
    }

    /**
     * 绑定该材质的纹理和材质属性
     */
    public void bind() {
        // 设置漫反射贴图
        textures.forEach((key, texture) -> {
            if (texture.type == Texture.Type.DIFFUSE) {
                texture.bind();
            }
        });
        // 设置颜色属性
        glMaterialfv(GL_FRONT, GL_AMBIENT, ambient);
        glMaterialfv(GL_FRONT, GL_DIFFUSE, diffuse);
        glMaterialfv(GL_FRONT, GL_SPECULAR, specular);
        glMaterialfv(GL_FRONT, GL_EMISSION, emissive);
        glMaterialf(GL_FRONT, GL_SHININESS, shininess);
    }
}
