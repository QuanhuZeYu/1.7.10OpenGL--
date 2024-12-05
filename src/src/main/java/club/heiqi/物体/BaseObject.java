package club.heiqi.物体;

import club.heiqi.接口.IBind;
import club.heiqi.接口.IDrawable;
import club.heiqi.物体.baseObject.Material;
import club.heiqi.物体.baseObject.Mesh;
import club.heiqi.物体.baseObject.Texture;
import club.heiqi.组件.Transform;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static club.heiqi.loger.MyLog.logger;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class BaseObject implements IDrawable {
    public List<Mesh> meshes = new ArrayList<>();

    public Matrix4f tempMatrix = new Matrix4f();
    public float[] tempMatrixArray = new float[16];

    public Transform transform;

    public BaseObject() {
        transform = new Transform();
    }

    public BaseObject(String absPath) {
        loadModel(absPath);
        setup();
        transform = new Transform();
    }

    @Override
    public void draw() {
        setModelMatrix();
        for (Mesh mesh : meshes) {
            mesh.draw();
        }
    }

    public void setup() {
        for (Mesh mesh : meshes) {
            mesh.setup();
        }
    }

    /**
     * 根据当前的Transform计算OpenGL固定管线矩阵
     */
    public void setModelMatrix() {
        glPushMatrix();
        glLoadIdentity();
        Quaternionf rotate = transform.rotation;
        Vector3f position = transform.position;
        Vector3f scale = transform.scale;
        // 应用平移 (Translation)
        glTranslatef(position.x, position.y, position.z);
        // 应用旋转 (Rotation)
        // 四元数转换为旋转矩阵
        Matrix4f rotationMatrix = tempMatrix;
        rotate.get(rotationMatrix);  // 将四元数转换为4x4矩阵
        glMultMatrixf(rotationMatrix.get(tempMatrixArray));
        // 应用缩放 (Scaling)
        glScalef(scale.x, scale.y, scale.z);
        glPopMatrix();
    }

    public void loadModel(String absPath) {
        AIScene scene = aiImportFile(absPath, aiProcess_Triangulate | aiProcess_JoinIdenticalVertices);
        try {
            if (scene == null) return;
            int numMeshes = scene.mNumMeshes();
            int numMaterials = scene.mNumMaterials();
            for (int i = 0; i < numMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(i));
                Mesh mesh = processMesh(aiMesh);
                meshes.add(mesh);
                aiMesh.close();
            }
            for (int matID = 0; matID < numMaterials; matID++) {
                AIMaterial aiMaterial = AIMaterial.create(Objects.requireNonNull(scene.mMaterials()).get(matID));
                Material material = processMaterial(aiMaterial, new File(absPath).getParent());
                material.materialID = matID;
                Material.materials.add(material);
                // 绑定材质索引
                for (Mesh mesh : meshes) {
                    if (mesh.aimMaterialIndex == matID) {
                        mesh.materials.put(material.materialID, material);
                        break;
                    }
                }
                aiMaterial.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Material processMaterial(AIMaterial material, String modelDir) {
        Material waitCacheMat = new Material();
        // 提取漫反射贴图
        int numTexture = aiGetMaterialTextureCount(material, aiTextureType_DIFFUSE);
        if (numTexture > 0) {
            Texture texture = new Texture(); // 该材质对象需要提取的贴图-漫反射贴图
            String relativePath = extractTexturePath(material, aiTextureType_DIFFUSE);
            String fileName = new File(relativePath).getName();
            String absPath = modelDir + File.separator + fileName;
            texture.absPath = absPath;
            texture.textureName = fileName;
            waitCacheMat.textures.put(absPath, texture);
        }
        // 提取材质属性
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // 获取环境色
            AIColor4D colorBuffer = AIColor4D.calloc(stack);
            int result = aiGetMaterialColor(material, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, colorBuffer);
            if (result == aiReturn_SUCCESS) {
                waitCacheMat.ambient = new float[]{colorBuffer.r(), colorBuffer.g(), colorBuffer.b(), colorBuffer.a()};
            }
            // 获取漫反射颜色
            colorBuffer = AIColor4D.calloc(stack);
            result = aiGetMaterialColor(material, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, colorBuffer);
            if (result == aiReturn_SUCCESS) {
                waitCacheMat.diffuse = new float[]{colorBuffer.r(), colorBuffer.g(), colorBuffer.b(), colorBuffer.a()};
            }
            // 获取镜面高光颜色
            colorBuffer = AIColor4D.calloc(stack);
            result = aiGetMaterialColor(material, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, colorBuffer);
            if (result == aiReturn_SUCCESS) {
                waitCacheMat.specular = new float[]{colorBuffer.r(), colorBuffer.g(), colorBuffer.b(), colorBuffer.a()};
            }
            // 自发光
            colorBuffer = AIColor4D.calloc(stack);
            result = aiGetMaterialColor(material, AI_MATKEY_COLOR_EMISSIVE, aiTextureType_NONE, 0, colorBuffer);
            if (result == aiReturn_SUCCESS) {
                waitCacheMat.emissive = new float[]{colorBuffer.r(), colorBuffer.g(), colorBuffer.b(), colorBuffer.a()};
            }
            // 获取镜面高光指数
            float[] shininessFactor = new float[]{0.0f};
            int[] pMax = new int[]{1};
            result = aiGetMaterialFloatArray(material, AI_MATKEY_SHININESS_STRENGTH, aiTextureType_NONE, 0, shininessFactor, pMax);
            if (result != aiReturn_SUCCESS) {
                waitCacheMat.shininess = shininessFactor[0];
            }
        }
        return waitCacheMat;
    }

    public Mesh processMesh(AIMesh aiMesh) {
        Mesh mesh = new Mesh();
        List<Float> vertices = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> textureCoords = new ArrayList<>();
        List<Float> colors = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        // 处理顶点数据
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D vertex = aiVertices.get();
            vertices.add(vertex.x());
            vertices.add(vertex.y());
            vertices.add(vertex.z());
        }
        mesh.vertices = toFloatArray(vertices);
        // 法线
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals.remaining() > 0) {
            AIVector3D normal = aiNormals.get();
            normals.add(normal.x());
            normals.add(normal.y());
            normals.add(normal.z());
        }
        mesh.normals = toFloatArray(normals);
        // 纹理坐标
        AIVector3D.Buffer aiTexCoords = aiMesh.mTextureCoords(0);
        while (aiTexCoords.remaining() > 0) {
            AIVector3D texCoord = aiTexCoords.get();
            textureCoords.add(texCoord.x());
            textureCoords.add(texCoord.y());
        }
        mesh.textureCoords = toFloatArray(textureCoords);
        // 颜色
        AIColor4D.Buffer aiColors = aiMesh.mColors(0);
        if (aiColors != null) {
            while (aiColors.remaining() > 0) {
                AIColor4D color = aiColors.get();
                colors.add(color.r());
                colors.add(color.g());
                colors.add(color.b());
                colors.add(color.a());
            }
        }
        mesh.colors = toFloatArray(colors);
        // 索引
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace face = aiFaces.get(i);
            for (int j = 0; j < face.mNumIndices(); j++) {
                int index = face.mIndices().get(j);
                indices.add(index);
            }
        }
        mesh.indices = toIntArray(indices);
        // 材质ID
        mesh.aimMaterialIndex = aiMesh.mMaterialIndex();
        return mesh;
    }

    public String extractTexturePath(AIMaterial material, int textureType) {
        AIString path = AIString.calloc();
        IntBuffer max = BufferUtils.createIntBuffer(1024);
        aiGetMaterialTexture(material, textureType, 0, path, max,
                null, null, null, null,
                null);
        String pathStr = path.dataString();
        if (pathStr != null && !pathStr.isEmpty()) {
            return pathStr;
        }
        return null;
    }

    public float[] toFloatArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public int[] toIntArray(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
