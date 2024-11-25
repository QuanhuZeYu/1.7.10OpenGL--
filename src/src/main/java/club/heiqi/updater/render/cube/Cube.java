package club.heiqi.updater.render.cube;

import club.heiqi.shader.FragShader;
import club.heiqi.shader.VertexShader;
import club.heiqi.updater.render.Scene;
import club.heiqi.updater.render.plane.AMesh;
import club.heiqi.updater.render.transform.Transform;
import club.heiqi.window.Window;

import java.io.File;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Cube extends AMesh {
    public Cube(Window window, Scene scene) {
        super(window, scene);
        vertices = new float[] {
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,

                -0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,

                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,

                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,

                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f, -0.5f,

                -0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
        };
        normals = new float[]{
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,
                0.0f,  0.0f, -1.0f,

                0.0f,  0.0f,  1.0f,
                0.0f,  0.0f,  1.0f,
                0.0f,  0.0f,  1.0f,
                0.0f,  0.0f,  1.0f,
                0.0f,  0.0f,  1.0f,
                0.0f,  0.0f,  1.0f,

                -1.0f,  0.0f,  0.0f,
                -1.0f,  0.0f,  0.0f,
                -1.0f,  0.0f,  0.0f,
                -1.0f,  0.0f,  0.0f,
                -1.0f,  0.0f,  0.0f,
                -1.0f,  0.0f,  0.0f,

                1.0f,  0.0f,  0.0f,
                1.0f,  0.0f,  0.0f,
                1.0f,  0.0f,  0.0f,
                1.0f,  0.0f,  0.0f,
                1.0f,  0.0f,  0.0f,
                1.0f,  0.0f,  0.0f,

                0.0f, -1.0f,  0.0f,
                0.0f, -1.0f,  0.0f,
                0.0f, -1.0f,  0.0f,
                0.0f, -1.0f,  0.0f,
                0.0f, -1.0f,  0.0f,
                0.0f, -1.0f,  0.0f,

                0.0f,  1.0f,  0.0f,
                0.0f,  1.0f,  0.0f,
                0.0f,  1.0f,  0.0f,
                0.0f,  1.0f,  0.0f,
                0.0f,  1.0f,  0.0f,
                0.0f,  1.0f,  0.0f,
        };
        texturePath = "texture/test.png";
    }

    @Override
    public void setup() {
        vaoID = createVAO();
        glBindVertexArray(vaoID);

        vertexVBOID = createVBO(vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        if (normals != null) {
            normalVBOID = createVBO(normals, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
            glEnableVertexAttribArray(1);
        }
        // 预处理顶点颜色
//        if (colors == null || colors.length != vertices.length) {
//            colors = new float[vertices.length];
//            for (int i = 0; i < (colors.length / 3); i++) {
//                colors[i] = DEFAULT_COLOR.x;
//                colors[i + 1] = DEFAULT_COLOR.y;
//                colors[i + 2] = DEFAULT_COLOR.z;
//            }
//        }
//        colorVBOID = createVBO(colors, GL_STATIC_DRAW);
//        glVertexAttribPointer(2, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
//        glEnableVertexAttribArray(2);
        // 预处理纹理坐标
//        if (textureCoords == null || textureCoords.length < 2) {
//            textureCoords = DEFAULT_TEXTURE_COORDS;
//        }
//        if (texturePath != null) {
//            File textureF = new File(texturePath);
//            createTexture(textureF, textureCoords);
//            glEnableVertexAttribArray(3);
//        }

        if (objectColor == null) objectColor = DEFAULT_OBJECT_COLOR;

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        isSetup = true;
    }

    @Override
    public void draw() {
        if (!isSetup) setup();
        glBindVertexArray(vaoID);
        if (hasTexture) glBindTexture(GL_TEXTURE_2D, textureID);
        objShaderProgram.setUniform(FragShader.UniformName.OBJECT_COLOR.name, objectColor);
        objShaderProgram.setUniform(VertexShader.UniformName.ModelTrans.name, transform.modelMatrix);
        drawElement();
        glBindVertexArray(0);
    }

    @Override
    public void drawElement() {
//        glDrawElements(GL_TRIANGLES, vertices.length, GL_UNSIGNED_INT, 0);
        glDrawArrays(GL_TRIANGLES, 0, vertices.length / 3);
    }
}
