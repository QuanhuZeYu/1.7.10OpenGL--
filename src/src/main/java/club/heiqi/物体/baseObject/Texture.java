package club.heiqi.物体.baseObject;

import club.heiqi.接口.IBind;
import club.heiqi.接口.ISetup;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_load;

/**
 * 在固定管线中只能使用漫反射贴图
 */
public class Texture implements ISetup, IBind {
    public static List<Texture> textures = new ArrayList<>();

    public String absPath;
    public String textureName;
    /** 指为本类中的枚举类型Type */
    public Type type;

    public int textureID = -1;

    /**
     * 给纹理绑定ID并设置mipmap
     */
    public void setup() {
        // 纹理重复直接使用相同的纹理ID
        for (Texture texture : textures) {
            if (texture.absPath.equals(absPath)) {
                textureID = texture.textureID;
                return;
            }
        }
        if (absPath == null) return;
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        ImageData image = loadImage(absPath);
        if (image != null) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width, image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image.buf);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glGenerateMipmap(GL_TEXTURE_2D);
        } else {
            logger.error("{} 纹理加载失败", absPath);
        }
    }

    /**
     * 绑定纹理ID用于渲染
     */
    public void bind() {
        if (textureID == -1) {
            logger.error("{} 纹理ID无效", absPath);
            return;
        }
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
    }

    public ImageData loadImage(String absPath) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer nrChannels = stack.mallocInt(1);
            // RGBA
            ByteBuffer buf = stbi_load(absPath, width, height, nrChannels, 4);
            ImageData data = new ImageData();
            data.buf = buf;
            data.width = width.get();
            data.height = height.get();
            return data;
        }
    }

    public enum Type {
        DIFFUSE("diffuse"),     // 漫反射
        SPECULAR("specular"),   // 镜面反射
        ;
        public final String type;
        Type(String type) {
            this.type = type;
        }
    }

    public class ImageData {
        public ByteBuffer buf;
        public int width;
        public int height;
    }
}
